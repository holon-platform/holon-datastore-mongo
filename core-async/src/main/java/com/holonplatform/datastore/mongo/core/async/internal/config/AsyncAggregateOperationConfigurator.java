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

import java.util.concurrent.TimeUnit;

import org.bson.conversions.Bson;

import com.holonplatform.datastore.mongo.core.internal.operation.AggregateOperationConfigurator;
import com.mongodb.async.client.AggregateIterable;
import com.mongodb.client.model.Collation;

/**
 * Default {@link AggregateOperationConfigurator} implementation.
 *
 * @since 5.2.0
 */
public class AsyncAggregateOperationConfigurator implements AggregateOperationConfigurator {

	private final AggregateIterable<?> iterable;

	public AsyncAggregateOperationConfigurator(AggregateIterable<?> iterable) {
		super();
		this.iterable = iterable;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.QueryOperationConfigurator#maxTime(long,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public AggregateOperationConfigurator maxTime(long maxTime, TimeUnit timeUnit) {
		iterable.maxTime(maxTime, timeUnit);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.QueryOperationConfigurator#batchSize(int)
	 */
	@Override
	public AggregateOperationConfigurator batchSize(int batchSize) {
		iterable.batchSize(batchSize);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.operation.QueryOperationConfigurator#collation(com.mongodb.client
	 * .model.Collation)
	 */
	@Override
	public AggregateOperationConfigurator collation(Collation collation) {
		iterable.collation(collation);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#comment(java.lang.String)
	 */
	@Override
	public AggregateOperationConfigurator comment(String comment) {
		iterable.comment(comment);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#hint(org.bson.conversions.
	 * Bson)
	 */
	@Override
	public AggregateOperationConfigurator hint(Bson hint) {
		iterable.hint(hint);
		return this;
	}

}
