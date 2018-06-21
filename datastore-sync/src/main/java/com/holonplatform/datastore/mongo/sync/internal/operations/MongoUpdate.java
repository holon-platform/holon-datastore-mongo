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
import com.holonplatform.core.datastore.operation.Update;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.datastore.operation.AbstractUpdate;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.internal.MongoOperationConfigurator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

/**
 * MongoDB {@link Update}.
 *
 * @since 5.2.0
 */
public class MongoUpdate extends AbstractUpdate {

	private static final long serialVersionUID = -6928634330323556178L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Update> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Update>() {

		@Override
		public Class<? extends Update> getCommodityType() {
			return Update.class;
		}

		@Override
		public Update createCommodity(SyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new MongoUpdate(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public MongoUpdate(MongoOperationContext<MongoDatabase> operationContext) {
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

		// value to update
		final PropertyBox value = getConfiguration().getValue();

		// resolution context
		final MongoDocumentContext context = MongoDocumentContext.createForUpdate(operationContext, value);

		// check document id property
		final Property<?> idProperty = context.getDocumentIdProperty().orElseThrow(() -> new DataAccessException(
				"Cannot perform an UPDATE operation: missing document id property" + " for value [" + value + "]"));

		final ObjectId id = context.getDocumentIdResolver().encode(value.getValue(idProperty));
		if (id == null) {
			throw new DataAccessException(
					"Cannot perform an UPDATE operation: missing document id value for property [" + idProperty + "]");
		}

		// resolve collection
		final String collectionName = context.resolveOrFail(getConfiguration().getTarget(), CollectionName.class)
				.getName();

		return operationContext.withDatabase(database -> {

			// get and configure collection
			final MongoCollection<Document> collection = MongoOperationConfigurator
					.configureWrite(database.getCollection(collectionName), context, getConfiguration());

			// encode Document
			Document document = context.resolveOrFail(PropertyBoxValue.create(value), DocumentValue.class).getValue();

			// trace
			operationContext.trace("Update document", document);

			// update
			final UpdateResult result = collection.updateOne(Filters.eq(id), document,
					MongoOperations.getUpdateOptions(getConfiguration(), false));

			// result
			return OperationResult.builder().type(OperationType.UPDATE)
					.affectedCount(MongoOperations.getAffectedCount(result)).build();

		});
	}

}
