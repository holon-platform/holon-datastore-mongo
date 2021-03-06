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

import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.Refresh;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.datastore.operation.AbstractRefresh;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.internal.configurator.SyncMongoCollectionConfigurator;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

/**
 * MongoDB {@link Refresh}.
 *
 * @since 5.2.0
 */
public class MongoRefresh extends AbstractRefresh {

	private static final long serialVersionUID = -7486319071876743650L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Refresh> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Refresh>() {

		@Override
		public Class<? extends Refresh> getCommodityType() {
			return Refresh.class;
		}

		@Override
		public Refresh createCommodity(SyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new MongoRefresh(context);
		}
	};

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public MongoRefresh(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.ExecutableOperation#execute()
	 */
	@Override
	public PropertyBox execute() {

		// validate
		getConfiguration().validate();

		// value to refresh
		final PropertyBox value = getConfiguration().getValue();

		// resolution context
		final MongoDocumentContext<ClientSession> context = MongoDocumentContext.create(operationContext, value);
		context.addExpressionResolvers(getConfiguration().getExpressionResolvers());

		// check document id property
		final Property<?> idProperty = context.getDocumentIdProperty().orElseThrow(() -> new DataAccessException(
				"Cannot perform a REFRESH operation: missing document id property" + " for value [" + value + "]"));

		final ObjectId id = context.getDocumentIdResolver().encode(value.getValue(idProperty));
		if (id == null) {
			throw new DataAccessException(
					"Cannot perform a REFRESH operation: missing document id value for property [" + idProperty + "]");
		}

		// resolve collection
		final String collectionName = context.resolveOrFail(getConfiguration().getTarget(), CollectionName.class)
				.getName();

		return operationContext.withDatabase(database -> {

			// get and configure collection
			final MongoCollection<Document> collection = SyncMongoCollectionConfigurator
					.configureRead(database.getCollection(collectionName), context, getConfiguration().getParameters());

			// get document by id
			final Document document = context.getClientSession().map(cs -> collection.find(cs, Filters.eq(id)))
					.orElse(collection.find(Filters.eq(id))).first();

			if (document == null) {
				throw new DataAccessException("No document found with id [" + id + "]");
			}

			// trace
			operationContext.trace("Refreshed document", document);

			// decode the document
			return context.resolveOrFail(DocumentValue.create(document), PropertyBoxValue.class).getValue();

		});
	}

}
