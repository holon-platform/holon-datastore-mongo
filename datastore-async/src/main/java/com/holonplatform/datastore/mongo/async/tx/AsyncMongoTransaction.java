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
package com.holonplatform.datastore.mongo.async.tx;

import java.util.concurrent.CompletionStage;

import com.holonplatform.async.datastore.transaction.AsyncTransaction;
import com.holonplatform.core.datastore.transaction.Transaction;
import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.core.datastore.transaction.TransactionStatus;
import com.holonplatform.datastore.mongo.async.internal.tx.DefaultAsyncMongoTransaction;
import com.holonplatform.datastore.mongo.async.internal.tx.DelegatedAsyncMongoTransaction;
import com.holonplatform.datastore.mongo.core.tx.MongoTransaction;
import com.mongodb.reactivestreams.client.ClientSession;

/**
 * Asynchronous MongoDB {@link Transaction}.
 *
 * @since 5.2.0
 */
public interface AsyncMongoTransaction extends MongoTransaction<ClientSession>, AsyncTransaction {

	/**
	 * Start the transaction.
	 * @throws TransactionException If an error occurred
	 */
	void start() throws TransactionException;

	/**
	 * Finalize the transaction.
	 * @return A {@link CompletionStage} to handle the transaction end asynchronously
	 * @throws TransactionException If an error occurred
	 */
	CompletionStage<Void> end() throws TransactionException;

	/**
	 * Create a new {@link AsyncMongoTransaction}.
	 * @param session Client session (not null)
	 * @param configuration Transaction configuration (not null)
	 * @return A new {@link AsyncMongoTransaction} implementation
	 */
	static AsyncMongoTransaction create(ClientSession session, TransactionConfiguration configuration) {
		return new DefaultAsyncMongoTransaction(session, configuration);
	}

	/**
	 * Create a {@link AsyncMongoTransaction} which delegates its operations and status to the given delegated
	 * transaction.
	 * <p>
	 * The delegated transaction returns <code>false</code> from the {@link TransactionStatus#isNew()} method.
	 * </p>
	 * @param delegated Delegated transaction (not null)
	 * @return A delegated {@link AsyncMongoTransaction} implementation
	 */
	static AsyncMongoTransaction delegate(AsyncMongoTransaction delegated) {
		return new DelegatedAsyncMongoTransaction(delegated);
	}

}
