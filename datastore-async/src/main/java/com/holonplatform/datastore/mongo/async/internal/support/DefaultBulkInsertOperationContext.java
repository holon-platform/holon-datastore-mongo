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

import java.util.Collections;
import java.util.List;

import org.bson.Document;

import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.internal.support.ResolvedDocument;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

/**
 * Default {@link BulkInsertOperationContext} implementation.
 *
 * @since 5.2.0
 */
public class DefaultBulkInsertOperationContext extends DefaultBulkOperationContext
		implements BulkInsertOperationContext {

	private final MongoDocumentContext documentContext;
	private final PropertySet<?> propertySet;

	private List<ResolvedDocument> documents;

	public DefaultBulkInsertOperationContext(MongoOperationContext<MongoDatabase> operationContext,
			DatastoreOperationConfiguration configuration, MongoDocumentContext documentContext,
			MongoCollection<Document> collection, PropertySet<?> propertySet) {
		super(operationContext, configuration, documentContext, collection, null);
		this.documentContext = documentContext;
		this.propertySet = propertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.BulkInsertOperationContext#getPropertySet()
	 */
	@Override
	public PropertySet<?> getPropertySet() {
		return propertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.BulkInsertOperationContext#getDocumentContext()
	 */
	@Override
	public MongoDocumentContext getDocumentContext() {
		return documentContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.BulkInsertOperationContext#getDocuments()
	 */
	@Override
	public List<ResolvedDocument> getDocuments() {
		if (documents == null) {
			return Collections.emptyList();
		}
		return documents;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.async.internal.support.BulkInsertOperationContext#setDocuments(java.util.Map)
	 */
	@Override
	public void setDocuments(List<ResolvedDocument> documents) {
		this.documents = documents;
	}

}
