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
package com.holonplatform.datastore.mongo.async.internal.operations;

import java.util.concurrent.CompletionStage;

import com.holonplatform.async.datastore.operation.AsyncSave;
import com.holonplatform.async.internal.datastore.operation.AbstractAsyncSave;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.mongodb.async.client.MongoDatabase;

/**
 * MongoDB {@link AsyncSave}.
 *
 * @since 5.2.0
 */
public class AsyncMongoSave extends AbstractAsyncSave {

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncSave> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, AsyncSave>() {

		@Override
		public Class<? extends AsyncSave> getCommodityType() {
			return AsyncSave.class;
		}

		@Override
		public AsyncSave createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new AsyncMongoSave(context);
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public AsyncMongoSave(MongoOperationContext<MongoDatabase> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.commons.ExecutableOperation#execute()
	 */
	@Override
	public CompletionStage<OperationResult> execute() {
		// TODO Auto-generated method stub
		return null;
	}

}
