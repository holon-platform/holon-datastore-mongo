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
package com.holonplatform.datastore.mongo.async;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.DatastoreCommodity;
import com.holonplatform.core.datastore.DatastoreOperations;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityFactory;
import com.holonplatform.datastore.mongo.async.operation.AsyncBulkDelete;
import com.holonplatform.datastore.mongo.async.operation.AsyncBulkInsert;
import com.holonplatform.datastore.mongo.async.operation.AsyncBulkUpdate;
import com.holonplatform.datastore.mongo.async.operation.AsyncQuery;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;

/**
 * TODO
 */
public interface AsyncMongoDatastore extends
		DatastoreOperations<SingleResultCallback<OperationResult>, SingleResultCallback<PropertyBox>, AsyncBulkInsert, AsyncBulkUpdate, AsyncBulkDelete, AsyncQuery> {

	/**
	 * Get a builder to create a {@link AsyncMongoDatastore} instance.
	 * @return Datastore builder
	 */
	static Builder<AsyncMongoDatastore> builder() {
		// return new DefaultJdbcDatastore.DefaultBuilder();
		// TODO
		return null;
	}

	/**
	 * {@link AsyncMongoDatastore} builder.
	 * @param <D> {@link AsyncMongoDatastore} type
	 */
	public interface Builder<D extends AsyncMongoDatastore> extends DatastoreOperations.Builder<D, Builder<D>> {

		/**
		 * Set the {@link MongoClient} to use.
		 * @param client MongoClient to set (not null)
		 * @return this
		 */
		Builder<D> client(MongoClient client);

		/**
		 * Set the database name to use.
		 * @param database The database name to set (not null)
		 * @return this
		 */
		Builder<D> database(String database);

		/**
		 * Register a {@link SyncMongoDatastoreCommodityFactory}.
		 * @param <C> Commodity type
		 * @param commodityFactory The factory to register (not null)
		 * @return this
		 */
		<C extends DatastoreCommodity> Builder<D> withCommodity(
				AsyncMongoDatastoreCommodityFactory<C> commodityFactory);

	}

}
