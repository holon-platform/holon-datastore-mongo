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

import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.datastore.AbstractDatastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.MongoDatabaseOperation;
import com.holonplatform.datastore.mongo.core.config.MongoDatastoreExpressionResolver;
import com.holonplatform.datastore.mongo.core.document.DocumentIdResolver;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;
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
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Default {@link MongoDatastore} implementation.
 *
 * @since 5.2.0
 */
public class DefaultMongoDatastore extends AbstractDatastore<SyncMongoDatastoreCommodityContext>
		implements MongoDatastore, SyncMongoDatastoreCommodityContext {

	private static final long serialVersionUID = -3618780277490335232L;

	/**
	 * Logger
	 */
	protected static final Logger LOGGER = MongoDatastoreLogger.create();

	/**
	 * Document id resolver
	 */
	private DocumentIdResolver documentIdResolver = DocumentIdResolver.getDefault();

	/**
	 * Enum codec strategy
	 */
	private EnumCodecStrategy enumCodecStrategy = EnumCodecStrategy.getDefault();

	/**
	 * Default {@link ReadPreference}
	 */
	private ReadPreference defaultReadPreference;

	/**
	 * Default {@link ReadConcern}
	 */
	private ReadConcern defaultReadConcern;

	/**
	 * Default {@link WriteConcern}
	 */
	private WriteConcern defaultWriteConcern;

	/**
	 * Mongo client
	 */
	private MongoClient client;

	/**
	 * Database name
	 */
	private String databaseName;

	/**
	 * Whether the datastore was initialized
	 */
	private boolean initialized = false;

	/**
	 * Constructor.
	 * @param initialize Whether to initialize the Datastore
	 */
	public DefaultMongoDatastore(boolean initialize) {
		super(SyncMongoDatastoreCommodityFactory.class, MongoDatastoreExpressionResolver.class);

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

	/**
	 * Initialize the datastore if it is not already initialized.
	 * @param classLoader ClassLoader to use to load default factories and resolvers
	 */
	public void initialize(ClassLoader classLoader) {
		if (!initialized) {
			loadExpressionResolvers(classLoader);
			loadCommodityFactories(classLoader);
			initialized = true;
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

	/**
	 * Set the database name.
	 * @param databaseName the database name to set
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext#getClient()
	 */
	@Override
	public MongoClient getClient() {
		return client;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.config.MongoDatastoreCommodityContext#getDatabaseName()
	 */
	@Override
	public String getDatabaseName() {
		return databaseName;
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
	 * Check the database name is available and returns it.
	 * <p>
	 * If the database name is not available, an {@link IllegalStateException} is thrown.
	 * </p>
	 * @return The database name
	 */
	protected String checkDatabaseName() {
		String databaseName = getDatabaseName();
		if (databaseName == null) {
			throw new IllegalStateException("No database name configured");
		}
		return databaseName;
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
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDocumentIdResolver()
	 */
	@Override
	public DocumentIdResolver getDocumentIdResolver() {
		return documentIdResolver;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDefaultEnumCodecStrategy()
	 */
	@Override
	public EnumCodecStrategy getDefaultEnumCodecStrategy() {
		return enumCodecStrategy;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoOperationContext#getDefaultReadPreference()
	 */
	@Override
	public Optional<ReadPreference> getDefaultReadPreference() {
		return Optional.ofNullable(defaultReadPreference);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoOperationContext#getDefaultReadConcern()
	 */
	@Override
	public Optional<ReadConcern> getDefaultReadConcern() {
		return Optional.ofNullable(defaultReadConcern);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoOperationContext#getDefaultWriteConcern()
	 */
	@Override
	public Optional<WriteConcern> getDefaultWriteConcern() {
		return Optional.ofNullable(defaultWriteConcern);
	}

	/**
	 * Set the {@link DocumentIdResolver} to use.
	 * @param documentIdResolver the document Id resolver to set (not null)
	 */
	public void setDocumentIdResolver(DocumentIdResolver documentIdResolver) {
		ObjectUtils.argumentNotNull(documentIdResolver, "DocumentIdResolver must be not null");
		this.documentIdResolver = documentIdResolver;
	}

	/**
	 * Set the {@link EnumCodecStrategy} to use.
	 * @param enumCodecStrategy the enum codec strategy to set (not null)
	 */
	public void setEnumCodecStrategy(EnumCodecStrategy enumCodecStrategy) {
		ObjectUtils.argumentNotNull(enumCodecStrategy, "EnumCodecStrategy must be not null");
		this.enumCodecStrategy = enumCodecStrategy;
	}

	/**
	 * Set the default {@link ReadPreference}.
	 * @param defaultReadPreference the default ReadPreference to set
	 */
	public void setDefaultReadPreference(ReadPreference defaultReadPreference) {
		this.defaultReadPreference = defaultReadPreference;
	}

	/**
	 * Set the default {@link ReadConcern}.
	 * @param defaultReadConcern the default ReadConcern to set
	 */
	public void setDefaultReadConcern(ReadConcern defaultReadConcern) {
		this.defaultReadConcern = defaultReadConcern;
	}

	/**
	 * Set the default {@link WriteConcern}.
	 * @param defaultWriteConcern the default WriteConcern to set
	 */
	public void setDefaultWriteConcern(WriteConcern defaultWriteConcern) {
		this.defaultWriteConcern = defaultWriteConcern;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#trace(java.lang.String)
	 */
	@Override
	public void trace(final String title, final String json) {
		if (isTraceEnabled()) {
			LOGGER.info("(TRACE) " + ((title != null) ? title : "JSON") + ": [" + json + "]");
		} else {
			LOGGER.debug(() -> ((title != null) ? title : "JSON") + ": [" + json + "]");
		}
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

		// get the database
		final MongoDatabase database = checkClient().getDatabase(checkDatabaseName());

		try {
			return operation.execute(database);
		} catch (DataAccessException e) {
			throw e;
		} catch (Exception e) {
			throw new DataAccessException("Failed to execute operation", e);
		}
	}

}
