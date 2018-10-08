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
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.spring.boot.test.config.ConfigCheckCommodity;
import com.holonplatform.datastore.mongo.sync.MongoDatastore;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("p1")
public class TestMongoDatastoreAutoConfigSync extends AbstractMongoSpringBootTest {

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
	private Datastore datastore;

	@Autowired
	private MongoDatastore mongoDatastore;

	@Test
	public void testConfig() {
		assertNotNull(datastore);

		assertTrue(datastore.hasCommodity(ConfigCheckCommodity.class));

		assertEquals("test1", datastore.create(ConfigCheckCommodity.class).getDatabaseName());

		Optional<ReadPreference> rp = datastore.create(ConfigCheckCommodity.class).getDefaultReadPreference();

		assertTrue(rp.isPresent());
		assertEquals(ReadPreference.primaryPreferred(), rp.get());

		assertEquals(EnumCodecStrategy.ORDINAL, datastore.create(ConfigCheckCommodity.class).getEnumCodecStrategy());

		String databaseName = mongoDatastore.withDatabase(db -> {
			return db.getName();
		});

		assertEquals("test1", databaseName);
	}

	@Test
	public void testOps() {

		PropertyBox value = PropertyBox.builder(ModelTest.SET).set(ModelTest.TEXT, "testv1").build();

		datastore.insert(ModelTest.T1, value, DefaultWriteOption.BRING_BACK_GENERATED_IDS);

		assertNotNull(value.getValue(ModelTest.ID));

		OperationResult result = datastore.delete(ModelTest.T1, value);

		assertEquals(1, result.getAffectedCount());

	}

}
