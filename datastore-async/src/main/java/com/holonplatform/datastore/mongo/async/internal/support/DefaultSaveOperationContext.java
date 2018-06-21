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

import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.operation.commons.PropertyBoxOperationConfiguration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

/**
 * Default {@link SaveOperationContext} implementation.
 *
 * @since 5.2.0
 */
public class DefaultSaveOperationContext extends DefaultPropertyBoxOperationContext implements SaveOperationContext {

	private final MongoCollection<Document> collection;
	private final OperationType operationType;
	private final BsonValue upsertedId;
	private final Document document;

	public DefaultSaveOperationContext(PropertyBoxOperationConfiguration configuration, MongoOperationContext<MongoDatabase> operationContext,
			MongoDocumentContext documentContext, MongoCollection<Document> collection, OperationType operationType, long affectedCount) {
		this(configuration, operationContext, documentContext, collection, operationType, affectedCount, null, null);
	}

	public DefaultSaveOperationContext(PropertyBoxOperationConfiguration configuration, MongoOperationContext<MongoDatabase> operationContext,
			MongoDocumentContext documentContext, MongoCollection<Document> collection, OperationType operationType, long affectedCount, BsonValue upsertedId,
			Document document) {
		super(configuration, operationContext, documentContext);
		ObjectUtils.argumentNotNull(collection, "MongoCollection must be not null");
		ObjectUtils.argumentNotNull(operationType, "OperationType must be not null");
		this.collection = collection;
		this.operationType = operationType;
		this.upsertedId = upsertedId;
		this.document = document;
		setAffectedCount(affectedCount);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.SaveOperationContext#getCollection()
	 */
	@Override
	public MongoCollection<Document> getCollection() {
		return collection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.SaveOperationContext#getOperationType()
	 */
	@Override
	public OperationType getOperationType() {
		return operationType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.SaveOperationContext#getUpsertedId()
	 */
	@Override
	public Optional<BsonValue> getUpsertedId() {
		return Optional.ofNullable(upsertedId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.SaveOperationContext#getDocument()
	 */
	@Override
	public Optional<Document> getDocument() {
		return Optional.ofNullable(document);
	}

}
