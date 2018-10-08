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

import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.STR;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.STR2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;

public class DistinctTest extends AbstractDatastoreOperationTest {

	@Test
	public void testDistinct() {

		long count = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "tmpft1").set(INT, 1).set(STR2, "v2").build())
				.add(PropertyBox.builder(SET1).set(STR, "tmpft2").set(INT, 2).set(STR2, "v1").build())
				.add(PropertyBox.builder(SET1).set(STR, "tmpft3").set(STR2, "v2").build()).execute()
				.thenAccept(r -> assertEquals(3, r.getAffectedCount()))
				.thenCompose(v -> getDatastore().query().target(TARGET).distinct().list(INT)).thenAccept(vals -> {
					assertNotNull(vals);
					assertEquals(2, vals.size());
					assertTrue(vals.contains(Integer.valueOf(1)));
					assertTrue(vals.contains(Integer.valueOf(2)));
				}).thenCompose(v -> getDatastore().query().target(TARGET).distinct().list(STR2)).thenAccept(svals -> {
					assertNotNull(svals);
					assertEquals(2, svals.size());
					assertTrue(svals.contains("v1"));
					assertTrue(svals.contains("v2"));
				})
				.thenCompose(
						v -> getDatastore().bulkDelete(TARGET).filter(STR.in("tmpft1", "tmpft2", "tmpft3")).execute())
				.thenApply(r -> r.getAffectedCount()).toCompletableFuture().join();

		assertEquals(3, count);

	}

}
