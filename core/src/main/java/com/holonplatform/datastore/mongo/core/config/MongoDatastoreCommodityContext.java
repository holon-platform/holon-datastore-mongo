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
package com.holonplatform.datastore.mongo.core.config;

import com.holonplatform.core.datastore.DatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.mongodb.session.ClientSession;

/**
 * Base MongoDB Datastore {@link DatastoreCommodityContext}.
 *
 * @param <MongoDatabase> Actual MongoDatabase type (sync or async)
 * @param <S> Concrete ClientSession type
 *
 * @since 5.2.0
 */
public interface MongoDatastoreCommodityContext<MongoDatabase, S extends ClientSession>
		extends MongoOperationContext<MongoDatabase, S>, DatastoreCommodityContext {

	/**
	 * Get the MongoDB database name to which the Datastore is bound.
	 * @return MongoDB database name
	 */
	String getDatabaseName();

}
