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

import java.util.Map;

import org.bson.Document;

import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

/**
 * Bulk insert operation context.
 *
 * @since 5.2.0
 */
public interface BulkInsertOperationContext extends BulkOperationContext {

	/**
	 * Get the operation {@link PropertySet}.
	 * @return Operation {@link PropertySet}
	 */
	PropertySet<?> getPropertySet();

	/**
	 * Get the document context.
	 * @return Document context
	 */
	MongoDocumentContext getDocumentContext();

	/**
	 * Get the inserted documents.
	 * @return Inserted documents
	 */
	Map<Document, PropertyBox> getDocuments();

	/**
	 * Set the inserted documents.
	 * @param documents Inserted documents
	 */
	void setDocuments(Map<Document, PropertyBox> documents);

	static BulkInsertOperationContext create(MongoOperationContext<MongoDatabase> operationContext,
			DatastoreOperationConfiguration configuration, MongoDocumentContext documentContext,
			MongoCollection<Document> collection, PropertySet<?> propertySet) {
		return new DefaultBulkInsertOperationContext(operationContext, configuration, documentContext, collection,
				propertySet);
	}

}
