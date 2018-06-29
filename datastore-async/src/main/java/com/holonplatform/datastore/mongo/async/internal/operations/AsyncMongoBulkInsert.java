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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.holonplatform.async.datastore.operation.AsyncBulkInsert;
import com.holonplatform.async.internal.datastore.operation.AbstractAsyncBulkInsert;
import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.commons.BulkInsertOperationConfiguration;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.async.internal.configurator.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.AsyncMultiPropertyBoxOperationResultContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.holonplatform.datastore.mongo.core.internal.support.ResolvedDocument;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;

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

			// configuration
			final BulkInsertOperationConfiguration configuration = getConfiguration();
			// validate
			configuration.validate();

			// property set
			final PropertySet<?> propertySet = configuration.getPropertySet()
					.orElseThrow(() -> new InvalidExpressionException("Missing bulk insert operation property set"));

			// resolution context
			final MongoDocumentContext context = MongoDocumentContext.create(operationContext, propertySet);
			context.addExpressionResolvers(configuration.getExpressionResolvers());

			// resolve collection name
			final String collectionName = context.resolveOrFail(configuration.getTarget(), CollectionName.class)
					.getName();
			// get and configure collection
			final MongoCollection<Document> collection = operationContext.withDatabase(database -> {
				return AsyncMongoCollectionConfigurator.configureWrite(database.getCollection(collectionName), context,
						configuration);
			});

			// encode documents
			final List<ResolvedDocument> documentValues = MongoOperations.resolveDocumentValues(context,
					configuration.getValues());

			// documents to insert
			final List<Document> documents = documentValues.stream().map(v -> v.getDocument())
					.collect(Collectors.toList());

			// prepare
			final CompletableFuture<AsyncMultiPropertyBoxOperationResultContext> operation = new CompletableFuture<>();

			// insert
			collection.insertMany(documents, MongoOperations.getInsertManyOptions(configuration), (result, error) -> {
				if (error != null) {
					operation.completeExceptionally(error);
				} else {
					operation.complete(AsyncMultiPropertyBoxOperationResultContext.create(context, collection,
							configuration, documents.size(), OperationType.INSERT, documentValues));
				}
			});
			// join the future
			return operation.join();
		}).thenApply(context -> {

			// trace
			context.trace("Inserted documents",
					context.getValues().stream().map(v -> v.getDocument()).collect(Collectors.toList()));

			final OperationResult result = OperationResult.builder().type(OperationType.INSERT)
					.affectedCount(context.getAffectedCount()).build();

			// check inserted keys
			List<ObjectId> insertedIds = MongoOperations.checkInsertedKeys(context.getContext(),
					context.getConfiguration(), context.getValues());

			// check if the identifier property has to be updated with the document ids values
			List<CompletableFuture<OperationResult>> operations = new ArrayList<>(insertedIds.size());
			if (!insertedIds.isEmpty()) {
				MongoOperations.getPropertyDocumentIdFieldName(context.getContext()).ifPresent(fieldName -> {
					for (ObjectId insertedId : insertedIds) {
						MongoOperations.getIdUpdateDocument(context.getContext(), insertedId, fieldName)
								.ifPresent(toUpdate -> {
									final CompletableFuture<OperationResult> updateOperation = new CompletableFuture<>();
									context.getCollection().updateOne(Filters.eq(insertedId),
											toUpdate.getUpdateDocument(), (ur, error) -> {
												if (error != null) {
													updateOperation.completeExceptionally(error);
												} else {
													context.trace("Updated identifier property value",
															toUpdate.getUpdateDocument());
													updateOperation.complete(result);
												}
											});
								});
					}
				});
			}

			if (!operations.isEmpty()) {
				CompletableFuture.allOf(operations.toArray(new CompletableFuture[operations.size()])).join();
			}

			// result
			return result;
		});
	}

}
