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
package com.holonplatform.datastore.mongo.spring.boot.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.holonplatform.datastore.mongo.spring.boot.test.config.ConfigCheckCommodity;
import com.holonplatform.reactor.datastore.ReactiveDatastore;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("p4")
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.4.11")
public class TestMongoDatastoreAutoConfigReactive extends AbstractMongoSpringBootTest {

	@Configuration
	@ComponentScan(basePackageClasses = ConfigCheckCommodity.class)
	@EnableAutoConfiguration
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
	private ReactiveDatastore datastore;

	@Test
	public void testConfig() {
		assertNotNull(datastore);

		assertTrue(datastore.hasCommodity(ConfigCheckCommodity.class));

		assertEquals("test4", datastore.create(ConfigCheckCommodity.class).getDatabaseName());
	}

}
