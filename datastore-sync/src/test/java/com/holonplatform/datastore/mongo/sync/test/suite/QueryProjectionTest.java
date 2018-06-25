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

import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.A_BYT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.A_CHR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.A_ENM;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.A_INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.A_STR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.BGD;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.BOOL;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.BYT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.C_ENM;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.C_INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.C_LNG;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.C_STR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.DAT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.DBL;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ENM;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.FLT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID3;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID4;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID5;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LNG;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NBL;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET3;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET4;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET5;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SHR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.TMS;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.VRT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.BeanProjection;
import com.holonplatform.core.query.ConstantExpression;
import com.holonplatform.datastore.mongo.core.test.data.TestProjectionBean;
import com.holonplatform.datastore.mongo.core.test.data.TestProjectionBean2;
import com.holonplatform.datastore.mongo.core.test.data.TestValues;

public class QueryProjectionTest extends AbstractDatastoreOperationTest {

	@Test
	public void testPropertySetProjection() {

		final ObjectId oid = new ObjectId();

		PropertyBox value = PropertyBox.builder(SET1).set(ID, oid).set(STR, TestValues.STR).set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
				.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
				.set(C_LNG, TestValues.C_LNG).set(NBL, true).build();

		OperationResult result = getDatastore().insert(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET1).orElse(null);
		assertNotNull(value);

		assertEquals(oid, value.getValue(ID));
		assertEquals(TestValues.STR, value.getValue(STR));
		assertEquals(TestValues.BOOL, value.getValue(BOOL));
		assertEquals(TestValues.INT, value.getValue(INT));
		assertEquals(TestValues.LNG, value.getValue(LNG));
		assertEquals(TestValues.DBL, value.getValue(DBL));
		assertEquals(TestValues.FLT, value.getValue(FLT));
		assertEquals(TestValues.SHR, value.getValue(SHR));
		assertEquals(TestValues.BYT, value.getValue(BYT));
		assertEquals(TestValues.BGD, value.getValue(BGD));
		assertEquals(TestValues.ENM, value.getValue(ENM));
		assertEquals(TestValues.DAT, value.getValue(DAT));
		assertEquals(TestValues.TMS, value.getValue(TMS));
		assertEquals(TestValues.LDAT, value.getValue(LDAT));
		assertEquals(TestValues.LTMS, value.getValue(LTMS));
		assertEquals(TestValues.LTM, value.getValue(LTM));
		assertTrue(Arrays.equals(TestValues.A_STR, value.getValue(A_STR)));
		assertTrue(Arrays.equals(TestValues.A_INT, value.getValue(A_INT)));
		assertTrue(Arrays.equals(TestValues.A_ENM, value.getValue(A_ENM)));
		assertTrue(Arrays.equals(TestValues.A_CHR, value.getValue(A_CHR)));
		assertTrue(Arrays.equals(TestValues.A_BYT, value.getValue(A_BYT)));
		assertEquals(TestValues.C_STR, value.getValue(C_STR));
		assertEquals(TestValues.C_INT, value.getValue(C_INT));
		assertEquals(TestValues.C_ENM, value.getValue(C_ENM));
		assertEquals(TestValues.C_LNG, value.getValue(C_LNG));
		assertTrue(value.getValue(NBL));
		assertEquals("STR:" + TestValues.STR, value.getValue(VRT));

		final ObjectId oid2 = new ObjectId();

		value = PropertyBox.builder(SET1).set(ID, oid2).set(STR, "str2").set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
				.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
				.set(C_LNG, TestValues.C_LNG).set(NBL, true).build();

		result = getDatastore().insert(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		List<PropertyBox> values = getDatastore().query(TARGET).filter(ID.eq(oid).or(ID.eq(oid2))).stream(SET1)
				.collect(Collectors.toList());
		assertEquals(2, values.size());

		value = values.get(0);

		assertEquals(oid, value.getValue(ID));
		assertEquals(TestValues.STR, value.getValue(STR));
		assertEquals(TestValues.BOOL, value.getValue(BOOL));
		assertEquals(TestValues.INT, value.getValue(INT));
		assertEquals(TestValues.LNG, value.getValue(LNG));
		assertEquals(TestValues.DBL, value.getValue(DBL));
		assertEquals(TestValues.FLT, value.getValue(FLT));
		assertEquals(TestValues.SHR, value.getValue(SHR));
		assertEquals(TestValues.BYT, value.getValue(BYT));
		assertEquals(TestValues.BGD, value.getValue(BGD));
		assertEquals(TestValues.ENM, value.getValue(ENM));
		assertEquals(TestValues.DAT, value.getValue(DAT));
		assertEquals(TestValues.TMS, value.getValue(TMS));
		assertEquals(TestValues.LDAT, value.getValue(LDAT));
		assertEquals(TestValues.LTMS, value.getValue(LTMS));
		assertEquals(TestValues.LTM, value.getValue(LTM));
		assertTrue(Arrays.equals(TestValues.A_STR, value.getValue(A_STR)));
		assertTrue(Arrays.equals(TestValues.A_INT, value.getValue(A_INT)));
		assertTrue(Arrays.equals(TestValues.A_ENM, value.getValue(A_ENM)));
		assertTrue(Arrays.equals(TestValues.A_CHR, value.getValue(A_CHR)));
		assertTrue(Arrays.equals(TestValues.A_BYT, value.getValue(A_BYT)));
		assertEquals(TestValues.C_STR, value.getValue(C_STR));
		assertEquals(TestValues.C_INT, value.getValue(C_INT));
		assertEquals(TestValues.C_ENM, value.getValue(C_ENM));
		assertEquals(TestValues.C_LNG, value.getValue(C_LNG));
		assertTrue(value.getValue(NBL));
		assertEquals("STR:" + TestValues.STR, value.getValue(VRT));

		value = values.get(1);

		assertEquals(oid2, value.getValue(ID));
		assertEquals("str2", value.getValue(STR));
		assertEquals(TestValues.BOOL, value.getValue(BOOL));
		assertEquals(TestValues.INT, value.getValue(INT));
		assertEquals(TestValues.LNG, value.getValue(LNG));
		assertEquals(TestValues.DBL, value.getValue(DBL));
		assertEquals(TestValues.FLT, value.getValue(FLT));
		assertEquals(TestValues.SHR, value.getValue(SHR));
		assertEquals(TestValues.BYT, value.getValue(BYT));
		assertEquals(TestValues.BGD, value.getValue(BGD));
		assertEquals(TestValues.ENM, value.getValue(ENM));
		assertEquals(TestValues.DAT, value.getValue(DAT));
		assertEquals(TestValues.TMS, value.getValue(TMS));
		assertEquals(TestValues.LDAT, value.getValue(LDAT));
		assertEquals(TestValues.LTMS, value.getValue(LTMS));
		assertEquals(TestValues.LTM, value.getValue(LTM));
		assertTrue(Arrays.equals(TestValues.A_STR, value.getValue(A_STR)));
		assertTrue(Arrays.equals(TestValues.A_INT, value.getValue(A_INT)));
		assertTrue(Arrays.equals(TestValues.A_ENM, value.getValue(A_ENM)));
		assertTrue(Arrays.equals(TestValues.A_CHR, value.getValue(A_CHR)));
		assertTrue(Arrays.equals(TestValues.A_BYT, value.getValue(A_BYT)));
		assertEquals(TestValues.C_STR, value.getValue(C_STR));
		assertEquals(TestValues.C_INT, value.getValue(C_INT));
		assertEquals(TestValues.C_ENM, value.getValue(C_ENM));
		assertEquals(TestValues.C_LNG, value.getValue(C_LNG));
		assertTrue(value.getValue(NBL));
		assertEquals("STR:str2", value.getValue(VRT));

		result = getDatastore().bulkDelete(TARGET).filter(ID.eq(oid).or(ID.eq(oid2))).execute();
		assertEquals(2, result.getAffectedCount());

	}

	@Test
	public void testPropertyProjection() {

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "bktfp1").set(INT, 1).build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp2").set(INT, 2).build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp3").set(INT, 3).build()).execute();
		assertEquals(3, result.getAffectedCount());

		List<PropertyBox> values = getDatastore().query(TARGET).sort(INT.asc()).stream(STR, INT)
				.collect(Collectors.toList());
		assertEquals(3, values.size());

		assertEquals("bktfp1", values.get(0).getValue(STR));
		assertEquals(Integer.valueOf(1), values.get(0).getValue(INT));
		assertEquals("bktfp2", values.get(1).getValue(STR));
		assertEquals(Integer.valueOf(2), values.get(1).getValue(INT));
		assertEquals("bktfp3", values.get(2).getValue(STR));
		assertEquals(Integer.valueOf(3), values.get(2).getValue(INT));

		result = getDatastore().bulkDelete(TARGET).filter(STR.in("bktfp1", "bktfp2", "bktfp3")).execute();
		assertEquals(3, result.getAffectedCount());

	}

	@Test
	public void testLiteralProjection() {

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "bktfp1").build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp2").build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp3").build()).execute();
		assertEquals(3, result.getAffectedCount());

		List<Integer> literals = getDatastore().query(TARGET).stream(ConstantExpression.create(1))
				.collect(Collectors.toList());
		assertEquals(3, literals.size());

		literals.forEach(l -> {
			assertEquals(Integer.valueOf(1), l);
		});

		List<String> sliterals = getDatastore().query(TARGET).stream(ConstantExpression.create("S"))
				.collect(Collectors.toList());
		assertEquals(3, sliterals.size());

		sliterals.forEach(l -> {
			assertEquals("S", l);
		});

		result = getDatastore().bulkDelete(TARGET).filter(STR.in("bktfp1", "bktfp2", "bktfp3")).execute();
		assertEquals(3, result.getAffectedCount());
	}

	@Test
	public void testTemporalFunctionProjection() {

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "bktfp1").set(LDAT, LocalDate.of(2016, Month.JANUARY, 3))
						.set(LTMS, LocalDateTime.of(2016, Month.JANUARY, 3, 10, 30)).build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp2").set(LDAT, LocalDate.of(2017, Month.FEBRUARY, 4))
						.set(LTMS, LocalDateTime.of(2016, Month.JANUARY, 3, 12, 30)).build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp3").set(LDAT, LocalDate.of(2018, Month.MARCH, 5))
						.set(LTMS, LocalDateTime.of(2016, Month.JANUARY, 3, 18, 30)).build())
				.execute();
		assertEquals(3, result.getAffectedCount());

		List<Integer> years = getDatastore().query(TARGET).stream(LDAT.year()).collect(Collectors.toList());
		assertEquals(3, years.size());
		assertTrue(years.contains(2016));
		assertTrue(years.contains(2017));
		assertTrue(years.contains(2018));

		List<Integer> months = getDatastore().query(TARGET).stream(LDAT.month()).collect(Collectors.toList());
		assertEquals(3, months.size());
		assertTrue(months.contains(1));
		assertTrue(months.contains(2));
		assertTrue(months.contains(3));

		List<Integer> days = getDatastore().query(TARGET).stream(LDAT.day()).collect(Collectors.toList());
		assertEquals(3, days.size());
		assertTrue(days.contains(3));
		assertTrue(days.contains(4));
		assertTrue(days.contains(5));

		List<Integer> hours = getDatastore().query(TARGET).stream(LTMS.hour()).collect(Collectors.toList());
		assertEquals(3, hours.size());
		assertTrue(hours.contains(10));
		assertTrue(hours.contains(12));
		assertTrue(hours.contains(18));

		result = getDatastore().bulkDelete(TARGET).filter(STR.in("bktfp1", "bktfp2", "bktfp3")).execute();
		assertEquals(3, result.getAffectedCount());

	}

	@Test
	public void testDocumentIdProjection() {

		final ObjectId oid = new ObjectId();
		OperationResult result = getDatastore().insert(TARGET,
				PropertyBox.builder(SET3).set(ID3, oid).set(STR, "test1").build());
		assertEquals(1, result.getAffectedCount());

		PropertyBox value = getDatastore().query(TARGET).filter(ID3.eq(oid)).findOne(SET3).orElse(null);
		assertNotNull(value);

		assertEquals(oid, value.getValue(ID3));
		assertEquals("test1", value.getValue(STR));

		result = getDatastore().bulkDelete(TARGET).filter(ID3.eq(oid)).execute();
		assertEquals(1, result.getAffectedCount());

	}

	@Test
	public void testDocumentIdProjectionString() {

		final ObjectId oid = new ObjectId();
		final String code = oid.toHexString();

		OperationResult result = getDatastore().insert(TARGET,
				PropertyBox.builder(SET4).set(ID4, code).set(STR, "test1").build());
		assertEquals(1, result.getAffectedCount());

		PropertyBox value = getDatastore().query(TARGET).filter(ID4.eq(code)).findOne(SET4).orElse(null);
		assertNotNull(value);

		assertEquals(code, value.getValue(ID4));
		assertEquals("test1", value.getValue(STR));

		result = getDatastore().bulkDelete(TARGET).filter(ID4.eq(code)).execute();
		assertEquals(1, result.getAffectedCount());

	}

	@Test
	public void testDocumentIdProjectionBigInteger() {

		final ObjectId oid = new ObjectId();
		final BigInteger code = new BigInteger(oid.toHexString(), 16);

		OperationResult result = getDatastore().insert(TARGET,
				PropertyBox.builder(SET5).set(ID5, code).set(STR, "test1").build());
		assertEquals(1, result.getAffectedCount());

		PropertyBox value = getDatastore().query(TARGET).filter(ID5.eq(code)).findOne(SET5).orElse(null);
		assertNotNull(value);

		assertEquals(code, value.getValue(ID5));
		assertEquals("test1", value.getValue(STR));

		result = getDatastore().bulkDelete(TARGET).filter(ID5.eq(code)).execute();
		assertEquals(1, result.getAffectedCount());

	}

	@Test
	public void testPropertyConversion() {

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "nbktfp1").set(NBL, true).build())
				.add(PropertyBox.builder(SET1).set(STR, "nbktfp2").set(NBL, false).build())
				.add(PropertyBox.builder(SET1).set(STR, "nbktfp3").build()).execute();
		assertEquals(3, result.getAffectedCount());

		List<Boolean> values = getDatastore().query().target(TARGET).sort(STR.asc()).list(NBL);
		assertNotNull(values);
		assertEquals(3, values.size());
		assertEquals(Boolean.TRUE, values.get(0));
		assertEquals(Boolean.FALSE, values.get(1));
		assertEquals(Boolean.FALSE, values.get(2));

		result = getDatastore().bulkDelete(TARGET).filter(STR.in("nbktfp1", "nbktfp2", "nbktfp3")).execute();
		assertEquals(3, result.getAffectedCount());
	}

	@Test
	public void testProjectionBean() {

		final ObjectId oid1 = new ObjectId();
		final ObjectId oid2 = new ObjectId();

		PropertyBox value1 = PropertyBox.builder(SET1).set(ID, oid1).set(STR, "One").build();
		OperationResult result = getDatastore().insert(TARGET, value1);
		assertEquals(1, result.getAffectedCount());

		PropertyBox value2 = PropertyBox.builder(SET1).set(ID, oid2).set(STR, "Two").build();
		result = getDatastore().insert(TARGET, value2);
		assertEquals(1, result.getAffectedCount());

		List<TestProjectionBean> results = getDatastore().query().target(TARGET).sort(STR.asc())
				.list(BeanProjection.of(TestProjectionBean.class));
		assertNotNull(results);
		assertEquals(2, results.size());

		assertEquals(oid1.toHexString(), results.get(0).getId());
		assertEquals("One", results.get(0).getText());

		assertEquals(oid2.toHexString(), results.get(1).getId());
		assertEquals("Two", results.get(1).getText());

		List<TestProjectionBean2> results2 = getDatastore().query().target(TARGET).sort(STR.asc())
				.list(BeanProjection.of(TestProjectionBean2.class));
		assertNotNull(results);
		assertEquals(2, results.size());

		assertEquals(oid1, new ObjectId(results2.get(0).getCode().toString(16)));
		assertEquals("One", results2.get(0).getText());

		assertEquals(oid2, new ObjectId(results2.get(1).getCode().toString(16)));
		assertEquals("Two", results2.get(1).getText());

		result = getDatastore().delete(TARGET, value1);
		assertEquals(1, result.getAffectedCount());
		result = getDatastore().delete(TARGET, value2);
		assertEquals(1, result.getAffectedCount());
	}

}
