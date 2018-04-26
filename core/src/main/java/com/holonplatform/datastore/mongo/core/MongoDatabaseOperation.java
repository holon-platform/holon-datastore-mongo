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
package com.holonplatform.datastore.mongo.core;

/**
 * Represents an operation to be executed using a Datastore managed <code>MongoDatabase</code>.
 * 
 * @param <MongoDatabase> Actual MongoDatabase type
 * @param <R> Operation result type
 * 
 * @since 5.2.0
 */
@FunctionalInterface
public interface MongoDatabaseOperation<MongoDatabase, R> {

	/**
	 * Execute an operation using the provided MongoDatabase an return a result.
	 * @param database The MongoDatabase reference
	 * @return Operation result
	 * @throws Exception If an error occurred
	 */
	R execute(MongoDatabase database) throws Exception;

}
