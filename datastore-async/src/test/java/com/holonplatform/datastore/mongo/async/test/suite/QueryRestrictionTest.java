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
package com.holonplatform.datastore.mongo.async.test.suite;

import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;

public class QueryRestrictionTest extends AbstractDatastoreOperationTest {

	@Test
	public void testRestrictions() {

		long count = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "bktfp1").set(INT, 1).build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp2").set(INT, 2).build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp3").set(INT, 3).build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp4").set(INT, 4).build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp5").set(INT, 5).build()).execute()
				.thenAccept(r -> assertEquals(5, r.getAffectedCount()))
				.thenCompose(x -> getDatastore().query(TARGET).sort(INT.asc()).limit(2).list(STR))
				.thenAccept(values -> {
					assertEquals(2, values.size());
					assertEquals("bktfp1", values.get(0));
					assertEquals("bktfp2", values.get(1));
				}).thenCompose(x -> getDatastore().query(TARGET).sort(INT.asc()).limit(3).offset(2).list(STR))
				.thenAccept(values -> {
					assertEquals(3, values.size());
					assertEquals("bktfp3", values.get(0));
					assertEquals("bktfp4", values.get(1));
					assertEquals("bktfp5", values.get(2));
				}).thenCompose(x -> getDatastore().query(TARGET).sort(INT.asc()).limit(2).offset(4).list(STR))
				.thenAccept(values -> {
					assertEquals(1, values.size());
					assertEquals("bktfp5", values.get(0));
				})
				.thenCompose(x -> getDatastore().bulkDelete(TARGET)
						.filter(STR.in("bktfp1", "bktfp2", "bktfp3", "bktfp4", "bktfp5")).execute())
				.thenApply(r -> r.getAffectedCount()).toCompletableFuture().join();

		assertEquals(5, count);

	}

}
