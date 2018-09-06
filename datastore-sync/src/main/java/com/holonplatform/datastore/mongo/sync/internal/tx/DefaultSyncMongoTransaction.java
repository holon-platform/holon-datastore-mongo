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
package com.holonplatform.datastore.mongo.sync.internal.tx;

import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.datastore.mongo.core.internal.tx.AbstractMongoTransaction;
import com.holonplatform.datastore.mongo.sync.tx.SyncMongoTransaction;
import com.mongodb.client.ClientSession;

/**
 * Default {@link SyncMongoTransaction} implementation.
 *
 * @since 5.2.0
 */
public class DefaultSyncMongoTransaction extends AbstractMongoTransaction<ClientSession>
		implements SyncMongoTransaction {

	/**
	 * Constructor.
	 * @param session Client session (not null)
	 * @param configuration Transaction configuration (not null)
	 */
	public DefaultSyncMongoTransaction(ClientSession session, TransactionConfiguration configuration) {
		super(session, configuration);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.transaction.Transaction#isActive()
	 */
	@Override
	public boolean isActive() {
		return getSession().hasActiveTransaction();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.tx.MongoTransaction#start()
	 */
	@Override
	public void start() throws TransactionException {

		// check not already started
		if (isActive()) {
			throw new IllegalTransactionStatusException(
					"The transaction is already active and bound to session [" + getSession() + "]");
		}

		// start transaction
		getSession().startTransaction(getTransactionOptions());

		LOGGER.debug(() -> "MongoDB transaction [" + this + "] started");
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.tx.MongoTransaction#end()
	 */
	@Override
	public void end() throws TransactionException {

		// check active
		if (!isActive()) {
			throw new IllegalTransactionStatusException("The transaction is not active");
		}

		// check completed
		if (!isCompleted()) {
			if (isRollbackOnly()) {
				rollback();
			} else {
				if (getConfiguration().isAutoCommit()) {
					commit();
				}
			}
		}

		LOGGER.debug(() -> "MongoDB transaction [" + this + "] finalized");
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.transaction.Transaction#commit()
	 */
	@Override
	public boolean commit() throws TransactionException {

		// check active
		if (!isActive()) {
			throw new IllegalTransactionStatusException("Cannot commit the transaction: the transaction is not active");
		}

		// check completed
		if (isCompleted()) {
			throw new IllegalTransactionStatusException(
					"Cannot commit the transaction: the transaction is already completed");
		}

		final boolean committed;
		try {
			// check rollback only
			if (isRollbackOnly()) {
				rollback();
				committed = false;
			} else {
				// commit
				getSession().commitTransaction();
				committed = true;
				LOGGER.debug(() -> "MongoDB transaction [" + this + "] committed");
			}
		} catch (Exception e) {
			throw new TransactionException("Failed to commit the transaction", e);
		}

		// set as completed
		setCompleted();

		return committed;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.transaction.Transaction#rollback()
	 */
	@Override
	public void rollback() throws TransactionException {

		// check active
		if (!isActive()) {
			throw new IllegalTransactionStatusException(
					"Cannot rollback the transaction: the transaction is not active");
		}

		// check completed
		if (isCompleted()) {
			throw new IllegalTransactionStatusException(
					"Cannot rollback the transaction: the transaction is already completed");
		}
		try {
			getSession().abortTransaction();
			LOGGER.debug(() -> "MongoDB transaction [" + this + "] rolled back");
		} catch (Exception e) {
			throw new TransactionException("Failed to rollback the transaction", e);
		}

		// set as completed
		setCompleted();
	}

}
