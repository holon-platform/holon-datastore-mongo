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
package com.holonplatform.datastore.mongo.async.internal.support;

import org.bson.Document;

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
public class DefaultAsyncOperationResultContext<C extends MongoContext<ClientSession>> implements AsyncOperationResultContext<C> {

	private final C context;
	private final MongoCollection<Document> collection;
	private final DatastoreOperationConfiguration configuration;
	private final long affectedCount;
	private final OperationType operationType;

	public DefaultAsyncOperationResultContext(C mongoContext, MongoCollection<Document> collection,
			DatastoreOperationConfiguration configuration, long affectedCount, OperationType operationType) {
		super();
		ObjectUtils.argumentNotNull(mongoContext, "MongoContext must be not null");
		ObjectUtils.argumentNotNull(collection, "Collection must be not null");
		ObjectUtils.argumentNotNull(configuration, "Operation configuration must be not null");
		ObjectUtils.argumentNotNull(operationType, "OperationType must be not null");
		this.context = mongoContext;
		this.collection = collection;
		this.configuration = configuration;
		this.affectedCount = affectedCount;
		this.operationType = operationType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.AsyncOperationResultContext#getMongoContext()
	 */
	@Override
	public C getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.AsyncOperationResultContext#getCollection()
	 */
	@Override
	public MongoCollection<Document> getCollection() {
		return collection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.AsyncOperationResultContext#getConfiguration()
	 */
	@Override
	public DatastoreOperationConfiguration getConfiguration() {
		return configuration;
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
