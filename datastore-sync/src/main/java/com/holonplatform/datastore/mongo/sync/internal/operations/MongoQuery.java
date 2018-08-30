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
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.holonplatform.datastore.mongo.core.internal.driver.MongoDriverInfo;
import com.holonplatform.datastore.mongo.core.internal.driver.MongoVersion;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.internal.configurator.SyncAggregateOperationConfigurator;
import com.holonplatform.datastore.mongo.sync.internal.configurator.SyncDistinctOperationConfigurator;
import com.holonplatform.datastore.mongo.sync.internal.configurator.SyncFindOperationConfigurator;
import com.holonplatform.datastore.mongo.sync.internal.configurator.SyncMongoCollectionConfigurator;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.ClientSession;
import com.mongodb.client.DistinctIterable;
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

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public MongoQuery(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
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
		final MongoResolutionContext<ClientSession> context = MongoResolutionContext.create(operationContext);
		context.addExpressionResolvers(queryOperation.getConfiguration().getExpressionResolvers());

		// resolve query
		final BsonQuery query = context.resolveOrFail(queryOperation, BsonQuery.class);

		// collection name
		final String collectionName = query.getDefinition().getCollectionName();

		return operationContext.withDatabase(database -> {

			// get and configure collection
			final MongoCollection<Document> collection = SyncMongoCollectionConfigurator
					.configureRead(database.getCollection(collectionName), context, queryOperation.getConfiguration());

			// query operation type
			final QueryOperationType queryOperationType = context.getQueryOperationType()
					.orElse(QueryOperationType.FIND);
			switch (queryOperationType) {
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
	@SuppressWarnings({ "unchecked", "deprecation", "resource" })
	private static <R> Stream<R> count(MongoContext<ClientSession> context, MongoCollection<Document> collection,
			BsonQueryDefinition definition) {

		// trace
		context.trace("COUNT query", "Filter: \n" + DocumentSerializer.getDefault()
				.toJson(definition.getFilter().map(f -> f.getExpression()).orElse(null)));

		// check driver version
		final MongoVersion version = MongoDriverInfo.getMongoVersion();
		final boolean backwardMode = version.wasDriverVersionDetected() && version.getDriverMajorVersion() <= 3
				&& version.getDriverMinorVersion() < 8;

		// session
		final ClientSession cs = context.getClientSession().orElse(null);

		// count
		final Long count;
		if (backwardMode) {
			if (cs != null) {
				count = definition.getFilter().map(f -> f.getExpression()).map(e -> collection.count(cs, e))
						.orElse(collection.count(cs));
			} else {
				count = definition.getFilter().map(f -> f.getExpression()).map(e -> collection.count(e))
						.orElse(collection.count());
			}
		} else {
			if (cs != null) {
				count = definition.getFilter().map(f -> f.getExpression()).map(e -> collection.countDocuments(cs, e))
						.orElse(collection.countDocuments(cs));
			} else {
				count = definition.getFilter().map(f -> f.getExpression()).map(e -> collection.countDocuments(e))
						.orElse(collection.countDocuments());
			}
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
	private static <R> Stream<R> find(MongoResolutionContext<ClientSession> context,
			MongoCollection<Document> collection, Class<? extends R> resultType, BsonQuery query) {

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(query, resultType);

		// iterable
		final FindIterable<Document> fi = context.getClientSession().map(cs -> collection.find(cs))
				.orElse(collection.find());

		// configure
		Optional<Bson> projection = MongoOperations.configure(query, new SyncFindOperationConfigurator(fi));

		// trace
		context.trace("FIND query", () -> MongoOperations.traceQuery(context, query, projection.orElse(null)));

		// stream with converter mapper
		return StreamSupport.stream(fi.spliterator(), false)
				.map(document -> documentConverter.convert(context, document));
	}

	/**
	 * Perform a <em>distinct</em> operation on given collection using given {@link BsonQuery}.
	 * @param <R> Operation result type
	 * @param context Resolution context
	 * @param collection The collection to use
	 * @param resultType Expected query result type
	 * @param query Query definition
	 * @return The operation result
	 */
	private static <R> Stream<R> distinct(MongoResolutionContext<ClientSession> context,
			MongoCollection<Document> collection, Class<? extends R> resultType, BsonQuery query) {

		// check distinct field name
		if (!query.getDistinctFieldName().isPresent()) {
			return find(context, collection, resultType, query);
		}

		final String fieldName = query.getDistinctFieldName().get();

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(query, resultType);

		// iterable
		@SuppressWarnings("unchecked")
		final DistinctIterable<R> fi = context.getClientSession()
				.map(cs -> collection.distinct(cs, fieldName, (Class<R>) resultType))
				.orElse(collection.distinct(fieldName, (Class<R>) resultType));

		// configure
		MongoOperations.configure(query, new SyncDistinctOperationConfigurator(fi));

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
	private static <R> Stream<R> aggregate(MongoResolutionContext<ClientSession> context,
			MongoCollection<Document> collection, Class<? extends R> resultType, BsonQuery query) {

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(query, resultType);

		// aggregation pipeline
		final List<Bson> pipeline = MongoOperations.buildAggregationPipeline(query);

		// trace
		context.trace("Aggregation pipeline", () -> MongoOperations.traceAggregationPipeline(context, pipeline));

		// iterable
		final AggregateIterable<Document> ai = context.getClientSession().map(cs -> collection.aggregate(cs, pipeline))
				.orElse(collection.aggregate(pipeline));

		// configure
		MongoOperations.configure(query, new SyncAggregateOperationConfigurator(ai));

		// stream with converter mapper
		return StreamSupport.stream(ai.spliterator(), false)
				.map(document -> documentConverter.convert(context, document));
	}

}
