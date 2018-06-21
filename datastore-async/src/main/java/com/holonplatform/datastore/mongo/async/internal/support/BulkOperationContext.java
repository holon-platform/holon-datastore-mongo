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
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

/**
 * Bulk operation context.
 *
 * @since 5.2.0
 */
public interface BulkOperationContext extends AsyncOperationContext {

	/**
	 * Get the operation configuration.
	 * @return Operation configuration
	 */
	DatastoreOperationConfiguration getConfiguration();

	/**
	 * Get the resolution context.
	 * @return Resolution context
	 */
	MongoResolutionContext getResolutionContext();

	/**
	 * Get the mongo collection reference.
	 * @return Mongo collection
	 */
	MongoCollection<Document> getCollection();

	/**
	 * Get the optional operation filter.
	 * @return Optional operation filter
	 */
	Optional<Bson> getFilter();

	static BulkOperationContext create(MongoOperationContext<MongoDatabase> operationContext,
			DatastoreOperationConfiguration configuration, MongoResolutionContext resolutionContext,
			MongoCollection<Document> collection, Bson filter) {
		return new DefaultBulkOperationContext(operationContext, configuration, resolutionContext, collection, filter);
	}

}
