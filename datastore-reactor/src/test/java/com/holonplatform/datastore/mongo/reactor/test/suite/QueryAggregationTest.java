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
package com.holonplatform.datastore.mongo.reactor.test.suite;

import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryAggregation;
import com.holonplatform.core.query.QueryFunction.Sum;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class QueryAggregationTest extends AbstractDatastoreOperationTest {

	@Test
	public void testQueryAggregation() {

		final Sum<Integer> SUM = INT.sum();

		final Mono<Long> op = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "g1").set(INT, 1).build())
				.add(PropertyBox.builder(SET1).set(STR1, "g2").set(INT, 2).build())
				.add(PropertyBox.builder(SET1).set(STR1, "g3").set(INT, 3).build())
				.add(PropertyBox.builder(SET1).set(STR1, "g1").set(INT, 9).build())
				.add(PropertyBox.builder(SET1).set(STR1, "g2").set(INT, 18).build()).execute()
				.doOnSuccess(r -> assertEquals(5, r.getAffectedCount()))
				.then(getDatastore().query(TARGET).aggregate(STR1).list(INT.sum())).doOnSuccess(values -> {
					assertNotNull(values);
					assertEquals(3, values.size());
					assertTrue(values.contains(Integer.valueOf(10)));
					assertTrue(values.contains(Integer.valueOf(20)));
					assertTrue(values.contains(Integer.valueOf(3)));
				}).then(getDatastore().query(TARGET).filter(STR1.in("g1", "g2")).aggregate(STR1).list(INT.sum()))
				.doOnSuccess(values -> {
					assertNotNull(values);
					assertEquals(2, values.size());
					assertTrue(values.contains(Integer.valueOf(10)));
					assertTrue(values.contains(Integer.valueOf(20)));
				})
				.then(getDatastore().query(TARGET)
						.aggregate(QueryAggregation.builder().path(STR1).filter(SUM.gt(10)).build()).list(SUM))
				.doOnSuccess(values -> {
					assertNotNull(values);
					assertEquals(1, values.size());
					assertTrue(values.contains(Integer.valueOf(20)));
				})
				.then(getDatastore().query(TARGET).filter(INT.lt(10))
						.aggregate(QueryAggregation.builder().path(STR1).filter(SUM.goe(10)).build()).list(SUM))
				.doOnSuccess(values -> {
					assertNotNull(values);
					assertEquals(1, values.size());
					assertTrue(values.contains(Integer.valueOf(10)));
				})
				.then(getDatastore().query(TARGET)
						.aggregate(QueryAggregation.builder().path(STR1).filter(SUM.eq(3).or(SUM.loe(10))).build())
						.list(SUM))
				.doOnSuccess(values -> {
					assertNotNull(values);
					assertEquals(2, values.size());
				}).then(getDatastore().bulkDelete(TARGET).filter(STR1.in("g1", "g2", "g3")).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(5L).expectComplete().verify();

	}

	@Test
	public void testQueryAggregationMulti() {

		final Property<?> MSTR2 = STR2.max();

		final Mono<Long> op = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "g1").set(INT, 1).set(STR2, "mg1").build())
				.add(PropertyBox.builder(SET1).set(STR1, "g2").set(INT, 10).set(STR2, "mg2").build())
				.add(PropertyBox.builder(SET1).set(STR1, "g2").set(INT, 20).set(STR2, "mg3").build())
				.add(PropertyBox.builder(SET1).set(STR1, "g1").set(INT, 1).set(STR2, "mg1").build())
				.add(PropertyBox.builder(SET1).set(STR1, "g2").set(INT, 10).set(STR2, "mg2").build()).execute()
				.doOnSuccess(r -> assertEquals(5, r.getAffectedCount()))
				.then(getDatastore().query(TARGET).aggregate(STR1, INT).list(STR2.max())).doOnSuccess(values -> {
					assertNotNull(values);
					assertEquals(3, values.size());
					assertTrue(values.contains("mg1"));
					assertTrue(values.contains("mg2"));
					assertTrue(values.contains("mg3"));
				}).then(getDatastore().query(TARGET).aggregate(STR1, INT).sort(STR1.desc()).sort(INT.asc()).list(STR1, INT,
						MSTR2))
				.doOnSuccess(pbs -> {
					assertNotNull(pbs);
					assertEquals(3, pbs.size());

					PropertyBox pb = pbs.get(0);
					assertEquals("mg2", pb.getValue(MSTR2));
					pb = pbs.get(1);
					assertEquals("mg3", pb.getValue(MSTR2));
					pb = pbs.get(2);
					assertEquals("mg1", pb.getValue(MSTR2));
				}).then(getDatastore().bulkDelete(TARGET).filter(STR1.in("g1", "g2", "g3")).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(5L).expectComplete().verify();
	}

}
