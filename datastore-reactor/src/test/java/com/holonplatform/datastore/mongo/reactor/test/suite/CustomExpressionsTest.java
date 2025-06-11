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

import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.A_STR;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.BsonFilter;
import com.holonplatform.datastore.mongo.core.BsonSort;
import com.holonplatform.datastore.mongo.reactor.test.expression.IntIsFilter;
import com.holonplatform.datastore.mongo.reactor.test.expression.StrIntSort;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CustomExpressionsTest extends AbstractDatastoreOperationTest {

	@Test
	public void testResolvers() {

		final Mono<Long> op = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "cext1").set(INT, 1).build())
				.add(PropertyBox.builder(SET1).set(STR1, "cext2").set(INT, 2).build())
				.add(PropertyBox.builder(SET1).set(STR1, "cext1").set(INT, 3).build()).execute()
				.doOnSuccess(r -> assertEquals(3, r.getAffectedCount()))
				.then(getDatastore().query().withExpressionResolver(IntIsFilter.RESOLVER).target(TARGET)
						.filter(new IntIsFilter(2)).list(STR1))
				.doOnSuccess(vals -> {
					assertNotNull(vals);
					assertEquals(1, vals.size());
					assertEquals("cext2", vals.get(0));
				}).then(getDatastore().query().withExpressionResolver(StrIntSort.RESOLVER).target(TARGET)
						.sort(new StrIntSort()).list(STR1))
				.doOnSuccess(vals -> {
					assertNotNull(vals);
					assertEquals(3, vals.size());
					assertEquals("cext2", vals.get(0));
					assertEquals("cext1", vals.get(1));
					assertEquals("cext1", vals.get(2));
				}).then(getDatastore().bulkDelete(TARGET).filter(STR1.in("cext1", "cext2")).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(3L).expectComplete().verify();

	}

	@Test
	public void testBsonFilterSort() {

		final Mono<Long> op = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "cext1").set(INT, 1).set(A_STR, new String[] { "a", "b" })
						.build())
				.add(PropertyBox.builder(SET1).set(STR1, "cext2").set(INT, 2).set(A_STR, new String[] { "e", "f", "g" })
						.build())
				.add(PropertyBox.builder(SET1).set(STR1, "cext3").set(INT, 3).set(A_STR, new String[] { "a", "b", "c" })
						.build())
				.execute().doOnSuccess(r -> assertEquals(3, r.getAffectedCount()))
				.then(getDatastore().query().target(TARGET).filter(BsonFilter.create(Filters.eq("int", 2))).list(STR1))
				.doOnSuccess(vals -> {
					assertNotNull(vals);
					assertEquals(1, vals.size());
					assertEquals("cext2", vals.get(0));
				}).then(getDatastore().query().target(TARGET).filter(BsonFilter.create(Filters.size("astr", 3)))
						.sort(INT.asc()).list(STR1))
				.doOnSuccess(vals -> {
					assertNotNull(vals);
					assertEquals(2, vals.size());
					assertEquals("cext2", vals.get(0));
					assertEquals("cext3", vals.get(1));
				}).then(getDatastore().query().target(TARGET).sort(BsonSort.create(Sorts.descending("int"))).list(STR1))
				.doOnSuccess(vals -> {
					assertNotNull(vals);
					assertEquals(3, vals.size());
					assertEquals("cext3", vals.get(0));
				}).then(getDatastore().bulkDelete(TARGET).filter(STR1.in("cext1", "cext2", "cext3")).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(3L).expectComplete().verify();

	}

}
