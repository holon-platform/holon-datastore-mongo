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

import com.holonplatform.core.datastore.operation.commons.PropertyBoxOperation;
import com.holonplatform.core.datastore.operation.commons.PropertyBoxOperationConfiguration;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.mongodb.async.client.MongoDatabase;

/**
 * {@link PropertyBoxOperation} execution context.
 * 
 * @since 5.2.0
 */
public interface PropertyBoxOperationContext extends AsyncOperationContext {

	/**
	 * Get the {@link PropertyBoxOperation} configuration.
	 * @return Operation configuration
	 */
	PropertyBoxOperationConfiguration getConfiguration();

	/**
	 * Get the document context.
	 * @return Document context
	 */
	MongoDocumentContext getDocumentContext();

	/**
	 * Create a new {@link PropertyBoxOperationContext}.
	 * @param configuration Operation configuration (not null)
	 * @param operationContext Operation context (not null)
	 * @param documentContext Document context (not null)
	 * @return A new {@link PropertyBoxOperationContext} instance
	 */
	static PropertyBoxOperationContext create(PropertyBoxOperationConfiguration configuration,
			MongoOperationContext<MongoDatabase> operationContext, MongoDocumentContext documentContext) {
		return new DefaultPropertyBoxOperationContext(configuration, operationContext, documentContext);
	}

}
