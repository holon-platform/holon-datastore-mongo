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

import org.bson.codecs.configuration.CodecRegistries;

import com.holonplatform.core.datastore.DatastoreCommodity;
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
import com.holonplatform.datastore.mongo.async.internal.operations.AsyncMongoRefresh;
import com.holonplatform.datastore.mongo.async.internal.operations.AsyncMongoSave;
import com.holonplatform.datastore.mongo.async.internal.operations.AsyncMongoUpdate;
import com.holonplatform.datastore.mongo.core.MongoDatabaseOperation;
import com.holonplatform.datastore.mongo.core.internal.datastore.AbstractMongoDatastore;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoDatabase;

/**
 * Default {@link AsyncMongoDatastore} implementation.
 *
 * @since 5.2.0
 */
public class DefaultAsyncMongoDatastore
		extends AbstractMongoDatastore<AsyncMongoDatastoreCommodityContext, MongoDatabase>
		implements AsyncMongoDatastore, AsyncMongoDatastoreCommodityContext {

	private static final long serialVersionUID = 5851873626687056062L;

	/**
	 * Mongo client
	 */
	private MongoClient client;

	/**
	 * Constructor.
	 * @param initialize Whether to initialize the Datastore
	 */
	public DefaultAsyncMongoDatastore(boolean initialize) {
		super(AsyncMongoDatastoreCommodityFactory.class);

		// default resolvers
		addExpressionResolvers(MongoExpressionResolver.getDefaultResolvers());

		// TODO
		// register operation commodities
		registerCommodity(AsyncMongoRefresh.FACTORY);
		registerCommodity(AsyncMongoInsert.FACTORY);
		registerCommodity(AsyncMongoUpdate.FACTORY);
		registerCommodity(AsyncMongoSave.FACTORY);
		registerCommodity(AsyncMongoDelete.FACTORY);
		registerCommodity(AsyncMongoBulkDelete.FACTORY);
		registerCommodity(AsyncMongoBulkInsert.FACTORY);
		registerCommodity(AsyncMongoBulkUpdate.FACTORY);

		/*
		 * registerCommodity(MongoQuery.FACTORY);
		 */

		// check initialize
		if (initialize) {
			initialize(getClass().getClassLoader());
		}
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[Async] MongoDatastore [database name=" + databaseName + ", data context id=" + getDataContextId()
				+ "]";
	}

	// ------- Builder

	public static class DefaultBuilder extends
			AbstractMongoDatastore.AbstractBuilder<MongoDatabase, AsyncMongoDatastoreCommodityContext, DefaultAsyncMongoDatastore, AsyncMongoDatastore, AsyncMongoDatastore.Builder>
			implements AsyncMongoDatastore.Builder {

		public DefaultBuilder() {
			super(new DefaultAsyncMongoDatastore(false));
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
