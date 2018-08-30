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
package com.holonplatform.datastore.mongo.core.internal.datastore;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.holonplatform.core.Expression;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.datastore.DatastoreCommodityContext;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.DatastoreConfigProperties;
import com.holonplatform.core.datastore.DatastoreOperations;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.datastore.AbstractInitializableDatastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.MongoDatastoreBuilder;
import com.holonplatform.datastore.mongo.core.config.MongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.config.MongoDatastoreExpressionResolver;
import com.holonplatform.datastore.mongo.core.document.DocumentIdResolver;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.session.ClientSession;

/**
 * Abstract MongoDB Datastore class.
 *
 * @since 5.2.0
 */
public abstract class AbstractMongoDatastore<X extends DatastoreCommodityContext, S extends ClientSession, MongoDatabase>
		extends AbstractInitializableDatastore<X> implements MongoDatastoreCommodityContext<MongoDatabase, S> {

	private static final long serialVersionUID = -378734658521151958L;

	/**
	 * Logger
	 */
	protected static final Logger LOGGER = MongoDatastoreLogger.create();

	/**
	 * Document id resolver
	 */
	protected DocumentIdResolver documentIdResolver = DocumentIdResolver.getDefault();

	/**
	 * Enum codec strategy
	 */
	protected EnumCodecStrategy enumCodecStrategy = EnumCodecStrategy.getDefault();

	/**
	 * Default {@link ReadPreference}
	 */
	protected ReadPreference defaultReadPreference;

	/**
	 * Default {@link ReadConcern}
	 */
	protected ReadConcern defaultReadConcern;

	/**
	 * Default {@link WriteConcern}
	 */
	protected WriteConcern defaultWriteConcern;

	/**
	 * Database name
	 */
	protected String databaseName;

	/**
	 * Externally provided codecs
	 */
	protected CodecRegistry additionalCodecRegistry;

	/**
	 * Constructor
	 * @param commodityFactoryType Commodity factory actual type
	 */
	@SuppressWarnings("rawtypes")
	public AbstractMongoDatastore(Class<? extends DatastoreCommodityFactory> commodityFactoryType) {
		super(commodityFactoryType, MongoDatastoreExpressionResolver.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.datastore.AbstractInitializableDatastore#initialize(java.lang.ClassLoader)
	 */
	@Override
	protected boolean initialize(ClassLoader classLoader) {
		loadExpressionResolvers(classLoader);
		loadCommodityFactories(classLoader);

		onDatastoreInitialized(classLoader);
		return true;
	}

	/**
	 * Invoked when the Datastore is initialized.
	 * @param classLoader Initialization ClassLoader
	 */
	protected abstract void onDatastoreInitialized(ClassLoader classLoader);

	/**
	 * Set the database name.
	 * @param databaseName the database name to set
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
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

	/**
	 * Get the externally provided codec registry, if available.
	 * @return Optional externally provided codec registry
	 */
	protected Optional<CodecRegistry> getAdditionalCodecRegistry() {
		return Optional.ofNullable(additionalCodecRegistry);
	}

	/**
	 * Set the additional codec registry.
	 * @param additionalCodecRegistry the additional CodecRegistry to set
	 */
	protected void setAdditionalCodecRegistry(CodecRegistry additionalCodecRegistry) {
		this.additionalCodecRegistry = additionalCodecRegistry;
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
	public void trace(final String title, final Supplier<String> json) {
		if (isTraceEnabled()) {
			LOGGER.info("(TRACE) " + ((title != null) ? title : "JSON") + ": \n" + json.get());
		} else {
			LOGGER.debug(() -> ((title != null) ? title : "JSON") + ": \n" + json.get());
		}
	}

	@SuppressWarnings("rawtypes")
	public abstract static class AbstractBuilder<MDB, CX extends DatastoreCommodityContext, S extends ClientSession, I extends AbstractMongoDatastore<CX, S, MDB>, D extends DatastoreOperations, B extends MongoDatastoreBuilder<D, B>>
			implements MongoDatastoreBuilder<D, B> {

		private final List<Codec<?>> codecs = new LinkedList<>();
		private final List<CodecProvider> codecProviders = new LinkedList<>();

		private final I datastore;

		public AbstractBuilder(I datastore) {
			super();
			this.datastore = datastore;
		}

		protected I getDatastore() {
			return datastore;
		}

		protected abstract B getActualBuilder();

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.MongoDatastoreBuilder#withCodec(org.bson.codecs.Codec)
		 */
		@Override
		public B withCodec(Codec<?> codec) {
			ObjectUtils.argumentNotNull(codec, "Codec must be not null");
			codecs.add(codec);
			return getActualBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.MongoDatastoreBuilder#withCodecProvider(org.bson.codecs.configuration.
		 * CodecProvider)
		 */
		@Override
		public B withCodecProvider(CodecProvider codecProvider) {
			ObjectUtils.argumentNotNull(codecProvider, "CodecProvider must be not null");
			codecProviders.add(codecProvider);
			return getActualBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.MongoDatastoreBuilder#readPreference(com.mongodb.ReadPreference)
		 */
		@Override
		public B readPreference(ReadPreference readPreference) {
			getDatastore().setDefaultReadPreference(readPreference);
			return getActualBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.MongoDatastoreBuilder#readConcern(com.mongodb.ReadConcern)
		 */
		@Override
		public B readConcern(ReadConcern readConcern) {
			getDatastore().setDefaultReadConcern(readConcern);
			return getActualBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.MongoDatastoreBuilder#writeConcern(com.mongodb.WriteConcern)
		 */
		@Override
		public B writeConcern(WriteConcern writeConcern) {
			getDatastore().setDefaultWriteConcern(writeConcern);
			return getActualBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.MongoDatastoreBuilder#enumCodecStrategy(com.holonplatform.datastore.
		 * mongo.core.document.EnumCodecStrategy)
		 */
		@Override
		public B enumCodecStrategy(EnumCodecStrategy defaultEnumCodecStrategy) {
			getDatastore().setEnumCodecStrategy(defaultEnumCodecStrategy);
			return getActualBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.datastore.DatastoreOperations.Builder#dataContextId(java.lang.String)
		 */
		@Override
		public B dataContextId(String dataContextId) {
			getDatastore().setDataContextId(dataContextId);
			return getActualBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.datastore.DatastoreOperations.Builder#traceEnabled(boolean)
		 */
		@Override
		public B traceEnabled(boolean trace) {
			getDatastore().setTraceEnabled(trace);
			return getActualBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.core.datastore.DatastoreOperations.Builder#configuration(com.holonplatform.core.datastore.
		 * DatastoreConfigProperties)
		 */
		@Override
		public B configuration(DatastoreConfigProperties configuration) {
			ObjectUtils.argumentNotNull(configuration, "Datastore configuration must be not null");
			getDatastore().setTraceEnabled(configuration.isTrace());
			return getActualBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.core.ExpressionResolver.ExpressionResolverBuilder#withExpressionResolver(com.holonplatform.
		 * core.ExpressionResolver)
		 */
		@Override
		public <E extends Expression, R extends Expression> B withExpressionResolver(
				ExpressionResolver<E, R> expressionResolver) {
			getDatastore().addExpressionResolver(expressionResolver);
			return getActualBuilder();
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.sync.MongoDatastore.Builder#database(java.lang.String)
		 */
		@Override
		public B database(String database) {
			ObjectUtils.argumentNotNull(database, "Database name must be not null");
			getDatastore().setDatabaseName(database);
			return getActualBuilder();
		}

		protected I configure() {
			I datastore = getDatastore();
			// codecs
			List<CodecRegistry> registries = new LinkedList<>();
			if (!codecs.isEmpty()) {
				registries.add(CodecRegistries.fromCodecs(codecs));
			}
			if (!codecProviders.isEmpty()) {
				registries.add(CodecRegistries.fromProviders(codecProviders));
			}
			if (!registries.isEmpty()) {
				datastore.setAdditionalCodecRegistry(CodecRegistries.fromRegistries(registries));
			}
			// init
			datastore.initialize();
			return datastore;
		}

	}

}
