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
import com.holonplatform.async.datastore.transaction.AsyncTransactional;
import com.holonplatform.datastore.mongo.async.internal.DefaultAsyncMongoDatastore;
import com.holonplatform.datastore.mongo.async.tx.AsyncMongoTransaction;
import com.holonplatform.datastore.mongo.core.async.BaseAsyncMongoDatastore;

/**
 * MongoDB {@link AsyncDatastore} implementation.
 *
 * @since 5.2.0
 */
public interface AsyncMongoDatastore extends BaseAsyncMongoDatastore, AsyncDatastore, AsyncTransactional {

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
	public interface Builder
			extends BaseAsyncMongoDatastore.Builder<AsyncMongoDatastore, AsyncMongoTransaction, Builder> {

	}

}
