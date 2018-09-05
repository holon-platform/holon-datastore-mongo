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
package com.holonplatform.datastore.mongo.sync.internal;

import java.util.Optional;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.holonplatform.core.datastore.DatastoreCommodity;
import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.core.datastore.transaction.TransactionStatus.TransactionException;
import com.holonplatform.core.datastore.transaction.TransactionalOperation;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.MongoDatabaseOperation;
import com.holonplatform.datastore.mongo.core.internal.datastore.AbstractMongoDatastore;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;
import com.holonplatform.datastore.mongo.sync.MongoDatastore;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityFactory;
import com.holonplatform.datastore.mongo.sync.internal.operations.MongoBulkDelete;
import com.holonplatform.datastore.mongo.sync.internal.operations.MongoBulkInsert;
import com.holonplatform.datastore.mongo.sync.internal.operations.MongoBulkUpdate;
import com.holonplatform.datastore.mongo.sync.internal.operations.MongoDelete;
import com.holonplatform.datastore.mongo.sync.internal.operations.MongoInsert;
import com.holonplatform.datastore.mongo.sync.internal.operations.MongoQuery;
import com.holonplatform.datastore.mongo.sync.internal.operations.MongoRefresh;
import com.holonplatform.datastore.mongo.sync.internal.operations.MongoSave;
import com.holonplatform.datastore.mongo.sync.internal.operations.MongoUpdate;
import com.holonplatform.datastore.mongo.sync.tx.SyncMongoTransaction;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Default {@link MongoDatastore} implementation.
 *
 * @since 5.2.0
 */
