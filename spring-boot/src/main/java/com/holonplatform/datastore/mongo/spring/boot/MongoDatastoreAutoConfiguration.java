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
package com.holonplatform.datastore.mongo.spring.boot;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.datastore.mongo.async.AsyncMongoDatastore;
import com.holonplatform.datastore.mongo.spring.boot.internal.MongoAsyncDatastoreAutoConfigurationRegistrar;
import com.holonplatform.datastore.mongo.spring.boot.internal.MongoReactiveDatastoreAutoConfigurationRegistrar;
import com.holonplatform.datastore.mongo.spring.boot.internal.MongoSyncDatastoreAutoConfigurationRegistrar;
import com.holonplatform.datastore.mongo.sync.MongoDatastore;
import com.holonplatform.spring.EnableDatastoreConfiguration;

/**
 * Spring boot auto-configuration to enable MongoDB {@link Datastore} beans.
 * 
 * @since 5.2.0
 */
@Configuration
@EnableDatastoreConfiguration
@AutoConfigureAfter(MongoAutoConfiguration.class)
public class MongoDatastoreAutoConfiguration {

	/**
	 * Sync
	 */
	@Configuration
	@ConditionalOnClass(name = "com.mongodb.client.MongoClient")
	@ConditionalOnMissingBean(MongoDatastore.class)
	@Import(MongoSyncDatastoreAutoConfigurationRegistrar.class)
	static class MongoSyncDatastoreConfiguration {

	}

	/**
	 * Async
	 */
	@Configuration
	@ConditionalOnClass(name = "com.mongodb.reactivestreams.client.MongoClient")
	@ConditionalOnMissingBean(AsyncMongoDatastore.class)
	@Import(MongoAsyncDatastoreAutoConfigurationRegistrar.class)
	static class MongoAsyncDatastoreConfiguration {

	}

	/**
	 * Reactive
	 */
	@Configuration
	@ConditionalOnClass(name = { "com.mongodb.reactivestreams.client.MongoClient", "reactor.core.publisher.Mono" })
	@ConditionalOnMissingBean(type = "com.holonplatform.datastore.mongo.reactor.ReactiveMongoDatastore")
	@Import(MongoReactiveDatastoreAutoConfigurationRegistrar.class)
	static class MongoReactiveDatastoreConfiguration {

	}

}
