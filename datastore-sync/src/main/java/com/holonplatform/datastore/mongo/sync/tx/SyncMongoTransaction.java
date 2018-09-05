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
package com.holonplatform.datastore.mongo.sync.tx;

import com.holonplatform.core.datastore.transaction.Transaction;
import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.core.datastore.transaction.TransactionStatus;
import com.holonplatform.datastore.mongo.core.tx.MongoTransaction;
import com.holonplatform.datastore.mongo.sync.internal.tx.DefaultSyncMongoTransaction;
import com.holonplatform.datastore.mongo.sync.internal.tx.DelegatedSyncMongoTransaction;
import com.mongodb.client.ClientSession;

/**
 * Synchronous MongoDB {@link Transaction}.
 *
 * @since 5.2.0
 */
public interface SyncMongoTransaction extends MongoTransaction<ClientSession>, Transaction {

	/**
	 * Start the transaction.
	 * @throws TransactionException If an error occurred
	 */
	void start() throws TransactionException;

	/**
	 * Finalize the transaction.
	 * @throws TransactionException If an error occurred
	 */
	void end() throws TransactionException;

	/**
	 * Create a new {@link SyncMongoTransaction}.
	 * @param session Client session (not null)
	 * @param configuration Transaction configuration (not null)
	 * @return A new {@link SyncMongoTransaction} implementation
	 */
	static SyncMongoTransaction create(ClientSession session, TransactionConfiguration configuration) {
		return new DefaultSyncMongoTransaction(session, configuration);
	}

	/**
	 * Create a {@link SyncMongoTransaction} which delegates its operations and status to the given delegated
	 * transaction.
	 * <p>
	 * The delegated transaction returns <code>false</code> from the {@link TransactionStatus#isNew()} method.
	 * </p>
	 * @param delegated Delegated transaction (not null)
	 * @return A delegated {@link SyncMongoTransaction} implementation
	 */
	static SyncMongoTransaction delegate(SyncMongoTransaction delegated) {
		return new DelegatedSyncMongoTransaction(delegated);
	}

}
