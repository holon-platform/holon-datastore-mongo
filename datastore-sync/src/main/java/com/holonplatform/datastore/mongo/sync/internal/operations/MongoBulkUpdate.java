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
package com.holonplatform.datastore.mongo.sync.internal.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.core.Path;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.bulk.BulkUpdate;
import com.holonplatform.core.internal.datastore.bulk.AbstractBulkUpdate;
import com.holonplatform.datastore.mongo.core.CollationOption;
import com.holonplatform.datastore.mongo.core.DocumentWriteOption;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.internal.MongoOperationConfigurator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

/**
 * Mongo {@link BulkUpdate} implementation.
 * 
 * @since 5.2.0
 */
public class MongoBulkUpdate extends AbstractBulkUpdate {

	private static final long serialVersionUID = 1628023945720514817L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, BulkUpdate> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, BulkUpdate>() {

		@Override
		public Class<? extends BulkUpdate> getCommodityType() {
			return BulkUpdate.class;
		}

		@Override
		public BulkUpdate createCommodity(SyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new MongoBulkUpdate(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public MongoBulkUpdate(MongoOperationContext<MongoDatabase> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.ExecutableOperation#execute()
	 */
	@Override
	public OperationResult execute() {

		// validate
		getConfiguration().validate();

		// context
		final MongoResolutionContext context = MongoResolutionContext.create(operationContext);

		// resolve collection
		final String collectionName = context.resolveOrFail(getConfiguration().getTarget(), CollectionName.class)
				.getName();

		// resolve filter
		Optional<Bson> filter = getConfiguration().getFilter()
				.map(f -> context.resolveOrFail(f, BsonExpression.class).getValue());

		// resolve values
		final Map<Path<?>, TypedExpression<?>> values = getConfiguration().getValues();
		List<Bson> updates = new ArrayList<>(values.size());
		for (Entry<Path<?>, TypedExpression<?>> value : values.entrySet()) {
			// child update context
			final MongoResolutionContext subContext = context.childContextForUpdate(value.getKey());

			if (value.getValue() == null) {
				// resolve field name
				final String fieldName = subContext.resolveOrFail(value.getKey(), FieldName.class).getFieldName();
				// $unset for null values
				updates.add(Updates.unset(fieldName));
			} else {
				// check value expression resolution
				updates.add(context.resolve(value.getValue(), BsonExpression.class).map(be -> be.getValue())
						.orElseGet(() -> {
							// resolve field name
							final String fieldName = subContext.resolveOrFail(value.getKey(), FieldName.class)
									.getFieldName();
							// resolve field value
							final Object fieldValue = context.resolveOrFail(value.getValue(), FieldValue.class)
									.getValue();
							// $set value
							return Updates.set(fieldName, fieldValue);
						}));
			}
		}

		Bson update = Updates.combine(updates);

		return operationContext.withDatabase(database -> {

			// get and configure collection
			final MongoCollection<Document> collection = MongoOperationConfigurator
					.configureWrite(database.getCollection(collectionName), context, getConfiguration());

			// options
			UpdateOptions options = new UpdateOptions();
			options.bypassDocumentValidation(getConfiguration().hasWriteOption(DocumentWriteOption.BYPASS_VALIDATION));
			getConfiguration().getWriteOption(CollationOption.class)
					.ifPresent(o -> options.collation(o.getCollation()));

			// trace
			operationContext.trace("Update documents:", trace(filter, update));

			// delete
			UpdateResult result = collection.updateMany(filter.orElse(null), update, options);

			return OperationResult.builder().type(OperationType.UPDATE).affectedCount(result.getModifiedCount())
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
