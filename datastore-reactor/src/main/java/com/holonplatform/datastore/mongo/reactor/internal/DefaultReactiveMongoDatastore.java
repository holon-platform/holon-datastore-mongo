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
package com.holonplatform.datastore.mongo.reactor.internal;

import java.util.Optional;

import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.core.datastore.transaction.TransactionStatus.TransactionException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.async.internal.AbstractAsyncMongoDatastore;
import com.holonplatform.datastore.mongo.reactor.ReactiveMongoDatastore;
import com.holonplatform.datastore.mongo.reactor.internal.operation.ReactiveMongoBulkDelete;
import com.holonplatform.datastore.mongo.reactor.internal.operation.ReactiveMongoBulkInsert;
import com.holonplatform.datastore.mongo.reactor.internal.operation.ReactiveMongoBulkUpdate;
import com.holonplatform.datastore.mongo.reactor.internal.operation.ReactiveMongoDelete;
import com.holonplatform.datastore.mongo.reactor.internal.operation.ReactiveMongoInsert;
import com.holonplatform.datastore.mongo.reactor.internal.operation.ReactiveMongoQuery;
import com.holonplatform.datastore.mongo.reactor.internal.operation.ReactiveMongoRefresh;
import com.holonplatform.datastore.mongo.reactor.internal.operation.ReactiveMongoSave;
import com.holonplatform.datastore.mongo.reactor.internal.operation.ReactiveMongoUpdate;
import com.holonplatform.datastore.mongo.reactor.tx.ReactiveMongoTransaction;
import com.holonplatform.reactor.datastore.transaction.ReactiveTransactionalOperation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Default {@link ReactiveMongoDatastore} implementation.
 *
 * @since 5.2.0
 */
