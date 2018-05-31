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

import com.holonplatform.core.datastore.operation.commons.PropertyBoxOperationConfiguration;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.mongodb.async.client.MongoDatabase;

/**
 * Default {@link PropertyBoxOperationContext} implementation.
 *
 * @since 5.2.0
 */
public class DefaultPropertyBoxOperationContext implements PropertyBoxOperationContext {

	private final PropertyBoxOperationConfiguration configuration;
	private final MongoOperationContext<MongoDatabase> operationContext;
	private final MongoDocumentContext documentContext;

	public DefaultPropertyBoxOperationContext(PropertyBoxOperationConfiguration configuration,
			MongoOperationContext<MongoDatabase> operationContext, MongoDocumentContext documentContext) {
		super();
		this.configuration = configuration;
		this.operationContext = operationContext;
		this.documentContext = documentContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.PropertyBoxOperationContext#getConfiguration()
	 */
	@Override
	public PropertyBoxOperationConfiguration getConfiguration() {
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.PropertyBoxOperationContext#getOperationContext()
	 */
	@Override
	public MongoOperationContext<MongoDatabase> getOperationContext() {
		return operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.PropertyBoxOperationContext#getDocumentContext()
	 */
	@Override
	public MongoDocumentContext getDocumentContext() {
		return documentContext;
	}

}
