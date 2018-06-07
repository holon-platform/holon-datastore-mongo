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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.datastore.bulk.BulkInsert;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.datastore.bulk.AbstractBulkInsert;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.DocumentWriteOption;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.internal.MongoOperationConfigurator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;

/**
 * Mongo {@link BulkInsert} implementation.
 * 
 * @since 5.2.0
 */
public class MongoBulkInsert extends AbstractBulkInsert {

	private static final long serialVersionUID = -7090144911918354693L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, BulkInsert> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, BulkInsert>() {

		@Override
		public Class<? extends BulkInsert> getCommodityType() {
			return BulkInsert.class;
		}

		@Override
		public BulkInsert createCommodity(SyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new MongoBulkInsert(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public MongoBulkInsert(MongoOperationContext<MongoDatabase> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.ExecutableOperation#execute()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public OperationResult execute() {
		try {
			// validate
			getConfiguration().validate();

			// property set
			final PropertySet<?> propertySet = getConfiguration().getPropertySet()
					.orElseThrow(() -> new InvalidExpressionException("Missing bulk insert operation property set"));

			// resolution context
			final MongoDocumentContext context = MongoDocumentContext.create(operationContext, propertySet);

			// encode documents
			final Map<Document, PropertyBox> documentValues = new LinkedHashMap<>(
					getConfiguration().getValues().size());
			getConfiguration().getValues().forEach(v -> {
				documentValues.put(context.resolveOrFail(PropertyBoxValue.create(v), DocumentValue.class).getValue(),
						v);
			});

			// resolve collection
			final String collectionName = context.resolveOrFail(getConfiguration().getTarget(), CollectionName.class)
					.getName();

			return operationContext.withDatabase(database -> {

				// get and configure collection
				final MongoCollection<Document> collection = MongoOperationConfigurator
						.configureWrite(database.getCollection(collectionName), context, getConfiguration());

				// options
				final InsertManyOptions options = new InsertManyOptions();
				options.bypassDocumentValidation(
						getConfiguration().hasWriteOption(DocumentWriteOption.BYPASS_VALIDATION));
				options.ordered(!getConfiguration().hasWriteOption(DocumentWriteOption.UNORDERED));

				// insert
				final List<Document> documents = new ArrayList<>(documentValues.keySet());

				collection.insertMany(documents, options);

				// trace
				operationContext.trace("Inserted documents",
						DocumentSerializer.getDefault().toJson(collection.getCodecRegistry(), documents));

				final OperationResult.Builder builder = OperationResult.builder().type(OperationType.INSERT)
						.affectedCount(documents.size());

				// check inserted keys
				if (getConfiguration().hasWriteOption(DefaultWriteOption.BRING_BACK_GENERATED_IDS)) {
					context.getDocumentIdProperty().ifPresent(idProperty -> {
						for (Entry<Document, PropertyBox> document : documentValues.entrySet()) {
							final ObjectId oid = document.getKey().getObjectId(MongoDocumentContext.ID_FIELD_NAME);
							if (oid != null) {
								final Object idPropertyValue = context.getDocumentIdResolver().decode(oid,
										idProperty.getType());
								if (document.getValue().contains(idProperty)) {
									document.getValue().setValue((Property<Object>) idProperty, idPropertyValue);
								}
							}
						}
					});
				}

				return builder.build();

			});
		} catch (Exception e) {
			throw new DataAccessException("Bulk INSERT operation failed", e);
		}
	}

}
