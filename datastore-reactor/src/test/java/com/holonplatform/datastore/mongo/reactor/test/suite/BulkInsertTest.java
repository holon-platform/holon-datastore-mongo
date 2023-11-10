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

import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.ID4;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET4;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.DocumentWriteOption;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class BulkInsertTest extends AbstractDatastoreOperationTest {

	@Test
	public void testBulkInsert() {

		final Flux<Long> op = getDatastore().bulkInsert(TARGET, SET1).add(PropertyBox.builder(SET1).set(STR, "bkiv1").build())
				.add(PropertyBox.builder(SET1).set(STR, "bkiv2").build())
				.add(PropertyBox.builder(SET1).set(STR, "bkiv3").build()).execute().map(r -> r.getAffectedCount())
				// count
				.concatWith(getDatastore().query(TARGET).filter(STR.in("bkiv1", "bkiv2", "bkiv3")).count())
				// query
				.concatWith(getDatastore().query(TARGET).filter(STR.in("bkiv1", "bkiv2", "bkiv3")).sort(STR.asc())
						.list(STR).map(rs -> {
							if (rs.size() == 3 && "bkiv1".equals(rs.get(0)) && "bkiv2".equals(rs.get(1))
									&& "bkiv3".equals(rs.get(2))) {
								return Long.valueOf(15);
							}
							return Long.valueOf(0);
						}))
				// delete
				.concatWith(getDatastore().bulkDelete(TARGET).filter(STR.in("bkiv1", "bkiv2", "bkiv3")).execute()
						.map(r -> r.getAffectedCount()))
				// count
				.concatWith(getDatastore().query(TARGET).filter(STR.in("bkiv1", "bkiv2", "bkiv3")).count())
				.subscribeOn(Schedulers.boundedElastic());

		StepVerifier.create(op).expectNext(3L).expectNext(3L).expectNext(15L).expectNext(3L).expectNext(0L)
				.expectComplete().verify();
	}

	@Test
	public void testBulkInsertIds() {

		final PropertyBox v1 = PropertyBox.builder(SET1).set(STR, "bkiv10").build();
		final PropertyBox v2 = PropertyBox.builder(SET1).set(STR, "bkiv11").build();
		final PropertyBox v3 = PropertyBox.builder(SET1).set(STR, "bkiv12").build();

		final Flux<Long> op = getDatastore().bulkInsert(TARGET, SET1, DefaultWriteOption.BRING_BACK_GENERATED_IDS).add(v1)
				.add(v2).add(v3).execute().map(r -> r.getAffectedCount())
				.concatWith(getDatastore().delete(TARGET, v1).map(r -> r.getAffectedCount()))
				.concatWith(getDatastore().delete(TARGET, v2).map(r -> r.getAffectedCount()))
				.concatWith(getDatastore().delete(TARGET, v3).map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(3L).expectNext(1L).expectNext(1L).expectNext(1L).expectComplete().verify();

		assertTrue(v1.containsValue(ID));
		assertTrue(v2.containsValue(ID));
		assertTrue(v3.containsValue(ID));

	}

	@Test
	public void testBulkInsertOptions() {

		final Flux<Long> op = getDatastore()
				.bulkInsert(TARGET, SET1, DocumentWriteOption.BYPASS_VALIDATION, DocumentWriteOption.UNORDERED)
				.add(PropertyBox.builder(SET1).set(STR, "bkiv20").build())
				.add(PropertyBox.builder(SET1).set(STR, "bkiv21").build()).execute().map(r -> r.getAffectedCount())
				.concatWith(getDatastore().bulkDelete(TARGET).filter(STR.eq("bkiv20").or(STR.eq("bkiv21"))).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(2L).expectNext(2L).expectComplete().verify();

	}

	@Test
	public void testUpdateIdPropertyValue() {

		final Flux<Long> op = getDatastore().bulkInsert(TARGET, SET4)
				.add(PropertyBox.builder(SET4).set(STR, "ubkiv200").build())
				.add(PropertyBox.builder(SET4).set(STR, "ubkiv201").build()).execute().map(r -> r.getAffectedCount())
				.concatWith(getDatastore().query(TARGET).filter(STR.in("ubkiv200", "ubkiv201")).list(ID4)
						.map(l -> Long.valueOf(l.size())))
				.concatWith(getDatastore().bulkDelete(TARGET).filter(STR.eq("ubkiv200").or(STR.eq("ubkiv201")))
						.execute().map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(2L).expectNext(2L).expectNext(2L).expectComplete().verify();
	}

}
