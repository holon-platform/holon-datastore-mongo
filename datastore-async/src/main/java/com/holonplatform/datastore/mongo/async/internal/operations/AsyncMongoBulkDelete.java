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

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.async.datastore.internal.operation.AbstractAsyncBulkDelete;
import com.holonplatform.async.datastore.operation.AsyncBulkDelete;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.commons.BulkDeleteOperationConfiguration;
import com.holonplatform.datastore.mongo.async.internal.CompletableFutureSubscriber;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.async.internal.config.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.core.async.internal.support.AsyncOperationResultContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

/**
 * Mongo {@link AsyncBulkDelete} implementation.
 * 
 * @since 5.2.0
 */
public class AsyncMongoBulkDelete extends AbstractAsyncBulkDelete {

	private static final long serialVersionUID = 6063389679462034895L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncBulkDelete> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncBulkDelete>() {

		@Override
		public Class<? extends AsyncBulkDelete> getCommodityType() {
			return AsyncBulkDelete.class;
		}

		@Override
		public AsyncBulkDelete createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new AsyncMongoBulkDelete(context);
		}
	};

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public AsyncMongoBulkDelete(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.commons.ExecutableOperation#execute()
	 */
	@Override
	public CompletionStage<OperationResult> execute() {

		// configuration
		final BulkDeleteOperationConfiguration configuration = getConfiguration();
		// validate
		configuration.validate();

		// context
		final MongoResolutionContext<ClientSession> context = MongoResolutionContext.create(operationContext);
		context.addExpressionResolvers(configuration.getExpressionResolvers());

		// filter
		final Optional<Bson> filter = configuration.getFilter()
				.map(f -> context.resolveOrFail(f, BsonExpression.class).getValue());

		// resolve collection name
		final String collectionName = context.resolveOrFail(configuration.getTarget(), CollectionName.class).getName();
		// get and configure collection
		final MongoCollection<Document> collection = operationContext.withDatabase(database -> {
			return AsyncMongoCollectionConfigurator.configureWrite(database.getCollection(collectionName), context,
					configuration);
		});

		return context.getClientSession()
				.map(session -> CompletableFutureSubscriber.fromPublisher(collection.deleteMany(session,
						filter.orElse(null), MongoOperations.getDeleteOptions(configuration))))
				.orElseGet(() -> CompletableFutureSubscriber.fromPublisher(
						collection.deleteMany(filter.orElse(null), MongoOperations.getDeleteOptions(configuration))))
				.thenApply(result -> AsyncOperationResultContext.create(context, collection, configuration,
						result.getDeletedCount(), OperationType.DELETE))
				.thenApply(ctx -> {
					// trace
					context.trace("Deleted documents - filter",
							ctx.getFilter().map(f -> operationContext.toJson(f)).orElse("[NONE]"));
					// result
					return OperationResult.builder().type(OperationType.DELETE).affectedCount(ctx.getAffectedCount())
							.build();
				});
	}

}
