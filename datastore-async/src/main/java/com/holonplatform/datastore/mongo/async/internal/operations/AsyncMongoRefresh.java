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

import com.holonplatform.async.datastore.operation.AsyncRefresh;
import com.holonplatform.async.internal.datastore.operation.AbstractAsyncRefresh;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.async.internal.MongoOperationConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.DocumentOperationContext;
import com.holonplatform.datastore.mongo.async.internal.support.PropertyBoxOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;

/**
 * MongoDB {@link AsyncRefresh}.
 *
 * @since 5.2.0
 */
public class AsyncMongoRefresh extends AbstractAsyncRefresh {

	private static final long serialVersionUID = 5721530187597592446L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncRefresh> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncRefresh>() {

		@Override
		public Class<? extends AsyncRefresh> getCommodityType() {
			return AsyncRefresh.class;
		}

		@Override
		public AsyncRefresh createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new AsyncMongoRefresh(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public AsyncMongoRefresh(MongoOperationContext<MongoDatabase> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.commons.ExecutableOperation#execute()
	 */
	@Override
	public CompletionStage<PropertyBox> execute() {
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
				return MongoOperationConfigurator.configureRead(database.getCollection(collectionName),
						context.getDocumentContext(), context.getConfiguration().getParameters());
			});
			// build context
			return DocumentOperationContext.create(context, collection);
		}).thenCompose(context -> {
			// id property
			final Property<?> idProperty = context.getDocumentContext().getDocumentIdProperty().orElseThrow(
					() -> new DataAccessException("Cannot perform a REFRESH operation: missing document id property"
							+ " for value [" + context.getConfiguration().getValue() + "]"));
			// document id
			final ObjectId id = context.getDocumentContext().getDocumentIdResolver()
					.encode(context.getConfiguration().getValue().getValue(idProperty));
			if (id == null) {
				throw new DataAccessException(
						"Cannot perform a REFRESH operation: missing document id value for property [" + idProperty
								+ "]");
			}
			// prepare
			final CompletableFuture<DocumentOperationContext> operation = new CompletableFuture<>();
			// insert
			context.getCollection().find(Filters.eq(id)).first((result, error) -> {
				if (error != null) {
					operation.completeExceptionally(error);
				} else {
					operation.complete(DocumentOperationContext.create(context, context.getCollection(), result));
				}
			});
			// return the future
			return operation;
		}).thenApply(context -> {
			// check document
			final Document document = context.getDocument()
					.orElseThrow(() -> new DataAccessException("No document found using id property ["
							+ context.getDocumentContext().getDocumentIdProperty().orElse(null) + "]"));
			// trace
			context.getOperationContext().trace("Refreshed document", document);
			// build operation result
			return context.getDocumentContext().resolveOrFail(DocumentValue.create(document), PropertyBoxValue.class)
					.getValue();
		});
	}

}
