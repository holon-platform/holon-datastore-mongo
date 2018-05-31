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
package com.holonplatform.datastore.mongo.async.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.holonplatform.core.internal.utils.TestUtils;
import com.holonplatform.datastore.mongo.async.AsyncMongoDatastore;

public class TestConfigUT extends AbstractMongoDBTest {

	@Test
	public void testDatastoreConfig() {

		final AsyncMongoDatastore ds1 = AsyncMongoDatastore.builder().build();

		TestUtils.expectedException(IllegalStateException.class, () -> ds1.withDatabase(db -> {
			db.getName();
		}));

		final AsyncMongoDatastore ds2 = AsyncMongoDatastore.builder().client(getMongo()).build();

		TestUtils.expectedException(IllegalStateException.class, () -> ds2.withDatabase(db -> {
			db.getName();
		}));

		final AsyncMongoDatastore ds = AsyncMongoDatastore.builder().client(getMongo()).database("testdb1").build();

		String name = ds.withDatabase(db -> {
			return db.getName();
		});

		assertEquals("testdb1", name);

	}

}