public class DefaultReactiveMongoDatastore extends AbstractAsyncMongoDatastore<ReactiveMongoTransaction>
		implements ReactiveMongoDatastore {

	private static final long serialVersionUID = -6271179021927882154L;

	/**
	 * Current local transaction
	 */
	private static final ThreadLocal<ReactiveMongoTransaction> CURRENT_TRANSACTION = new ThreadLocal<>();

	public DefaultReactiveMongoDatastore() {
		super((s, c) -> ReactiveMongoTransaction.create(s, c));

		// register operation commodities
		registerCommodity(ReactiveMongoRefresh.FACTORY);
		registerCommodity(ReactiveMongoInsert.FACTORY);
		registerCommodity(ReactiveMongoUpdate.FACTORY);
		registerCommodity(ReactiveMongoSave.FACTORY);
		registerCommodity(ReactiveMongoDelete.FACTORY);
		registerCommodity(ReactiveMongoBulkDelete.FACTORY);
		registerCommodity(ReactiveMongoBulkInsert.FACTORY);
		registerCommodity(ReactiveMongoBulkUpdate.FACTORY);
		registerCommodity(ReactiveMongoQuery.FACTORY);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.reactor.datastore.transaction.ReactiveTransactional#withTransaction(com.holonplatform.reactor.
	 * datastore.transaction.ReactiveTransactionalOperation,
	 * com.holonplatform.core.datastore.transaction.TransactionConfiguration)
	 */
	@Override
	public <R> Flux<R> withTransaction(ReactiveTransactionalOperation<R> operation,
			TransactionConfiguration transactionConfiguration) {
		ObjectUtils.argumentNotNull(operation, "TransactionalOperation must be not null");

		// check active transaction or create a new one
		return getCurrentTransaction().map(tx -> Mono.just(tx))
				.orElseGet(() -> startTransaction(transactionConfiguration))
				// end transaction at termination
				.doFinally(type -> {
					getCurrentTransaction().ifPresent(tx -> endTransaction(tx));
				})
				// execute operation
				.flatMapMany(tx -> operation.execute(tx));
	}

	/**
	 * Starts a {@link ReactiveMongoTransaction}. If a local transaction is active, it will be forcedly finalized.
	 * @param configuration Transaction configuration. If <code>null</code>, a default configuration will be used
	 * @return A {@link Mono} to handle the operation outcome and obtain the {@link ReactiveMongoTransaction} instance
	 */
	private Mono<ReactiveMongoTransaction> startTransaction(TransactionConfiguration configuration)
			throws TransactionException {

		// check supported
		checkTransactionSupported();

		// check if a current transaction is present
		getCurrentTransaction().ifPresent(tx -> {
			LOGGER.warn("A thread bound transaction was already present [" + tx
					+ "] - The current transaction will be finalized");
			try {
				endTransaction(tx).block();
			} catch (Exception e) {
				LOGGER.warn("Failed to force current transaction finalization", e);
			}
		});

		// configuration
		final TransactionConfiguration cfg = (configuration != null) ? configuration
				: TransactionConfiguration.getDefault();

		return Mono.from(checkClient().startSession())
				.map(session -> getTransactionFactory().createTransaction(session, cfg)).map(tx -> {
					// start transaction
					try {
						tx.start();
					} catch (TransactionException e) {
						// ensure session finalization
						try {
							tx.getSession().close();
						} catch (Exception re) {
							LOGGER.warn("Transaction failed to start but the transaction session cannot be closed", re);
						}
						// propagate
						throw e;
					}
					return tx;
				}).map(tx -> {
					// set as current transaction
					CURRENT_TRANSACTION.set(tx);
					// log
					LOGGER.debug(() -> "MongoDB transaction [" + tx + "] created and setted as current transaction");
					return tx;
				});
	}

	/**
	 * Finalize the given transaction, only if the transaction is new.
	 * @param tx Transaction to finalize
	 * @throws TransactionException Error finalizing transaction
	 * @return A {@link Mono} to handle the operation outcome, with result value <code>true</code> if the transaction
	 *         was actually finalized
	 */
	private Mono<Boolean> endTransaction(ReactiveMongoTransaction tx) throws TransactionException {
		ObjectUtils.argumentNotNull(tx, "Transaction must be not null");

		// check new
		if (!tx.isNew()) {
			LOGGER.debug(() -> "MongoDB transaction [" + tx + "] was not finalized because it is not new");
			return Mono.just(Boolean.FALSE);
		}

		// remove reference
		getCurrentTransaction().filter(current -> current == tx).ifPresent(current -> CURRENT_TRANSACTION.remove());

		return Mono.just(tx).doFinally(type -> {
			// close session
			tx.getSession().close();
			LOGGER.debug(() -> "MongoDB transaction [" + tx + "] finalized");
		}).filter(t -> t.isActive()).flatMap(t -> t.end()).map(v -> Boolean.TRUE);

	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.async.internal.AbstractAsyncMongoDatastore#getCurrentTransaction()
	 */
	@Override
	protected Optional<ReactiveMongoTransaction> getCurrentTransaction() {
		return Optional.ofNullable(CURRENT_TRANSACTION.get());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.datastore.AbstractMongoDatastore#onDatastoreInitialized(java.lang
	 * .ClassLoader)
	 */
	@Override
	protected void onDatastoreInitialized(ClassLoader classLoader) {
		LOGGER.info("MongoDB Reactive Datastore initialized [Database name: " + getDatabaseName()
				+ getDataContextId().map(id -> ", Data context id: " + id).orElse("") + "]");
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[Reactive] MongoDatastore [database name=" + databaseName + ", data context id=" + getDataContextId()
				+ "]";
	}

	// ------- Builder

	public static class DefaultBuilder extends
			AbstractAsyncMongoDatastore.AsyncBuilder<ReactiveMongoTransaction, DefaultReactiveMongoDatastore, ReactiveMongoDatastore, ReactiveMongoDatastore.Builder>
			implements ReactiveMongoDatastore.Builder {

		public DefaultBuilder() {
			super(new DefaultReactiveMongoDatastore());
		}

		@Override
		protected ReactiveMongoDatastore.Builder getActualBuilder() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.datastore.DatastoreOperations.Builder#build()
		 */
		@Override
		public ReactiveMongoDatastore build() {
			return configure();
		}

	}

}
