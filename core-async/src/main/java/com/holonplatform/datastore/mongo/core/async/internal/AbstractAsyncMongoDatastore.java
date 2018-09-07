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
package com.holonplatform.datastore.mongo.core.async.internal;

import java.util.Optional;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.holonplatform.core.datastore.DatastoreCommodity;
import com.holonplatform.core.datastore.DatastoreOperations;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.MongoDatabaseOperation;
import com.holonplatform.datastore.mongo.core.async.BaseAsyncMongoDatastore;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityFactory;
import com.holonplatform.datastore.mongo.core.internal.datastore.AbstractMongoDatastore;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;
import com.holonplatform.datastore.mongo.core.tx.MongoTransaction;
import com.holonplatform.datastore.mongo.core.tx.MongoTransactionFactory;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoDatabase;

/**
 * Abstract asynchronous MongoDB Datastore implementation.
 * 
 * @param <TX> Concrete transaction type
 *
 * @since 5.2.0
 */
public abstract class AbstractAsyncMongoDatastore<TX extends MongoTransaction<ClientSession>>
		extends AbstractMongoDatastore<AsyncMongoDatastoreCommodityContext, ClientSession, TX, MongoDatabase>
		implements AsyncMongoDatastoreCommodityContext {

	private static final long serialVersionUID = -5535246252629690073L;

	/**
	 * Mongo client
	 */
	private MongoClient client;

	public AbstractAsyncMongoDatastore(MongoTransactionFactory<ClientSession, TX> transactionFactory) {
		super(AsyncMongoDatastoreCommodityFactory.class, transactionFactory);

		// default resolvers
		addExpressionResolvers(MongoExpressionResolver.getDefaultResolvers());
	}

	/**
	 * Get the current transaction, if active.
	 * @return Optional current transaction
	 */
	protected abstract Optional<TX> getCurrentTransaction();

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

	// ------- Builder

	@SuppressWarnings("rawtypes")
	public static abstract class AsyncBuilder<TX extends MongoTransaction<ClientSession>, I extends AbstractAsyncMongoDatastore<TX>, D extends DatastoreOperations, B extends BaseAsyncMongoDatastore.Builder<D, TX, B>>
			extends
			AbstractMongoDatastore.AbstractBuilder<MongoDatabase, AsyncMongoDatastoreCommodityContext, ClientSession, TX, I, D, B>
			implements BaseAsyncMongoDatastore.Builder<D, TX, B> {

		public AsyncBuilder(I datastore) {
			super(datastore);
		}

		@Override
		public B client(MongoClient client) {
			ObjectUtils.argumentNotNull(client, "MongoClient must be not null");
			getDatastore().setClient(client);
			return getActualBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.async.AsyncMongoDatastore.Builder#withCommodity(com.holonplatform.datastore
		 * .mongo.async.config.AsyncMongoDatastoreCommodityFactory)
		 */
		@Override
		public <C extends DatastoreCommodity> B withCommodity(AsyncMongoDatastoreCommodityFactory<C> commodityFactory) {
			getDatastore().registerCommodity(commodityFactory);
			return getActualBuilder();
		}

	}

}
