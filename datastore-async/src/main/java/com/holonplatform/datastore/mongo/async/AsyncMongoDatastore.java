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

import com.holonplatform.async.datastore.AsyncDatastore;
import com.holonplatform.core.datastore.DatastoreCommodity;
import com.holonplatform.core.datastore.DatastoreCommodityRegistrar;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityFactory;
import com.holonplatform.datastore.mongo.async.internal.DefaultAsyncMongoDatastore;
import com.holonplatform.datastore.mongo.core.MongoDatabaseHandler;
import com.holonplatform.datastore.mongo.core.MongoDatastoreBuilder;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoDatabase;

/**
 * MongoDB {@link AsyncDatastore} implementation.
 *
 * @since 5.2.0
 */
public interface AsyncMongoDatastore extends AsyncDatastore, MongoDatabaseHandler<MongoDatabase>,
		DatastoreCommodityRegistrar<AsyncMongoDatastoreCommodityContext> {

	/**
	 * Get a builder to create a {@link AsyncMongoDatastore} instance.
	 * @return Datastore builder
	 */
	static Builder builder() {
		return new DefaultAsyncMongoDatastore.DefaultBuilder();
	}

	/**
	 * {@link AsyncMongoDatastore} builder.
	 */
	public interface Builder extends MongoDatastoreBuilder<AsyncMongoDatastore, Builder> {

		/**
		 * Set the {@link MongoClient} to use.
		 * @param client MongoClient to set (not null)
		 * @return this
		 */
		Builder client(MongoClient client);

		/**
		 * Register a {@link AsyncMongoDatastoreCommodityFactory}.
		 * @param <C> Commodity type
		 * @param commodityFactory The factory to register (not null)
		 * @return this
		 */
		<C extends DatastoreCommodity> Builder withCommodity(AsyncMongoDatastoreCommodityFactory<C> commodityFactory);

	}

}
