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

import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.RefreshOperation;
import com.holonplatform.core.internal.datastore.operation.AbstractRefreshOperation;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDB {@link RefreshOperation}.
 *
 * @since 5.2.0
 */
public class MongoRefresh extends AbstractRefreshOperation {

	private static final long serialVersionUID = -7486319071876743650L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, RefreshOperation> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, RefreshOperation>() {

		@Override
		public Class<? extends RefreshOperation> getCommodityType() {
			return RefreshOperation.class;
		}

		@Override
		public RefreshOperation createCommodity(SyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new MongoRefresh(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public MongoRefresh(MongoOperationContext<MongoDatabase> operationContext) {
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

		// resolution context
		final MongoResolutionContext context = MongoResolutionContext.create(operationContext);
		context.addExpressionResolvers(getConfiguration().getExpressionResolvers());
		
		PropertyBox value = getConfiguration().getValue();
		
		operationContext.withDatabase(database -> {
			
			database.getCollection("todo");
			
		});

		return null;
	}

}
