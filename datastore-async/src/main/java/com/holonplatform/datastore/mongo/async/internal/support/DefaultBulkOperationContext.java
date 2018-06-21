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

import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

/**
 * Default {@link BulkOperationContext} implementation.
 *
 * @since 5.2.0
 */
public class DefaultBulkOperationContext extends AbstractAsyncOperationContext implements BulkOperationContext {

	private final DatastoreOperationConfiguration configuration;
	private final MongoResolutionContext resolutionContext;
	private final MongoCollection<Document> collection;
	private final Bson filter;

	public DefaultBulkOperationContext(MongoOperationContext<MongoDatabase> operationContext,
			DatastoreOperationConfiguration configuration, MongoResolutionContext resolutionContext,
			MongoCollection<Document> collection, Bson filter) {
		super(operationContext);
		ObjectUtils.argumentNotNull(configuration, "Operation configuration must be not null");
		ObjectUtils.argumentNotNull(resolutionContext, "Resolution context must be not null");
		ObjectUtils.argumentNotNull(collection, "MongoCollection must be not null");
		this.configuration = configuration;
		this.resolutionContext = resolutionContext;
		this.collection = collection;
		this.filter = filter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.BulkOperationContext#getConfiguration()
	 */
	@Override
	public DatastoreOperationConfiguration getConfiguration() {
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.BulkOperationContext#getResolutionContext()
	 */
	@Override
	public MongoResolutionContext getResolutionContext() {
		return resolutionContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.BulkOperationContext#getCollection()
	 */
	@Override
	public MongoCollection<Document> getCollection() {
		return collection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.BulkOperationContext#getFilter()
	 */
	@Override
	public Optional<Bson> getFilter() {
		return Optional.ofNullable(filter);
	}

}
