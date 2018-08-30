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
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoCollection;

/**
 * Default {@link AsyncPropertyBoxOperationResultContext} implementation.
 *
 * @since 5.2.0
 */
public class DefaultAsyncPropertyBoxOperationResultContext extends
		DefaultAsyncOperationResultContext<MongoDocumentContext<ClientSession>> implements AsyncPropertyBoxOperationResultContext {

	private final PropertyBox value;
	private final Document document;
	private final ObjectId documentId;
	private final BsonValue upsertedId;

	public DefaultAsyncPropertyBoxOperationResultContext(MongoDocumentContext<ClientSession> mongoContext,
			MongoCollection<Document> collection, DatastoreOperationConfiguration configuration, long affectedCount,
			OperationType operationType, PropertyBox value, Document document, ObjectId documentId,
			BsonValue upsertedId) {
		super(mongoContext, collection, configuration, affectedCount, operationType);
		ObjectUtils.argumentNotNull(value, "PropertyBox value must be not null");
		this.value = value;
		this.document = document;
		this.documentId = documentId;
		this.upsertedId = upsertedId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.AsyncPropertyBoxOperationResultContext#getValue()
	 */
	@Override
	public PropertyBox getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.async.internal.support.AsyncPropertyBoxOperationResultContext#getDocument()
	 */
	@Override
	public Optional<Document> getDocument() {
		return Optional.ofNullable(document);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.async.internal.support.AsyncPropertyBoxOperationResultContext#getDocumentId()
	 */
	@Override
	public Optional<ObjectId> getDocumentId() {
		return Optional.ofNullable(documentId);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.async.internal.support.AsyncPropertyBoxOperationResultContext#getUpsertedId()
	 */
	@Override
	public Optional<BsonValue> getUpsertedId() {
		return Optional.ofNullable(upsertedId);
	}

}
