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
import org.bson.conversions.Bson;

import com.holonplatform.core.datastore.operation.commons.BulkUpdateOperationConfiguration;
import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

/**
 * Default {@link BulkUpdateOperationContext} implementation.
 *
 * @since 5.2.0
 */
public class DefaultBulkUpdateOperationContext extends DefaultBulkOperationContext
		implements BulkUpdateOperationContext {

	private final BulkUpdateOperationConfiguration operationConfiguration;

	public DefaultBulkUpdateOperationContext(MongoOperationContext<MongoDatabase> operationContext,
			DatastoreOperationConfiguration configuration, MongoResolutionContext resolutionContext,
			MongoCollection<Document> collection, Bson filter,
			BulkUpdateOperationConfiguration operationConfiguration) {
		super(operationContext, configuration, resolutionContext, collection, filter);
		ObjectUtils.argumentNotNull(operationConfiguration, "BulkUpdateOperationConfiguration must be not null");
		this.operationConfiguration = operationConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.async.internal.support.BulkUpdateOperationContext#getOperationConfiguration()
	 */
	@Override
	public BulkUpdateOperationConfiguration getOperationConfiguration() {
		return operationConfiguration;
	}

}
