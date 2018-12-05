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

import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.context.MongoContext;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoCollection;

/**
 * Defaut {@link AsyncOperationContext} implementation.
 *
 * @param <C> Context type
 *
 * @since 5.2.0
 */
public class DefaultAsyncOperationContext<C extends MongoContext<ClientSession>> implements AsyncOperationContext<C> {

	private final C context;
	private final MongoCollection<Document> collection;
	private final DatastoreOperationConfiguration configuration;
	private final Bson filter;

	public DefaultAsyncOperationContext(C mongoContext, MongoCollection<Document> collection,
			DatastoreOperationConfiguration configuration, Bson filter) {
		super();
		ObjectUtils.argumentNotNull(mongoContext, "MongoContext must be not null");
		ObjectUtils.argumentNotNull(collection, "Collection must be not null");
		ObjectUtils.argumentNotNull(configuration, "Operation configuration must be not null");
		this.context = mongoContext;
		this.collection = collection;
		this.configuration = configuration;
		this.filter = filter;
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

	/* (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.async.internal.support.AsyncOperationContext#getFilter()
	 */
	@Override
	public Optional<Bson> getFilter() {
		return Optional.ofNullable(filter);
	}

}
