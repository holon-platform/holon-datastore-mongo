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
package com.holonplatform.datastore.mongo.core.async.internal.support;

import org.bson.Document;

import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.datastore.mongo.core.context.MongoContext;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoCollection;

/**
 * Async operation result context.
 * 
 * @param <C> Context type
 *
 * @since 5.2.0
 */
public interface AsyncOperationResultContext<C extends MongoContext<ClientSession>> extends AsyncOperationContext<C> {

	/**
	 * Get the elements affected by the operation execution.
	 * @return Affected count
	 */
	long getAffectedCount();

	/**
	 * Get the performed operation type.
	 * @return Operation type
	 */
	OperationType getOperationType();

	static <C extends MongoContext<ClientSession>> AsyncOperationResultContext<C> create(C mongoContext,
			MongoCollection<Document> collection, DatastoreOperationConfiguration configuration, long affectedCount,
			OperationType operationType) {
		return new DefaultAsyncOperationResultContext<>(mongoContext, collection, configuration, affectedCount,
				operationType);
	}

}
