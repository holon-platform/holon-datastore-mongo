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

import org.bson.Document;

import com.mongodb.async.client.MongoCollection;

/**
 * Document and collection aware {@link PropertyBoxOperationContext}.
 * 
 * @since 5.2.0
 */
public interface DocumentOperationContext extends PropertyBoxOperationContext {

	MongoCollection<Document> getCollection();

	Document getDocument();

	static DocumentOperationContext create(PropertyBoxOperationContext parent, MongoCollection<Document> collection,
			Document document) {
		return new DefaultDocumentOperationContext(parent, collection, document);
	}

}
