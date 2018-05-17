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
package com.holonplatform.datastore.mongo.sync;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.DatastoreCommodity;
import com.holonplatform.core.datastore.DatastoreCommodityRegistrar;
import com.holonplatform.datastore.mongo.core.MongoDatabaseHandler;
import com.holonplatform.datastore.mongo.core.MongoDatastoreBuilder;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityFactory;
import com.holonplatform.datastore.mongo.sync.internal.DefaultMongoDatastore;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDB {@link Datastore} implementation.
 *
 * @since 5.2.0
 */
public interface MongoDatastore extends Datastore, MongoDatabaseHandler<MongoDatabase>,
		DatastoreCommodityRegistrar<SyncMongoDatastoreCommodityContext> {

	/**
	 * Get a builder to create a {@link MongoDatastore} instance.
	 * @return Datastore builder
	 */
	static Builder builder() {
		return new DefaultMongoDatastore.DefaultBuilder();
	}

	/**
	 * {@link MongoDatastore} builder.
	 * @param <D> {@link MongoDatastore} type
	 */
	public interface Builder extends MongoDatastoreBuilder<MongoDatastore, Builder> {

		/**
		 * Set the {@link MongoClient} to use.
		 * @param client MongoClient to set (not null)
		 * @return this
		 */
		Builder client(MongoClient client);

		/**
		 * Set the database name to use.
		 * @param database The database name to set (not null)
		 * @return this
		 */
		Builder database(String database);

		/**
		 * Register a {@link SyncMongoDatastoreCommodityFactory}.
		 * @param <C> Commodity type
		 * @param commodityFactory The factory to register (not null)
		 * @return this
		 */
		<C extends DatastoreCommodity> Builder withCommodity(SyncMongoDatastoreCommodityFactory<C> commodityFactory);

	}

}
