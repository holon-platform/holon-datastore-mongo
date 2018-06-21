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
import com.holonplatform.core.datastore.operation.Delete;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.datastore.operation.AbstractDelete;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.internal.MongoOperationConfigurator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

/**
 * MongoDB {@link Delete}.
 *
 * @since 5.2.0
 */
public class MongoDelete extends AbstractDelete {

	private static final long serialVersionUID = 7267920035347307152L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Delete> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Delete>() {

		@Override
		public Class<? extends Delete> getCommodityType() {
			return Delete.class;
		}

		@Override
		public Delete createCommodity(SyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new MongoDelete(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public MongoDelete(MongoOperationContext<MongoDatabase> operationContext) {
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

		// check document id property
		final Property<?> idProperty = context.getDocumentIdProperty().orElseThrow(() -> new DataAccessException(
				"Cannot perform DELETE operation: missing document id property" + " for value [" + value + "]"));

		final ObjectId id = context.getDocumentIdResolver().encode(value.getValue(idProperty));
		if (id == null) {
			throw new DataAccessException(
					"Cannot perform DELETE operation: missing document id value for property [" + idProperty + "]");
		}

		return operationContext.withDatabase(database -> {

			// get and configure collection
			final MongoCollection<Document> collection = MongoOperationConfigurator
					.configureWrite(database.getCollection(collectionName), context, getConfiguration());

			// delete
			final DeleteResult result = collection.deleteOne(Filters.eq(id),
					MongoOperations.getDeleteOptions(getConfiguration()));

			// trace
			operationContext.trace("Deleted document",
					() -> context.resolve(PropertyBoxValue.create(value), DocumentValue.class).map(d -> d.getValue())
							.map(d -> DocumentSerializer.getDefault().toJson(collection.getCodecRegistry(), d))
							.orElse("with id [" + id + "]"));

			return OperationResult.builder().type(OperationType.DELETE).affectedCount(result.getDeletedCount()).build();

		});
	}

}
