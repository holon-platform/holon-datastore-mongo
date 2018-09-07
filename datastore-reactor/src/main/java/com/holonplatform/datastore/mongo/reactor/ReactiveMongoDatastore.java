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
package com.holonplatform.datastore.mongo.reactor;

import com.holonplatform.datastore.mongo.core.async.BaseAsyncMongoDatastore;
import com.holonplatform.datastore.mongo.reactor.internal.DefaultReactiveMongoDatastore;
import com.holonplatform.datastore.mongo.reactor.tx.ReactiveMongoTransaction;
import com.holonplatform.reactor.datastore.ReactiveDatastore;
import com.holonplatform.reactor.datastore.transaction.ReactiveTransactional;

/**
 * MongoDB {@link ReactiveDatastore} implementation, using Project Reactor APIs.
 *
 * @since 5.2.0
 */
public interface ReactiveMongoDatastore extends BaseAsyncMongoDatastore, ReactiveDatastore, ReactiveTransactional {

	/**
	 * Get a builder to create a {@link ReactiveMongoDatastore} instance.
	 * @return Datastore builder
	 */
	static Builder builder() {
		return new DefaultReactiveMongoDatastore.DefaultBuilder();
	}

	/**
	 * {@link ReactiveDatastore} builder.
	 */
	public interface Builder
			extends BaseAsyncMongoDatastore.Builder<ReactiveMongoDatastore, ReactiveMongoTransaction, Builder> {

	}

}
