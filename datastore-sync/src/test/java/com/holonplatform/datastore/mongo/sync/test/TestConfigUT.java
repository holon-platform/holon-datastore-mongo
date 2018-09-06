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
package com.holonplatform.datastore.mongo.sync.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.holonplatform.core.internal.utils.TestUtils;
import com.holonplatform.datastore.mongo.sync.MongoDatastore;

public class TestConfigUT extends AbstractMongoDBTest {

	/*
	 * @Test public void testConfig() { MongoDatabase database = getMongo().getDatabase("testdb");
	 * MongoCollection<Document> collection = database.getCollection("test_collection"); Document doc = new
	 * Document("name", "MongoDB") .append("type", "database") .append("count", 1) .append("versions",
	 * Arrays.asList("v3.2", "v3.0", "v2.6")) .append("info", new Document("x", 203).append("y", 102));
	 * collection.insertOne(doc); ObjectId oid = doc.getObjectId("_id"); System.err.println(oid);
	 * System.err.println(oid.toHexString()); System.err.println(oid.toByteArray()); }
	 */

	@Test
	public void testDatastoreConfig() {

		final MongoDatastore ds1 = MongoDatastore.builder().build();

		TestUtils.expectedException(IllegalStateException.class, () -> ds1.withDatabase(db -> {
			db.getName();
		}));

		final MongoDatastore ds2 = MongoDatastore.builder().client(getMongo()).build();

		TestUtils.expectedException(IllegalStateException.class, () -> ds2.withDatabase(db -> {
			db.getName();
		}));

		final MongoDatastore ds = MongoDatastore.builder().client(getMongo()).database("testdb1").build();

		String name = ds.withDatabase(db -> {
			return db.getName();
		});

		assertEquals("testdb1", name);

	}

}
