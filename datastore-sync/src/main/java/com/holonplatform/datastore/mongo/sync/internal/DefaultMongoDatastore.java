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

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.holonplatform.core.datastore.DatastoreCommodity;
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
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Default {@link MongoDatastore} implementation.
 *
 * @since 5.2.0
 */
public class DefaultMongoDatastore extends AbstractMongoDatastore<SyncMongoDatastoreCommodityContext, MongoDatabase>
		implements MongoDatastore, SyncMongoDatastoreCommodityContext {

	private static final long serialVersionUID = -3618780277490335232L;

	/**
	 * Mongo client
	 */
	private MongoClient client;

	/**
	 * Constructor.
	 * @param initialize Whether to initialize the Datastore
	 */
	public DefaultMongoDatastore(boolean initialize) {
		super(SyncMongoDatastoreCommodityFactory.class);

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

		// check initialize
		if (initialize) {
			initialize(getClass().getClassLoader());
		}
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

	/* (non-Javadoc)
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[Sync] MongoDatastore [database name=" + databaseName + ", data context id=" + getDataContextId() + "]";
	}

	// ------- Builder

	public static class DefaultBuilder extends
			AbstractMongoDatastore.AbstractBuilder<MongoDatabase, SyncMongoDatastoreCommodityContext, DefaultMongoDatastore, MongoDatastore, MongoDatastore.Builder>
			implements MongoDatastore.Builder {

		public DefaultBuilder() {
			super(new DefaultMongoDatastore(false));
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
