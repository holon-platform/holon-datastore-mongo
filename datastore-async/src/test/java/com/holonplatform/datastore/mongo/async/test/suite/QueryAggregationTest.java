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

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryAggregation;
import com.holonplatform.core.query.QueryFunction.Sum;

public class QueryAggregationTest extends AbstractDatastoreOperationTest {

	@Test
	public void testQueryAggregation() {

		long count = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "g1").set(INT, 1).build())
				.add(PropertyBox.builder(SET1).set(STR, "g2").set(INT, 2).build())
				.add(PropertyBox.builder(SET1).set(STR, "g3").set(INT, 3).build())
				.add(PropertyBox.builder(SET1).set(STR, "g1").set(INT, 9).build())
				.add(PropertyBox.builder(SET1).set(STR, "g2").set(INT, 18).build()).execute()
				.thenAccept(r -> assertEquals(5, r.getAffectedCount()))
				.thenCompose(x -> getDatastore().query(TARGET).aggregate(STR).list(INT.sum())).thenAccept(values -> {
					assertNotNull(values);
					assertEquals(3, values.size());
					assertTrue(values.contains(Integer.valueOf(10)));
					assertTrue(values.contains(Integer.valueOf(20)));
					assertTrue(values.contains(Integer.valueOf(3)));
				})
				.thenCompose(
						x -> getDatastore().query(TARGET).filter(STR.in("g1", "g2")).aggregate(STR).list(INT.sum()))
				.thenAccept(values -> {
					assertNotNull(values);
					assertEquals(2, values.size());
					assertTrue(values.contains(Integer.valueOf(10)));
					assertTrue(values.contains(Integer.valueOf(20)));
				}).thenCompose(x -> {
					final Sum<Integer> SUM = INT.sum();
					return getDatastore().query(TARGET)
							.aggregate(QueryAggregation.builder().path(STR).filter(SUM.gt(10)).build()).list(SUM);
				}).thenAccept(values -> {
					assertNotNull(values);
					assertEquals(1, values.size());
					assertTrue(values.contains(Integer.valueOf(20)));
				}).thenCompose(x -> {
					final Sum<Integer> SUM = INT.sum();
					return getDatastore().query(TARGET).filter(INT.lt(10))
							.aggregate(QueryAggregation.builder().path(STR).filter(SUM.goe(10)).build()).list(SUM);
				}).thenAccept(values -> {
					assertNotNull(values);
					assertEquals(1, values.size());
					assertTrue(values.contains(Integer.valueOf(10)));
				}).thenCompose(x -> {
					final Sum<Integer> SUM = INT.sum();
					return getDatastore().query(TARGET)
							.aggregate(QueryAggregation.builder().path(STR).filter(SUM.eq(3).or(SUM.loe(10))).build())
							.list(SUM);
				}).thenAccept(values -> {
					assertNotNull(values);
					assertEquals(2, values.size());
				}).thenCompose(x -> getDatastore().bulkDelete(TARGET).filter(STR.in("g1", "g2", "g3")).execute())
				.thenApply(r -> r.getAffectedCount()).toCompletableFuture().join();

		assertEquals(5, count);

	}

	@Test
	public void testQueryAggregationMulti() {

		final Property<?> MSTR2 = STR2.max();

		long count = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "g1").set(INT, 1).set(STR2, "mg1").build())
				.add(PropertyBox.builder(SET1).set(STR, "g2").set(INT, 10).set(STR2, "mg2").build())
				.add(PropertyBox.builder(SET1).set(STR, "g2").set(INT, 20).set(STR2, "mg3").build())
				.add(PropertyBox.builder(SET1).set(STR, "g1").set(INT, 1).set(STR2, "mg1").build())
				.add(PropertyBox.builder(SET1).set(STR, "g2").set(INT, 10).set(STR2, "mg2").build()).execute()
				.thenAccept(r -> assertEquals(5, r.getAffectedCount()))
				.thenCompose(x -> getDatastore().query(TARGET).aggregate(STR, INT).list(STR2.max()))
				.thenAccept(values -> {
					assertNotNull(values);
					assertEquals(3, values.size());
					assertTrue(values.contains("mg1"));
					assertTrue(values.contains("mg2"));
					assertTrue(values.contains("mg3"));
				}).thenCompose(x -> {
					return getDatastore().query(TARGET).aggregate(STR, INT).sort(STR.desc()).sort(INT.asc()).list(STR,
							INT, MSTR2);
				}).thenAccept(pbs -> {
					assertNotNull(pbs);
					assertEquals(3, pbs.size());

					PropertyBox pb = pbs.get(0);
					assertEquals("mg2", pb.getValue(MSTR2));
					pb = pbs.get(1);
					assertEquals("mg3", pb.getValue(MSTR2));
					pb = pbs.get(2);
					assertEquals("mg1", pb.getValue(MSTR2));
				}).thenCompose(x -> getDatastore().bulkDelete(TARGET).filter(STR.in("g1", "g2", "g3")).execute())
				.thenApply(r -> r.getAffectedCount()).toCompletableFuture().join();

		assertEquals(5, count);
	}

}
