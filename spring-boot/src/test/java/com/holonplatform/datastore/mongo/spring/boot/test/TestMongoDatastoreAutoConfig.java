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
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.holonplatform.async.datastore.AsyncDatastore;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.async.AsyncMongoDatastore;
import com.holonplatform.datastore.mongo.spring.boot.test.config.ConfigCheckCommodity;
import com.holonplatform.datastore.mongo.sync.MongoDatastore;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("p3")
public class TestMongoDatastoreAutoConfig extends AbstractMongoSpringBootTest {

	@Configuration
	@ComponentScan(basePackageClasses = ConfigCheckCommodity.class)
	@EnableAutoConfiguration
	protected static class Config {

		@Bean(name = "mongoClientSync")
		public com.mongodb.client.MongoClient mongoClientSync() {
			return com.mongodb.client.MongoClients
					.create(MongoClientSettings.builder()
							.applyToClusterSettings(
									builder -> builder.hosts(Arrays.asList(new ServerAddress("localhost", 12345))))
							.build());
		}

		@Bean(name = "mongoClientAsync")
		public com.mongodb.async.client.MongoClient mongoClientAsync() {
			return com.mongodb.async.client.MongoClients
					.create(MongoClientSettings.builder()
							.applyToClusterSettings(
									builder -> builder.hosts(Arrays.asList(new ServerAddress("localhost", 12345))))
							.build());
		}

	}

	@Autowired
	private Datastore syncDatastore;

	@Autowired
	private MongoDatastore syncMongoDatastore;

	@Autowired
	private AsyncDatastore asyncDatastore;

	@Autowired
	private AsyncMongoDatastore asyncMongoDatastore;

	@Test
	public void testConfigSync() {
		assertNotNull(syncDatastore);
		assertTrue(syncDatastore.hasCommodity(ConfigCheckCommodity.class));
		assertEquals("test3", syncDatastore.create(ConfigCheckCommodity.class).getDatabaseName());
		String databaseName = syncMongoDatastore.withDatabase(db -> {
			return db.getName();
		});
		assertEquals("test3", databaseName);
	}

	@Test
	public void testOpsSync() {
		PropertyBox value = PropertyBox.builder(ModelTest.SET).set(ModelTest.TEXT, "testv3").build();
		syncDatastore.insert(ModelTest.T1, value, DefaultWriteOption.BRING_BACK_GENERATED_IDS);
		assertNotNull(value.getValue(ModelTest.ID));
		long result = syncDatastore.delete(ModelTest.T1, value).getAffectedCount();
		assertEquals(1, result);
	}

	@Test
	public void testConfigAsync() {
		assertNotNull(asyncDatastore);
		assertTrue(asyncDatastore.hasCommodity(ConfigCheckCommodity.class));
		assertEquals("test3", asyncDatastore.create(ConfigCheckCommodity.class).getDatabaseName());
		String databaseName = asyncMongoDatastore.withDatabase(db -> {
			return db.getName();
		});
		assertEquals("test3", databaseName);
	}

	@Test
	public void testOpsAsync() throws InterruptedException, ExecutionException {
		PropertyBox value = PropertyBox.builder(ModelTest.SET).set(ModelTest.TEXT, "testv3").build();
		asyncDatastore.insert(ModelTest.T1, value, DefaultWriteOption.BRING_BACK_GENERATED_IDS).toCompletableFuture()
				.get();
		assertNotNull(value.getValue(ModelTest.ID));
		long result = asyncDatastore.delete(ModelTest.T1, value).thenApply(r -> r.getAffectedCount())
				.toCompletableFuture().get();
		assertEquals(1, result);
	}

}
