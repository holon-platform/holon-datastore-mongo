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

import java.util.Optional;

import org.bson.Document;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.Save;
import com.holonplatform.core.internal.datastore.operation.AbstractSave;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.CollationOption;
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
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

/**
 * MongoDB {@link Save}.
 *
 * @since 5.2.0
 */
public class MongoSave extends AbstractSave {

	private static final long serialVersionUID = -7779005644734871789L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Save> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Save>() {

		@Override
		public Class<? extends Save> getCommodityType() {
			return Save.class;
		}

		@Override
		public Save createCommodity(SyncMongoDatastoreCommodityContext context) throws CommodityConfigurationException {
			return new MongoSave(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public MongoSave(MongoOperationContext<MongoDatabase> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.ExecutableOperation#execute()
	 */
	@Override
	public OperationResult execute() {
		
		// TODO
		
		/*

		// validate
		getConfiguration().validate();

		// value to save
		final PropertyBox value = getConfiguration().getValue();

		// resolution context
		final MongoDocumentContext context = MongoDocumentContext.create(operationContext, value);

		// document id property
		final Optional<Property<?>> idProperty = context.getDocumentIdProperty();

		// resolve collection
		final String collectionName = context.resolveOrFail(getConfiguration().getTarget(), CollectionName.class)
				.getName();

		return operationContext.withDatabase(database -> {

			// get and configure collection
			final MongoCollection<Document> collection = MongoOperationConfigurator
					.configureWrite(database.getCollection(collectionName), context, getConfiguration());

			// encode Document
			Document document = context.resolveOrFail(PropertyBoxValue.create(value), DocumentValue.class).getValue();

			// options
			final UpdateOptions options = new UpdateOptions();
			options.bypassDocumentValidation(getConfiguration().hasWriteOption(DocumentWriteOption.BYPASS_VALIDATION));
			getConfiguration().getWriteOption(CollationOption.class)
					.ifPresent(o -> options.collation(o.getCollation()));
			options.upsert(true);
			
			//collection.up

			// update
			UpdateResult result = collection.updateOne(Filters.eq(id), document, options);

			int affected = result.isModifiedCountAvailable() ? Long.valueOf(result.getModifiedCount()).intValue() : 1;

			// trace
			operationContext.trace("Updated document",
					DocumentSerializer.getDefault().toJson(collection.getCodecRegistry(), document));

			// result
			return OperationResult.builder().type(OperationType.UPDATE).affectedCount(affected).build();

		});
		*/
		
		return null;
	}

}
