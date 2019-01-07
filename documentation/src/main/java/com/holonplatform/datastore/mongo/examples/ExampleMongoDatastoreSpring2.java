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
package com.holonplatform.datastore.mongo.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.async.datastore.AsyncDatastore;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.datastore.mongo.spring.EnableMongoAsyncDatastore;
import com.holonplatform.datastore.mongo.spring.EnableMongoDatastore;

public class ExampleMongoDatastoreSpring2 {

	// tag::clientref[]
	@Configuration
	@EnableMongoDatastore(database = "test", mongoClientReference = "syncMongoClient") // <1>
	@EnableMongoAsyncDatastore(database = "test", mongoClientReference = "asyncMongoClient") // <2>
	class Config {

		@Bean
		public com.mongodb.client.MongoClient syncMongoClient() {
			return com.mongodb.client.MongoClients.create();
		}

		@Bean
		public com.mongodb.reactivestreams.client.MongoClient asyncMongoClient() {
			return com.mongodb.reactivestreams.client.MongoClients.create();
		}

	}

	@Autowired
	Datastore datastore;

	@Autowired
	AsyncDatastore asyncDatastore;
	// end::clientref[]

}
