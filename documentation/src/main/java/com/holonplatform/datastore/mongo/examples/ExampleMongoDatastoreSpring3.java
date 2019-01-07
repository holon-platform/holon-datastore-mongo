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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.enumerations.MongoReadConcern;
import com.holonplatform.datastore.mongo.core.enumerations.MongoReadPreference;
import com.holonplatform.datastore.mongo.core.enumerations.MongoWriteConcern;
import com.holonplatform.datastore.mongo.spring.EnableMongoDatastore;

public class ExampleMongoDatastoreSpring3 {

	// tag::enums[]
	@Configuration
	@EnableMongoDatastore(database = "test", enumCodecStrategy = EnumCodecStrategy.ORDINAL) // <1>
	class Config {

		@Bean
		public com.mongodb.client.MongoClient mongoClient() {
			return com.mongodb.client.MongoClients.create();
		}

	}
	// end::enums[]

	// tag::prefs[]
	@Configuration
	@EnableMongoDatastore(database = "test", readPreference = MongoReadPreference.PRIMARY, // <1>
			readConcern = MongoReadConcern.LOCAL, // <2>
			writeConcern = MongoWriteConcern.ACKNOWLEDGED // <3>
	)
	class Config2 {

		@Bean
		public com.mongodb.client.MongoClient mongoClient() {
			return com.mongodb.client.MongoClients.create();
		}

	}
	// end::prefs[]

}
