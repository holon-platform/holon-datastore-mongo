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

import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.DBL;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR2;

import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class AggregationFunctionsTest extends AbstractDatastoreOperationTest {

	@Test
	public void testAggregationFunctions() {

		final Flux<Long> op = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "tmpft1").set(INT, 1).set(DBL, 1d).set(STR2, "v1").build())
				.add(PropertyBox.builder(SET1).set(STR, "tmpft2").set(INT, 2).set(STR2, "v2").build())
				.add(PropertyBox.builder(SET1).set(STR, "tmpft3").set(DBL, 2d).set(STR2, "v1").build()).execute()
				.map(r -> r.getAffectedCount()) // 3
				.concatWith(getDatastore().query().target(TARGET).findOne(INT.max()).map(i -> Long.valueOf(i))) // 2
				.concatWith(getDatastore().query().target(TARGET).findOne(INT.min()).map(i -> Long.valueOf(i))) // 1
				.concatWith(getDatastore().query().target(TARGET).findOne(INT.sum()).map(i -> Long.valueOf(i))) // 3
				.concatWith(getDatastore().query().target(TARGET).findOne(DBL.avg())
						.map(d -> (Double.valueOf(1.5).equals(d)) ? 15L : 0L)) // 1.5
				.concatWith(getDatastore().query().target(TARGET).findOne(INT.count()).map(i -> Long.valueOf(i))) // 2
				.concatWith(getDatastore().query().target(TARGET).findOne(STR2.count()).map(i -> Long.valueOf(i))) // 3
				.concatWith(getDatastore().query().target(TARGET).distinct().findOne(STR2.count())
						.map(i -> Long.valueOf(i))) // 2
				.concatWith(getDatastore().bulkDelete(TARGET).filter(STR.in("tmpft1", "tmpft2", "tmpft3")).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(3L).expectNext(2L).expectNext(1L).expectNext(3L).expectNext(15L)
				.expectNext(2L).expectNext(3L).expectNext(2L).expectNext(3L).expectComplete().verify();

	}

}
