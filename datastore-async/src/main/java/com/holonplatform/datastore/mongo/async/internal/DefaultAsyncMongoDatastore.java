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
package com.holonplatform.datastore.mongo.async.internal;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.holonplatform.async.datastore.transaction.AsyncTransactionalOperation;
import com.holonplatform.core.datastore.DatastoreCommodity;
import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.core.datastore.transaction.TransactionStatus.TransactionException;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.async.AsyncMongoDatastore;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityFactory;
import com.holonplatform.datastore.mongo.async.internal.operations.AsyncMongoBulkDelete;
import com.holonplatform.datastore.mongo.async.internal.operations.AsyncMongoBulkInsert;
import com.holonplatform.datastore.mongo.async.internal.operations.AsyncMongoBulkUpdate;
import com.holonplatform.datastore.mongo.async.internal.operations.AsyncMongoDelete;
import com.holonplatform.datastore.mongo.async.internal.operations.AsyncMongoInsert;
import com.holonplatform.datastore.mongo.async.internal.operations.AsyncMongoQuery;
import com.holonplatform.datastore.mongo.async.internal.operations.AsyncMongoRefresh;
import com.holonplatform.datastore.mongo.async.internal.operations.AsyncMongoSave;
import com.holonplatform.datastore.mongo.async.internal.operations.AsyncMongoUpdate;
import com.holonplatform.datastore.mongo.async.tx.AsyncMongoTransaction;
import com.holonplatform.datastore.mongo.core.MongoDatabaseOperation;
import com.holonplatform.datastore.mongo.core.internal.datastore.AbstractMongoDatastore;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoDatabase;

/**
 * Default {@link AsyncMongoDatastore} implementation.
 *
 * @since 5.2.0
 */
