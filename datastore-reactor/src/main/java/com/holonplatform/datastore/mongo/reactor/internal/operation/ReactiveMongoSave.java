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
package com.holonplatform.datastore.mongo.reactor.internal.operation;

import java.util.Optional;

import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.commons.PropertyBoxOperationConfiguration;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.property.Property;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.async.internal.config.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.core.async.internal.support.AsyncPropertyBoxOperationContext;
import com.holonplatform.datastore.mongo.core.async.internal.support.AsyncPropertyBoxOperationResultContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.holonplatform.reactor.datastore.internal.operation.AbstractReactiveSave;
import com.holonplatform.reactor.datastore.operation.ReactiveSave;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import reactor.core.publisher.Mono;

/**
 * MongoDB {@link ReactiveSave}.
 *
 * @since 5.2.0
 */
public class ReactiveMongoSave extends AbstractReactiveSave {

	private static final long serialVersionUID = 5871822996614117327L;

	private final static Logger LOGGER = MongoDatastoreLogger.create();

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, ReactiveSave> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, ReactiveSave>() {

		@Override
		public Class<? extends ReactiveSave> getCommodityType() {
			return ReactiveSave.class;
		}

		@Override
		public ReactiveSave createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new ReactiveMongoSave(context);
		}
	};

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public ReactiveMongoSave(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.commons.ExecutableOperation#execute()
	 */
	@Override
	public Mono<OperationResult> execute() {
		return Mono.fromSupplier(() -> {
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

			// document id
			final Optional<Property<?>> idProperty = context.getDocumentIdProperty();
			final ObjectId id = idProperty
					.map(p -> context.getDocumentIdResolver().encode(configuration.getValue().getValue(p)))
					.orElse(null);

			// done
			return AsyncPropertyBoxOperationContext.create(context, collection, configuration, configuration.getValue(),
					id);
		}).flatMap(ctx -> {
			ObjectId id = ctx.getDocumentId().orElse(null);
			if (id == null) {
				// fallback to insert
				LOGGER.debug(() -> "Save operation: missing id property or value, fallback to insert");

				// document to insert
				final Document document = ctx.getContext()
						.resolveOrFail(PropertyBoxValue.create(ctx.getValue()), DocumentValue.class).getValue();

				// check client session available
				return ctx.getContext().getClientSession()
						.map(session -> Mono
								.from(ctx.getCollection().insertOne(session, document,
										MongoOperations.getInsertOneOptions(ctx.getConfiguration())))
								.map(result -> AsyncPropertyBoxOperationResultContext.create(ctx.getContext(),
										ctx.getCollection(), ctx.getConfiguration(), 1, OperationType.INSERT,
										ctx.getValue(), document)))
						.orElseGet(() -> Mono
								.from(ctx.getCollection().insertOne(document,
										MongoOperations.getInsertOneOptions(ctx.getConfiguration())))
								.map(result -> AsyncPropertyBoxOperationResultContext.create(ctx.getContext(),
										ctx.getCollection(), ctx.getConfiguration(), 1, OperationType.INSERT,
										ctx.getValue(), document)));
			} else {
				// build context (for update)
				final MongoDocumentContext<ClientSession> upsertContext = MongoDocumentContext
						.createForUpdate(ctx.getContext(), ctx.getValue());

				// document to upsert
				final Document document = upsertContext
						.resolveOrFail(PropertyBoxValue.create(ctx.getValue()), DocumentValue.class).getValue();

				// upsert
				return ctx
						.getContext().getClientSession().map(
								session -> Mono
										.from(ctx.getCollection().updateOne(session, Filters.eq(id), document,
												MongoOperations.getUpdateOptions(ctx.getConfiguration(), true)))
										.map(result -> {
											// check insert
											final BsonValue upsertedId = result.getUpsertedId();
											final long affected = (upsertedId != null) ? 1
													: MongoOperations.getAffectedCount(result);
											return AsyncPropertyBoxOperationResultContext.create(ctx.getContext(),
													ctx.getCollection(), ctx.getConfiguration(), affected,
													(upsertedId != null) ? OperationType.INSERT : OperationType.UPDATE,
													ctx.getValue(), document, upsertedId);
										}))
						.orElseGet(
								() -> Mono
										.from(ctx.getCollection().updateOne(Filters.eq(id), document,
												MongoOperations.getUpdateOptions(ctx.getConfiguration(), true)))
										.map(result -> {
											// check insert
											final BsonValue upsertedId = result.getUpsertedId();
											final long affected = (upsertedId != null) ? 1
													: MongoOperations.getAffectedCount(result);
											return AsyncPropertyBoxOperationResultContext.create(ctx.getContext(),
													ctx.getCollection(), ctx.getConfiguration(), affected,
													(upsertedId != null) ? OperationType.INSERT : OperationType.UPDATE,
													ctx.getValue(), document, upsertedId);
										}));
			}
		}).map(context -> {

			// check trace
			context.getDocument().ifPresent(document -> {
				context.trace("Saved document [" + context.getOperationType().name() + "]", document);
			});

			// operation result
			OperationResult.Builder builder = OperationResult.builder().type(context.getOperationType())
					.affectedCount(context.getAffectedCount());
			if (context.getOperationType() == OperationType.INSERT) {
				if (!context.getUpsertedId().isPresent()) {
					// inserted key
					context.getDocument().ifPresent(document -> {
						MongoOperations.checkInsertedKeys(builder, context.getContext(), context.getConfiguration(),
								document, context.getValue());
					});
				} else {
					// upserted key
					MongoOperations.checkUpsertedKey(builder, context.getContext(), context.getConfiguration(),
							context.getUpsertedId().orElse(null), context.getValue());
				}
			}
			return builder.build();
		});
	}

}
