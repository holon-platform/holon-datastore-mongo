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
package com.holonplatform.datastore.mongo.sync.config;

import com.holonplatform.core.datastore.DatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.config.MongoDatastoreCommodityContext;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Synchronous MongoDB Datastore {@link DatastoreCommodityContext}.
 *
 * @since 5.2.0
 */
public interface SyncMongoDatastoreCommodityContext
		extends MongoDatastoreCommodityContext<MongoDatabase, ClientSession> {

	/**
	 * Get the MongoDB client.
	 * @return the MongoDB client
	 */
	MongoClient getClient();

}
