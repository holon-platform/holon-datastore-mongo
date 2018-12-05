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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.datastore.mongo.async.internal.CompletableFutureSubscriber;
import com.holonplatform.datastore.mongo.async.tx.AsyncMongoTransaction;
import com.holonplatform.datastore.mongo.core.internal.tx.AbstractMongoTransaction;
import com.mongodb.reactivestreams.client.ClientSession;

/**
 * Default {@link AsyncMongoTransaction} implementation.
 *
 * @since 5.2.0
 */
public class DefaultAsyncMongoTransaction extends AbstractMongoTransaction<ClientSession>
		implements AsyncMongoTransaction {

	public DefaultAsyncMongoTransaction(ClientSession session, TransactionConfiguration configuration) {
		super(session, configuration);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.transaction.TransactionStatus#isActive()
	 */
	@Override
	public boolean isActive() {
		return getSession().hasActiveTransaction();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.tx.AsyncMongoTransaction#start()
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
	 * @see com.holonplatform.datastore.mongo.async.tx.AsyncMongoTransaction#end()
	 */
	@Override
	public CompletionStage<Void> end() throws TransactionException {

		// check active
		if (!isActive()) {
			throw new IllegalTransactionStatusException("The transaction is not active");
		}

		// check completed
		if (!isCompleted()) {
			if (isRollbackOnly()) {
				return rollback();
			} else {
				if (getConfiguration().isAutoCommit()) {
					return commit().thenApply(r -> null);
				}
			}
		}

		LOGGER.debug(() -> "MongoDB transaction [" + this + "] finalized");

		return CompletableFuture.completedFuture(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.async.datastore.transaction.AsyncTransaction#commit()
	 */
	@Override
	public CompletionStage<Boolean> commit() {

		// check active
		if (!isActive()) {
			throw new IllegalTransactionStatusException("Cannot commit the transaction: the transaction is not active");
		}

		// check completed
		if (isCompleted()) {
			throw new IllegalTransactionStatusException(
					"Cannot commit the transaction: the transaction is already completed");
		}

		// check rollback only
		if (isRollbackOnly()) {
			return rollback().thenApply(r -> Boolean.FALSE);
		}

		// commit
		return CompletableFutureSubscriber.fromPublisher(getSession().commitTransaction()).thenAccept(r -> {
			// set as completed
			setCompleted();
			// log
			LOGGER.debug(() -> "MongoDB transaction [" + this + "] committed");
		}).thenApply(r -> Boolean.TRUE);

	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.async.datastore.transaction.AsyncTransaction#rollback()
	 */
	@Override
	public CompletionStage<Void> rollback() {

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

		// rollback
		return CompletableFutureSubscriber.fromPublisher(getSession().abortTransaction()).thenAccept(r -> {
			// set as completed
			setCompleted();
			// log
			LOGGER.debug(() -> "MongoDB transaction [" + this + "] rolled back");
		});

	}

}
