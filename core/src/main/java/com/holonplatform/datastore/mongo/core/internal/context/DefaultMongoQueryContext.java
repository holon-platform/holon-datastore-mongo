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
package com.holonplatform.datastore.mongo.core.internal.context;

import java.util.Optional;

import com.holonplatform.datastore.mongo.core.context.MongoContext;
import com.holonplatform.datastore.mongo.core.context.MongoQueryContext;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;

/**
 * Default {@link MongoQueryContext} implementation.
 *
 * @since 5.2.0
 */
public class DefaultMongoQueryContext extends DefaultMongoResolutionContext implements MongoQueryContext {

	private QueryOperationType queryOperationType;

	/**
	 * Default constructor.
	 * @param context Mongo context (not null)
	 */
	public DefaultMongoQueryContext(MongoContext context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoQueryContext#getQueryOperationType()
	 */
	@Override
	public Optional<QueryOperationType> getQueryOperationType() {
		return Optional.ofNullable(queryOperationType);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoQueryContext#setQueryOperationType(com.holonplatform.
	 * datastore.mongo.core.document.QueryOperationType)
	 */
	@Override
	public void setQueryOperationType(QueryOperationType queryOperationType) {
		this.queryOperationType = queryOperationType;
	}

}
