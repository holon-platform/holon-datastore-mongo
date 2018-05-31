/*
 * Copyright 2016-2017 Axioma srl.
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

import org.bson.Document;
import org.bson.types.ObjectId;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.Insert;
import com.holonplatform.core.internal.datastore.operation.AbstractInsert;
import com.holonplatform.core.property.PropertyBox;
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
import com.mongodb.client.model.InsertOneOptions;

/**
 * MongoDB {@link Insert}.
 *
 * @since 5.2.0
 */
public class MongoInsert extends AbstractInsert {

	private static final long serialVersionUID = -6120583386835783717L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Insert> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Insert>() {

		@Override
		public Class<? extends Insert> getCommodityType() {
			return Insert.class;
		}

		@Override
		public Insert createCommodity(SyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new MongoInsert(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public MongoInsert(MongoOperationContext<MongoDatabase> operationContext) {
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

		// value to insert
		final PropertyBox value = getConfiguration().getValue();

		// resolution context
		final MongoDocumentContext context = MongoDocumentContext.create(operationContext, value);

		// resolve collection
		final String collectionName = context.resolveOrFail(getConfiguration().getTarget(), CollectionName.class)
				.getName();

		// TODO ensure indexes

		return operationContext.withDatabase(database -> {

			// get and configure collection
			final MongoCollection<Document> collection = MongoOperationConfigurator
					.configureWrite(database.getCollection(collectionName), context, getConfiguration());

			// encode Document
			Document document = context.resolveOrFail(PropertyBoxValue.create(value), DocumentValue.class).getValue();

			// options
			final InsertOneOptions options = new InsertOneOptions();
			options.bypassDocumentValidation(getConfiguration().hasWriteOption(DocumentWriteOption.BYPASS_VALIDATION));
			
			// insert
			collection.insertOne(document, options);

			// trace
			operationContext.trace("Inserted document",
					DocumentSerializer.getDefault().toJson(collection.getCodecRegistry(), document));

			final OperationResult.Builder builder = OperationResult.builder().type(OperationType.INSERT)
					.affectedCount(1);

			// check inserted keys
			if (document.containsKey(MongoDocumentContext.ID_FIELD_NAME)) {
				// get document id value
				final ObjectId oid = document.getObjectId(MongoDocumentContext.ID_FIELD_NAME);
				if (oid != null) {
					context.getDocumentIdPath().ifPresent(idp -> {
						builder.withInsertedKey(idp, context.getDocumentIdResolver().decode(oid, idp.getType()));
					});
				}
			}

			return builder.build();

		});
	}

}
