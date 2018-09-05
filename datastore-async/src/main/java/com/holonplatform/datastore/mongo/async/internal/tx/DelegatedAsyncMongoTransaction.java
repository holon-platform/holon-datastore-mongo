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
package com.holonplatform.datastore.mongo.async.internal.tx;

import java.util.concurrent.CompletionStage;

import com.holonplatform.datastore.mongo.async.tx.AsyncMongoTransaction;
import com.holonplatform.datastore.mongo.core.internal.tx.AbstractDelegatedMongoTransaction;
import com.mongodb.async.client.ClientSession;

/**
 * An {@link AsyncMongoTransaction} which delegates its operations to another transaction.
 *
 * @since 5.2.0
 */
public class DelegatedAsyncMongoTransaction extends
		AbstractDelegatedMongoTransaction<ClientSession, AsyncMongoTransaction> implements AsyncMongoTransaction {

	public DelegatedAsyncMongoTransaction(AsyncMongoTransaction delegate) {
		super(delegate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.async.datastore.transaction.AsyncTransaction#commit()
	 */
	@Override
	public CompletionStage<Boolean> commit() {
		return getDelegate().commit();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.async.datastore.transaction.AsyncTransaction#rollback()
	 */
	@Override
	public CompletionStage<Void> rollback() {
		return getDelegate().rollback();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.tx.MongoTransaction#start()
	 */
	@Override
	public void start() throws TransactionException {
		throw new IllegalTransactionStatusException("A delegated transaction should not be started");
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.tx.AsyncMongoTransaction#end()
	 */
	@Override
	public CompletionStage<Void> end() throws TransactionException {
		throw new IllegalTransactionStatusException("A delegated transaction should not be ended");
	}

}
