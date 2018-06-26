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
import com.holonplatform.datastore.mongo.async.internal.configurator.AsyncAggregateOperationConfigurator;
import com.holonplatform.datastore.mongo.async.internal.configurator.AsyncDistinctOperationConfigurator;
import com.holonplatform.datastore.mongo.async.internal.configurator.AsyncFindOperationConfigurator;
import com.holonplatform.datastore.mongo.async.internal.configurator.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.QueryOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoQueryContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.mongodb.async.client.AggregateIterable;
import com.mongodb.async.client.DistinctIterable;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

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
			final MongoQueryContext context = MongoQueryContext.create(operationContext);
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
			return (QueryOperationContext<R>) QueryOperationContext.create(operationContext, context, collection, query,
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
	private static <R> CompletionStage<Stream<R>> count(QueryOperationContext<R> queryContext) {

		// check filter
		final Bson filter = queryContext.getQuery().getDefinition().getFilter().map(f -> f.getExpression()).orElse(null);

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

		// configure
		Optional<Bson> projection = MongoOperations.configure(queryContext.getQuery(),
				new AsyncFindOperationConfigurator(fi));

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

		// configure
		MongoOperations.configure(queryContext.getQuery(), new AsyncDistinctOperationConfigurator(fi));

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
		final List<Bson> pipeline = MongoOperations.buildAggregationPipeline(queryContext.getQuery());

		// trace
		queryContext.getOperationContext().trace("Aggregation pipeline",
				() -> MongoOperations.traceAggregationPipeline(queryContext.getOperationContext(), pipeline));

		// iterable
		final AggregateIterable<Document> ai = queryContext.getCollection().aggregate(pipeline);

		// configure
		MongoOperations.configure(queryContext.getQuery(), new AsyncAggregateOperationConfigurator(ai));

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
