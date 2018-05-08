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

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.InsertOperation;
import com.holonplatform.core.internal.datastore.operation.AbstractInsertOperation;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDB {@link InsertOperation}.
 *
 * @since 5.2.0
 */
public class MongoInsert extends AbstractInsertOperation {

	private static final long serialVersionUID = -6120583386835783717L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, InsertOperation> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, InsertOperation>() {

		@Override
		public Class<? extends InsertOperation> getCommodityType() {
			return InsertOperation.class;
		}

		@Override
		public InsertOperation createCommodity(SyncMongoDatastoreCommodityContext context)
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

		// resolution context
		final MongoResolutionContext context = MongoResolutionContext.create(operationContext);
		context.addExpressionResolvers(getConfiguration().getExpressionResolvers());
		
		// TODO ensure indexes

		operationContext.withDatabase(database -> {
			
			// TODO
			//database.withCodecRegistry(codecRegistry)

			database.getCollection("todo");

		});

		return null;
	}

}
