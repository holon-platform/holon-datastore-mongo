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

import com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator;
import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Collation;

/**
 * Default {@link FindOperationConfigurator} implementation.
 *
 * @since 5.2.0
 */
public class SyncFindOperationConfigurator implements FindOperationConfigurator {

	private final FindIterable<?> iterable;

	public SyncFindOperationConfigurator(FindIterable<?> iterable) {
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
	public FindOperationConfigurator filter(Bson filter) {
		iterable.filter(filter);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.QueryOperationConfigurator#maxTime(long,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public FindOperationConfigurator maxTime(long maxTime, TimeUnit timeUnit) {
		iterable.maxTime(maxTime, timeUnit);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.QueryOperationConfigurator#batchSize(int)
	 */
	@Override
	public FindOperationConfigurator batchSize(int batchSize) {
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
	public FindOperationConfigurator collation(Collation collation) {
		iterable.collation(collation);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#sort(org.bson.conversions.
	 * Bson)
	 */
	@Override
	public FindOperationConfigurator sort(Bson sort) {
		iterable.sort(sort);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#projection(org.bson.
	 * conversions.Bson)
	 */
	@Override
	public FindOperationConfigurator projection(Bson projection) {
		iterable.projection(projection);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#limit(int)
	 */
	@Override
	public FindOperationConfigurator limit(int limit) {
		iterable.limit(limit);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#skip(int)
	 */
	@Override
	public FindOperationConfigurator skip(int skip) {
		iterable.skip(skip);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#partial(boolean)
	 */
	@Override
	public FindOperationConfigurator partial(boolean partial) {
		iterable.partial(partial);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#cursorType(com.mongodb.
	 * CursorType)
	 */
	@Override
	public FindOperationConfigurator cursorType(CursorType cursorType) {
		iterable.cursorType(cursorType);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#comment(java.lang.String)
	 */
	@Override
	public FindOperationConfigurator comment(String comment) {
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
	public FindOperationConfigurator hint(Bson hint) {
		iterable.hint(hint);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#max(org.bson.conversions.
	 * Bson)
	 */
	@Override
	public FindOperationConfigurator max(Bson max) {
		iterable.max(max);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#min(org.bson.conversions.
	 * Bson)
	 */
	@Override
	public FindOperationConfigurator min(Bson min) {
		iterable.min(min);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#returnKey(boolean)
	 */
	@Override
	public FindOperationConfigurator returnKey(boolean returnKey) {
		iterable.returnKey(returnKey);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.operation.FindOperationConfigurator#showRecordId(boolean)
	 */
	@Override
	public FindOperationConfigurator showRecordId(boolean showRecordId) {
		iterable.showRecordId(showRecordId);
		return this;
	}

}
