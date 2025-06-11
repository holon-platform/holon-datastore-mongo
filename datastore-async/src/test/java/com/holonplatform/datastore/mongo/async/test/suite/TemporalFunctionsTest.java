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

import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.DAT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.STR1;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.TMS;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.async.test.data.TestValues;

public class TemporalFunctionsTest extends AbstractDatastoreOperationTest {

	@Test
	public void testTemporalFunctionFilters() {

		long count = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "tmpft1").set(DAT, TestValues.DAT).set(TMS, TestValues.TMS)
						.set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS).set(LTM, TestValues.LTM).build())
				.add(PropertyBox.builder(SET1).set(STR1, "tmpft2").set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
						.build())
				.add(PropertyBox.builder(SET1).set(STR1, "tmpft3").set(DAT, TestValues.U_DAT).set(TMS, TestValues.U_TMS)
						.set(LDAT, TestValues.U_LDAT).set(LTMS, TestValues.U_LTMS).set(LTM, TestValues.U_LTM).build())
				.execute().thenAccept(r -> assertEquals(3, r.getAffectedCount()))
				.thenCompose(x -> getDatastore().query().target(TARGET).filter(LDAT.year().eq(2018)).count())
				.thenAccept(c -> assertEquals(Long.valueOf(2), c))
				.thenCompose(x -> getDatastore().query().target(TARGET).filter(LDAT.year().eq(2019)).count())
				.thenAccept(c -> assertEquals(Long.valueOf(1), c))
				.thenCompose(x -> getDatastore().query().target(TARGET).filter(LDAT.year().lt(2000)).count())
				.thenAccept(c -> assertEquals(Long.valueOf(0), c))
				.thenCompose(x -> getDatastore().query().target(TARGET)
						.filter(LDAT.year().eq(2019).and(LTMS.year().eq(2018).or(LTMS.year().eq(2019)))).count())
				.thenAccept(c -> assertEquals(Long.valueOf(1), c))
				.thenCompose(x -> getDatastore().query().target(TARGET)
						.filter(LTMS.year().eq(2018).or(LTMS.year().eq(2019))).count())
				.thenAccept(c -> assertEquals(Long.valueOf(3), c))
				.thenCompose(x -> getDatastore().query().target(TARGET)
						.filter(LDAT.year().in(2018, 2019).and(LTMS.year().eq(2018).or(LTMS.year().eq(2019)))).count())
				.thenAccept(c -> assertEquals(Long.valueOf(3), c))
				.thenCompose(x -> getDatastore().query().target(TARGET).filter(LTMS.month().eq(2)).count())
				.thenAccept(c -> assertEquals(Long.valueOf(2), c))
				.thenCompose(x -> getDatastore().query().target(TARGET).filter(LDAT.day().eq(4)).count())
				.thenAccept(c -> assertEquals(Long.valueOf(1), c))
				.thenCompose(x -> getDatastore().query().target(TARGET).filter(LTMS.hour().eq(11).or(LTM.hour().eq(18)))
						.count())
				.thenAccept(c -> assertEquals(Long.valueOf(2), c))
				.thenCompose(x -> getDatastore().query().target(TARGET).filter(DAT.year().eq(2018)).count())
				.thenAccept(c -> assertEquals(Long.valueOf(1), c))
				.thenCompose(x -> getDatastore().query().target(TARGET).filter(TMS.year().in(2018, 2019)).count())
				.thenAccept(c -> assertEquals(Long.valueOf(2), c))
				.thenCompose(
						x -> getDatastore().bulkDelete(TARGET).filter(STR1.in("tmpft1", "tmpft2", "tmpft3")).execute())
				.thenApply(r -> r.getAffectedCount()).toCompletableFuture().join();

		assertEquals(3, count);

	}

}
