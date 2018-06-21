/*
 * Copyright 2016-2018 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.datastore.mongo.sync.internal.operations;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.query.QueryAdapterQuery;
import com.holonplatform.core.internal.query.QueryDefinition;
import com.holonplatform.core.query.Query;
import com.holonplatform.core.query.QueryAdapter;
import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.core.query.QueryOperation;
import com.holonplatform.datastore.mongo.core.context.MongoContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.internal.MongoOperationConfigurator;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Projections;

/**
 * MongoDB {@link QueryAdapter}.
 *
 * @since 5.2.0
 */
public class MongoQuery implements QueryAdapter<QueryConfiguration> {

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Query> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Query>() {

		@Override
		public Class<? extends Query> getCommodityType() {
			return Query.class;
		}

		@Override
		public Query createCommodity(SyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new QueryAdapterQuery<>(new MongoQuery(context), QueryDefinition.create());
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public MongoQuery(MongoOperationContext<MongoDatabase> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryAdapter#stream(com.holonplatform.core.query.QueryOperation)
	 */
	@Override
	public <R> Stream<R> stream(final QueryOperation<QueryConfiguration, R> queryOperation) throws DataAccessException {

		// validate
		queryOperation.validate();

		// resolution context
		final MongoResolutionContext context = MongoResolutionContext.create(operationContext);

		// resolve query
		final BsonQuery query = context.resolveOrFail(queryOperation, BsonQuery.class);

		// collection name
		final String collectionName = query.getDefinition().getCollectionName();

		return operationContext.withDatabase(database -> {

			// get and configure collection
			final MongoCollection<Document> collection = MongoOperationConfigurator
					.configureRead(database.getCollection(collectionName), context, queryOperation.getConfiguration());

			// query operation type
			switch (query.getOperationType()) {
			case AGGREGATE:
				return aggregate(context, collection, queryOperation.getProjection().getType(), query);
			case COUNT:
				return count(context, collection, query.getDefinition());
			case DISTINCT:
				return distinct(context, collection, queryOperation.getProjection().getType(), query);
			case FIND:
			default:
				return find(context, collection, queryOperation.getProjection().getType(), query);
			}
		});
	}

	/**
	 * Perform a <em>count</em> operation on given collection using given {@link BsonQueryDefinition}.
	 * @param <R> Operation result type
	 * @param context Operation context
	 * @param collection The collection to use
	 * @param definition Query definition
	 * @return The operation result
	 */
	@SuppressWarnings("unchecked")
	private static <R> Stream<R> count(MongoContext context, MongoCollection<Document> collection,
			BsonQueryDefinition definition) {

		// trace
		context.trace("COUNT query",
				"Filter: \n" + DocumentSerializer.getDefault().toJson(definition.getFilter().orElse(null)));

		// count
		Long count;
		if (definition.getFilter().isPresent()) {
			count = collection.count(definition.getFilter().get());
		} else {
			count = collection.count();
		}

		return Stream.of((R) count);
	}

	/**
	 * Perform a <em>find</em> operation on given collection using given {@link BsonQuery}.
	 * @param <R> Operation result type
	 * @param context Resolution context
	 * @param collection The collection to use
	 * @param resultType Expected query result type
	 * @param query Query definition
	 * @return The operation result
	 */
	private static <R> Stream<R> find(MongoResolutionContext context, MongoCollection<Document> collection,
			Class<? extends R> resultType, BsonQuery query) {

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(query, resultType);

		final FindIterable<Document> fi = collection.find();

		// definition
		final BsonQueryDefinition definition = query.getDefinition();
		// filter
		definition.getFilter().ifPresent(f -> fi.filter(f));
		// sort
		definition.getSort().ifPresent(s -> fi.sort(s));
		// limit-offset
		definition.getLimit().ifPresent(l -> fi.limit(l));
		definition.getOffset().ifPresent(o -> fi.skip(o));
		// timeout
		definition.getTimeout().ifPresent(t -> fi.maxTime(t, definition.getTimeoutUnit()));
		// cursor
		definition.getCursorType().ifPresent(c -> fi.cursorType(c));
		// batch size
		definition.getBatchSize().ifPresent(b -> fi.batchSize(b));
		// collation
		definition.getCollation().ifPresent(c -> fi.collation(c));
		// comment
		definition.getComment().ifPresent(c -> fi.comment(c));
		// hint
		definition.getHint().ifPresent(h -> fi.hint(h));
		// max-min
		definition.getMax().ifPresent(m -> fi.max(m));
		definition.getMin().ifPresent(m -> fi.min(m));
		// max scan
		definition.getMaxScan().ifPresent(m -> fi.maxScan(m));
		// partial
		if (definition.isPartial()) {
			fi.partial(true);
		}
		// return key
		if (definition.isReturnKey()) {
			fi.returnKey(true);
		}
		// record id
		if (definition.isShowRecordId()) {
			fi.showRecordId(true);
		}
		// snapshot
		if (definition.isSnapshot()) {
			fi.snapshot(true);
		}

		// projection
		Optional<Bson> projection = query.getProjection().filter(p -> !p.isEmpty())
				.map(p -> Projections.fields(p.getFieldProjections()));
		projection.ifPresent(p -> fi.projection(p));

		// trace
		context.trace("FIND query", () -> MongoOperations.traceQuery(context, query, projection.orElse(null)));

		// stream with converter mapper
		return StreamSupport.stream(fi.spliterator(), false)
				.map(document -> documentConverter.convert(context, document));
	}

	private static <R> Stream<R> distinct(MongoResolutionContext context, MongoCollection<Document> collection,
			Class<? extends R> resultType, BsonQuery query) {

		// check distinct field name
		if (!query.getDistinctFieldName().isPresent()) {
			return find(context, collection, resultType, query);
		}

		final String fieldName = query.getDistinctFieldName().get();

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(query, resultType);

		final DistinctIterable<Object> fi = collection.distinct(fieldName, Object.class);

		// definition
		final BsonQueryDefinition definition = query.getDefinition();
		// filter
		definition.getFilter().ifPresent(f -> fi.filter(f));
		// timeout
		definition.getTimeout().ifPresent(t -> fi.maxTime(t, definition.getTimeoutUnit()));
		// batch size
		definition.getBatchSize().ifPresent(b -> fi.batchSize(b));
		// collation
		definition.getCollation().ifPresent(c -> fi.collation(c));

		// trace
		context.trace("DISTINCT query on [" + fieldName + "]", () -> MongoOperations.traceQuery(context, query, null));

		// stream with converter mapper
		return StreamSupport.stream(fi.spliterator(), false)
				.map(value -> new Document(Collections.singletonMap(fieldName, value)))
				.map(document -> documentConverter.convert(context, document));
	}

	/**
	 * Perform a <em>aggregate</em> operation on given collection using given {@link BsonQuery}.
	 * @param <R> Operation result type
	 * @param operationContext Operation context
	 * @param context Resolution context
	 * @param collection The collection to use
	 * @param resultType Expected query result type
	 * @param query Query definition
	 * @return The operation result
	 */
	private static <R> Stream<R> aggregate(MongoResolutionContext context, MongoCollection<Document> collection,
			Class<? extends R> resultType, BsonQuery query) {

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(query, resultType);

		// aggregation pipeline
		List<Bson> pipeline = new LinkedList<>();

		// definition
		final BsonQueryDefinition definition = query.getDefinition();

		// ------ match
		definition.getFilter().ifPresent(f -> pipeline.add(Aggregates.match(f)));

		// ------ group
		definition.getGroup().ifPresent(g -> {

			final List<BsonField> fieldAccumulators = new LinkedList<>();

			// check projection
			query.getProjection().ifPresent(p -> {
				p.getFields().entrySet().stream().filter(e -> e.getValue() != null)
						.map(e -> new BsonField(e.getKey(), e.getValue())).forEach(bf -> fieldAccumulators.add(bf));
			});

			if (!fieldAccumulators.isEmpty()) {
				pipeline.add(Aggregates.group(g, fieldAccumulators));
			} else {
				pipeline.add(Aggregates.group(g));
			}

			definition.getGroupFilter().ifPresent(gf -> {
				pipeline.add(Aggregates.match(gf));
			});
		});

		// ------ sort
		definition.getSort().ifPresent(s -> pipeline.add(Aggregates.sort(s)));

		// ------ limit
		definition.getLimit().ifPresent(l -> pipeline.add(Aggregates.limit(l)));

		// ------ skip
		definition.getOffset().ifPresent(o -> pipeline.add(Aggregates.skip(o)));

		// ------ project
		if (!definition.getGroup().isPresent()) {
			query.getProjection().filter(p -> !p.isEmpty()).map(p -> Projections.fields(p.getFieldProjections()))
					.ifPresent(prj -> {
						pipeline.add(Aggregates.project(prj));
					});
		}
		// trace
		context.trace("Aggregation pipeline", () -> MongoOperations.traceAggregationPipeline(context, pipeline));

		// iterable
		final AggregateIterable<Document> ai = collection.aggregate(pipeline);

		// timeout
		definition.getTimeout().ifPresent(t -> ai.maxTime(t, definition.getTimeoutUnit()));
		// batch size
		definition.getBatchSize().ifPresent(b -> ai.batchSize(b));
		// collation
		definition.getCollation().ifPresent(c -> ai.collation(c));
		// comment
		definition.getComment().ifPresent(c -> ai.comment(c));
		// hint
		definition.getHint().ifPresent(h -> ai.hint(h));

		// stream with converter mapper
		return StreamSupport.stream(ai.spliterator(), false)
				.map(document -> documentConverter.convert(context, document));
	}

}
