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

import com.holonplatform.async.datastore.operation.AsyncInsert;
import com.holonplatform.async.internal.datastore.operation.AbstractAsyncInsert;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.async.internal.MongoOperationConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.DocumentOperationContext;
import com.holonplatform.datastore.mongo.async.internal.support.PropertyBoxOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

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

	private final MongoOperationContext<MongoDatabase> operationContext;

	public AsyncMongoInsert(MongoOperationContext<MongoDatabase> operationContext) {
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
			// prepare
			final CompletableFuture<DocumentOperationContext> operation = new CompletableFuture<>();
			// insert
			context.getCollection().insertOne(context.requireDocument(),
					MongoOperations.getInsertOneOptions(context.getConfiguration()), (result, error) -> {
						if (error != null) {
							operation.completeExceptionally(error);
						} else {
							operation.complete(context);
						}
					});
			// return the future
			return operation;
		}).thenApply(context -> {
			// trace
			context.getOperationContext().trace("Inserted document", DocumentSerializer.getDefault()
					.toJson(context.getCollection().getCodecRegistry(), context.requireDocument()));
			// build operation result
			final OperationResult.Builder builder = OperationResult.builder().type(OperationType.INSERT)
					.affectedCount(1);
			// check inserted keys
			MongoOperations.checkInsertedKeys(builder, context.getDocumentContext(), context.getConfiguration(),
					context.requireDocument(), context.getConfiguration().getValue());
			return builder.build();
		});
	}

}
