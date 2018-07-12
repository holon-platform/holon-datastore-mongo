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
package com.holonplatform.datastore.mongo.spring.internal;

import java.util.Map;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import com.holonplatform.core.datastore.DatastoreConfigProperties;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.datastore.mongo.async.AsyncMongoDatastore;
import com.holonplatform.datastore.mongo.async.internal.DefaultAsyncMongoDatastore;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.enumerations.MongoReadConcern;
import com.holonplatform.datastore.mongo.core.enumerations.MongoReadPreference;
import com.holonplatform.datastore.mongo.core.enumerations.MongoWriteConcern;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;
import com.holonplatform.datastore.mongo.spring.EnableMongoAsyncDatastore;
import com.holonplatform.datastore.mongo.spring.MongoDatastoreConfigProperties;
import com.holonplatform.spring.EnvironmentConfigPropertyProvider;
import com.holonplatform.spring.PrimaryMode;
import com.holonplatform.spring.internal.AbstractConfigPropertyRegistrar;
import com.holonplatform.spring.internal.BeanRegistryUtils;
import com.holonplatform.spring.internal.GenericDataContextBoundBeanDefinition;

/**
 * Registrar for MongoDB async Datastore bean registration using the {@link EnableAsyncMongoDatastore} annotation.
 * 
 * @since 5.2.0
 */
public class MongoAsyncDatastoreRegistrar extends AbstractConfigPropertyRegistrar implements BeanClassLoaderAware {

	/*
	 * Logger
	 */
	private static final Logger LOGGER = MongoDatastoreLogger.create();

