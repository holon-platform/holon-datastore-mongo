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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoCollection;

/**
 * Default {@link QueryOperationContext} implementation.
 *
 * @param <R> Query result type
 *
 * @since 5.2.0
 */
public class DefaultQueryOperationContext<R> implements QueryOperationContext<R> {

	private final MongoResolutionContext<ClientSession> resolutionContext;
	private final MongoCollection<Document> collection;
	private final BsonQuery query;
	private final Class<? extends R> resultType;

	public DefaultQueryOperationContext(MongoResolutionContext<ClientSession> resolutionContext, MongoCollection<Document> collection,
			BsonQuery query, Class<? extends R> resultType) {
		super();
		ObjectUtils.argumentNotNull(resolutionContext, "Resolution context must be not null");
		ObjectUtils.argumentNotNull(collection, "MongoCollection must be not null");
		ObjectUtils.argumentNotNull(query, "Query must be not null");
		ObjectUtils.argumentNotNull(resultType, "Query result type must be not null");
		this.resolutionContext = resolutionContext;
		this.collection = collection;
		this.query = query;
		this.resultType = resultType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.QueryOperationContext#getResolutionContext()
	 */
	@Override
	public MongoResolutionContext<ClientSession> getResolutionContext() {
		return resolutionContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.QueryOperationContext#getCollection()
	 */
	@Override
	public MongoCollection<Document> getCollection() {
		return collection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.QueryOperationContext#getQuery()
	 */
	@Override
	public BsonQuery getQuery() {
		return query;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.QueryOperationContext#getResultType()
	 */
	@Override
	public Class<? extends R> getResultType() {
		return resultType;
	}

}
