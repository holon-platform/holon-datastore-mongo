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
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LNG;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NBL;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SHR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR2;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.TMS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.TemporalFunction.CurrentDate;
import com.holonplatform.core.query.TemporalFunction.CurrentLocalDate;
import com.holonplatform.core.query.TemporalFunction.CurrentLocalDateTime;
import com.holonplatform.core.query.TemporalFunction.CurrentTimestamp;
import com.holonplatform.datastore.mongo.core.test.data.TestValues;

public class BulkUpdateTest extends AbstractDatastoreOperationTest {

	@Test
	public void testBulkUpdate() {

		final ObjectId oid1 = new ObjectId();

		PropertyBox value1 = PropertyBox.builder(SET1).set(ID, oid1).set(STR, "bkuv1").set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
				.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
				.set(C_LNG, TestValues.C_LNG).set(NBL, true).build();

		OperationResult result = getDatastore().insert(TARGET, value1).toCompletableFuture().join();
		assertEquals(1, result.getAffectedCount());

		final ObjectId oid2 = new ObjectId();

		PropertyBox value2 = PropertyBox.builder(SET1).set(ID, oid2).set(STR, "bkuv2").set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
				.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
				.set(C_LNG, TestValues.C_LNG).set(NBL, true).build();

		result = getDatastore().insert(TARGET, value2).toCompletableFuture().join();
		assertEquals(1, result.getAffectedCount());

		final ObjectId oid3 = new ObjectId();

		PropertyBox value3 = PropertyBox.builder(SET1).set(ID, oid3).set(STR, "bkuv3").set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
				.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
				.set(C_LNG, TestValues.C_LNG).set(NBL, true).build();

		result = getDatastore().insert(TARGET, value3).toCompletableFuture().join();
		assertEquals(1, result.getAffectedCount());

		long count = getDatastore().query(TARGET).filter(STR.in("bkuv1", "bkuv2", "bkuv3")).count().toCompletableFuture().join();
		assertEquals(3, count);

		result = getDatastore().bulkUpdate(TARGET).filter(STR.eq("bkuv2")).set(STR, "bkuv2_upd").execute().toCompletableFuture().join();
		assertEquals(1, result.getAffectedCount());

		value2 = getDatastore().query(TARGET).filter(ID.eq(oid2)).findOne(SET1).toCompletableFuture().join().orElse(null);
		assertNotNull(value2);

		assertEquals(oid2, value2.getValue(ID));
		assertEquals("bkuv2_upd", value2.getValue(STR));
		assertEquals(TestValues.BOOL, value2.getValue(BOOL));
		assertEquals(TestValues.INT, value2.getValue(INT));
		assertEquals(TestValues.LNG, value2.getValue(LNG));
		assertEquals(TestValues.DBL, value2.getValue(DBL));
		assertEquals(TestValues.FLT, value2.getValue(FLT));
		assertEquals(TestValues.SHR, value2.getValue(SHR));
		assertEquals(TestValues.BYT, value2.getValue(BYT));
		assertEquals(TestValues.BGD, value2.getValue(BGD));
		assertEquals(TestValues.ENM, value2.getValue(ENM));
		assertEquals(TestValues.DAT, value2.getValue(DAT));
		assertEquals(TestValues.TMS, value2.getValue(TMS));
		assertEquals(TestValues.LDAT, value2.getValue(LDAT));
		assertEquals(TestValues.LTMS, value2.getValue(LTMS));
		assertEquals(TestValues.LTM, value2.getValue(LTM));
		assertTrue(Arrays.equals(TestValues.A_STR, value2.getValue(A_STR)));
		assertTrue(Arrays.equals(TestValues.A_INT, value2.getValue(A_INT)));
		assertTrue(Arrays.equals(TestValues.A_ENM, value2.getValue(A_ENM)));
		assertTrue(Arrays.equals(TestValues.A_CHR, value2.getValue(A_CHR)));
		assertTrue(Arrays.equals(TestValues.A_BYT, value2.getValue(A_BYT)));
		assertEquals(TestValues.C_STR, value2.getValue(C_STR));
		assertEquals(TestValues.C_INT, value2.getValue(C_INT));
		assertEquals(TestValues.C_ENM, value2.getValue(C_ENM));
		assertEquals(TestValues.C_LNG, value2.getValue(C_LNG));
		assertTrue(value2.getValue(NBL));

		result = getDatastore().bulkUpdate(TARGET).filter(ID.in(oid1, oid3)).set(STR, TestValues.U_STR)
				.set(STR2, TestValues.U_STR2).set(BOOL, TestValues.U_BOOL).set(INT, TestValues.U_INT)
				.set(LNG, TestValues.U_LNG).set(DBL, TestValues.U_DBL).set(FLT, TestValues.U_FLT)
				.set(SHR, TestValues.U_SHR).set(BYT, TestValues.U_BYT).set(BGD, TestValues.U_BGD)
				.set(ENM, TestValues.U_ENM).set(DAT, TestValues.U_DAT).set(TMS, TestValues.U_TMS)
				.set(LDAT, TestValues.U_LDAT).set(LTMS, TestValues.U_LTMS).set(LTM, TestValues.U_LTM)
				.set(A_STR, TestValues.U_A_STR).set(A_INT, TestValues.U_A_INT).set(A_ENM, TestValues.U_A_ENM)
				.set(A_CHR, TestValues.U_A_CHR).set(A_BYT, TestValues.U_A_BYT).set(C_STR, TestValues.U_C_STR)
				.set(C_INT, TestValues.U_C_INT).set(C_ENM, TestValues.U_C_ENM).set(C_LNG, TestValues.U_C_LNG)
				.set(NBL, false).execute().toCompletableFuture().join();
		assertEquals(2, result.getAffectedCount());

		value1 = getDatastore().query(TARGET).filter(ID.eq(oid1)).findOne(SET1).toCompletableFuture().join().orElse(null);
		assertNotNull(value1);

		assertEquals(oid1, value1.getValue(ID));
		assertEquals(TestValues.U_STR, value1.getValue(STR));
		assertEquals(TestValues.U_BOOL, value1.getValue(BOOL));
		assertEquals(TestValues.U_INT, value1.getValue(INT));
		assertEquals(TestValues.U_LNG, value1.getValue(LNG));
		assertEquals(TestValues.U_DBL, value1.getValue(DBL));
		assertEquals(TestValues.U_FLT, value1.getValue(FLT));
		assertEquals(TestValues.U_SHR, value1.getValue(SHR));
		assertEquals(TestValues.U_BYT, value1.getValue(BYT));
		assertEquals(TestValues.U_BGD, value1.getValue(BGD));
		assertEquals(TestValues.U_ENM, value1.getValue(ENM));
		assertEquals(TestValues.U_DAT, value1.getValue(DAT));
		assertEquals(TestValues.U_TMS, value1.getValue(TMS));
		assertEquals(TestValues.U_LDAT, value1.getValue(LDAT));
		assertEquals(TestValues.U_LTMS, value1.getValue(LTMS));
		assertEquals(TestValues.U_LTM, value1.getValue(LTM));
		assertTrue(Arrays.equals(TestValues.U_A_STR, value1.getValue(A_STR)));
		assertTrue(Arrays.equals(TestValues.U_A_INT, value1.getValue(A_INT)));
		assertTrue(Arrays.equals(TestValues.U_A_ENM, value1.getValue(A_ENM)));
		assertTrue(Arrays.equals(TestValues.U_A_CHR, value1.getValue(A_CHR)));
		assertTrue(Arrays.equals(TestValues.U_A_BYT, value1.getValue(A_BYT)));
		assertEquals(TestValues.U_C_STR, value1.getValue(C_STR));
		assertEquals(TestValues.U_C_INT, value1.getValue(C_INT));
		assertEquals(TestValues.U_C_ENM, value1.getValue(C_ENM));
		assertEquals(TestValues.U_C_LNG, value1.getValue(C_LNG));
		assertFalse(value1.getValue(NBL));

		value3 = getDatastore().query(TARGET).filter(ID.eq(oid3)).findOne(SET1).toCompletableFuture().join().orElse(null);
		assertNotNull(value1);

		assertEquals(oid3, value3.getValue(ID));
		assertEquals(TestValues.U_STR, value3.getValue(STR));
		assertEquals(TestValues.U_BOOL, value3.getValue(BOOL));
		assertEquals(TestValues.U_INT, value3.getValue(INT));
		assertEquals(TestValues.U_LNG, value3.getValue(LNG));
		assertEquals(TestValues.U_DBL, value3.getValue(DBL));
		assertEquals(TestValues.U_FLT, value3.getValue(FLT));
		assertEquals(TestValues.U_SHR, value3.getValue(SHR));
		assertEquals(TestValues.U_BYT, value3.getValue(BYT));
		assertEquals(TestValues.U_BGD, value3.getValue(BGD));
		assertEquals(TestValues.U_ENM, value3.getValue(ENM));
		assertEquals(TestValues.U_DAT, value3.getValue(DAT));
		assertEquals(TestValues.U_TMS, value3.getValue(TMS));
		assertEquals(TestValues.U_LDAT, value3.getValue(LDAT));
		assertEquals(TestValues.U_LTMS, value3.getValue(LTMS));
		assertEquals(TestValues.U_LTM, value3.getValue(LTM));
		assertTrue(Arrays.equals(TestValues.U_A_STR, value3.getValue(A_STR)));
		assertTrue(Arrays.equals(TestValues.U_A_INT, value3.getValue(A_INT)));
		assertTrue(Arrays.equals(TestValues.U_A_ENM, value3.getValue(A_ENM)));
		assertTrue(Arrays.equals(TestValues.U_A_CHR, value3.getValue(A_CHR)));
		assertTrue(Arrays.equals(TestValues.U_A_BYT, value3.getValue(A_BYT)));
		assertEquals(TestValues.U_C_STR, value3.getValue(C_STR));
		assertEquals(TestValues.U_C_INT, value3.getValue(C_INT));
		assertEquals(TestValues.U_C_ENM, value3.getValue(C_ENM));
		assertEquals(TestValues.U_C_LNG, value3.getValue(C_LNG));
		assertFalse(value3.getValue(NBL));

		// unset

		result = getDatastore().bulkUpdate(TARGET).filter(ID.in(oid1, oid2, oid3)).set(DBL, 77.99d).setNull(INT)
				.execute().toCompletableFuture().join();
		assertEquals(3, result.getAffectedCount());

		Integer iv = getDatastore().query(TARGET).filter(ID.eq(oid1)).findOne(INT).toCompletableFuture().join().orElse(null);
		assertNull(iv);
		iv = getDatastore().query(TARGET).filter(ID.eq(oid2)).findOne(INT).toCompletableFuture().join().orElse(null);
		assertNull(iv);
		iv = getDatastore().query(TARGET).filter(ID.eq(oid3)).findOne(INT).toCompletableFuture().join().orElse(null);
		assertNull(iv);

		Double dv = getDatastore().query(TARGET).filter(ID.eq(oid1)).findOne(DBL).toCompletableFuture().join().orElse(null);
		assertEquals(Double.valueOf(77.99d), dv);
		dv = getDatastore().query(TARGET).filter(ID.eq(oid2)).findOne(DBL).toCompletableFuture().join().orElse(null);
		assertEquals(Double.valueOf(77.99d), dv);
		dv = getDatastore().query(TARGET).filter(ID.eq(oid3)).findOne(DBL).toCompletableFuture().join().orElse(null);
		assertEquals(Double.valueOf(77.99d), dv);

		result = getDatastore().bulkDelete(TARGET).filter(ID.in(oid1, oid2, oid3)).execute().toCompletableFuture().join();
		assertEquals(3, result.getAffectedCount());

	}

