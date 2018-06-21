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
package com.holonplatform.datastore.mongo.async.internal.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.async.datastore.operation.AsyncQuery;
import com.holonplatform.async.datastore.operation.AsyncQueryAdapter;
import com.holonplatform.async.internal.datastore.operation.AsyncQueryAdapterQuery;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.internal.query.QueryDefinition;
import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.core.query.QueryOperation;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.async.internal.MongoOperationConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.QueryOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.mongodb.async.client.AggregateIterable;
import com.mongodb.async.client.DistinctIterable;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Projections;

/**
 * MongoDB {@link AsyncQueryAdapter}.
 *
 * @since 5.2.0
 */
public class AsyncMongoQuery implements AsyncQueryAdapter<QueryConfiguration> {

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncQuery> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncQuery>() {

		@Override
		public Class<? extends AsyncQuery> getCommodityType() {
			return AsyncQuery.class;
		}

		@Override
		public AsyncQuery createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new AsyncQueryAdapterQuery<>(new AsyncMongoQuery(context), QueryDefinition.create());
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public AsyncMongoQuery(MongoOperationContext<MongoDatabase> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.async.datastore.operation.AsyncQueryAdapter#stream(com.holonplatform.core.query.QueryOperation)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <R> CompletionStage<Stream<R>> stream(final QueryOperation<QueryConfiguration, R> queryOperation) {
		return CompletableFuture.supplyAsync(() -> {
			// validate
			queryOperation.validate();
			// context
			final MongoResolutionContext context = MongoResolutionContext.create(operationContext);
			// resolve query
			final BsonQuery query = context.resolveOrFail(queryOperation, BsonQuery.class);
			// resolve collection name
			final String collectionName = query.getDefinition().getCollectionName();
			// get and configure collection
			final MongoCollection<Document> collection = operationContext.withDatabase(database -> {
				return MongoOperationConfigurator.configureRead(database.getCollection(collectionName), context,
						queryOperation.getConfiguration());
			});
			// build context
			return (QueryOperationContext<R>) QueryOperationContext.create(operationContext, context, collection, query,
					queryOperation.getProjection().getType());
		}).thenCompose(context -> {
			// query operation type
			switch (context.getQuery().getOperationType()) {
			case AGGREGATE:
				return aggregate(context);
			case COUNT:
				return count(context);
			case DISTINCT:
				return distinct(context);
			case FIND:
			default:
				return find(context);
			}
		});
	}

	/**
	 * Perform a <em>count</em> operation.
	 * @param <R> Query result type
	 * @param queryContext Operation context
	 * @return The operation result
	 */
	@SuppressWarnings("unchecked")
	private static <R> CompletionStage<Stream<R>> count(QueryOperationContext<R> queryContext) {

		// check filter
		final Bson filter = queryContext.getQuery().getDefinition().getFilter().orElse(null);

		// trace
		queryContext.getOperationContext().trace("COUNT query",
				"Filter: \n" + DocumentSerializer.getDefault().toJson(filter));

		final CompletableFuture<Stream<R>> operation = new CompletableFuture<>();

		// count
		if (filter != null) {
			queryContext.getCollection().count(filter, (result, error) -> {
				if (error != null) {
					operation.completeExceptionally(error);
				} else {
					operation.complete(Stream.of((R) result));
				}
			});
		} else {
			queryContext.getCollection().count((result, error) -> {
				if (error != null) {
					operation.completeExceptionally(error);
				} else {
					operation.complete(Stream.of((R) result));
				}
			});
		}

		return operation;
	}

	/**
	 * Perform a <em>find</em> operation.
	 * @param <R> Query result type
	 * @param queryContext Operation context
	 * @return The operation result
	 */
	private static <R> CompletionStage<Stream<R>> find(QueryOperationContext<R> queryContext) {

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(queryContext.getQuery(),
				queryContext.getResultType());

		final FindIterable<Document> fi = queryContext.getCollection().find();

		// definition
		final BsonQueryDefinition definition = queryContext.getQuery().getDefinition();
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
		Optional<Bson> projection = queryContext.getQuery().getProjection().filter(p -> !p.isEmpty())
				.map(p -> Projections.fields(p.getFieldProjections()));
		projection.ifPresent(p -> fi.projection(p));

		// trace
		queryContext.getOperationContext().trace("FIND query", () -> MongoOperations
				.traceQuery(queryContext.getOperationContext(), queryContext.getQuery(), projection.orElse(null)));

		// documents
		final List<Document> documents = new ArrayList<>();

		final CompletableFuture<Stream<R>> operation = new CompletableFuture<>();
		// execute query
		fi.forEach(document -> documents.add(document), (result, error) -> {
			if (error != null) {
				operation.completeExceptionally(error);
			} else {
				operation.complete(documents.stream()
						// apply converter
						.map(document -> documentConverter.convert(queryContext.getResolutionContext(), document)));
			}
		});
		return operation;
	}

	/**
	 * Perform a <em>distinct</em> operation.
	 * @param <R> Query result type
	 * @param queryContext Operation context
	 * @return The operation result
	 */
	private static <R> CompletionStage<Stream<R>> distinct(QueryOperationContext<R> queryContext) {

		// check distinct field name
		if (!queryContext.getQuery().getDistinctFieldName().isPresent()) {
			return find(queryContext);
		}

		final String fieldName = queryContext.getQuery().getDistinctFieldName().get();

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(queryContext.getQuery(),
				queryContext.getResultType());

		final DistinctIterable<Object> fi = queryContext.getCollection().distinct(fieldName, Object.class);

		// definition
		final BsonQueryDefinition definition = queryContext.getQuery().getDefinition();
		// filter
		definition.getFilter().ifPresent(f -> fi.filter(f));
		// timeout
		definition.getTimeout().ifPresent(t -> fi.maxTime(t, definition.getTimeoutUnit()));
		// batch size
		definition.getBatchSize().ifPresent(b -> fi.batchSize(b));
		// collation
		definition.getCollation().ifPresent(c -> fi.collation(c));

		// trace
		queryContext.getOperationContext().trace("DISTINCT query on [" + fieldName + "]",
				() -> MongoOperations.traceQuery(queryContext.getOperationContext(), queryContext.getQuery(), null));

		final CompletableFuture<Stream<R>> operation = new CompletableFuture<>();

		// documents
		final List<Document> documents = new ArrayList<>();

		// execute query
		fi.forEach(value -> documents.add(new Document(Collections.singletonMap(fieldName, value))),
				(result, error) -> {
					if (error != null) {
						operation.completeExceptionally(error);
					} else {
						operation.complete(documents.stream()
								// apply converter
								.map(document -> documentConverter.convert(queryContext.getResolutionContext(),
										document)));
					}
				});
		return operation;
	}

	/**
	 * Perform a <em>aggregate</em> operation.
	 * @param <R> Query result type
	 * @param queryContext Operation context
	 * @return The operation result
	 */
	private static <R> CompletionStage<Stream<R>> aggregate(QueryOperationContext<R> queryContext) {

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(queryContext.getQuery(),
				queryContext.getResultType());

		// aggregation pipeline
		List<Bson> pipeline = new LinkedList<>();

		// definition
		final BsonQueryDefinition definition = queryContext.getQuery().getDefinition();

		// ------ match
		definition.getFilter().ifPresent(f -> pipeline.add(Aggregates.match(f)));

		// ------ group
		definition.getGroup().ifPresent(g -> {

			final List<BsonField> fieldAccumulators = new LinkedList<>();

			// check projection
			queryContext.getQuery().getProjection().ifPresent(p -> {
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
			queryContext.getQuery().getProjection().filter(p -> !p.isEmpty())
					.map(p -> Projections.fields(p.getFieldProjections())).ifPresent(prj -> {
						pipeline.add(Aggregates.project(prj));
					});
		}
		// trace
		queryContext.getOperationContext().trace("Aggregation pipeline",
				() -> MongoOperations.traceAggregationPipeline(queryContext.getOperationContext(), pipeline));

		// iterable
		final AggregateIterable<Document> ai = queryContext.getCollection().aggregate(pipeline);

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

		// documents
		final List<Document> documents = new ArrayList<>();

		final CompletableFuture<Stream<R>> operation = new CompletableFuture<>();
		// execute query
		ai.forEach(document -> documents.add(document), (result, error) -> {
			if (error != null) {
				operation.completeExceptionally(error);
			} else {
				operation.complete(documents.stream()
						// apply converter
						.map(document -> documentConverter.convert(queryContext.getResolutionContext(), document)));
			}
		});
		return operation;
	}

}
