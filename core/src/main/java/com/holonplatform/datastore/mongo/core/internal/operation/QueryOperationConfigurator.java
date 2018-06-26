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
package com.holonplatform.datastore.mongo.core.internal.operation;

import java.util.concurrent.TimeUnit;

import com.mongodb.client.model.Collation;

/**
 * Generic interface to configure a query operation.
 * 
 * @param <C> Concrete configurator
 *
 * @since 5.2.0
 */
public interface QueryOperationConfigurator<C extends QueryOperationConfigurator<C>> {

	/**
	 * Sets the maximum execution time on the server for this operation.
	 * @param maxTime the max time
	 * @param timeUnit the time unit, which may not be null
	 * @return this
	 */
	C maxTime(long maxTime, TimeUnit timeUnit);

	/**
	 * Sets the number of documents to return per batch.
	 * @param batchSize the batch size
	 * @return this
	 */
	C batchSize(int batchSize);

	/**
	 * Sets the collation options.
	 * <p>
	 * A null value represents the server default.
	 * </p>
	 * @param collation the collation options to use
	 * @return this
	 */
	C collation(Collation collation);

}
