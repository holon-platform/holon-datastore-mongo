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

import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.DAT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.TMS;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.reactor.test.data.TestValues;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class TemporalFunctionsTest extends AbstractDatastoreOperationTest {

	@Test
	public void testTemporalFunctionFilters() {

		final Mono<Long> op = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "tmpft1").set(DAT, TestValues.DAT).set(TMS, TestValues.TMS)
						.set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS).set(LTM, TestValues.LTM).build())
				.add(PropertyBox.builder(SET1).set(STR, "tmpft2").set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
						.build())
				.add(PropertyBox.builder(SET1).set(STR, "tmpft3").set(DAT, TestValues.U_DAT).set(TMS, TestValues.U_TMS)
						.set(LDAT, TestValues.U_LDAT).set(LTMS, TestValues.U_LTMS).set(LTM, TestValues.U_LTM).build())
				.execute().doOnSuccess(r -> assertEquals(3, r.getAffectedCount()))
				.then(getDatastore().query().target(TARGET).filter(LDAT.year().eq(2018)).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(2), c))
				.then(getDatastore().query().target(TARGET).filter(LDAT.year().eq(2019)).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(LDAT.year().lt(2000)).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(0), c))
				.then(getDatastore().query().target(TARGET)
						.filter(LDAT.year().eq(2019).and(LTMS.year().eq(2018).or(LTMS.year().eq(2019)))).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore()
						.query().target(TARGET).filter(LTMS.year().eq(2018).or(LTMS.year().eq(2019))).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(3), c))
				.then(getDatastore().query().target(TARGET)
						.filter(LDAT.year().in(2018, 2019).and(LTMS.year().eq(2018).or(LTMS.year().eq(2019)))).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(3), c))
				.then(getDatastore().query().target(TARGET).filter(LTMS.month().eq(2)).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(2), c))
				.then(getDatastore().query().target(TARGET).filter(LDAT.day().eq(4)).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(LTMS.hour().eq(11).or(LTM.hour().eq(18))).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(2), c))
				.then(getDatastore().query().target(TARGET).filter(DAT.year().eq(2018)).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(TMS.year().in(2018, 2019)).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(2), c)).then(getDatastore().bulkDelete(TARGET)
						.filter(STR.in("tmpft1", "tmpft2", "tmpft3")).execute().map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(3L).expectComplete().verify();

	}

}
