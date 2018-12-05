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

import java.util.concurrent.CompletionStage;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.holonplatform.async.datastore.internal.operation.AbstractAsyncRefresh;
import com.holonplatform.async.datastore.operation.AsyncRefresh;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.commons.PropertyBoxOperationConfiguration;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.async.internal.CompletableFutureSubscriber;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.async.internal.config.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.core.async.internal.support.AsyncPropertyBoxOperationResultContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

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

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public AsyncMongoRefresh(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.commons.ExecutableOperation#execute()
	 */
	@Override
	public CompletionStage<PropertyBox> execute() {

		// configuration
		final PropertyBoxOperationConfiguration configuration = getConfiguration();
		// validate
		configuration.validate();

		// build context
		final MongoDocumentContext<ClientSession> context = MongoDocumentContext.create(operationContext,
				configuration.getValue());
		context.addExpressionResolvers(configuration.getExpressionResolvers());

		// resolve collection name
		final String collectionName = context.resolveOrFail(configuration.getTarget(), CollectionName.class).getName();
		// get and configure collection
		final MongoCollection<Document> collection = operationContext.withDatabase(database -> {
			return AsyncMongoCollectionConfigurator.configureRead(database.getCollection(collectionName), context,
					configuration.getParameters());
		});

		// id property
		final Property<?> idProperty = context.getDocumentIdProperty().orElseThrow(
				() -> new DataAccessException("Cannot perform a REFRESH operation: missing document id property"
						+ " for value [" + configuration.getValue() + "]"));
		// document id
		final ObjectId id = context.getDocumentIdResolver().encode(configuration.getValue().getValue(idProperty));
		if (id == null) {
			throw new DataAccessException(
					"Cannot perform a REFRESH operation: missing document id value for property [" + idProperty + "]");
		}

		return context.getClientSession()
				.map(session -> CompletableFutureSubscriber
						.fromPublisher(collection.find(session, Filters.eq(id)).first()))
				.orElseGet(() -> CompletableFutureSubscriber.fromPublisher(collection.find(Filters.eq(id)).first()))
				.thenApply(result -> AsyncPropertyBoxOperationResultContext.create(context, collection, configuration,
						1L, OperationType.UPDATE, configuration.getValue(), result))
				.thenApply(ctx -> {
					// check document
					final Document document = ctx.getDocument()
							.orElseThrow(() -> new DataAccessException("No document found using id property ["
									+ ctx.getContext().getDocumentIdProperty().orElse(null) + "]"));
					// trace
					ctx.trace("Refreshed document", document);
					// build operation result
					return ctx.getContext().resolveOrFail(DocumentValue.create(document), PropertyBoxValue.class)
							.getValue();
				});
	}

}
