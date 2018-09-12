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
package com.holonplatform.datastore.mongo.reactor.internal.tx;

import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.datastore.mongo.core.internal.tx.AbstractMongoTransaction;
import com.holonplatform.datastore.mongo.reactor.tx.ReactiveMongoTransaction;
import com.mongodb.async.client.ClientSession;

import reactor.core.publisher.Mono;

/**
 * Default {@link ReactiveMongoTransaction} implementation.
 *
 * @since 5.2.0
 */
public class DefaultReactiveMongoTransaction extends AbstractMongoTransaction<ClientSession>
		implements ReactiveMongoTransaction {

	public DefaultReactiveMongoTransaction(ClientSession session, TransactionConfiguration configuration) {
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
	 * @see com.holonplatform.datastore.mongo.reactor.tx.ReactiveMongoTransaction#start()
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
	 * @see com.holonplatform.datastore.mongo.reactor.tx.ReactiveMongoTransaction#end()
	 */
	@Override
	public Mono<Void> end() throws TransactionException {

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
					return commit().map(r -> null);
				}
			}
		}

		LOGGER.debug(() -> "MongoDB transaction [" + this + "] finalized");

		return Mono.empty();

	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.datastore.transaction.ReactiveTransaction#commit()
	 */
	@Override
	public Mono<Boolean> commit() {

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
			return rollback().map(r -> Boolean.FALSE);
		}

		// commit
		return Mono.<Boolean>create(sink -> {
			getSession().commitTransaction((result, error) -> {
				if (error != null) {
					sink.error(error);
				} else {
					// set as completed
					setCompleted();

					LOGGER.debug(() -> "MongoDB transaction [" + this + "] committed");

					sink.success(Boolean.TRUE);
				}
			});
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.datastore.transaction.ReactiveTransaction#rollback()
	 */
	@Override
	public Mono<Void> rollback() {

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
		return Mono.<Void>create(sink -> {
			getSession().abortTransaction((result, error) -> {
				if (error != null) {
					sink.error(error);
				} else {
					// set as completed
					setCompleted();

					LOGGER.debug(() -> "MongoDB transaction [" + this + "] committed");

					sink.success();
				}
			});
		});
	}

}