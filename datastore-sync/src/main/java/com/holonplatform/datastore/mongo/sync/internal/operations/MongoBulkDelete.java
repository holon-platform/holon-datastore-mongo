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

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.bulk.BulkDelete;
import com.holonplatform.core.internal.datastore.bulk.AbstractBulkDeleteOperation;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.mongodb.client.MongoDatabase;

/**
 * Mongo {@link BulkDelete} implementation.
 * 
 * @since 5.2.0
 */
public class MongoBulkDelete extends AbstractBulkDeleteOperation<BulkDelete> implements BulkDelete {

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
	 * @see com.holonplatform.core.internal.datastore.operation.AbstractDatastoreOperation#getActualOperation()
	 */
	@Override
	protected BulkDelete getActualOperation() {
		return this;
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
