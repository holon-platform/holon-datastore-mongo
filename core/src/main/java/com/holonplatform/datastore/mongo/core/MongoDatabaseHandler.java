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
 * Mongo Database handler.
 * 
 * @param <MongoDatabase> Actual MongoDatabase type (sync or async)
 *
 * @since 5.2.0
 */
public interface MongoDatabaseHandler<MongoDatabase> {

	/**
	 * Execute given {@link MongoDatabaseOperation} using a managed MongoDatabase.
	 * @param <R> Operation result type
	 * @param operation Operation to execute (not null)
	 * @return Operation result
	 */
	<R> R withConnection(MongoDatabaseOperation<MongoDatabase, R> operation);

	/**
	 * Execute given {@link MongoDatabaseRunnable} operation using a managed MongoDatabase.
	 * @param operation Operation to execute (not null)
	 */
	default void withConnection(MongoDatabaseRunnable<MongoDatabase> operation) {
		withConnection(connection -> {
			operation.execute(connection);
			return null;
		});
	}

}
