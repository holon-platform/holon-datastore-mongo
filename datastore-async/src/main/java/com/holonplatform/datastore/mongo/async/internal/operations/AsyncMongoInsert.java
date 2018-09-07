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
import org.bson.types.ObjectId;

import com.holonplatform.async.datastore.internal.operation.AbstractAsyncInsert;
import com.holonplatform.async.datastore.operation.AsyncInsert;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.commons.PropertyBoxOperationConfiguration;
import com.holonplatform.datastore.mongo.async.internal.configurator.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.AsyncPropertyBoxOperationResultContext;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.holonplatform.datastore.mongo.core.internal.support.IdUpdateDocument;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;

/**
 * MongoDB {@link AsyncInsert}.
 *
 * @since 5.2.0
 */
public class AsyncMongoInsert extends AbstractAsyncInsert {

	private static final long serialVersionUID = 5721530187597592446L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncInsert> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncInsert>() {

		@Override
		public Class<? extends AsyncInsert> getCommodityType() {
			return AsyncInsert.class;
		}

		@Override
		public AsyncInsert createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new AsyncMongoInsert(context);
		}
	};

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public AsyncMongoInsert(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
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

			// build context
			final MongoDocumentContext<ClientSession> context = MongoDocumentContext.create(operationContext,
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

			// prepare
			final CompletableFuture<AsyncPropertyBoxOperationResultContext> operation = new CompletableFuture<>();

			// document to insert
			final Document document = context
					.resolveOrFail(PropertyBoxValue.create(configuration.getValue()), DocumentValue.class).getValue();

			// insert
			if (context.getClientSession().isPresent()) {
				collection.insertOne(context.getClientSession().get(), document,
						MongoOperations.getInsertOneOptions(configuration), (result, error) -> {
							if (error != null) {
								operation.completeExceptionally(error);
							} else {
								operation.complete(AsyncPropertyBoxOperationResultContext.create(context, collection,
										configuration, 1, OperationType.INSERT, configuration.getValue(), document));
							}
						});
			} else {
				collection.insertOne(document, MongoOperations.getInsertOneOptions(configuration), (result, error) -> {
					if (error != null) {
						operation.completeExceptionally(error);
					} else {
						operation.complete(AsyncPropertyBoxOperationResultContext.create(context, collection,
								configuration, 1, OperationType.INSERT, configuration.getValue(), document));
					}
				});
			}

			// join the future
			return operation.join();
		}).thenApply(context -> {

			// trace
			context.trace("Inserted document", context.requireDocument());
			// build operation result
			final OperationResult.Builder builder = OperationResult.builder().type(OperationType.INSERT)
					.affectedCount(1);

			final CompletableFuture<OperationResult> operation = new CompletableFuture<>();

			// check inserted keys
			Optional<ObjectId> insertedId = MongoOperations.checkInsertedKeys(builder, context.getContext(),
					context.getConfiguration(), context.requireDocument(), context.getValue());

			final OperationResult result = builder.build();

			// check if the identifier property has to be updated with the document id value
			final IdUpdateDocument toUpdate = (!insertedId.isPresent()) ? null
					: MongoOperations.getIdUpdateDocument(context.getContext(), insertedId.get()).orElse(null);
			if (insertedId.isPresent() && toUpdate != null) {
				context.getCollection().updateOne(Filters.eq(insertedId.get()), toUpdate.getUpdateDocument(),
						(ur, error) -> {
							if (error != null) {
								operation.completeExceptionally(error);
							} else {
								// TODO ensure unique index
								context.trace("Updated identifier property value", toUpdate.getUpdateDocument());
								operation.complete(result);
							}
						});
			} else {
				operation.complete(result);
			}

			return operation.join();
		}).thenApply(result -> result);
	}

}
