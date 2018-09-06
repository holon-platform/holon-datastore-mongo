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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.holonplatform.async.datastore.AsyncDatastore;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.async.AsyncMongoDatastore;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.spring.boot.test.config.ConfigCheckCommodity;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@ActiveProfiles("p2")
public class TestMongoDatastoreAutoConfigAsync extends AbstractMongoSpringBootTest {

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
	private AsyncDatastore datastore;

	@Autowired
	private AsyncMongoDatastore mongoDatastore;

	@Test
	public void testConfig() {
		assertNotNull(datastore);

		assertTrue(datastore.hasCommodity(ConfigCheckCommodity.class));

		assertEquals("test2", datastore.create(ConfigCheckCommodity.class).getDatabaseName());

		Optional<ReadPreference> rp = datastore.create(ConfigCheckCommodity.class).getDefaultReadPreference();

		assertTrue(rp.isPresent());
		assertEquals(ReadPreference.primaryPreferred(), rp.get());

		assertEquals(EnumCodecStrategy.getDefault(),
				datastore.create(ConfigCheckCommodity.class).getEnumCodecStrategy());

		String databaseName = mongoDatastore.withDatabase(db -> {
			return db.getName();
		});

		assertEquals("test2", databaseName);
	}

	@Test
	public void testOps() throws InterruptedException, ExecutionException {

		PropertyBox value = PropertyBox.builder(ModelTest.SET).set(ModelTest.TEXT, "testv2").build();

		datastore.insert(ModelTest.T1, value, DefaultWriteOption.BRING_BACK_GENERATED_IDS).toCompletableFuture().get();

		assertNotNull(value.getValue(ModelTest.ID));

		long result = datastore.delete(ModelTest.T1, value).thenApply(r -> r.getAffectedCount()).toCompletableFuture()
				.get();

		assertEquals(1, result);

	}

}
