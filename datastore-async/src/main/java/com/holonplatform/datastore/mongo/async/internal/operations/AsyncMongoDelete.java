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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.holonplatform.async.datastore.operation.AsyncDelete;
import com.holonplatform.async.internal.datastore.operation.AbstractAsyncDelete;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.property.Property;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.async.internal.MongoOperationConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.DocumentOperationContext;
import com.holonplatform.datastore.mongo.async.internal.support.PropertyBoxOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;

/**
 * MongoDB {@link AsyncDelete}.
 *
 * @since 5.2.0
 */
public class AsyncMongoDelete extends AbstractAsyncDelete {

	private static final long serialVersionUID = 6962624446111899529L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncDelete> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncDelete>() {

		@Override
		public Class<? extends AsyncDelete> getCommodityType() {
			return AsyncDelete.class;
		}

		@Override
		public AsyncDelete createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new AsyncMongoDelete(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public AsyncMongoDelete(MongoOperationContext<MongoDatabase> operationContext) {
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
			// build context
			return PropertyBoxOperationContext.create(getConfiguration(), operationContext,
					MongoDocumentContext.create(operationContext, getConfiguration().getValue()));
		}).thenApply(context -> {
			// resolve collection name
			final String collectionName = context.getDocumentContext()
					.resolveOrFail(context.getConfiguration().getTarget(), CollectionName.class).getName();
			// get and configure collection
			MongoCollection<Document> collection = context.getOperationContext().withDatabase(database -> {
				return MongoOperationConfigurator.configureWrite(database.getCollection(collectionName),
						context.getDocumentContext(), context.getConfiguration());
			});
			// build context
			return DocumentOperationContext.create(context, collection);
		}).thenCompose(context -> {
			// id property
			final Property<?> idProperty = context.getDocumentContext().getDocumentIdProperty().orElseThrow(
					() -> new DataAccessException("Cannot perform a DELETE operation: missing document id property"
							+ " for value [" + context.getConfiguration().getValue() + "]"));
			// document id
			final ObjectId id = context.getDocumentContext().getDocumentIdResolver()
					.encode(context.getConfiguration().getValue().getValue(idProperty));
			if (id == null) {
				throw new DataAccessException(
						"Cannot perform a DELETE operation: missing document id value for property [" + idProperty
								+ "]");
			}
			context.setDocumentId(id);
			// prepare
			final CompletableFuture<DocumentOperationContext> operation = new CompletableFuture<>();
			// delete
			context.getCollection().deleteOne(Filters.eq(id),
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
			// trace
			context.getOperationContext().trace("Deleted document",
					"Deleted document id: " + context.getDocumentId().orElse(null));

			// operation result
			return OperationResult.builder().type(OperationType.DELETE).affectedCount(context.getAffectedCount())
					.build();
		});
	}

}
