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

import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.DBL;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.STR1;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.STR2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.PropertyBox;

public class AggregationFunctionsTest extends AbstractDatastoreOperationTest {

	@Test
	public void testAggregationFunctions() {

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "tmpft1").set(INT, 1).set(DBL, 1d).set(STR2, "v1").build())
				.add(PropertyBox.builder(SET1).set(STR1, "tmpft2").set(INT, 2).set(STR2, "v2").build())
				.add(PropertyBox.builder(SET1).set(STR1, "tmpft3").set(DBL, 2d).set(STR2, "v1").build()).execute()
				.thenAccept(r -> assertEquals(3, r.getAffectedCount())).thenAccept(v -> {
					Integer mv = getDatastore().query().target(TARGET).findOne(INT.max()).toCompletableFuture().join()
							.orElse(null);
					assertNotNull(mv);
					assertEquals(Integer.valueOf(2), mv);
				}).thenAccept(v -> {
					Integer mv = getDatastore().query().target(TARGET).findOne(INT.min()).toCompletableFuture().join()
							.orElse(null);
					assertNotNull(mv);
					assertEquals(Integer.valueOf(1), mv);
				}).thenAccept(v -> {
					Integer mv = getDatastore().query().target(TARGET).findOne(INT.sum()).toCompletableFuture().join()
							.orElse(null);
					assertNotNull(mv);
					assertEquals(Integer.valueOf(3), mv);
				}).thenAccept(v -> {
					Double dv = getDatastore().query().target(TARGET).findOne(DBL.avg()).toCompletableFuture().join()
							.orElse(null);
					assertNotNull(dv);
					assertEquals(Double.valueOf(1.5), dv);
				}).thenAccept(v -> {
					Long ct = getDatastore().query().target(TARGET).findOne(INT.count()).toCompletableFuture().join()
							.orElse(null);
					assertNotNull(ct);
					assertEquals(Long.valueOf(2), ct);
				}).thenAccept(v -> {
					Long ct = getDatastore().query().target(TARGET).findOne(STR2.count()).toCompletableFuture().join()
							.orElse(null);
					assertNotNull(ct);
					assertEquals(Long.valueOf(3), ct);
				}).thenAccept(v -> {
					Long ct = getDatastore().query().target(TARGET).distinct().findOne(STR2.count())
							.toCompletableFuture().join().orElse(null);
					assertNotNull(ct);
					assertEquals(Long.valueOf(2), ct);
				}).thenCompose(v -> {
					return getDatastore().bulkDelete(TARGET).filter(STR1.in("tmpft1", "tmpft2", "tmpft3")).execute();
				}).toCompletableFuture().join();

		assertEquals(3, result.getAffectedCount());

	}

}
