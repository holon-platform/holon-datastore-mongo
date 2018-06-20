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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.mongodb.async.client.MongoDatabase;

/**
 * Base {@link AsyncOperationContext} implementation.
 *
 * @since 5.2.0
 */
public abstract class AbstractAsyncOperationContext implements AsyncOperationContext {

	private final MongoOperationContext<MongoDatabase> operationContext;

	private long affectedCount;

	public AbstractAsyncOperationContext(MongoOperationContext<MongoDatabase> operationContext) {
		super();
		ObjectUtils.argumentNotNull(operationContext, "Operation context must be not null");
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.AsyncOperationContext#getOperationContext()
	 */
	@Override
	public MongoOperationContext<MongoDatabase> getOperationContext() {
		return operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.AsyncOperationContext#getAffectedCount()
	 */
	@Override
	public long getAffectedCount() {
		return affectedCount;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.internal.support.AsyncOperationContext#setAffectedCount(int)
	 */
	@Override
	public void setAffectedCount(long affectedCount) {
		this.affectedCount = affectedCount;
	}

}
