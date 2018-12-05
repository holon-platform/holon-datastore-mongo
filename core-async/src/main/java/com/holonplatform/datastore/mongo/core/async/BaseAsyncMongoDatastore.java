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
package com.holonplatform.datastore.mongo.core.async;

import com.holonplatform.core.datastore.DatastoreCommodity;
import com.holonplatform.core.datastore.DatastoreCommodityRegistrar;
import com.holonplatform.core.datastore.DatastoreOperations;
import com.holonplatform.datastore.mongo.core.MongoDatabaseHandler;
import com.holonplatform.datastore.mongo.core.MongoDatastoreBuilder;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityFactory;
import com.holonplatform.datastore.mongo.core.tx.MongoTransaction;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;

/**
 * MongoDB asynchronous Datastore implementations base interface.
 *
 * @since 5.2.0
 */
public interface BaseAsyncMongoDatastore
		extends MongoDatabaseHandler<MongoDatabase>, DatastoreCommodityRegistrar<AsyncMongoDatastoreCommodityContext> {

	/**
	 * Base asynchronous Datastore builder.
	 * 
	 * @param <D> {@link DatastoreOperations} type
	 * @param <TX> Concrete transaction type
	 * @param <B> Concrete builder type
	 */
	@SuppressWarnings("rawtypes")
	public interface Builder<D extends DatastoreOperations, TX extends MongoTransaction<ClientSession>, B extends Builder<D, TX, B>>
			extends MongoDatastoreBuilder<D, ClientSession, TX, B> {

		/**
		 * Set the {@link MongoClient} to use.
		 * @param client MongoClient to set (not null)
		 * @return this
		 */
		B client(MongoClient client);

		/**
		 * Register a {@link AsyncMongoDatastoreCommodityFactory}.
		 * @param <C> Commodity type
		 * @param commodityFactory The factory to register (not null)
		 * @return this
		 */
		<C extends DatastoreCommodity> B withCommodity(AsyncMongoDatastoreCommodityFactory<C> commodityFactory);

	}

}
