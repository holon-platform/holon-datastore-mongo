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
import org.bson.types.ObjectId;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.mongodb.async.client.MongoCollection;

/**
 * Document and collection aware {@link PropertyBoxOperationContext}.
 * 
 * @since 5.2.0
 */
public interface DocumentOperationContext extends PropertyBoxOperationContext {

	/**
	 * Get the mongo collection reference.
	 * @return Mongo collection
	 */
	MongoCollection<Document> getCollection();

	/**
	 * Get the {@link Document} bound to this context, if available.
	 * @return Optional document
	 */
	Optional<Document> getDocument();

	/**
	 * Get the document id as {@link ObjectId}.
	 * @return Optional document id
	 */
	Optional<ObjectId> getDocumentId();

	/**
	 * Set the document id.
	 * @param id Document id
	 */
	void setDocumentId(ObjectId id);

	/**
	 * Get the {@link Document} bound to this context, throwing an {@link IllegalStateException} if not available.
	 * @return The Document
	 */
	default Document requireDocument() {
		return getDocument().orElseThrow(
				() -> new IllegalStateException("Failed to resolve a Document from this context: " + this));
	}

	/**
	 * Create a new {@link DocumentOperationContext}.
	 * @param parent Parent context (not null)
	 * @param collection Mongo collection (not null)
	 * @return A new {@link DocumentOperationContext} instance
	 */
	static DocumentOperationContext create(PropertyBoxOperationContext parent, MongoCollection<Document> collection) {
		ObjectUtils.argumentNotNull(parent, "Parent context must be not null");
		return new DefaultDocumentOperationContext(parent, collection);
	}

	/**
	 * Create a new {@link DocumentOperationContext}.
	 * @param parent Parent context (not null)
	 * @param collection Mongo collection (not null)
	 * @param document The {@link Document} value
	 * @return A new {@link DocumentOperationContext} instance
	 */
	static DocumentOperationContext create(PropertyBoxOperationContext parent, MongoCollection<Document> collection,
			Document document) {
		ObjectUtils.argumentNotNull(parent, "Parent context must be not null");
		return new DefaultDocumentOperationContext(parent, collection, document);
	}

}
