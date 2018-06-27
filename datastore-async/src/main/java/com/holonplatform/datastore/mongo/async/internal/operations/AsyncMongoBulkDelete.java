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

import com.holonplatform.async.datastore.operation.AsyncBulkDelete;
import com.holonplatform.async.internal.datastore.operation.AbstractAsyncBulkDelete;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.async.internal.configurator.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.BulkOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

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

	private final MongoOperationContext<MongoDatabase> operationContext;

	public AsyncMongoBulkDelete(MongoOperationContext<MongoDatabase> operationContext) {
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
			// validate
			getConfiguration().validate();
			// context
			final MongoResolutionContext context = MongoResolutionContext.create(operationContext);
			context.addExpressionResolvers(getConfiguration().getExpressionResolvers());
			// filter
			Optional<Bson> filter = getConfiguration().getFilter()
					.map(f -> context.resolveOrFail(f, BsonExpression.class).getValue());
			// resolve collection name
			final String collectionName = context.resolveOrFail(getConfiguration().getTarget(), CollectionName.class)
					.getName();
			// get and configure collection
			MongoCollection<Document> collection = operationContext.withDatabase(database -> {
				return AsyncMongoCollectionConfigurator.configureWrite(database.getCollection(collectionName), context,
						getConfiguration());
			});
			// build context
			return BulkOperationContext.create(operationContext, getConfiguration(), context, collection,
					filter.orElse(null));
		}).thenCompose(context -> {
			// trace
			context.getOperationContext().trace("Delete documents - filter",
					context.getFilter().map(f -> context.getOperationContext().toJson(f)).orElse("[NONE]"));
			// prepare
			final CompletableFuture<BulkOperationContext> operation = new CompletableFuture<>();
			// delete
			context.getCollection().deleteMany(context.getFilter().orElse(null),
					MongoOperations.getDeleteOptions(context.getConfiguration()), (result, error) -> {
						if (error != null) {
							operation.completeExceptionally(error);
						} else {
							context.setAffectedCount(result.getDeletedCount());
							operation.complete(context);
						}
					});
			// return the future
			return operation;
		}).thenApply(context -> {
			return OperationResult.builder().type(OperationType.DELETE).affectedCount(context.getAffectedCount())
					.build();
		});
	}

}
