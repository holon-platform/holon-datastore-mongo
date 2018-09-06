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

import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoCollection;

/**
 * {@link AsyncOperationResultContext} for operations which involve a {@link PropertyBox} value.
 *
 * @since 5.2.0
 */
public interface AsyncPropertyBoxOperationResultContext
		extends AsyncOperationResultContext<MongoDocumentContext<ClientSession>> {

	/**
	 * Get the {@link PropertyBox} value.
	 * @return The value
	 */
	PropertyBox getValue();

	/**
	 * Get the {@link Document} which represents the value, if available.
	 * @return Optional document
	 */
	Optional<Document> getDocument();

	/**
	 * Get the document id, if available.
	 * @return Optional document id
	 */
	Optional<ObjectId> getDocumentId();

	/**
	 * If the performed operation was an upsert, returns the upserted document id.
	 * @return Optional upserted document id
	 */
	Optional<BsonValue> getUpsertedId();

	/**
	 * Get the {@link Document} which represents the value, throwing an {@link IllegalStateException} if not available.
	 * @return The Document
	 */
	default Document requireDocument() {
		return getDocument().orElseThrow(() -> new IllegalStateException("Document is not available"));
	}

	static AsyncPropertyBoxOperationResultContext create(MongoDocumentContext<ClientSession> mongoContext,
			MongoCollection<Document> collection, DatastoreOperationConfiguration configuration, long affectedCount,
			OperationType operationType, PropertyBox value, Document document) {
		return new DefaultAsyncPropertyBoxOperationResultContext(mongoContext, collection, configuration, affectedCount,
				operationType, value, document, null, null);
	}

	static AsyncPropertyBoxOperationResultContext create(MongoDocumentContext<ClientSession> mongoContext,
			MongoCollection<Document> collection, DatastoreOperationConfiguration configuration, long affectedCount,
			OperationType operationType, PropertyBox value, Document document, ObjectId documentId) {
		return new DefaultAsyncPropertyBoxOperationResultContext(mongoContext, collection, configuration, affectedCount,
				operationType, value, document, documentId, null);
	}

	static AsyncPropertyBoxOperationResultContext create(MongoDocumentContext<ClientSession> mongoContext,
			MongoCollection<Document> collection, DatastoreOperationConfiguration configuration, long affectedCount,
			OperationType operationType, PropertyBox value, Document document, BsonValue upsertedId) {
		return new DefaultAsyncPropertyBoxOperationResultContext(mongoContext, collection, configuration, affectedCount,
				operationType, value, document, null, upsertedId);
	}

}
