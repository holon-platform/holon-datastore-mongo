/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.datastore.mongo.spring;

import java.util.Optional;

import com.holonplatform.core.config.ConfigProperty;
import com.holonplatform.core.config.ConfigPropertySet;
import com.holonplatform.core.datastore.DataContextBound;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.config.DefaultConfigPropertySet;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.enumerations.MongoReadConcern;
import com.holonplatform.datastore.mongo.core.enumerations.MongoReadPreference;
import com.holonplatform.datastore.mongo.core.enumerations.MongoWriteConcern;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

/**
 * A {@link ConfigPropertySet} for MongoDB Datastore configuration, using {@link #DEFAULT_NAME} as property prefix.
 *
 * @since 5.2.0
 */
public interface MongoDatastoreConfigProperties extends ConfigPropertySet, DataContextBound {

	/**
	 * Default <code>MongoClient</code> bean name to lookup during automatic MongoDB Datastore registration.
	 */
	public static final String DEFAULT_MONGO_CLIENT_BEAN_NAME = "mongoClient";

	/**
	 * Configuration property set default name
	 */
	static final String DEFAULT_NAME = "holon.datastore.mongo";

	/**
	 * Whether to qualify the Datastore bean as <code>primary</code>, i.e. the preferential bean to be injected in a
	 * single-valued dependency when multiple candidates are present.
	 * <p>
	 * By default, the registered Datastore bean is marked as primary only when the <code>MongoClient</code> bean to
	 * which is bound is registered as primary candidate bean.
	 * </p>
	 */
	static final ConfigProperty<Boolean> PRIMARY = ConfigProperty.create("primary", Boolean.class);

	/**
	 * The database name to use.
	 */
	static final ConfigProperty<String> DATABASE = ConfigProperty.create("database", String.class);

	/**
	 * The default {@link ReadPreference} for query or data read operations.
	 * <p>
	 * Must be one of the {@link MongoReadPreference} enumeration names.
	 * </p>
	 */
	static final ConfigProperty<MongoReadPreference> READ_PREFERENCE = ConfigProperty.create("read-preference",
			MongoReadPreference.class);

	/**
	 * The default {@link ReadConcern} for the read operations isolation level.
	 * <p>
	 * Must be one of the {@link MongoReadConcern} enumeration names.
	 * </p>
	 */
	static final ConfigProperty<MongoReadConcern> READ_CONCERN = ConfigProperty.create("read-concern",
			MongoReadConcern.class);

	/**
	 * The default {@link WriteConcern} for write operations acknowledgment.
	 * <p>
	 * Must be one of the {@link MongoWriteConcern} enumeration names.
	 * </p>
	 */
	static final ConfigProperty<MongoWriteConcern> WRITE_CONCERN = ConfigProperty.create("write-concern",
			MongoWriteConcern.class);

	/**
	 * The default {@link EnumCodecStrategy} to use when encoding and decoding enum type values.
	 * <p>
	 * Must be one of the {@link EnumCodecStrategy} enumeration names.
	 * </p>
	 */
	static final ConfigProperty<EnumCodecStrategy> ENUM_CODEC_STRATEGY = ConfigProperty.create("enum-codec-strategy",
			EnumCodecStrategy.class);

	/**
	 * Builder to create property set instances bound to a property data source.
	 * @param dataContextId Optional data context id to which {@link Datastore} is bound
	 * @return ConfigPropertySet builder
	 */
	static Builder<MongoDatastoreConfigProperties> builder(String dataContextId) {
		return new DefaultConfigPropertySet.DefaultBuilder<>(new MongoDatastoreConfigPropertiesImpl(dataContextId));
	}

	/**
	 * Builder to create property set instances bound to a property data source, without data context id specification.
	 * @return ConfigPropertySet builder
	 */
	static Builder<MongoDatastoreConfigProperties> builder() {
		return new DefaultConfigPropertySet.DefaultBuilder<>(new MongoDatastoreConfigPropertiesImpl(null));
	}

	/**
	 * Default implementation
	 */
	static class MongoDatastoreConfigPropertiesImpl extends DefaultConfigPropertySet
			implements MongoDatastoreConfigProperties {

		private final String dataContextId;

		public MongoDatastoreConfigPropertiesImpl(String dataContextId) {
			super((dataContextId != null && !dataContextId.trim().equals("")) ? (DEFAULT_NAME + "." + dataContextId)
					: DEFAULT_NAME);
			this.dataContextId = (dataContextId != null && !dataContextId.trim().equals("")) ? dataContextId : null;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.core.datastore.DataContextBound#getDataContextId()
		 */
		@Override
		public Optional<String> getDataContextId() {
			return Optional.ofNullable(dataContextId);
		}

	}

}