public class DefaultMongoDatastore extends
		AbstractMongoDatastore<SyncMongoDatastoreCommodityContext, ClientSession, SyncMongoTransaction, MongoDatabase>
		implements MongoDatastore, SyncMongoDatastoreCommodityContext {

	private static final long serialVersionUID = -3618780277490335232L;

	/**
	 * Current local transaction
	 */
	private static final ThreadLocal<SyncMongoTransaction> CURRENT_TRANSACTION = new ThreadLocal<>();

	/**
	 * Mongo client
	 */
	private MongoClient client;

	/**
	 * Constructor.
	 */
	public DefaultMongoDatastore() {
		super(SyncMongoDatastoreCommodityFactory.class, (s, c) -> SyncMongoTransaction.create(s, c));

		// default resolvers
		addExpressionResolvers(MongoExpressionResolver.getDefaultResolvers());

		// register operation commodities
		registerCommodity(MongoRefresh.FACTORY);
		registerCommodity(MongoInsert.FACTORY);
		registerCommodity(MongoUpdate.FACTORY);
		registerCommodity(MongoSave.FACTORY);
		registerCommodity(MongoDelete.FACTORY);
		registerCommodity(MongoBulkInsert.FACTORY);
		registerCommodity(MongoBulkUpdate.FACTORY);
		registerCommodity(MongoBulkDelete.FACTORY);
		registerCommodity(MongoQuery.FACTORY);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.datastore.AbstractDatastore#getCommodityContext()
	 */
	@Override
	protected SyncMongoDatastoreCommodityContext getCommodityContext() throws CommodityConfigurationException {
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
	 * @see com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext#getClient()
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoOperationContext#isAsync()
	 */
	@Override
	public boolean isAsync() {
		return false;
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
	 * @see com.holonplatform.core.datastore.transaction.Transactional#withTransaction(com.holonplatform.core.datastore.
	 * transaction.TransactionalOperation, com.holonplatform.core.datastore.transaction.TransactionConfiguration)
	 */
	@Override
	public <R> R withTransaction(TransactionalOperation<R> operation,
			TransactionConfiguration transactionConfiguration) {
		ObjectUtils.argumentNotNull(operation, "TransactionalOperation must be not null");

		// check active transaction or create a new one
		final SyncMongoTransaction tx = getCurrentTransaction().map(t -> SyncMongoTransaction.delegate(t))
				.orElseGet(() -> startTransaction(transactionConfiguration));

		try {
			// execute operation
			return operation.execute(tx);
		} catch (Exception e) {
			// check rollback transaction
			if (tx.getConfiguration().isRollbackOnError() && !tx.isCompleted()) {
				tx.setRollbackOnly();
			}
			throw e;
		} finally {
			try {
				endTransaction(tx);
			} catch (Exception e) {
				throw new DataAccessException("Failed to finalize transaction", e);
			}
		}
	}

	/**
	 * Starts a {@link SyncMongoTransaction}. If a local transaction is active, it will be forcedly finalized.
	 * @param configuration Transaction configuration. If <code>null</code>, a default configuration will be used
	 * @return A new transaction
	 */
	@SuppressWarnings("resource")
	private SyncMongoTransaction startTransaction(TransactionConfiguration configuration) throws TransactionException {

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

		// start a client session
		final ClientSession session = checkClient().startSession();

		// create a new transaction
		final SyncMongoTransaction tx = getTransactionFactory().createTransaction(session, cfg);

		// start transaction
		try {
			tx.start();
		} catch (TransactionException e) {
			// ensure session finalization
			try {
				session.close();
			} catch (Exception re) {
				LOGGER.warn("Transaction failed to start but the transaction session cannot be closed", re);
			}
			// propagate
			throw e;
		}

		// set as current transaction
		CURRENT_TRANSACTION.set(tx);

		LOGGER.debug(() -> "MongoDB transaction [" + tx + "] created and setted as current transaction");

		// return the transaction
		return tx;
	}

	/**
	 * Finalize the given transaction, only if the transaction is new.
	 * @param tx Transaction to finalize
	 * @throws TransactionException Error finalizing transaction
	 * @return <code>true</code> if the transaction was actually finalized
	 */
	@SuppressWarnings("static-method")
	private boolean endTransaction(SyncMongoTransaction tx) throws TransactionException {
		ObjectUtils.argumentNotNull(tx, "Transaction must be not null");

		// check new
		if (!tx.isNew()) {
			LOGGER.debug(() -> "MongoDB transaction [" + tx + "] was not finalized because it is not new");
			return false;
		}

		// remove reference
		getCurrentTransaction().filter(current -> current == tx).ifPresent(current -> CURRENT_TRANSACTION.remove());

		try {
			// end the transaction if active
			if (tx.isActive()) {
				tx.end();
			}
		} finally {
			// close session
			try {
				tx.getSession().close();
			} catch (Exception e) {
				throw new TransactionException("Failed to close client session", e);
			}
		}

		LOGGER.debug(() -> "MongoDB transaction [" + tx + "] finalized");

		return true;
	}

	/**
	 * Get the current transaction, if active.
	 * @return Optional current transaction
	 */
	private static Optional<SyncMongoTransaction> getCurrentTransaction() {
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
		LOGGER.info("MongoDB SYNC Datastore initialized [Database name: " + getDatabaseName()
				+ getDataContextId().map(id -> ", Data context id: " + id).orElse("") + "]");
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[Sync] MongoDatastore [database name=" + databaseName + ", data context id=" + getDataContextId() + "]";
	}

	// ------- Builder

	public static class DefaultBuilder extends
			AbstractMongoDatastore.AbstractBuilder<MongoDatabase, SyncMongoDatastoreCommodityContext, ClientSession, SyncMongoTransaction, DefaultMongoDatastore, MongoDatastore, MongoDatastore.Builder>
			implements MongoDatastore.Builder {

		public DefaultBuilder() {
			super(new DefaultMongoDatastore());
		}

		@Override
		protected MongoDatastore.Builder getActualBuilder() {
			return this;
		}

		@Override
		public MongoDatastore.Builder client(MongoClient client) {
			ObjectUtils.argumentNotNull(client, "MongoClient must be not null");
			getDatastore().setClient(client);
			return this;
		}

		@Override
		public <C extends DatastoreCommodity> MongoDatastore.Builder withCommodity(
				SyncMongoDatastoreCommodityFactory<C> commodityFactory) {
			getDatastore().registerCommodity(commodityFactory);
			return this;
		}

		@Override
		public MongoDatastore build() {
			return configure();
		}

	}

}
