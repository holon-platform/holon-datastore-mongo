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
package com.holonplatform.datastore.mongo.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.holonplatform.datastore.mongo.spring.EnableMongoDatastore;
import com.holonplatform.datastore.mongo.sync.MongoDatastore;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestEnableMongoSyncDatastore.Config.class)
public class TestEnableMongoSyncDatastore extends AbstractMongoDBSpringTest {

	@Configuration
	@EnableMongoDatastore(database = "test")
	protected static class Config {

		@Bean
		public MongoClient mongoClient() {
			return MongoClients
					.create(MongoClientSettings.builder()
							.applyToClusterSettings(
									builder -> builder.hosts(Arrays.asList(new ServerAddress("localhost", 12345))))
							.build());
		}

	}

	@Autowired
	private MongoDatastore datastore;

	@Test
	public void testConfig() {

		assertNotNull(datastore);

		String databaseName = datastore.withDatabase(db -> {
			return db.getName();
		});

		assertEquals("test", databaseName);
	}

}
