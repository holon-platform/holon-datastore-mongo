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
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

/**
 * Save operation context.
 *
 * @since 5.2.0
 */
public interface SaveOperationContext extends PropertyBoxOperationContext {

	/**
	 * Get the mongo collection reference.
	 * @return Mongo collection
	 */
	MongoCollection<Document> getCollection();

	/**
	 * Get the performed operation type.
	 * @return Operation type
	 */
	OperationType getOperationType();

	/**
	 * If the performed operation was an upsert, returns the upserted document id.
	 * @return Optional upserted document id
	 */
	Optional<BsonValue> getUpsertedId();

	/**
	 * Get the {@link Document} bound to this context, if available.
	 * @return Optional document
	 */
	Optional<Document> getDocument();

	static SaveOperationContext create(PropertyBoxOperationConfiguration configuration, MongoOperationContext<MongoDatabase> operationContext,
			MongoDocumentContext documentContext, MongoCollection<Document> collection, OperationType operationType, long affectedCount) {
		return new DefaultSaveOperationContext(configuration, operationContext, documentContext, collection, operationType, affectedCount);
	}

	static SaveOperationContext create(PropertyBoxOperationConfiguration configuration, MongoOperationContext<MongoDatabase> operationContext,
			MongoDocumentContext documentContext, MongoCollection<Document> collection, OperationType operationType, long affectedCount, BsonValue upsertedId,
			Document document) {
		return new DefaultSaveOperationContext(configuration, operationContext, documentContext, collection, operationType, affectedCount, upsertedId,
				document);
	}

}
