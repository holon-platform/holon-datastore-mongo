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
package com.holonplatform.datastore.mongo.core.async.internal.support;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.context.MongoContext;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoCollection;

/**
 * Defaut {@link AsyncOperationResultContext} implementation.
 *
 * @param <C> Context type
 *
 * @since 5.2.0
 */
public class DefaultAsyncOperationResultContext<C extends MongoContext<ClientSession>>
		extends DefaultAsyncOperationContext<C> implements AsyncOperationResultContext<C> {

	private final long affectedCount;
	private final OperationType operationType;

	public DefaultAsyncOperationResultContext(C mongoContext, MongoCollection<Document> collection,
			DatastoreOperationConfiguration configuration, long affectedCount, OperationType operationType) {
		this(mongoContext, collection, configuration, null, affectedCount, operationType);
	}

	public DefaultAsyncOperationResultContext(C mongoContext, MongoCollection<Document> collection,
			DatastoreOperationConfiguration configuration, Bson filter, long affectedCount,
			OperationType operationType) {
		super(mongoContext, collection, configuration, filter);
		ObjectUtils.argumentNotNull(operationType, "OperationType must be not null");
		this.affectedCount = affectedCount;
		this.operationType = operationType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.AsyncOperationResultContext#getAffectedCount()
	 */
	@Override
	public long getAffectedCount() {
		return affectedCount;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.AsyncOperationResultContext#getOperationType()
	 */
	@Override
	public OperationType getOperationType() {
		return operationType;
	}

}
