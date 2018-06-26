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
package com.holonplatform.datastore.mongo.sync.internal.configurator;

import java.util.concurrent.TimeUnit;

import org.bson.conversions.Bson;

import com.holonplatform.datastore.mongo.core.internal.operation.DistinctOperationConfigurator;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.model.Collation;

/**
 * Default {@link DistinctOperationConfigurator} implementation.
 *
 * @since 5.2.0
 */
public class SyncDistinctOperationConfigurator implements DistinctOperationConfigurator {

	private final DistinctIterable<?> iterable;

	public SyncDistinctOperationConfigurator(DistinctIterable<?> iterable) {
		super();
		this.iterable = iterable;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.operation.QueryOperationConfigurator#filter(org.bson.conversions.
	 * Bson)
	 */
	@Override
	public DistinctOperationConfigurator filter(Bson filter) {
		iterable.filter(filter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.QueryOperationConfigurator#maxTime(long,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public DistinctOperationConfigurator maxTime(long maxTime, TimeUnit timeUnit) {
		iterable.maxTime(maxTime, timeUnit);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.QueryOperationConfigurator#batchSize(int)
	 */
	@Override
	public DistinctOperationConfigurator batchSize(int batchSize) {
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
	public DistinctOperationConfigurator collation(Collation collation) {
		iterable.collation(collation);
		return this;
	}

}
