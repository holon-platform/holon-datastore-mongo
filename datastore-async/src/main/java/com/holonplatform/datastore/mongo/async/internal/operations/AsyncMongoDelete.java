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

import com.holonplatform.async.datastore.internal.operation.AbstractAsyncDelete;
import com.holonplatform.async.datastore.operation.AsyncDelete;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.commons.PropertyBoxOperationConfiguration;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.property.Property;
import com.holonplatform.datastore.mongo.async.internal.CompletableFutureSubscriber;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.async.internal.config.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.core.async.internal.support.AsyncPropertyBoxOperationResultContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

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

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public AsyncMongoDelete(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.commons.ExecutableOperation#execute()
	 */
	@Override
	public CompletionStage<OperationResult> execute() {

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
			return AsyncMongoCollectionConfigurator.configureWrite(database.getCollection(collectionName), context,
					configuration);
		});

		// id property
		final Property<?> idProperty = context.getDocumentIdProperty().orElseThrow(
				() -> new DataAccessException("Cannot perform a DELETE operation: missing document id property"
						+ " for value [" + configuration.getValue() + "]"));
		// document id
		final ObjectId id = context.getDocumentIdResolver().encode(configuration.getValue().getValue(idProperty));
		if (id == null) {
			throw new DataAccessException(
					"Cannot perform a DELETE operation: missing document id value for property [" + idProperty + "]");
		}

		return context.getClientSession()
				.map(session -> CompletableFutureSubscriber.fromPublisher(
						collection.deleteOne(session, Filters.eq(id), MongoOperations.getDeleteOptions(configuration))))
				.orElseGet(() -> CompletableFutureSubscriber.fromPublisher(
						collection.deleteOne(Filters.eq(id), MongoOperations.getDeleteOptions(configuration))))
				.thenApply(result -> AsyncPropertyBoxOperationResultContext.create(context, collection, configuration,
						result.getDeletedCount(), OperationType.DELETE, configuration.getValue(), null, id))
				.thenApply(ctx -> {
					// trace
					context.trace("Deleted document", "Deleted document id: " + ctx.getDocumentId().orElse(null));

					// operation result
					return OperationResult.builder().type(OperationType.DELETE).affectedCount(ctx.getAffectedCount())
							.build();
				});
	}

}
