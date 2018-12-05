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
package com.holonplatform.datastore.mongo.reactor.internal.operation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.internal.query.QueryDefinition;
import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.core.query.QueryOperation;
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
import com.holonplatform.reactor.datastore.internal.operation.ReactiveQueryAdapterQuery;
import com.holonplatform.reactor.datastore.operation.ReactiveQuery;
import com.holonplatform.reactor.datastore.operation.ReactiveQueryAdapter;
import com.mongodb.reactivestreams.client.AggregatePublisher;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.DistinctPublisher;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * MongoDB {@link ReactiveQueryAdapter}.
 *
 * @since 5.2.0
 */
public class ReactiveMongoQuery implements ReactiveQueryAdapter<QueryConfiguration> {

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, ReactiveQuery> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, ReactiveQuery>() {

		@Override
		public Class<? extends ReactiveQuery> getCommodityType() {
			return ReactiveQuery.class;
		}

		@Override
		public ReactiveQuery createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new ReactiveQueryAdapterQuery<>(new ReactiveMongoQuery(context), QueryDefinition.create());
		}
	};

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public ReactiveMongoQuery(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.datastore.operation.ReactiveQueryAdapter#stream(com.holonplatform.core.query.
	 * QueryOperation)
	 */
	@Override
	public <R> Flux<R> stream(QueryOperation<QueryConfiguration, R> queryOperation) {
		return Mono.fromCallable(() -> {
			// validate
			queryOperation.validate();
			// context
			final MongoResolutionContext<ClientSession> context = MongoResolutionContext.create(operationContext);
			context.addExpressionResolvers(queryOperation.getConfiguration().getExpressionResolvers());
			// resolve query
			final BsonQuery query = context.resolveOrFail(queryOperation, BsonQuery.class);
			// get and configure collection
			final MongoCollection<Document> collection = operationContext.withDatabase(database -> {
				return AsyncMongoCollectionConfigurator.configureRead(
						database.getCollection(query.getDefinition().getCollectionName()), context,
						queryOperation.getConfiguration());
			});
			// build context
			return QueryOperationContext.create(context, collection, query, queryOperation.getProjection().getType());
		}).flatMapMany(context -> {
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
	@SuppressWarnings({ "resource", "unchecked" })
	private static <R> Flux<R> count(QueryOperationContext<R> queryContext) {
		return Mono.fromSupplier(() -> {
			// check filter
			final Bson filter = queryContext.getQuery().getDefinition().getFilter().map(f -> f.getExpression())
					.orElse(null);

			// trace
			queryContext.trace("COUNT query", "Filter: \n" + DocumentSerializer.getDefault().toJson(filter));

			return filter;
		}).flatMap(filter -> {
			// session
			final ClientSession cs = queryContext.getResolutionContext().getClientSession().orElse(null);
			// check filter
			if (filter != null) {
				if (cs != null) {
					return Mono.from(queryContext.getCollection().countDocuments(cs, filter));
				} else {
					return Mono.from(queryContext.getCollection().countDocuments(filter));
				}
			} else {
				if (cs != null) {
					return Mono.from(queryContext.getCollection().countDocuments(cs));
				} else {
					return Mono.from(queryContext.getCollection().countDocuments());
				}
			}
		}).map(r -> (R) r).flux();

	}

	/**
	 * Perform a <em>find</em> operation.
	 * @param <R> Query result type
	 * @param queryContext Operation context
	 * @return The operation result
	 */
	private static <R> Flux<R> find(QueryOperationContext<R> queryContext) {
		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(queryContext.getQuery(),
				queryContext.getResultType());

		// find
		final FindPublisher<Document> fi = queryContext.getResolutionContext().getClientSession()
				.map(cs -> queryContext.getCollection().find(cs)).orElse(queryContext.getCollection().find());

		// configure
		Optional<Bson> projection = MongoOperations.configure(queryContext.getQuery(),
				new AsyncFindOperationConfigurator(fi));

		// trace
		queryContext.trace("FIND query", () -> MongoOperations.traceQuery(queryContext.getResolutionContext(),
				queryContext.getQuery(), projection.orElse(null)));

		// map to Flux
		return mapResults(Flux.from(fi),
				document -> documentConverter.convert(queryContext.getResolutionContext(), document));
	}

	/**
	 * Perform a <em>distinct</em> operation.
	 * @param <R> Query result type
	 * @param queryContext Operation context
	 * @return The operation result
	 */
	private static <R> Flux<R> distinct(QueryOperationContext<R> queryContext) {

		// check distinct field name
		if (!queryContext.getQuery().getDistinctFieldName().isPresent()) {
			return find(queryContext);
		}

		// distinct field name
		final String fieldName = queryContext.getQuery().getDistinctFieldName().get();

		// converter
		final DocumentConverter<R> documentConverter = MongoOperations.getAndCheckConverter(queryContext.getQuery(),
				queryContext.getResultType());

		// distinct
		@SuppressWarnings("unchecked")
		final DistinctPublisher<R> fi = queryContext.getResolutionContext().getClientSession().map(
				cs -> queryContext.getCollection().distinct(cs, fieldName, (Class<R>) queryContext.getResultType()))
				.orElse(queryContext.getCollection().distinct(fieldName, (Class<R>) queryContext.getResultType()));

		// configure
		MongoOperations.configure(queryContext.getQuery(), new AsyncDistinctOperationConfigurator(fi));

		// trace
		queryContext.trace("DISTINCT query on [" + fieldName + "]",
				() -> MongoOperations.traceQuery(queryContext.getResolutionContext(), queryContext.getQuery(), null));

		// map to Flux
		return mapResults(Flux.from(fi).map(value -> new Document(Collections.singletonMap(fieldName, value))),
				document -> documentConverter.convert(queryContext.getResolutionContext(), document));
	}

	/**
	 * Perform a <em>aggregate</em> operation.
	 * @param <R> Query result type
	 * @param queryContext Operation context
	 * @return The operation result
	 */
	private static <R> Flux<R> aggregate(QueryOperationContext<R> queryContext) {

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

		// map to Flux
		return mapResults(Flux.from(ai),
				document -> documentConverter.convert(queryContext.getResolutionContext(), document));
	}

	/**
	 * Map the results, discarding <code>null</code> values.
	 * @param <R> Result type
	 * @param results The results to map
	 * @param mapper The mapper function
	 * @return The mapped results
	 */
	private static <R> Flux<R> mapResults(Flux<Document> results, Function<Document, R> mapper) {
		return results.flatMap(document -> {
			R result = mapper.apply(document);
			if (result != null) {
				return Flux.just(result);
			}
			return Flux.<R>empty();
		});
	}

}
