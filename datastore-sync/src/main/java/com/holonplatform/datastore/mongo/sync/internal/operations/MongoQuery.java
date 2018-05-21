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

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.query.QueryAdapterQuery;
import com.holonplatform.core.internal.query.QueryDefinition;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.query.Query;
import com.holonplatform.core.query.QueryAdapter;
import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.core.query.QueryOperation;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.internal.MongoOperationConfigurator;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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
			final MongoCollection<Document> collection = MongoOperationConfigurator.configureRead(
					database.getCollection(collectionName), operationContext, queryOperation.getConfiguration());

			// query operation type
			switch (query.getOperationType()) {
			case AGGREGATE:
				return aggregate(operationContext, context, collection, queryOperation.getProjection().getType(),
						query);
			case COUNT:
				return count(operationContext, collection, query.getDefinition());
			case FIND:
			default:
				return find(operationContext, context, collection, queryOperation.getProjection().getType(), query);
			}
		});
	}

	/**
	 * Perform a <em>count</em> operation on given collection using given {@link BsonQueryDefinition}.
	 * @param <R> Operation result type
	 * @param operationContext Operation context
	 * @param collection The collection to use
	 * @param definition Query definition
	 * @return The operation result
	 */
	@SuppressWarnings("unchecked")
	private static <R> Stream<R> count(MongoOperationContext<MongoDatabase> operationContext,
			MongoCollection<Document> collection, BsonQueryDefinition definition) {

		// trace
		operationContext.trace("COUNT query",
				"Filter: \n" + DocumentSerializer.getDefault().toJson(definition.getFilter().orElse(null)));

		// count
		Long count = definition.getFilter().map(f -> collection.count(f)).orElse(collection.count());

		return Stream.of((R) count);
	}

	/**
	 * Perform a <em>find</em> operation on given collection using given {@link BsonQuery}.
	 * @param <R> Operation result type
	 * @param operationContext Operation context
	 * @param context Resolution context
	 * @param collection The collection to use
	 * @param resultType Expected query result type
	 * @param query Query definition
	 * @return The operation result
	 */
	private static <R> Stream<R> find(MongoOperationContext<MongoDatabase> operationContext,
			MongoResolutionContext context, MongoCollection<Document> collection, Class<? extends R> resultType,
			BsonQuery query) {

		// check converter
		final DocumentConverter<?> converter = query.getConverter().orElse(DocumentConverter.identity());
		if (!TypeUtils.isAssignable(converter.getConversionType(), resultType)) {
			throw new DataAccessException("The query results converter type [" + converter.getConversionType()
					+ "] is not compatible with the query projection type [" + resultType + "]");
		}

		@SuppressWarnings("unchecked")
		final DocumentConverter<R> documentConverter = (DocumentConverter<R>) converter;

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
		query.getProjection().ifPresent(p -> fi.projection(p));

		// trace
		operationContext.trace("FIND query", () -> traceQuery(query));

		// stream with converter mapper
		return StreamSupport.stream(fi.spliterator(), false)
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
	private static <R> Stream<R> aggregate(MongoOperationContext<MongoDatabase> operationContext,
			MongoResolutionContext context, MongoCollection<Document> collection, Class<? extends R> resultType,
			BsonQuery query) {
		// TODO
		return null;
	}

	/**
	 * Build the trace information for given query.
	 * @param query Query to trace
	 * @return Query trace information
	 */
	private static String traceQuery(BsonQuery query) {
		final StringBuilder sb = new StringBuilder();

		sb.append("Collection name: ");
		sb.append(query.getDefinition().getCollectionName());

		query.getDefinition().getFilter().ifPresent(f -> {
			sb.append("\nFilter: \n");
			sb.append(DocumentSerializer.getDefault().toJson(f));
		});
		query.getDefinition().getSort().ifPresent(s -> {
			sb.append("\nSort: \n");
			sb.append(DocumentSerializer.getDefault().toJson(s));
		});
		query.getProjection().ifPresent(p -> {
			sb.append("\nProjection: \n");
			sb.append(DocumentSerializer.getDefault().toJson(p));
		});

		return sb.toString();
	}

}
