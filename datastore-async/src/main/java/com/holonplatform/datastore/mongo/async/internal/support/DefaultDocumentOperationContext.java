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
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.mongodb.async.client.MongoCollection;

/**
 * Default {@link DocumentOperationContext} implementation.
 *
 * @since 5.2.0
 */
public class DefaultDocumentOperationContext extends DefaultPropertyBoxOperationContext
		implements DocumentOperationContext {

	private final MongoCollection<Document> collection;
	private Document document;
	private final boolean resolveDocument;

	private ObjectId documentId;

	public DefaultDocumentOperationContext(PropertyBoxOperationContext parent, MongoCollection<Document> collection) {
		super(parent.getConfiguration(), parent.getOperationContext(), parent.getDocumentContext());
		ObjectUtils.argumentNotNull(collection, "MongoCollection must be not null");
		this.collection = collection;
		this.resolveDocument = true;
	}

	public DefaultDocumentOperationContext(PropertyBoxOperationContext parent, MongoCollection<Document> collection,
			Document document) {
		super(parent.getConfiguration(), parent.getOperationContext(), parent.getDocumentContext());
		ObjectUtils.argumentNotNull(collection, "MongoCollection must be not null");
		this.collection = collection;
		this.document = document;
		this.resolveDocument = false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.DocumentOperationContext#getCollection()
	 */
	@Override
	public MongoCollection<Document> getCollection() {
		return collection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.DocumentOperationContext#getDocumentId()
	 */
	@Override
	public Optional<ObjectId> getDocumentId() {
		return Optional.ofNullable(documentId);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.async.internal.support.DocumentOperationContext#setDocumentId(org.bson.types.
	 * ObjectId)
	 */
	@Override
	public void setDocumentId(ObjectId id) {
		this.documentId = id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.DocumentOperationContext#getDocument()
	 */
	@Override
	public Optional<Document> getDocument() {
		if (document == null && resolveDocument) {
			// try to resolve
			document = getDocumentContext()
					.resolve(PropertyBoxValue.create(getConfiguration().getValue()), DocumentValue.class)
					.map(d -> d.getValue()).orElse(null);
		}
		return Optional.ofNullable(document);
	}

}
