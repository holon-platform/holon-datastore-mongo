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

import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.holonplatform.async.datastore.operation.AsyncInsert;
import com.holonplatform.async.datastore.operation.AsyncSave;
import com.holonplatform.async.internal.datastore.operation.AbstractAsyncSave;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.async.internal.MongoOperationConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.DocumentOperationContext;
import com.holonplatform.datastore.mongo.async.internal.support.PropertyBoxOperationContext;
import com.holonplatform.datastore.mongo.async.internal.support.SaveOperationContext;
import com.holonplatform.datastore.mongo.core.CollationOption;
import com.holonplatform.datastore.mongo.core.DocumentWriteOption;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

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

	private final MongoOperationContext<MongoDatabase> operationContext;

	public AsyncMongoSave(MongoOperationContext<MongoDatabase> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.commons.ExecutableOperation#execute()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CompletionStage<OperationResult> execute() {
		return CompletableFuture.supplyAsync(() -> {
			// validate
			getConfiguration().validate();
			// build context (for update)
			return PropertyBoxOperationContext.create(getConfiguration(), operationContext,
					MongoDocumentContext.createForUpdate(operationContext, getConfiguration().getValue()));
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
			// document id
			final Optional<Property<?>> idProperty = context.getDocumentContext().getDocumentIdProperty();
			final ObjectId id = idProperty.map(p -> context.getDocumentContext().getDocumentIdResolver()
					.encode(context.getConfiguration().getValue().getValue(p))).orElse(null);

			// check id property/value
			if (!idProperty.isPresent() || id == null) {
				// fallback to insert
				LOGGER.debug(() -> "Save operation: missing id property or value, fallback to insert");
				return operationContext.create(AsyncInsert.class).target(getConfiguration().getTarget())
						.value(getConfiguration().getValue()).withWriteOptions(getConfiguration().getWriteOptions())
						.execute()
						.thenApply(result -> SaveOperationContext.create(context.getConfiguration(),
								context.getOperationContext(), context.getDocumentContext(), context.getCollection(),
								OperationType.INSERT, result.getAffectedCount()));

			}

			// options - set upsert
			final UpdateOptions options = new UpdateOptions();
			options.bypassDocumentValidation(getConfiguration().hasWriteOption(DocumentWriteOption.BYPASS_VALIDATION));
			getConfiguration().getWriteOption(CollationOption.class)
					.ifPresent(o -> options.collation(o.getCollation()));
			options.upsert(true);

			// document
			final Document document = context.requireDocument();

			// prepare
			final CompletableFuture<SaveOperationContext> operation = new CompletableFuture<>();

			// insert
			context.getCollection().updateOne(Filters.eq(id), document, (result, error) -> {
				if (error != null) {
					operation.completeExceptionally(error);
				} else {
					// check insert
					final BsonValue upsertedId = result.getUpsertedId();
					operation.complete(SaveOperationContext.create(context.getConfiguration(),
							context.getOperationContext(), context.getDocumentContext(), context.getCollection(),
							(upsertedId != null) ? OperationType.INSERT : OperationType.UPDATE,
							result.isModifiedCountAvailable() ? Long.valueOf(result.getModifiedCount()).intValue() : 1,
							upsertedId, document));
				}
			});
			// return the future
			return operation;
		}).thenApply(context -> {
			// check trace
			context.getDocument().ifPresent(document -> {
				context.getOperationContext().trace("Saved document [" + context.getOperationType().name() + "]",
						DocumentSerializer.getDefault().toJson(context.getCollection().getCodecRegistry(), document));
			});

			// operation result
			OperationResult.Builder builder = OperationResult.builder().type(context.getOperationType())
					.affectedCount(context.getAffectedCount());

			// upserted key
			context.getUpsertedId().ifPresent(upsertedId -> {
				final ObjectId oid = upsertedId.asObjectId().getValue();
				context.getDocumentContext().getDocumentIdPath().ifPresent(idp -> {
					final Object idPropertyValue = context.getDocumentContext().getDocumentIdResolver().decode(oid,
							idp.getType());
					builder.withInsertedKey(idp, idPropertyValue);

					// check bring back ids
					if (getConfiguration().hasWriteOption(DefaultWriteOption.BRING_BACK_GENERATED_IDS)) {
						final PropertyBox value = context.getConfiguration().getValue();
						context.getDocumentContext().getDocumentIdProperty().ifPresent(idprp -> {
							if (value.contains(idprp)) {
								value.setValue((Property<Object>) idprp, idPropertyValue);
							}
						});
					}
				});
			});

			return builder.build();
		});
	}

}
