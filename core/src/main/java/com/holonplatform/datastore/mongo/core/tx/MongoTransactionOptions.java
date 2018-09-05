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
package com.holonplatform.datastore.mongo.core.tx;

import java.util.Optional;

import com.holonplatform.core.datastore.transaction.TransactionOptions;
import com.holonplatform.datastore.mongo.core.enumerations.MongoReadConcern;
import com.holonplatform.datastore.mongo.core.enumerations.MongoReadPreference;
import com.holonplatform.datastore.mongo.core.enumerations.MongoWriteConcern;

/**
 * {@link MongoTransaction} configuration options.
 *
 * @since 5.2.0
 */
public interface MongoTransactionOptions extends TransactionOptions {

	/**
	 * Get the read concern to use with the transaction, if configured.
	 * @return Optional transaction read concern
	 */
	Optional<MongoReadConcern> getReadConcern();

	/**
	 * Get the write concern to use with the transaction, if configured.
	 * @return Optional transaction write concern
	 */
	Optional<MongoWriteConcern> getWriteConcern();

	/**
	 * Get the read preference to use with the transaction, if configured.
	 * @return Optional transaction read preference
	 */
	Optional<MongoReadPreference> getReadPreference();

}
