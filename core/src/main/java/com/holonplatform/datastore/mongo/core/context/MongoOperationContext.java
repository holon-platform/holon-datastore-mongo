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
package com.holonplatform.datastore.mongo.core.context;

import com.holonplatform.core.datastore.DatastoreCommodityHandler;
import com.holonplatform.datastore.mongo.core.MongoDatabaseHandler;

/**
 * MongoDB Datastore operations execution context.
 * 
 * @param <MongoDatabase> Actual MongoDatabase type (sync or async)
 *
 * @since 5.2.0
 */
public interface MongoOperationContext<MongoDatabase>
		extends MongoContext, MongoDatabaseHandler<MongoDatabase>, DatastoreCommodityHandler {

	/**
	 * Gets whether the MongoDB driver in use is asynchronous.
	 * @return <code>true</code> if in asynchronous mode, <code>false</code> if in synchronous mode
	 */
	boolean isAsync();

}
