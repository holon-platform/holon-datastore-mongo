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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.async.datastore.operation.AsyncBulkUpdate;
import com.holonplatform.async.internal.datastore.operation.AbstractAsyncBulkUpdate;
import com.holonplatform.core.Path;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.async.internal.MongoOperationConfigurator;
import com.holonplatform.datastore.mongo.async.internal.support.BulkOperationContext;
import com.holonplatform.datastore.mongo.core.CollationOption;
import com.holonplatform.datastore.mongo.core.DocumentWriteOption;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

/**
 * Mongo {@link AsyncBulkUpdate} implementation.
 * 
 * @since 5.2.0
 */
public class AsyncMongoBulkUpdate extends AbstractAsyncBulkUpdate {

	private static final long serialVersionUID = -3672271825567632474L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncBulkUpdate> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncBulkUpdate>() {

		@Override
		public Class<? extends AsyncBulkUpdate> getCommodityType() {
			return AsyncBulkUpdate.class;
		}

		@Override
		public AsyncBulkUpdate createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new AsyncMongoBulkUpdate(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public AsyncMongoBulkUpdate(MongoOperationContext<MongoDatabase> operationContext) {
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
			// context
			final MongoResolutionContext context = MongoResolutionContext.create(operationContext);
			// filter
			Optional<Bson> filter = getConfiguration().getFilter()
					.map(f -> context.resolveOrFail(f, BsonExpression.class).getValue());
			// resolve collection name
			final String collectionName = context.resolveOrFail(getConfiguration().getTarget(), CollectionName.class)
					.getName();
			// get and configure collection
			MongoCollection<Document> collection = operationContext.withDatabase(database -> {
				return MongoOperationConfigurator.configureWrite(database.getCollection(collectionName), context,
						getConfiguration());
			});

			// build context
			return BulkOperationContext.create(operationContext, getConfiguration(), context, collection,
					filter.orElse(null));

		}).thenCompose(context -> {
			// resolve values
			final Map<Path<?>, TypedExpression<?>> values = getConfiguration().getValues();
			List<Bson> updates = new ArrayList<>(values.size());
			for (Entry<Path<?>, TypedExpression<?>> value : values.entrySet()) {
				// child update context
				final MongoResolutionContext subContext = context.getResolutionContext()
						.childContextForUpdate(value.getKey());

				if (value.getValue() == null) {
					// resolve field name
					final String fieldName = subContext.resolveOrFail(value.getKey(), FieldName.class).getFieldName();
					// $unset for null values
					updates.add(Updates.unset(fieldName));
				} else {
					// check value expression resolution
					updates.add(subContext.resolve(value.getValue(), BsonExpression.class).map(be -> be.getValue())
							.orElseGet(() -> {
								// resolve field name
								final String fieldName = subContext.resolveOrFail(value.getKey(), FieldName.class)
										.getFieldName();
								// resolve field value
								final Object fieldValue = subContext.resolveOrFail(value.getValue(), FieldValue.class)
										.getValue();
								// $set value
								return Updates.set(fieldName, fieldValue);
							}));
				}
			}

			Bson update = Updates.combine(updates);

			// options
			UpdateOptions options = new UpdateOptions();
			options.bypassDocumentValidation(getConfiguration().hasWriteOption(DocumentWriteOption.BYPASS_VALIDATION));
			getConfiguration().getWriteOption(CollationOption.class)
					.ifPresent(o -> options.collation(o.getCollation()));

			// trace
			operationContext.trace("Update documents", trace(context.getFilter(), update));

			// prepare
			final CompletableFuture<BulkOperationContext> operation = new CompletableFuture<>();
			// update
			context.getCollection().updateMany(context.getFilter().orElse(null), update, options, (result, error) -> {
				if (error != null) {
					operation.completeExceptionally(error);
				} else {
					context.setAffectedCount(result.getModifiedCount());
					operation.complete(context);
				}
			});
			// return the future
			return operation;
		}).thenApply(context -> {
			return OperationResult.builder().type(OperationType.UPDATE).affectedCount(context.getAffectedCount())
					.build();
		});
	}

	private static String trace(Optional<Bson> filter, Bson update) {
		final StringBuilder sb = new StringBuilder();
		filter.ifPresent(f -> {
			sb.append("Filter:\n");
			sb.append(DocumentSerializer.getDefault().toJson(f));
			sb.append("\n");
		});
		sb.append("Values:\n");
		sb.append(DocumentSerializer.getDefault().toJson(update));
		return sb.toString();
	}

}
