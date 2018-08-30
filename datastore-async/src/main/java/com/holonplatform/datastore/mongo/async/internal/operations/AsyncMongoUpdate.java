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

import com.holonplatform.async.datastore.operation.AsyncUpdate;
import com.holonplatform.async.internal.datastore.operation.AbstractAsyncUpdate;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.commons.PropertyBoxOperationConfiguration;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.property.Property;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.async.internal.configurator.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.AsyncPropertyBoxOperationResultContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;

/**
 * MongoDB {@link AsyncUpdate}.
 *
 * @since 5.2.0
 */
public class AsyncMongoUpdate extends AbstractAsyncUpdate {

	private static final long serialVersionUID = 731321494063664934L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncUpdate> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncUpdate>() {

		@Override
		public Class<? extends AsyncUpdate> getCommodityType() {
			return AsyncUpdate.class;
		}

		@Override
		public AsyncUpdate createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new AsyncMongoUpdate(context);
		}
	};

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public AsyncMongoUpdate(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
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
			final PropertyBoxOperationConfiguration configuration = getConfiguration();
			// validate
			configuration.validate();

			// build context (for update)
			final MongoDocumentContext<ClientSession> context = MongoDocumentContext.createForUpdate(operationContext,
					configuration.getValue());
			context.addExpressionResolvers(configuration.getExpressionResolvers());

			// resolve collection name
			final String collectionName = context.resolveOrFail(configuration.getTarget(), CollectionName.class)
					.getName();
			// get and configure collection
			final MongoCollection<Document> collection = operationContext.withDatabase(database -> {
				return AsyncMongoCollectionConfigurator.configureWrite(database.getCollection(collectionName), context,
						configuration);
			});

			// id property
			final Property<?> idProperty = context.getDocumentIdProperty().orElseThrow(
					() -> new DataAccessException("Cannot perform a UPDATE operation: missing document id property"
							+ " for value [" + configuration.getValue() + "]"));
			// document id
			final ObjectId id = context.getDocumentIdResolver().encode(configuration.getValue().getValue(idProperty));
			if (id == null) {
				throw new DataAccessException(
						"Cannot perform a UPDATE operation: missing document id value for property [" + idProperty
								+ "]");
			}

			// prepare
			final CompletableFuture<AsyncPropertyBoxOperationResultContext> operation = new CompletableFuture<>();

			// document to update
			final Document document = context
					.resolveOrFail(PropertyBoxValue.create(configuration.getValue()), DocumentValue.class).getValue();

			// insert
			if (context.getClientSession().isPresent()) {
				collection.updateOne(context.getClientSession().get(), Filters.eq(id), document,
						MongoOperations.getUpdateOptions(configuration, false), (result, error) -> {
							if (error != null) {
								operation.completeExceptionally(error);
							} else {
								operation.complete(AsyncPropertyBoxOperationResultContext.create(context, collection,
										configuration, MongoOperations.getAffectedCount(result), OperationType.UPDATE,
										configuration.getValue(), document));
							}
						});
			} else {
				collection.updateOne(Filters.eq(id), document, MongoOperations.getUpdateOptions(configuration, false),
						(result, error) -> {
							if (error != null) {
								operation.completeExceptionally(error);
							} else {
								operation.complete(AsyncPropertyBoxOperationResultContext.create(context, collection,
										configuration, MongoOperations.getAffectedCount(result), OperationType.UPDATE,
										configuration.getValue(), document));
							}
						});
			}

			// join the future
			return operation.join();
		}).thenApply(context -> {

			// trace
			context.trace("Updated document", context.requireDocument());
			// operation result
			return OperationResult.builder().type(OperationType.UPDATE).affectedCount(context.getAffectedCount())
					.build();
		});
	}

}
