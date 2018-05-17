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
import com.holonplatform.core.datastore.operation.DeleteOperation;
import com.holonplatform.core.internal.datastore.operation.AbstractDeleteOperation;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDB {@link DeleteOperation}.
 *
 * @since 5.2.0
 */
public class MongoDelete extends AbstractDeleteOperation {

	private static final long serialVersionUID = 7267920035347307152L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, DeleteOperation> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, DeleteOperation>() {

		@Override
		public Class<? extends DeleteOperation> getCommodityType() {
			return DeleteOperation.class;
		}

		@Override
		public DeleteOperation createCommodity(SyncMongoDatastoreCommodityContext context)
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
		// TODO
		return null;
	}

}