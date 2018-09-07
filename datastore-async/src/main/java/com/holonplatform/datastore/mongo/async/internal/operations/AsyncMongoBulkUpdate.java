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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.async.datastore.internal.operation.AbstractAsyncBulkUpdate;
import com.holonplatform.async.datastore.operation.AsyncBulkUpdate;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.commons.BulkUpdateOperationConfiguration;
import com.holonplatform.datastore.mongo.async.internal.configurator.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.AsyncOperationResultContext;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

/**
 * Mongo {@link AsyncBulkUpdate} implementation.
 * 
 * @since 5.2.0
 */
public class AsyncMongoBulkUpdate extends AbstractAsyncBulkUpdate {

	private static final long serialVersionUID = -3672271825567632474L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncBulkUpdate> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncBulkUpdate>() {

		@Override
		public Class<? extends AsyncBulkUpdate> getCommodityType() {
			return AsyncBulkUpdate.class;
		}

		@Override
		public AsyncBulkUpdate createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new AsyncMongoBulkUpdate(context);
		}
	};

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public AsyncMongoBulkUpdate(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.commons.ExecutableOperation#execute()
	 */
	@Override
	public CompletionStage<OperationResult> execute() {
		return CompletableFuture.supplyAsync(() -> {

			// configuration
			final BulkUpdateOperationConfiguration configuration = getConfiguration();
			// validate
			configuration.validate();

			// context
			final MongoResolutionContext<ClientSession> context = MongoResolutionContext.create(operationContext);
			context.addExpressionResolvers(configuration.getExpressionResolvers());

			// filter
			final Optional<Bson> filter = configuration.getFilter()
					.map(f -> context.resolveOrFail(f, BsonExpression.class).getValue());

			// resolve collection name
			final String collectionName = context.resolveOrFail(configuration.getTarget(), CollectionName.class)
					.getName();
			// get and configure collection
			final MongoCollection<Document> collection = operationContext.withDatabase(database -> {
				return AsyncMongoCollectionConfigurator.configureWrite(database.getCollection(collectionName), context,
						configuration);
			});

			// update expression
			final Bson update = MongoOperations.getUpdateExpression(context, configuration);

			// trace
			context.trace("Update documents", MongoOperations.traceUpdate(context, filter, update));

			// prepare
			final CompletableFuture<AsyncOperationResultContext<?>> operation = new CompletableFuture<>();

			// update
			if (context.getClientSession().isPresent()) {
				collection.updateMany(context.getClientSession().get(), filter.orElse(null), update,
						MongoOperations.getUpdateOptions(configuration, false), (result, error) -> {
							if (error != null) {
								operation.completeExceptionally(error);
							} else {
								operation.complete(AsyncOperationResultContext.create(context, collection,
										configuration, result.getModifiedCount(), OperationType.UPDATE));
							}
						});
			} else {
				collection.updateMany(filter.orElse(null), update,
						MongoOperations.getUpdateOptions(configuration, false), (result, error) -> {
							if (error != null) {
								operation.completeExceptionally(error);
							} else {
								operation.complete(AsyncOperationResultContext.create(context, collection,
										configuration, result.getModifiedCount(), OperationType.UPDATE));
							}
						});
			}
			return operation.join();
		}).thenApply(context -> {

			// result
			return OperationResult.builder().type(OperationType.UPDATE).affectedCount(context.getAffectedCount())
					.build();

		});
	}

}
