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
package com.holonplatform.datastore.mongo.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.holonplatform.core.datastore.DataContextBound;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.enumerations.MongoReadConcern;
import com.holonplatform.datastore.mongo.core.enumerations.MongoReadPreference;
import com.holonplatform.datastore.mongo.core.enumerations.MongoWriteConcern;
import com.holonplatform.datastore.mongo.spring.internal.MongoAsyncDatastoreRegistrar;
import com.holonplatform.spring.EnableDatastoreConfiguration;
import com.holonplatform.spring.PrimaryMode;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

/**
 * Annotation to be used on Spring Configuration classes to setup an asynchronous MongoDB {@link Datastore}.
 *
 * @since 5.2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(MongoAsyncDatastoreRegistrar.class)
@EnableDatastoreConfiguration
public @interface EnableMongoAsyncDatastore {

	/**
	 * Default Datastore registration bean name.
	 */
	public static final String DEFAULT_DATASTORE_BEAN_NAME = "mongoAsyncDatastore";

	/**
	 * Optional data context id to use to discriminate Datastores when more than one persistence source is configured,
	 * i.e. when multiple <code>MongoClient</code> beans are configured in context.
	 * <p>
	 * The configured data context id will be returned by the {@link DataContextBound#getDataContextId()} method of the
	 * registered {@link Datastore}.
	 * </p>
	 * <p>
	 * When a data context id is specified, the registered Datastore is bound to the <code>MongoClient</code> with a
	 * matching data context id, if available. During registration phase, if the data context id is not null/empty and a
	 * {@link #mongoClientReference()} is not specified, a <code>MongoClient</code> bean is searched in context using
	 * the bean name pattern: <code>mongoClient_[datacontextid]</code> where <code>[datacontextid]</code> is equal to
	 * {@link #dataContextId()} attribute.
	 * </p>
	 * @return Data context id
	 */
	String dataContextId() default "";

	/**
	 * Configures the name of the <code>MongoClient</code> bean definition to be used to create the {@link Datastore}
	 * registered using this annotation. See {@link #dataContextId()} for informations about <code>MongoClient</code>
	 * bean lookup when a specific name is not configured.
	 * <p>
	 * By default, the {@link MongoDatastoreConfigProperties#DEFAULT_MONGO_CLIENT_BEAN_NAME} bean name is used.
	 * </p>
	 * @return The name of the <code>MongoClient</code> bean definition to be used to create the {@link Datastore}
	 */
	String mongoClientReference() default "";

	/**
	 * Get the database name to use.
	 * @return the database name
	 */
	String database() default "";

	/**
	 * Whether to qualify {@link Datastore} bean as <code>primary</code>, i.e. the preferential bean to be injected in a
	 * single-valued dependency when multiple candidates are present.
	 * <p>
	 * When mode is {@link PrimaryMode#AUTO}, the registred Datastore bean is marked as primary only when the
	 * <code>MongoClient</code> bean to which is bound is registered as primary bean.
	 * </p>
	 * @return Primary mode, defaults to {@link PrimaryMode#AUTO}
	 */
	PrimaryMode primary() default PrimaryMode.AUTO;

	/**
	 * Get the default {@link ReadPreference} for query or data read operations.
	 * @return The default {@link ReadPreference} using the {@link MongoReadPreference} enumeration
	 */
	MongoReadPreference readPreference() default MongoReadPreference.DEFAULT;

	/**
	 * Get the default {@link ReadConcern} for the read operations isolation level.
	 * @return The default {@link ReadConcern} using the {@link MongoReadConcern} enumeration
	 */
	MongoReadConcern readConcern() default MongoReadConcern.DEFAULT;

	/**
	 * Get the default {@link WriteConcern} for write operations acknowledgment.
	 * @return The default {@link WriteConcern} using the {@link MongoWriteConcern} enumeration
	 */
	MongoWriteConcern writeConcern() default MongoWriteConcern.DEFAULT;

	/**
	 * The default {@link EnumCodecStrategy} to use when encoding and decoding enum type values.
	 * @return The default {@link EnumCodecStrategy}, defaults to {@link EnumCodecStrategy#NAME}
	 */
	EnumCodecStrategy enumCodecStrategy() default EnumCodecStrategy.NAME;

}
