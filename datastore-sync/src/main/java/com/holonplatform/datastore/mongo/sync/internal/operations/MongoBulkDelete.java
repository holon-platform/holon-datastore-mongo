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
import com.holonplatform.core.datastore.bulk.BulkDelete;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.datastore.bulk.AbstractBulkDelete;
import com.holonplatform.datastore.mongo.core.CollationOption;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.internal.MongoOperationConfigurator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.DeleteResult;

/**
 * Mongo {@link BulkDelete} implementation.
 * 
 * @since 5.2.0
 */
public class MongoBulkDelete extends AbstractBulkDelete {

	private static final long serialVersionUID = 4726239970165566199L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, BulkDelete> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, BulkDelete>() {

		@Override
		public Class<? extends BulkDelete> getCommodityType() {
			return BulkDelete.class;
		}

		@Override
		public BulkDelete createCommodity(SyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new MongoBulkDelete(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public MongoBulkDelete(MongoOperationContext<MongoDatabase> operationContext) {
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
			final MongoResolutionContext context = MongoResolutionContext.create(operationContext);

			// resolve collection
			final String collectionName = context.resolveOrFail(getConfiguration().getTarget(), CollectionName.class)
					.getName();

			// resolve filter
			Optional<Bson> filter = getConfiguration().getFilter()
					.map(f -> context.resolveOrFail(f, BsonExpression.class).getValue());

			return operationContext.withDatabase(database -> {

				// get and configure collection
				final MongoCollection<Document> collection = MongoOperationConfigurator
						.configureWrite(database.getCollection(collectionName), context, getConfiguration());

				// options
				DeleteOptions options = new DeleteOptions();
				getConfiguration().getWriteOption(CollationOption.class)
						.ifPresent(o -> options.collation(o.getCollation()));

				// trace
				operationContext.trace("Delete documents - filter",
						filter.map(f -> DocumentSerializer.getDefault().toJson(f)).orElse("[NONE]"));

				// delete
				DeleteResult result = collection.deleteMany(filter.orElse(null), options);

				return OperationResult.builder().type(OperationType.DELETE).affectedCount(result.getDeletedCount())
						.build();

			});
		} catch (Exception e) {
			throw new DataAccessException("Bulk DELETE operation failed", e);
		}
	}

}