public class DefaultAsyncMongoDatastore extends
		AbstractMongoDatastore<AsyncMongoDatastoreCommodityContext, ClientSession, AsyncMongoTransaction, MongoDatabase>
		implements AsyncMongoDatastore, AsyncMongoDatastoreCommodityContext {

	private static final long serialVersionUID = 5851873626687056062L;

	/**
	 * Current local transaction
	 */
	private static final ThreadLocal<AsyncMongoTransaction> CURRENT_TRANSACTION = new ThreadLocal<>();

	/**
	 * Mongo client
	 */
	private MongoClient client;

	/**
	 * Constructor.
	 */
	public DefaultAsyncMongoDatastore() {
		super(AsyncMongoDatastoreCommodityFactory.class, (s, c) -> AsyncMongoTransaction.create(s, c));

		// default resolvers
		addExpressionResolvers(MongoExpressionResolver.getDefaultResolvers());

		// register operation commodities
		registerCommodity(AsyncMongoRefresh.FACTORY);
		registerCommodity(AsyncMongoInsert.FACTORY);
		registerCommodity(AsyncMongoUpdate.FACTORY);
		registerCommodity(AsyncMongoSave.FACTORY);
		registerCommodity(AsyncMongoDelete.FACTORY);
		registerCommodity(AsyncMongoBulkDelete.FACTORY);
		registerCommodity(AsyncMongoBulkInsert.FACTORY);
		registerCommodity(AsyncMongoBulkUpdate.FACTORY);
		registerCommodity(AsyncMongoQuery.FACTORY);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoOperationContext#isAsync()
	 */
	@Override
	public boolean isAsync() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getClientSession()
	 */
	@Override
	public Optional<ClientSession> getClientSession() {
		return getCurrentTransaction().map(tx -> tx.getSession());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDatabaseCodecRegistry()
	 */
	@Override
	public CodecRegistry getDatabaseCodecRegistry() {
		return checkClient().getDatabase(checkDatabaseName()).getCodecRegistry();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.datastore.AbstractDatastore#getCommodityContext()
	 */
	@Override
	protected AsyncMongoDatastoreCommodityContext getCommodityContext() throws CommodityConfigurationException {
		return this;
	}

	/**
	 * Set the MongoDB client to use.
	 * @param client the client to set
	 */
	public void setClient(MongoClient client) {
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.async.config.AsyncMongoDatastoreCommodityContext#getClient()
	 */
	@Override
	public MongoClient getClient() {
		return client;
	}

	/**
	 * Check the client is available and returns it.
	 * <p>
	 * If the client is not available, an {@link IllegalStateException} is thrown.
	 * </p>
	 * @return The client
	 */
	protected MongoClient checkClient() {
		MongoClient client = getClient();
		if (client == null) {
			throw new IllegalStateException("No MongoClient configured");
		}
		return client;
	}

	/**
	 * Configure the {@link MongoDatabase}, registering the additional codecs if configured.
	 * @param database The database to configure
	 * @return The configured database
	 */
	protected MongoDatabase checkAdditionalCodecs(MongoDatabase database) {
		return getAdditionalCodecRegistry()
				.map(r -> database.withCodecRegistry(CodecRegistries.fromRegistries(database.getCodecRegistry(), r)))
				.orElse(database);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.MongoDatabaseHandler#withDatabase(com.holonplatform.datastore.mongo.core.
	 * MongoDatabaseOperation)
	 */
	@Override
	public <R> R withDatabase(MongoDatabaseOperation<MongoDatabase, R> operation) {
		ObjectUtils.argumentNotNull(operation, "Operation must be not null");

		// get and configure the database
		final MongoDatabase database = checkAdditionalCodecs(checkClient().getDatabase(checkDatabaseName()));

		try {
			return operation.execute(database);
		} catch (DataAccessException e) {
			throw e;
		} catch (Exception e) {
			throw new DataAccessException("Failed to execute operation", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.async.datastore.transaction.AsyncTransactional#withTransaction(com.holonplatform.async.
	 * datastore.transaction.AsyncTransactionalOperation,
	 * com.holonplatform.core.datastore.transaction.TransactionConfiguration)
	 */
	@Override
	public <R> CompletionStage<R> withTransaction(AsyncTransactionalOperation<R> operation,
			TransactionConfiguration transactionConfiguration) {
		ObjectUtils.argumentNotNull(operation, "TransactionalOperation must be not null");

		// check active transaction or create a new one
		return getCurrentTransaction()
				.map(t -> (CompletionStage<AsyncMongoTransaction>) CompletableFuture
						.completedFuture(AsyncMongoTransaction.delegate(t)))
				.orElseGet(() -> startTransaction(transactionConfiguration)).thenCompose(tx -> {
					// execute operation
					try {
						return operation.execute(tx).thenApply(r -> new TransactionalOperationResult<>(tx, r));
					} catch (Exception e) {
						// check rollback transaction
						if (tx.getConfiguration().isRollbackOnError() && !tx.isCompleted()) {
							tx.setRollbackOnly();
						}
						return CompletableFuture.completedFuture(new TransactionalOperationResult<R>(tx, e));
					}
				}).thenApply(result -> {
					// finalize transaction
					endTransaction(result.getTransaction());
					// check execution error
					if (result.getError() != null) {
						throw new TransactionException("Failed to execute operation", result.getError());
					}
					// return the result
					return result.getResult();
				});

		/*
		 * try { // execute operation return operation.execute(tx); } catch (Exception e) { // check rollback
		 * transaction if (tx.getConfiguration().isRollbackOnError() && !tx.isCompleted()) { tx.setRollbackOnly(); }
		 * throw e; } finally { try { endTransaction(tx); } catch (Exception e) { throw new
		 * DataAccessException("Failed to finalize transaction", e); } }
		 */
	}

	private static final class TransactionalOperationResult<R> {

		private final AsyncMongoTransaction transaction;
		private final R result;
		private final Throwable error;

		public TransactionalOperationResult(AsyncMongoTransaction transaction, R result) {
			super();
			this.transaction = transaction;
			this.result = result;
			this.error = null;
		}

		public TransactionalOperationResult(AsyncMongoTransaction transaction, Throwable error) {
			super();
			this.transaction = transaction;
			this.result = null;
			this.error = error;
		}

		public AsyncMongoTransaction getTransaction() {
			return transaction;
		}

		public R getResult() {
			return result;
		}

		public Throwable getError() {
			return error;
		}

	}

	/**
	 * Starts a {@link AsyncMongoTransaction}. If a local transaction is active, it will be forcedly finalized.
	 * @param configuration Transaction configuration. If <code>null</code>, a default configuration will be used
	 * @return A {@link CompletionStage} to handle the operation outcome and obtain the {@link AsyncMongoTransaction}
	 *         instance
	 */
	private CompletionStage<AsyncMongoTransaction> startTransaction(TransactionConfiguration configuration)
			throws TransactionException {

		// check supported
		checkTransactionSupported();

		// check if a current transaction is present
		getCurrentTransaction().ifPresent(tx -> {
			LOGGER.warn("A thread bound transaction was already present [" + tx
					+ "] - The current transaction will be finalized");
			try {
				endTransaction(tx);
			} catch (Exception e) {
				LOGGER.warn("Failed to force current transaction finalization", e);
			}
		});

		// configuration
		final TransactionConfiguration cfg = (configuration != null) ? configuration
				: TransactionConfiguration.getDefault();

		return CompletableFuture.supplyAsync(() -> {
			final CompletableFuture<ClientSession> operation = new CompletableFuture<>();
			// start a client session
			checkClient().startSession((result, error) -> {
				if (error != null) {
					operation.completeExceptionally(error);
				} else {
					operation.complete(result);
				}
			});
			return operation.join();
		}).thenApply(s -> {
			// create a new transaction
			return getTransactionFactory().createTransaction(s, cfg);
		}).thenApply(tx -> {
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
		}).thenApply(tx -> {

			// set as current transaction
			CURRENT_TRANSACTION.set(tx);

			LOGGER.debug(() -> "MongoDB transaction [" + tx + "] created and setted as current transaction");

			return tx;
		});
	}

	/**
	 * Finalize the given transaction, only if the transaction is new.
	 * @param tx Transaction to finalize
	 * @throws TransactionException Error finalizing transaction
	 * @return A {@link CompletionStage} to handle the operation outcome, with result value <code>true</code> if the
	 *         transaction was actually finalized
	 */
	@SuppressWarnings("static-method")
	private CompletionStage<Boolean> endTransaction(AsyncMongoTransaction tx) throws TransactionException {
		ObjectUtils.argumentNotNull(tx, "Transaction must be not null");

		// check new
		if (!tx.isNew()) {
			LOGGER.debug(() -> "MongoDB transaction [" + tx + "] was not finalized because it is not new");
			return CompletableFuture.completedFuture(Boolean.FALSE);
		}

		// remove reference
		getCurrentTransaction().filter(current -> current == tx).ifPresent(current -> CURRENT_TRANSACTION.remove());

		// end the transaction if active
		if (tx.isActive()) {
			return tx.end().exceptionally(e -> {
				LOGGER.error("Failed to finalize the transaction", e);
				return null;
			}).thenApply(r -> {
				tx.getSession().close();
				return true;
			});
		}

		// close session
		try {
			tx.getSession().close();
		} catch (Exception e) {
			throw new TransactionException("Failed to close client session", e);
		}

		LOGGER.debug(() -> "MongoDB transaction [" + tx + "] finalized");

		return CompletableFuture.completedFuture(Boolean.TRUE);
	}

	/**
	 * Get the current transaction, if active.
	 * @return Optional current transaction
	 */
	private static Optional<AsyncMongoTransaction> getCurrentTransaction() {
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
		LOGGER.info("MongoDB ASYNC Datastore initialized [Database name: " + getDatabaseName()
				+ getDataContextId().map(id -> ", Data context id: " + id).orElse("") + "]");
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[Async] MongoDatastore [database name=" + databaseName + ", data context id=" + getDataContextId()
				+ "]";
	}

	// ------- Builder

	public static class DefaultBuilder extends
			AbstractMongoDatastore.AbstractBuilder<MongoDatabase, AsyncMongoDatastoreCommodityContext, ClientSession, AsyncMongoTransaction, DefaultAsyncMongoDatastore, AsyncMongoDatastore, AsyncMongoDatastore.Builder>
			implements AsyncMongoDatastore.Builder {

		public DefaultBuilder() {
			super(new DefaultAsyncMongoDatastore());
		}

		@Override
		protected AsyncMongoDatastore.Builder getActualBuilder() {
			return this;
		}

		@Override
		public AsyncMongoDatastore.Builder client(MongoClient client) {
			ObjectUtils.argumentNotNull(client, "MongoClient must be not null");
			getDatastore().setClient(client);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.async.AsyncMongoDatastore.Builder#withCommodity(com.holonplatform.datastore
		 * .mongo.async.config.AsyncMongoDatastoreCommodityFactory)
		 */
		@Override
		public <C extends DatastoreCommodity> Builder withCommodity(
				AsyncMongoDatastoreCommodityFactory<C> commodityFactory) {
			getDatastore().registerCommodity(commodityFactory);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.datastore.DatastoreOperations.Builder#build()
		 */
		@Override
		public AsyncMongoDatastore build() {
			return configure();
		}

	}

}