	/**
	 * Beans class loader
	 */
	private ClassLoader beanClassLoader;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang.ClassLoader)
	 */
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(org.springframework.
	 * core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

		if (!annotationMetadata.isAnnotated(EnableMongoAsyncDatastore.class.getName())) {
			// ignore call from sub classes
			return;
		}

		Map<String, Object> attributes = annotationMetadata
				.getAnnotationAttributes(EnableMongoAsyncDatastore.class.getName());

		// attributes
		String dataContextId = BeanRegistryUtils.getAnnotationValue(attributes, "dataContextId", null);
		String mongoClientReference = BeanRegistryUtils.getAnnotationValue(attributes, "mongoClientReference", null);

		String mongoClientBeanName = mongoClientReference;
		if (mongoClientBeanName == null) {
			mongoClientBeanName = BeanRegistryUtils.buildBeanName(dataContextId,
					MongoDatastoreConfigProperties.DEFAULT_MONGO_CLIENT_BEAN_NAME);
		}

		PrimaryMode primaryMode = BeanRegistryUtils.getAnnotationValue(attributes, "primary", PrimaryMode.AUTO);

		// defaults
		MongoDatastoreConfigProperties defaultConfig = MongoDatastoreConfigProperties.builder(dataContextId)
				.withProperty(MongoDatastoreConfigProperties.PRIMARY,
						(primaryMode == PrimaryMode.TRUE) ? Boolean.TRUE : null)
				.withProperty(MongoDatastoreConfigProperties.DATABASE,
						BeanRegistryUtils.getAnnotationValue(attributes, "database", null))
				.withProperty(MongoDatastoreConfigProperties.READ_PREFERENCE,
						BeanRegistryUtils.getAnnotationValue(attributes, "readPreference", MongoReadPreference.DEFAULT))
				.withProperty(MongoDatastoreConfigProperties.READ_CONCERN,
						BeanRegistryUtils.getAnnotationValue(attributes, "readConcern", MongoReadConcern.DEFAULT))
				.withProperty(MongoDatastoreConfigProperties.WRITE_CONCERN,
						BeanRegistryUtils.getAnnotationValue(attributes, "writeConcern", MongoWriteConcern.DEFAULT))
				.withProperty(MongoDatastoreConfigProperties.ENUM_CODEC_STRATEGY, BeanRegistryUtils
						.getAnnotationValue(attributes, "enumCodecStrategy", EnumCodecStrategy.getDefault()))
				.build();

		// register Datastore
		registerAsyncDatastore(registry, getEnvironment(), dataContextId, mongoClientBeanName, defaultConfig,
				beanClassLoader);
	}

	/**
	 * Register a {@link AsyncMongoDatastore} bean.
	 * @param registry BeanDefinitionRegistry
	 * @param environment Spring environment
	 * @param dataContextId Data context id
	 * @param mongoClientBeanName MongoClient bean name reference
	 * @param defaultConfig Default configuration properties
	 * @param beanClassLoader Bean class loader
	 * @return Registered Datastore bean name
	 */
	public static String registerAsyncDatastore(BeanDefinitionRegistry registry, Environment environment,
			String dataContextId, String mongoClientBeanName, MongoDatastoreConfigProperties defaultConfig,
			ClassLoader beanClassLoader) {

		// Datastore configuration
		DatastoreConfigProperties datastoreConfig = DatastoreConfigProperties.builder(dataContextId)
				.withPropertySource(EnvironmentConfigPropertyProvider.create(environment)).build();

		// Mongo Datastore configuration
		MongoDatastoreConfigProperties mongoDatastoreConfig = MongoDatastoreConfigProperties.builder(dataContextId)
				.withPropertySource(EnvironmentConfigPropertyProvider.create(environment)).build();

		// Configuration
		boolean primary = defaultConfig
				.getConfigPropertyValueOrElse(MongoDatastoreConfigProperties.PRIMARY,
						() -> mongoDatastoreConfig.getConfigPropertyValue(MongoDatastoreConfigProperties.PRIMARY))
				.orElse(false);

		String database = defaultConfig
				.getConfigPropertyValueOrElse(MongoDatastoreConfigProperties.DATABASE,
						() -> mongoDatastoreConfig.getConfigPropertyValue(MongoDatastoreConfigProperties.DATABASE))
				.orElse(null);

		MongoReadPreference readPreference = defaultConfig
				.getConfigPropertyValueOrElse(MongoDatastoreConfigProperties.READ_PREFERENCE,
						() -> mongoDatastoreConfig
								.getConfigPropertyValue(MongoDatastoreConfigProperties.READ_PREFERENCE))
				.orElse(MongoReadPreference.DEFAULT);

		MongoReadConcern readConcern = defaultConfig
				.getConfigPropertyValueOrElse(MongoDatastoreConfigProperties.READ_CONCERN,
						() -> mongoDatastoreConfig.getConfigPropertyValue(MongoDatastoreConfigProperties.READ_CONCERN))
				.orElse(MongoReadConcern.DEFAULT);

		MongoWriteConcern writeConcern = defaultConfig
				.getConfigPropertyValueOrElse(MongoDatastoreConfigProperties.WRITE_CONCERN,
						() -> mongoDatastoreConfig.getConfigPropertyValue(MongoDatastoreConfigProperties.WRITE_CONCERN))
				.orElse(MongoWriteConcern.DEFAULT);

		EnumCodecStrategy enumCodecStrategy = defaultConfig
				.getConfigPropertyValueOrElse(MongoDatastoreConfigProperties.ENUM_CODEC_STRATEGY,
						() -> mongoDatastoreConfig
								.getConfigPropertyValue(MongoDatastoreConfigProperties.ENUM_CODEC_STRATEGY))
				.orElse(EnumCodecStrategy.getDefault());

		// check primary
		if (!primary) {
			if (registry.containsBeanDefinition(mongoClientBeanName)) {
				BeanDefinition bd = registry.getBeanDefinition(mongoClientBeanName);
				primary = bd.isPrimary();
			}
		}

		// create bean definition
		GenericDataContextBoundBeanDefinition definition = new GenericDataContextBoundBeanDefinition();
		definition.setDataContextId(dataContextId);

		final Class<?> datastoreClass = DefaultAsyncMongoDatastore.class;

		definition.setBeanClass(datastoreClass);

		definition.setAutowireCandidate(true);
		definition.setPrimary(primary);
		definition.setDependsOn(mongoClientBeanName);

		if (dataContextId != null) {
			definition.addQualifier(new AutowireCandidateQualifier(Qualifier.class, dataContextId));
		}

		String beanName = BeanRegistryUtils.buildBeanName(dataContextId,
				EnableMongoAsyncDatastore.DEFAULT_DATASTORE_BEAN_NAME);

		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.add("initializationClassLoader", beanClassLoader);
		pvs.add("client", new RuntimeBeanReference(mongoClientBeanName));
		pvs.add("databaseName", database);

		if (dataContextId != null) {
			pvs.add("dataContextId", dataContextId);
		}

		if (readPreference != null && readPreference != MongoReadPreference.DEFAULT) {
			pvs.add("defaultReadPreference", readPreference);
		}
		if (readConcern != null && readConcern != MongoReadConcern.DEFAULT) {
			pvs.add("defaultReadConcern", readConcern);
		}
		if (writeConcern != null && writeConcern != MongoWriteConcern.DEFAULT) {
			pvs.add("defaultWriteConcern", writeConcern);
		}
		if (enumCodecStrategy != null && enumCodecStrategy != EnumCodecStrategy.getDefault()) {
			pvs.add("enumCodecStrategy", enumCodecStrategy);
		}

		if (datastoreConfig != null) {
			if (datastoreConfig.isTrace()) {
				pvs.add("traceEnabled", Boolean.TRUE);
			}
		}

		definition.setPropertyValues(pvs);

		// init method
		definition.setInitMethodName("initialize");

		registry.registerBeanDefinition(beanName, definition);

		// log
		StringBuilder log = new StringBuilder();
		if (dataContextId != null) {
			log.append("<Data context id: ");
			log.append(dataContextId);
			log.append("> ");
		}
		log.append("Registered MongoDB Async Datastore bean with name \"");
		log.append(beanName);
		log.append("\"");
		if (dataContextId != null) {
			log.append(" and qualifier \"");
			log.append(dataContextId);
			log.append("\"");
		}
		log.append(" bound to MongoClient bean: ");
		log.append(mongoClientBeanName);
		LOGGER.info(log.toString());

		return beanName;
	}

}
