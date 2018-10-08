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
package com.holonplatform.datastore.mongo.sync.test.suite;

import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.DAT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.DBL;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.STR;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.STR2;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.TMS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.sync.test.data.TestValues;

public class QueryFilterTest extends AbstractDatastoreOperationTest {

	@Test
	public void testFilters() {

		final ObjectId oid1 = new ObjectId();
		final ObjectId oid2 = new ObjectId();
		final ObjectId oid3 = new ObjectId();

		PropertyBox value1 = PropertyBox.builder(SET1).set(ID, oid1).set(STR, "One").set(INT, 1).build();
		OperationResult result = getDatastore().insert(TARGET, value1);
		assertEquals(1, result.getAffectedCount());

		PropertyBox value2 = PropertyBox.builder(SET1).set(ID, oid2).set(STR, "Two").set(INT, 2).build();
		result = getDatastore().insert(TARGET, value2);
		assertEquals(1, result.getAffectedCount());

		PropertyBox value3 = PropertyBox.builder(SET1).set(ID, oid3).set(DBL, 7.5d).set(STR2, "+20").build();
		result = getDatastore().insert(TARGET, value3);
		assertEquals(1, result.getAffectedCount());

		long count = getDatastore().query().target(TARGET).filter(STR.eq("One")).count();
		assertEquals(1, count);

		count = getDatastore().query().target(TARGET).filter(STR.eq("One").not()).count();
		assertEquals(2, count);

		count = getDatastore().query().target(TARGET).filter(STR.neq("Two")).count();
		assertEquals(2, count);

		count = getDatastore().query().target(TARGET).filter(STR.isNotNull()).count();
		assertEquals(2, count);

		count = getDatastore().query().target(TARGET).filter(STR.isNull()).count();
		assertEquals(1, count);

		count = getDatastore().query().target(TARGET).filter(STR.isNotNull().and(STR.neq("Two"))).count();
		assertEquals(1, count);

		count = getDatastore().query().target(TARGET).filter(DBL.isNull()).count();
		assertEquals(2, count);

		count = getDatastore().query().target(TARGET).filter(STR.endsWith("x")).count();
		assertEquals(0, count);

		count = getDatastore().query().target(TARGET).filter(STR.contains("w")).count();
		assertEquals(1, count);

		count = getDatastore().query().target(TARGET).filter(STR.containsIgnoreCase("O")).count();
		assertEquals(2, count);

		count = getDatastore().query().target(TARGET).filter(STR.startsWith("O")).count();
		assertEquals(1, count);

		count = getDatastore().query().target(TARGET).filter(STR.startsWithIgnoreCase("t")).count();
		assertEquals(1, count);

		count = getDatastore().query().target(TARGET).filter(STR2.startsWithIgnoreCase("+")).count();
		assertEquals(1, count);

		count = getDatastore().query().target(TARGET).filter(STR.startsWithIgnoreCase("o")).count();
		assertEquals(1, count);

		count = getDatastore().query().target(TARGET).filter(DBL.gt(7d)).count();
		assertEquals(1, count);

		count = getDatastore().query().target(TARGET).filter(DBL.lt(8d)).count();
		assertEquals(1, count);

		count = getDatastore().query().target(TARGET).filter(INT.between(1, 2)).count();
		assertEquals(2, count);

		count = getDatastore().query().target(TARGET).filter(INT.in(1, 2)).count();
		assertEquals(2, count);

		count = getDatastore().query().target(TARGET).filter(INT.nin(1, 2)).count();
		assertEquals(1, count);

		count = getDatastore().query().target(TARGET).filter(INT.isNotNull().and(INT.nin(1, 2))).count();
		assertEquals(0, count);

		count = getDatastore().query().target(TARGET).filter(INT.eq(1).or(INT.eq(2))).count();
		assertEquals(2, count);

		count = getDatastore().query().target(TARGET).filter(INT.eq(1).and(STR.eq("One"))).count();
		assertEquals(1, count);

		result = getDatastore().delete(TARGET, value1);
		assertEquals(1, result.getAffectedCount());
		result = getDatastore().delete(TARGET, value2);
		assertEquals(1, result.getAffectedCount());
		result = getDatastore().delete(TARGET, value3);
		assertEquals(1, result.getAffectedCount());
	}

	@Test
	public void testDateAndTimes() {

		final ObjectId oid1 = new ObjectId();
		final ObjectId oid2 = new ObjectId();

		PropertyBox value1 = PropertyBox.builder(SET1).set(ID, oid1).set(STR, "One").set(DAT, TestValues.DAT)
				.set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS).set(LTM, TestValues.LTM)
				.build();
		OperationResult result = getDatastore().insert(TARGET, value1);
		assertEquals(1, result.getAffectedCount());

		PropertyBox value2 = PropertyBox.builder(SET1).set(ID, oid2).set(STR, "Two").set(DAT, TestValues.U_DAT)
				.set(TMS, TestValues.U_TMS).set(LDAT, TestValues.U_LDAT).set(LTMS, TestValues.U_LTMS)
				.set(LTM, TestValues.U_LTM).build();
		result = getDatastore().insert(TARGET, value2);
		assertEquals(1, result.getAffectedCount());

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2018);
		c.set(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 7);

		List<Date> values = getDatastore().query().target(TARGET).filter(DAT.eq(c.getTime())).list(DAT);
		assertNotNull(values);
		assertEquals(1, values.size());

		c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2018);
		c.set(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 7);
		c.set(Calendar.HOUR_OF_DAY, 16);
		c.set(Calendar.MINUTE, 30);
		c.set(Calendar.SECOND, 15);
		c.set(Calendar.MILLISECOND, 0);

		values = getDatastore().query().target(TARGET).filter(TMS.eq(c.getTime())).list(TMS);
		assertNotNull(values);
		assertEquals(1, values.size());

		c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2018);
		c.set(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 7);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		values = getDatastore().query().target(TARGET).filter(TMS.goe(c.getTime())).list(TMS);
		assertNotNull(values);
		assertEquals(2, values.size());

		// Temporals

		List<LocalDate> lvalues = getDatastore().query().target(TARGET)
				.filter(LDAT.eq(LocalDate.of(2018, Month.FEBRUARY, 7))).list(LDAT);
		assertNotNull(lvalues);
		assertEquals(1, lvalues.size());

		lvalues = getDatastore().query().target(TARGET).filter(LDAT.goe(LocalDate.of(2018, Month.FEBRUARY, 7)))
				.list(LDAT);
		assertNotNull(lvalues);
		assertEquals(2, lvalues.size());

		LocalTime time = getDatastore().query().target(TARGET).filter(ID.eq(oid2)).findOne(LTM).orElse(null);
		assertNotNull(time);
		assertEquals(7, time.getHour());
		assertEquals(30, time.getMinute());
		assertEquals(15, time.getSecond());

		long cnt = getDatastore().query().target(TARGET).filter(LTM.eq(LocalTime.of(18, 45, 30))).count();
		assertEquals(1, cnt);

		List<LocalDateTime> ltvalues = getDatastore().query().target(TARGET)
				.filter(LTMS.eq(LocalDateTime.of(2018, Month.FEBRUARY, 7, 16, 30, 15))).list(LTMS);
		assertNotNull(ltvalues);
		assertEquals(1, ltvalues.size());

		result = getDatastore().delete(TARGET, value1);
		assertEquals(1, result.getAffectedCount());
		result = getDatastore().delete(TARGET, value2);
		assertEquals(1, result.getAffectedCount());

	}

}
