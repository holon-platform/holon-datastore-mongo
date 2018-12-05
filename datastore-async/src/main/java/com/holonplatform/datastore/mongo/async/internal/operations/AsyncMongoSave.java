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

import com.holonplatform.async.datastore.internal.operation.AbstractAsyncSave;
import com.holonplatform.async.datastore.operation.AsyncSave;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.commons.PropertyBoxOperationConfiguration;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.property.Property;
import com.holonplatform.datastore.mongo.async.internal.CompletableFutureSubscriber;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.async.internal.config.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.core.async.internal.support.AsyncPropertyBoxOperationResultContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

/**
 * MongoDB {@link AsyncSave}.
 *
 * @since 5.2.0
 */
public class AsyncMongoSave extends AbstractAsyncSave {

	private static final long serialVersionUID = 5871822996614117327L;

	private final static Logger LOGGER = MongoDatastoreLogger.create();

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncSave> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncSave>() {

		@Override
		public Class<? extends AsyncSave> getCommodityType() {
			return AsyncSave.class;
		}

		@Override
		public AsyncSave createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new AsyncMongoSave(context);
		}
	};

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public AsyncMongoSave(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
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

		// document id
		final Optional<Property<?>> idProperty = context.getDocumentIdProperty();
		final ObjectId id = idProperty
				.map(p -> context.getDocumentIdResolver().encode(configuration.getValue().getValue(p))).orElse(null);

		final CompletableFuture<AsyncPropertyBoxOperationResultContext> operation;

		// check id property/value
		if (!idProperty.isPresent() || id == null) {

			// fallback to insert
			LOGGER.debug(() -> "Save operation: missing id property or value, fallback to insert");

			// document to insert
			final Document document = context
					.resolveOrFail(PropertyBoxValue.create(configuration.getValue()), DocumentValue.class).getValue();

			// insert
			operation = context.getClientSession()
					.map(session -> CompletableFutureSubscriber.fromPublisher(collection.insertOne(session, document,
							MongoOperations.getInsertOneOptions(configuration))))
					.orElseGet(() -> CompletableFutureSubscriber.fromPublisher(
							collection.insertOne(document, MongoOperations.getInsertOneOptions(configuration))))
					.thenApply(result -> AsyncPropertyBoxOperationResultContext.create(context, collection,
							configuration, 1, OperationType.INSERT, configuration.getValue(), document));

		} else {

			// build context (for update)
			final MongoDocumentContext<ClientSession> upsertContext = MongoDocumentContext.createForUpdate(context,
					configuration.getValue());

			// document to upsert
			final Document document = upsertContext
					.resolveOrFail(PropertyBoxValue.create(configuration.getValue()), DocumentValue.class).getValue();

			// upsert
			operation = context.getClientSession()
					.map(session -> CompletableFutureSubscriber.fromPublisher(collection.updateOne(session,
							Filters.eq(id), document, MongoOperations.getUpdateOptions(configuration, true))))
					.orElseGet(() -> CompletableFutureSubscriber.fromPublisher(collection.updateOne(Filters.eq(id),
							document, MongoOperations.getUpdateOptions(configuration, true))))
					.thenApply(
							result -> AsyncPropertyBoxOperationResultContext.create(context, collection, configuration,
									(result.getUpsertedId() != null) ? 1 : MongoOperations.getAffectedCount(result),
									(result.getUpsertedId() != null) ? OperationType.INSERT : OperationType.UPDATE,
									configuration.getValue(), document, result.getUpsertedId()));
		}

		return operation.thenApply(ctx -> {
			// check trace
			ctx.getDocument().ifPresent(document -> {
				context.trace("Saved document [" + ctx.getOperationType().name() + "]", document);
			});
			// operation result
			OperationResult.Builder builder = OperationResult.builder().type(ctx.getOperationType())
					.affectedCount(ctx.getAffectedCount());
			if (ctx.getOperationType() == OperationType.INSERT) {
				if (!ctx.getUpsertedId().isPresent()) {
					// inserted key
					ctx.getDocument().ifPresent(document -> {
						MongoOperations.checkInsertedKeys(builder, ctx.getContext(), ctx.getConfiguration(), document,
								ctx.getValue());
					});
				} else {
					// upserted key
					MongoOperations.checkUpsertedKey(builder, ctx.getContext(), ctx.getConfiguration(),
							ctx.getUpsertedId().orElse(null), ctx.getValue());
				}
			}
			return builder.build();
		});
	}

}
