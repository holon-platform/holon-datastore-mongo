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

import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.mongodb.async.client.MongoDatabase;

/**
 * Async operation context.
 *
 * @since 5.2.0
 */
public interface AsyncOperationContext {

	/**
	 * Get the operation context.
	 * @return Operation context
	 */
	MongoOperationContext<MongoDatabase> getOperationContext();

	/**
	 * Get the elements affected by the operation execution.
	 * @return Affected count
	 */
	long getAffectedCount();

	/**
	 * Set the elements affected by the operation execution.
	 * @param affectedCount Affected count
	 */
	void setAffectedCount(long affectedCount);
	
	/**
	 * Serialize given document to JSON using the database codec registry.
	 * @param document The document to serialize
	 * @return Serialized document
	 */
	default String toJson(Document document) {
		return getOperationContext().toJson(document);
	}

}
