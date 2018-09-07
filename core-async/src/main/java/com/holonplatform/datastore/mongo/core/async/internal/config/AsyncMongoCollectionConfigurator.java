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
package com.holonplatform.datastore.mongo.core.async.internal.config;

import java.util.Optional;

import com.holonplatform.core.ParameterSet;
import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.datastore.mongo.core.ReadOperationConfiguration;
import com.holonplatform.datastore.mongo.core.WriteConcernOption;
import com.holonplatform.datastore.mongo.core.context.MongoContext;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.client.MongoCollection;

/**
 * Helper class to configure the Mongo Datastore operations.
 *
 * @since 5.2.0
 */
public final class AsyncMongoCollectionConfigurator {

	private AsyncMongoCollectionConfigurator() {
	}

	/**
	 * Configure a write operation, setting the {@link WriteConcern} if available from write options or context.
	 * @param <T> Collection type
	 * @param collection Mongo collection
	 * @param context Mongo context
	 * @param operation Operation configuration
	 * @return The configured collection
	 */
	public static <T> MongoCollection<T> configureWrite(MongoCollection<T> collection, MongoContext<?> context,
			DatastoreOperationConfiguration operation) {

		// check write option
		Optional<WriteConcern> wc = operation.getWriteOption(WriteConcernOption.class).map(wo -> wo.getWriteConcern());
		if (!wc.isPresent()) {
			// use default if available
			wc = context.getDefaultWriteConcern();
		}

		return wc.map(wrc -> collection.withWriteConcern(wrc)).orElse(collection);

	}

	/**
	 * Configure a read operation, setting the {@link ReadConcern} and the {@link ReadPreference} if available from
	 * write options or context.
	 * @param <T> Collection type
	 * @param collection Mongo collection
	 * @param context Mongo context
	 * @param operationParameters Operation configuration parameters
	 * @return The configured collection
	 */
	public static <T> MongoCollection<T> configureRead(MongoCollection<T> collection, MongoContext<?> context,
			ParameterSet operationParameters) {

		if (operationParameters == null) {
			return collection;
		}

		// check read concern
		Optional<ReadConcern> rc = operationParameters.getParameter(ReadOperationConfiguration.READ_CONCERN);
		if (!rc.isPresent()) {
			// use default if available
			rc = context.getDefaultReadConcern();
		}

		MongoCollection<T> c = rc.map(rdc -> collection.withReadConcern(rdc)).orElse(collection);

		// check read preference
		Optional<ReadPreference> rp = operationParameters.getParameter(ReadOperationConfiguration.READ_PREFERENCE);
		if (!rp.isPresent()) {
			// use default if available
			rp = context.getDefaultReadPreference();
		}

		return rp.map(rdp -> c.withReadPreference(rdp)).orElse(c);

	}

}
