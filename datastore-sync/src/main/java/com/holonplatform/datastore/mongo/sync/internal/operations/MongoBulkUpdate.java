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

import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.bulk.BulkUpdate;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.datastore.bulk.AbstractBulkUpdate;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.internal.configurator.SyncMongoCollectionConfigurator;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
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

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public MongoBulkUpdate(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.ExecutableOperation#execute()
	 */
	@Override
	public OperationResult execute() {
		try {
			// validate
			getConfiguration().validate();

			// context
			final MongoResolutionContext<ClientSession> context = MongoResolutionContext.create(operationContext);
			context.addExpressionResolvers(getConfiguration().getExpressionResolvers());

			// resolve collection
			final String collectionName = context.resolveOrFail(getConfiguration().getTarget(), CollectionName.class)
					.getName();

			// resolve filter
			Optional<Bson> filter = getConfiguration().getFilter()
					.map(f -> context.resolveOrFail(f, BsonExpression.class).getValue());

			// update expression
			final Bson update = MongoOperations.getUpdateExpression(context, getConfiguration());

			return operationContext.withDatabase(database -> {

				// get and configure collection
				final MongoCollection<Document> collection = SyncMongoCollectionConfigurator
						.configureWrite(database.getCollection(collectionName), context, getConfiguration());

				// trace
				operationContext.trace("Update documents",
						MongoOperations.traceUpdate(operationContext, filter, update));

				// options
				final UpdateOptions options = MongoOperations.getUpdateOptions(getConfiguration(), false);

				// update
				final UpdateResult result = context.getClientSession()
						.map(cs -> collection.updateMany(cs, filter.orElse(null), update, options))
						.orElse(collection.updateMany(filter.orElse(null), update, options));

				return OperationResult.builder().type(OperationType.UPDATE).affectedCount(result.getModifiedCount())
						.build();

			});
		} catch (Exception e) {
			throw new DataAccessException("Bulk UPDATE operation failed", e);
		}
	}

}
