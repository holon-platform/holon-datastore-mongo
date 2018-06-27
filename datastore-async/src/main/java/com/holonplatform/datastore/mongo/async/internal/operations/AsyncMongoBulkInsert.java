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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import org.bson.Document;

import com.holonplatform.async.datastore.operation.AsyncBulkInsert;
import com.holonplatform.async.internal.datastore.operation.AbstractAsyncBulkInsert;
import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.async.internal.configurator.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.BulkInsertOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.holonplatform.datastore.mongo.core.internal.support.ResolvedDocument;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

/**
 * Mongo {@link AsyncBulkInsert} implementation.
 * 
 * @since 5.2.0
 */
public class AsyncMongoBulkInsert extends AbstractAsyncBulkInsert {

	private static final long serialVersionUID = 7902737798061112917L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncBulkInsert> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncBulkInsert>() {

		@Override
		public Class<? extends AsyncBulkInsert> getCommodityType() {
			return AsyncBulkInsert.class;
		}

		@Override
		public AsyncBulkInsert createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new AsyncMongoBulkInsert(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public AsyncMongoBulkInsert(MongoOperationContext<MongoDatabase> operationContext) {
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
			// property set
			final PropertySet<?> propertySet = getConfiguration().getPropertySet()
					.orElseThrow(() -> new InvalidExpressionException("Missing bulk insert operation property set"));
			// resolution context
			final MongoDocumentContext context = MongoDocumentContext.create(operationContext, propertySet);
			context.addExpressionResolvers(getConfiguration().getExpressionResolvers());
			// resolve collection name
			final String collectionName = context.resolveOrFail(getConfiguration().getTarget(), CollectionName.class)
					.getName();
			// get and configure collection
			MongoCollection<Document> collection = operationContext.withDatabase(database -> {
				return AsyncMongoCollectionConfigurator.configureWrite(database.getCollection(collectionName), context,
						getConfiguration());
			});
			// build context
			return BulkInsertOperationContext.create(operationContext, getConfiguration(), context, collection,
					propertySet);
		}).thenCompose(context -> {
			// encode documents
			final List<ResolvedDocument> documentValues = MongoOperations
					.resolveDocumentValues(context.getDocumentContext(), getConfiguration().getValues());
			// insert
			final List<Document> documents = documentValues.stream().map(v -> v.getDocument())
					.collect(Collectors.toList());
			// prepare
			final CompletableFuture<BulkInsertOperationContext> operation = new CompletableFuture<>();
			// insert
			context.getCollection().insertMany(documents,
					MongoOperations.getInsertManyOptions(context.getConfiguration()), (result, error) -> {
						if (error != null) {
							operation.completeExceptionally(error);
						} else {
							context.setAffectedCount(documents.size());
							context.setDocuments(documentValues);
							operation.complete(context);
						}
					});
			// return the future
			return operation;
		}).thenApply(context -> {

			// trace
			context.getOperationContext().trace("Inserted documents",
					context.getDocuments().stream().map(v -> v.getDocument()).collect(Collectors.toList()));

			final OperationResult.Builder builder = OperationResult.builder().type(OperationType.INSERT)
					.affectedCount(context.getDocuments().size());

			// check inserted keys
			MongoOperations.checkInsertedKeys(context.getDocumentContext(), context.getConfiguration(),
					context.getDocuments());
			return builder.build();
		});
	}

}