	@Test
	public void testBulkUpdateDate() {

		final ObjectId oid = new ObjectId();

		PropertyBox value = PropertyBox.builder(SET1).set(ID, oid).set(STR, "bkuv10").build();

		OperationResult result = getDatastore().insert(TARGET, value).toCompletableFuture().join();
		assertEquals(1, result.getAffectedCount());

		result = getDatastore().bulkUpdate(TARGET).filter(ID.eq(oid)).set(DAT, CurrentDate.create())
				.set(LDAT, CurrentLocalDate.create()).set(TMS, CurrentTimestamp.create())
				.set(LTMS, CurrentLocalDateTime.create()).execute().toCompletableFuture().join();
		assertEquals(1, result.getAffectedCount());

		final Calendar now = Calendar.getInstance();
		final LocalDate today = LocalDate.now();

		PropertyBox uvalue = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET1).toCompletableFuture().join().orElse(null);
		assertNotNull(uvalue);

		Date dat = uvalue.getValue(DAT);
		assertNotNull(dat);

		Calendar c = Calendar.getInstance();
		c.setTime(dat);
		assertEquals(now.get(Calendar.YEAR), c.get(Calendar.YEAR));
		assertEquals(now.get(Calendar.MONTH), c.get(Calendar.MONTH));
		assertEquals(now.get(Calendar.DAY_OF_MONTH), c.get(Calendar.DAY_OF_MONTH));

		LocalDate ldat = uvalue.getValue(LDAT);
		assertNotNull(ldat);

		assertEquals(today.getYear(), ldat.getYear());
		assertEquals(today.getMonth(), ldat.getMonth());
		assertEquals(today.getDayOfMonth(), ldat.getDayOfMonth());

		Date tms = uvalue.getValue(TMS);
		assertNotNull(tms);

		c = Calendar.getInstance();
		c.setTime(dat);
		assertEquals(now.get(Calendar.YEAR), c.get(Calendar.YEAR));
		assertEquals(now.get(Calendar.MONTH), c.get(Calendar.MONTH));
		assertEquals(now.get(Calendar.DAY_OF_MONTH), c.get(Calendar.DAY_OF_MONTH));
		assertEquals(now.get(Calendar.HOUR), c.get(Calendar.HOUR));

		LocalDateTime ltms = uvalue.getValue(LTMS);
		assertNotNull(ltms);

		assertEquals(today.getYear(), ltms.getYear());
		assertEquals(today.getMonth(), ltms.getMonth());
		assertEquals(today.getDayOfMonth(), ltms.getDayOfMonth());

		result = getDatastore().delete(TARGET, value).toCompletableFuture().join();
		assertEquals(1, result.getAffectedCount());

	}

}
