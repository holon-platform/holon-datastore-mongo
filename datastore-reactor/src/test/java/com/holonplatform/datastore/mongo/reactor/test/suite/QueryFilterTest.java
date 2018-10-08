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
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.DBL;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR2;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.TMS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Calendar;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.reactor.test.data.TestValues;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class QueryFilterTest extends AbstractDatastoreOperationTest {

	@Test
	public void testFilters() {

		final ObjectId oid1 = new ObjectId();
		final ObjectId oid2 = new ObjectId();
		final ObjectId oid3 = new ObjectId();

		final Mono<Long> op = getDatastore()
				.insert(TARGET, PropertyBox.builder(SET1).set(ID, oid1).set(STR, "One").set(INT, 1).build())
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().insert(TARGET,
						PropertyBox.builder(SET1).set(ID, oid2).set(STR, "Two").set(INT, 2).build()))
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().insert(TARGET,
						PropertyBox.builder(SET1).set(ID, oid3).set(DBL, 7.5d).set(STR2, "+20").build()))
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().query().target(TARGET).filter(STR.eq("One")).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(STR.eq("One").not()).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(2), c))
				.then(getDatastore().query().target(TARGET).filter(STR.neq("Two")).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(2), c))
				.then(getDatastore().query().target(TARGET).filter(STR.isNotNull()).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(2), c))
				.then(getDatastore().query().target(TARGET).filter(STR.isNull()).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(STR.isNotNull().and(STR.neq("Two"))).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(DBL.isNull()).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(2), c))
				.then(getDatastore().query().target(TARGET).filter(STR.endsWith("x")).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(0), c))
				.then(getDatastore().query().target(TARGET).filter(STR.contains("w")).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(STR.containsIgnoreCase("O")).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(2), c))
				.then(getDatastore().query().target(TARGET).filter(STR.startsWith("O")).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(STR.startsWithIgnoreCase("t")).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(STR2.startsWithIgnoreCase("+")).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(STR.startsWithIgnoreCase("o")).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(DBL.gt(7d)).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(DBL.lt(8d)).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(INT.between(1, 2)).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(2), c))
				.then(getDatastore().query().target(TARGET).filter(INT.in(1, 2)).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(2), c))
				.then(getDatastore().query().target(TARGET).filter(INT.nin(1, 2)).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c))
				.then(getDatastore().query().target(TARGET).filter(INT.isNotNull().and(INT.nin(1, 2))).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(0), c))
				.then(getDatastore().query().target(TARGET).filter(INT.eq(1).or(INT.eq(2))).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(2), c))
				.then(getDatastore().query().target(TARGET).filter(INT.eq(1).and(STR.eq("One"))).count())
				.doOnSuccess(c -> assertEquals(Long.valueOf(1), c)).then(getDatastore().bulkDelete(TARGET)
						.filter(ID.in(oid1, oid2, oid3)).execute().map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(3L).expectComplete().verify();
	}

	@Test
	public void testDateAndTimes() {

		final ObjectId oid1 = new ObjectId();
		final ObjectId oid2 = new ObjectId();

		final Mono<Long> op = getDatastore()
				.insert(TARGET,
						PropertyBox.builder(SET1).set(ID, oid1).set(STR, "One").set(DAT, TestValues.DAT)
								.set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
								.set(LTM, TestValues.LTM).build())
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().insert(TARGET,
						PropertyBox.builder(SET1).set(ID, oid2).set(STR, "Two").set(DAT, TestValues.U_DAT)
								.set(TMS, TestValues.U_TMS).set(LDAT, TestValues.U_LDAT).set(LTMS, TestValues.U_LTMS)
								.set(LTM, TestValues.U_LTM).build()))
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount())).flatMap(x -> {
					final Calendar c = Calendar.getInstance();
					c.set(Calendar.YEAR, 2018);
					c.set(Calendar.MONTH, 1);
					c.set(Calendar.DAY_OF_MONTH, 7);
					return getDatastore().query().target(TARGET).filter(DAT.eq(c.getTime())).list(DAT);
				}).doOnSuccess(values -> {
					assertNotNull(values);
					assertEquals(1, values.size());

					Calendar c1 = Calendar.getInstance();
					c1.set(Calendar.YEAR, 2018);
					c1.set(Calendar.MONTH, 1);
					c1.set(Calendar.DAY_OF_MONTH, 7);
					c1.set(Calendar.HOUR_OF_DAY, 16);
					c1.set(Calendar.MINUTE, 30);
					c1.set(Calendar.SECOND, 15);
					c1.set(Calendar.MILLISECOND, 0);
				}).flatMap(x -> {
					Calendar c = Calendar.getInstance();
					c.set(Calendar.YEAR, 2018);
					c.set(Calendar.MONTH, 1);
					c.set(Calendar.DAY_OF_MONTH, 7);
					c.set(Calendar.HOUR_OF_DAY, 16);
					c.set(Calendar.MINUTE, 30);
					c.set(Calendar.SECOND, 15);
					c.set(Calendar.MILLISECOND, 0);
					return getDatastore().query().target(TARGET).filter(TMS.eq(c.getTime())).list(TMS);
				}).doOnSuccess(values -> {

					assertNotNull(values);
					assertEquals(1, values.size());

					Calendar c2 = Calendar.getInstance();
					c2.set(Calendar.YEAR, 2018);
					c2.set(Calendar.MONTH, 1);
					c2.set(Calendar.DAY_OF_MONTH, 7);
					c2.set(Calendar.HOUR_OF_DAY, 0);
					c2.set(Calendar.MINUTE, 0);
					c2.set(Calendar.SECOND, 0);
					c2.set(Calendar.MILLISECOND, 0);
				}).flatMap(x -> {

					Calendar c = Calendar.getInstance();
					c.set(Calendar.YEAR, 2018);
					c.set(Calendar.MONTH, 1);
					c.set(Calendar.DAY_OF_MONTH, 7);
					c.set(Calendar.HOUR_OF_DAY, 0);
					c.set(Calendar.MINUTE, 0);
					c.set(Calendar.SECOND, 0);
					c.set(Calendar.MILLISECOND, 0);

					return getDatastore().query().target(TARGET).filter(TMS.goe(c.getTime())).list(TMS);
				}).doOnSuccess(values -> {
					assertNotNull(values);
					assertEquals(2, values.size());
				}).then(getDatastore().query().target(TARGET).filter(LDAT.eq(LocalDate.of(2018, Month.FEBRUARY, 7)))
						.list(LDAT))
				.doOnSuccess(lvalues -> {
					assertNotNull(lvalues);
					assertEquals(1, lvalues.size());
				}).then(getDatastore().query().target(TARGET).filter(LDAT.goe(LocalDate.of(2018, Month.FEBRUARY, 7)))
						.list(LDAT))
				.doOnSuccess(lvalues -> {
					assertNotNull(lvalues);
					assertEquals(2, lvalues.size());
				}).then(getDatastore().query().target(TARGET).filter(ID.eq(oid2)).findOne(LTM)).doOnSuccess(time -> {
					assertNotNull(time);
					assertEquals(7, time.getHour());
					assertEquals(30, time.getMinute());
					assertEquals(15, time.getSecond());
				}).then(getDatastore().query().target(TARGET).filter(LTM.eq(LocalTime.of(18, 45, 30))).count())
				.doOnSuccess(cnt -> {
					assertEquals(Long.valueOf(1), cnt);
				})
				.then(getDatastore().query().target(TARGET)
						.filter(LTMS.eq(LocalDateTime.of(2018, Month.FEBRUARY, 7, 16, 30, 15))).list(LTMS))
				.doOnSuccess(ltvalues -> {
					assertNotNull(ltvalues);
					assertEquals(1, ltvalues.size());
				}).then(getDatastore().bulkDelete(TARGET).filter(ID.in(oid1, oid2)).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(2L).expectComplete().verify();

	}

}
