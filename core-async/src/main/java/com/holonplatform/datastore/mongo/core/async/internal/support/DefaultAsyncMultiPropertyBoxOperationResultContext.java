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

import java.util.List;

import org.bson.Document;

import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.internal.support.ResolvedDocument;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoCollection;

/**
 * Default {@link AsyncMultiPropertyBoxOperationResultContext} implementation.
 *
 * @since 5.2.0
 */
public class DefaultAsyncMultiPropertyBoxOperationResultContext
		extends DefaultAsyncOperationResultContext<MongoDocumentContext<ClientSession>>
		implements AsyncMultiPropertyBoxOperationResultContext {

	private final List<ResolvedDocument> values;

	public DefaultAsyncMultiPropertyBoxOperationResultContext(MongoDocumentContext<ClientSession> mongoContext,
			MongoCollection<Document> collection, DatastoreOperationConfiguration configuration, long affectedCount,
			OperationType operationType, List<ResolvedDocument> values) {
		super(mongoContext, collection, configuration, affectedCount, operationType);
		this.values = values;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.async.internal.support.AsyncMultiPropertyBoxOperationResultContext#getValues()
	 */
	@Override
	public List<ResolvedDocument> getValues() {
		return values;
	}

}
