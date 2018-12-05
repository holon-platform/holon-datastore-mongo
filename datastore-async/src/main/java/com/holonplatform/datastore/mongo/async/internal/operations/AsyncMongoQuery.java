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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.async.datastore.internal.operation.AsyncQueryAdapterQuery;
import com.holonplatform.async.datastore.operation.AsyncQuery;
import com.holonplatform.async.datastore.operation.AsyncQueryAdapter;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.internal.query.QueryDefinition;
import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.core.query.QueryOperation;
import com.holonplatform.datastore.mongo.async.internal.CompletableFutureStreamSubscriber;
import com.holonplatform.datastore.mongo.async.internal.CompletableFutureSubscriber;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.async.internal.config.AsyncAggregateOperationConfigurator;
import com.holonplatform.datastore.mongo.core.async.internal.config.AsyncDistinctOperationConfigurator;
import com.holonplatform.datastore.mongo.core.async.internal.config.AsyncFindOperationConfigurator;
import com.holonplatform.datastore.mongo.core.async.internal.config.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.core.async.internal.support.QueryOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.mongodb.reactivestreams.client.AggregatePublisher;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.DistinctPublisher;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

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

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public AsyncMongoQuery(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
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
			final MongoResolutionContext<ClientSession> context = MongoResolutionContext.create(operationContext);
			context.addExpressionResolvers(queryOperation.getConfiguration().getExpressionResolvers());

			// resolve query
			final BsonQuery query = context.resolveOrFail(queryOperation, BsonQuery.class);

			// resolve collection name
			final String collectionName = query.getDefinition().getCollectionName();
			// get and configure collection
			final MongoCollection<Document> collection = operationContext.withDatabase(database -> {
				return AsyncMongoCollectionConfigurator.configureRead(database.getCollection(collectionName), context,
						queryOperation.getConfiguration());
			});

			// build context
			return (QueryOperationContext<R>) QueryOperationContext.create(context, collection, query,
					queryOperation.getProjection().getType());

		}).thenCompose(context -> {

			// query operation type
			final QueryOperationType queryOperationType = context.getResolutionContext().getQueryOperationType()
					.orElse(QueryOperationType.FIND);
			switch (queryOperationType) {
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
	private static <R> CompletableFuture<Stream<R>> count(QueryOperationContext<R> queryContext) {

		// check filter
		final Bson filter = queryContext.getQuery().getDefinition().getFilter().map(f -> f.getExpression())
				.orElse(null);

		// trace
		queryContext.trace("COUNT query", "Filter: \n" + DocumentSerializer.getDefault().toJson(filter));

		// check session
		return queryContext.getResolutionContext().getClientSession().map(session -> {
			if (filter != null) {
				return CompletableFutureSubscriber
						.fromPublisher(queryContext.getCollection().countDocuments(session, filter))
						.thenApply(result -> Stream.of((R) result));
			} else {
				return CompletableFutureSubscriber.fromPublisher(queryContext.getCollection().countDocuments(session))
						.thenApply(result -> Stream.of((R) result));
			}
		}).orElseGet(() -> {
			if (filter != null) {
				return CompletableFutureSubscriber.fromPublisher(queryContext.getCollection().countDocuments(filter))
						.thenApply(result -> Stream.of((R) result));
			} else {
				return CompletableFutureSubscriber.fromPublisher(queryContext.getCollection().countDocuments())
						.thenApply(result -> Stream.of((R) result));
			}
		});
	}

	/**
	 * Perform a <em>find</em> operation.
	 * @param <R> Query result type
	 * @param queryContext Operation context
	 * @return The operation result
	 */
	private static <R> CompletableFuture<Stream<R>> find(QueryOperationContext<R> queryContext) {

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(queryContext.getQuery(),
				queryContext.getResultType());

		final FindPublisher<Document> fi = queryContext.getResolutionContext().getClientSession()
				.map(cs -> queryContext.getCollection().find(cs)).orElse(queryContext.getCollection().find());

		// configure
		Optional<Bson> projection = MongoOperations.configure(queryContext.getQuery(),
				new AsyncFindOperationConfigurator(fi));

		// trace
		queryContext.trace("FIND query", () -> MongoOperations.traceQuery(queryContext.getResolutionContext(),
				queryContext.getQuery(), projection.orElse(null)));

		// execute and map
		return CompletableFutureStreamSubscriber.fromPublisher(fi).thenApply(documents -> documents
				.map(document -> documentConverter.convert(queryContext.getResolutionContext(), document)));

	}

	/**
	 * Perform a <em>distinct</em> operation.
	 * @param <R> Query result type
	 * @param queryContext Operation context
	 * @return The operation result
	 */
	private static <R> CompletableFuture<Stream<R>> distinct(QueryOperationContext<R> queryContext) {

		// check distinct field name
		if (!queryContext.getQuery().getDistinctFieldName().isPresent()) {
			return find(queryContext);
		}

		final String fieldName = queryContext.getQuery().getDistinctFieldName().get();

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(queryContext.getQuery(),
				queryContext.getResultType());

		@SuppressWarnings("unchecked")
		final DistinctPublisher<R> fi = queryContext.getResolutionContext().getClientSession().map(
				cs -> queryContext.getCollection().distinct(cs, fieldName, (Class<R>) queryContext.getResultType()))
				.orElse(queryContext.getCollection().distinct(fieldName, (Class<R>) queryContext.getResultType()));

		// configure
		MongoOperations.configure(queryContext.getQuery(), new AsyncDistinctOperationConfigurator(fi));

		// trace
		queryContext.trace("DISTINCT query on [" + fieldName + "]",
				() -> MongoOperations.traceQuery(queryContext.getResolutionContext(), queryContext.getQuery(), null));

		// execute and map
		return CompletableFutureStreamSubscriber.fromPublisher(fi)
				.thenApply(values -> values.map(value -> new Document(Collections.singletonMap(fieldName, value))))
				.thenApply(documents -> documents
						.map(document -> documentConverter.convert(queryContext.getResolutionContext(), document)));
	}

	/**
	 * Perform a <em>aggregate</em> operation.
	 * @param <R> Query result type
	 * @param queryContext Operation context
	 * @return The operation result
	 */
	private static <R> CompletableFuture<Stream<R>> aggregate(QueryOperationContext<R> queryContext) {

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(queryContext.getQuery(),
				queryContext.getResultType());

		// aggregation pipeline
		final List<Bson> pipeline = MongoOperations.buildAggregationPipeline(queryContext.getQuery());

		// trace
		queryContext.trace("Aggregation pipeline",
				() -> MongoOperations.traceAggregationPipeline(queryContext.getResolutionContext(), pipeline));

		// iterable
		final AggregatePublisher<Document> ai = queryContext.getResolutionContext().getClientSession()
				.map(cs -> queryContext.getCollection().aggregate(cs, pipeline))
				.orElse(queryContext.getCollection().aggregate(pipeline));

		// configure
		MongoOperations.configure(queryContext.getQuery(), new AsyncAggregateOperationConfigurator(ai));

		// execute and map
		return CompletableFutureStreamSubscriber.fromPublisher(ai).thenApply(documents -> documents
				.map(document -> documentConverter.convert(queryContext.getResolutionContext(), document)));
	}

}
