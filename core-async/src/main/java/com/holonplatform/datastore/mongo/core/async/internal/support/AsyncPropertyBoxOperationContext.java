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
import org.bson.types.ObjectId;

import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoCollection;

/**
 * Async {@link PropertyBox} operation context.
 * 
 * @param <C> Context type
 *
 * @since 5.2.0
 */
public interface AsyncPropertyBoxOperationContext extends AsyncOperationContext<MongoDocumentContext<ClientSession>> {

	/**
	 * Get the {@link PropertyBox} value.
	 * @return The value
	 */
	PropertyBox getValue();

	/**
	 * Get the document id, if available.
	 * @return Optional document id
	 */
	Optional<ObjectId> getDocumentId();

	static AsyncPropertyBoxOperationContext create(MongoDocumentContext<ClientSession> mongoContext,
			MongoCollection<Document> collection, DatastoreOperationConfiguration configuration, PropertyBox value,
			ObjectId documentId) {
		return new DefaultAsyncPropertyBoxOperationContext(mongoContext, collection, configuration, value, documentId);
	}

}
