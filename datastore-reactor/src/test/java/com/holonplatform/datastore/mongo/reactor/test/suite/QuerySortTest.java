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

import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class QuerySortTest extends AbstractDatastoreOperationTest {

	@Test
	public void testSorts() {

		final Mono<Long> op = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "bktfp1").set(INT, 1).build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp2").set(INT, 2).build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp3").set(INT, 3).build()).execute()
				.doOnSuccess(r -> assertEquals(3, r.getAffectedCount()))
				.then(getDatastore().query().target(TARGET).sort(STR.desc()).sort(INT.desc()).list(STR))
				.doOnSuccess(res -> {
					assertEquals(3, res.size());
					assertEquals("bktfp3", res.get(0));
				}).then(getDatastore().query().target(TARGET).sort(STR.desc()).sort(INT.asc()).list(STR))
				.doOnSuccess(res -> {
					assertEquals(3, res.size());
					assertEquals("bktfp3", res.get(0));
				}).then(getDatastore().query().target(TARGET).sort(INT.asc().and(STR.desc())).list(STR))
				.doOnSuccess(res -> {
					assertEquals(3, res.size());
					assertEquals("bktfp1", res.get(0));
				}).then(getDatastore().query().target(TARGET).sort(STR.asc()).list(STR)).doOnSuccess(res -> {
					assertEquals(3, res.size());
					assertEquals("bktfp1", res.get(0));
				}).then(getDatastore().query().target(TARGET).sort(STR.desc()).list(STR)).doOnSuccess(res -> {
					assertEquals(3, res.size());
					assertEquals("bktfp3", res.get(0));
				}).then(getDatastore().query().target(TARGET).sort(QuerySort.asc(INT)).list(STR)).doOnSuccess(res -> {
					assertEquals(3, res.size());
					assertEquals("bktfp1", res.get(0));
				})
				.then(getDatastore().query().target(TARGET).sort(QuerySort.of(STR, SortDirection.DESCENDING)).list(STR))
				.doOnSuccess(res -> {
					assertEquals(3, res.size());
					assertEquals("bktfp3", res.get(0));
				}).then(getDatastore().query().target(TARGET).sort(QuerySort.of(STR.desc(), INT.asc())).list(STR))
				.doOnSuccess(res -> {
					assertEquals(3, res.size());
					assertEquals("bktfp3", res.get(0));
				}).then(getDatastore().bulkDelete(TARGET).filter(STR.in("bktfp1", "bktfp2", "bktfp3")).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(3L).expectComplete().verify();
	}

}
