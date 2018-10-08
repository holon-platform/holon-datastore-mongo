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

import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.A_BYT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.A_CHR;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.A_ENM;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.A_INT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.A_STR;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.BGD;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.BOOL;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.BYT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.C_ENM;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.C_INT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.C_LNG;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.C_STR;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.DAT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.DBL;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.ENM;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.FLT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.LNG;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.NBL;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.SHR;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.STR;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.STR2;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.TMS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.TemporalFunction.CurrentDate;
import com.holonplatform.core.query.TemporalFunction.CurrentLocalDate;
import com.holonplatform.core.query.TemporalFunction.CurrentLocalDateTime;
import com.holonplatform.core.query.TemporalFunction.CurrentTimestamp;
import com.holonplatform.datastore.mongo.async.test.data.TestValues;

public class BulkUpdateTest extends AbstractDatastoreOperationTest {

	@Test
	public void testBulkUpdate() {

		final ObjectId oid1 = new ObjectId();
		final ObjectId oid2 = new ObjectId();
		final ObjectId oid3 = new ObjectId();

		final PropertyBox value1 = PropertyBox.builder(SET1).set(ID, oid1).set(STR, "bkuv1").set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
				.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
				.set(C_LNG, TestValues.C_LNG).set(NBL, true).build();

		final PropertyBox value2 = PropertyBox.builder(SET1).set(ID, oid2).set(STR, "bkuv2").set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
				.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
				.set(C_LNG, TestValues.C_LNG).set(NBL, true).build();

		final PropertyBox value3 = PropertyBox.builder(SET1).set(ID, oid3).set(STR, "bkuv3").set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
				.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
				.set(C_LNG, TestValues.C_LNG).set(NBL, true).build();

		long count = getDatastore().insert(TARGET, value1).thenAccept(r -> assertEquals(1, r.getAffectedCount()))
				.thenCompose(v -> getDatastore().insert(TARGET, value2))
				.thenAccept(r -> assertEquals(1, r.getAffectedCount()))
				.thenCompose(v -> getDatastore().insert(TARGET, value3))
				.thenAccept(r -> assertEquals(1, r.getAffectedCount()))
				.thenCompose(v -> getDatastore().query(TARGET).filter(STR.in("bkuv1", "bkuv2", "bkuv3")).count())
				.thenAccept(c -> assertEquals(Long.valueOf(3), c))
				.thenCompose(
						v -> getDatastore().bulkUpdate(TARGET).filter(STR.eq("bkuv2")).set(STR, "bkuv2_upd").execute())
				.thenAccept(r -> assertEquals(1, r.getAffectedCount()))
				.thenCompose(v -> getDatastore().query(TARGET).filter(ID.eq(oid2)).findOne(SET1)).thenApply(v2 -> {
					assertNotNull(v2);
					assertTrue(v2.isPresent());
					return v2.get();
				}).thenAccept(v2 -> {
					assertEquals(oid2, v2.getValue(ID));
					assertEquals("bkuv2_upd", v2.getValue(STR));
					assertEquals(TestValues.BOOL, v2.getValue(BOOL));
					assertEquals(TestValues.INT, v2.getValue(INT));
					assertEquals(TestValues.LNG, v2.getValue(LNG));
					assertEquals(TestValues.DBL, v2.getValue(DBL));
					assertEquals(TestValues.FLT, v2.getValue(FLT));
					assertEquals(TestValues.SHR, v2.getValue(SHR));
					assertEquals(TestValues.BYT, v2.getValue(BYT));
					assertEquals(TestValues.BGD, v2.getValue(BGD));
					assertEquals(TestValues.ENM, v2.getValue(ENM));
					assertEquals(TestValues.DAT, v2.getValue(DAT));
					assertEquals(TestValues.TMS, v2.getValue(TMS));
					assertEquals(TestValues.LDAT, v2.getValue(LDAT));
					assertEquals(TestValues.LTMS, v2.getValue(LTMS));
					assertEquals(TestValues.LTM, v2.getValue(LTM));
					assertTrue(Arrays.equals(TestValues.A_STR, v2.getValue(A_STR)));
					assertTrue(Arrays.equals(TestValues.A_INT, v2.getValue(A_INT)));
					assertTrue(Arrays.equals(TestValues.A_ENM, v2.getValue(A_ENM)));
					assertTrue(Arrays.equals(TestValues.A_CHR, v2.getValue(A_CHR)));
					assertTrue(Arrays.equals(TestValues.A_BYT, v2.getValue(A_BYT)));
					assertEquals(TestValues.C_STR, v2.getValue(C_STR));
					assertEquals(TestValues.C_INT, v2.getValue(C_INT));
					assertEquals(TestValues.C_ENM, v2.getValue(C_ENM));
					assertEquals(TestValues.C_LNG, v2.getValue(C_LNG));
					assertTrue(v2.getValue(NBL));
				})
				.thenCompose(v -> getDatastore().bulkUpdate(TARGET).filter(ID.in(oid1, oid3)).set(STR, TestValues.U_STR)
						.set(STR2, TestValues.U_STR2).set(BOOL, TestValues.U_BOOL).set(INT, TestValues.U_INT)
						.set(LNG, TestValues.U_LNG).set(DBL, TestValues.U_DBL).set(FLT, TestValues.U_FLT)
						.set(SHR, TestValues.U_SHR).set(BYT, TestValues.U_BYT).set(BGD, TestValues.U_BGD)
						.set(ENM, TestValues.U_ENM).set(DAT, TestValues.U_DAT).set(TMS, TestValues.U_TMS)
						.set(LDAT, TestValues.U_LDAT).set(LTMS, TestValues.U_LTMS).set(LTM, TestValues.U_LTM)
						.set(A_STR, TestValues.U_A_STR).set(A_INT, TestValues.U_A_INT).set(A_ENM, TestValues.U_A_ENM)
						.set(A_CHR, TestValues.U_A_CHR).set(A_BYT, TestValues.U_A_BYT).set(C_STR, TestValues.U_C_STR)
						.set(C_INT, TestValues.U_C_INT).set(C_ENM, TestValues.U_C_ENM).set(C_LNG, TestValues.U_C_LNG)
						.set(NBL, false).execute())
				.thenAccept(r -> assertEquals(2, r.getAffectedCount()))
				.thenCompose(v -> getDatastore().query(TARGET).filter(ID.eq(oid1)).findOne(SET1)).thenApply(v1 -> {
					assertNotNull(v1);
					assertTrue(v1.isPresent());
					return v1.get();
				}).thenAccept(v1 -> {
					assertEquals(oid1, v1.getValue(ID));
					assertEquals(TestValues.U_STR, v1.getValue(STR));
					assertEquals(TestValues.U_BOOL, v1.getValue(BOOL));
					assertEquals(TestValues.U_INT, v1.getValue(INT));
					assertEquals(TestValues.U_LNG, v1.getValue(LNG));
					assertEquals(TestValues.U_DBL, v1.getValue(DBL));
					assertEquals(TestValues.U_FLT, v1.getValue(FLT));
					assertEquals(TestValues.U_SHR, v1.getValue(SHR));
					assertEquals(TestValues.U_BYT, v1.getValue(BYT));
					assertEquals(TestValues.U_BGD, v1.getValue(BGD));
					assertEquals(TestValues.U_ENM, v1.getValue(ENM));
					assertEquals(TestValues.U_DAT, v1.getValue(DAT));
					assertEquals(TestValues.U_TMS, v1.getValue(TMS));
					assertEquals(TestValues.U_LDAT, v1.getValue(LDAT));
					assertEquals(TestValues.U_LTMS, v1.getValue(LTMS));
					assertEquals(TestValues.U_LTM, v1.getValue(LTM));
					assertTrue(Arrays.equals(TestValues.U_A_STR, v1.getValue(A_STR)));
					assertTrue(Arrays.equals(TestValues.U_A_INT, v1.getValue(A_INT)));
					assertTrue(Arrays.equals(TestValues.U_A_ENM, v1.getValue(A_ENM)));
					assertTrue(Arrays.equals(TestValues.U_A_CHR, v1.getValue(A_CHR)));
					assertTrue(Arrays.equals(TestValues.U_A_BYT, v1.getValue(A_BYT)));
					assertEquals(TestValues.U_C_STR, v1.getValue(C_STR));
					assertEquals(TestValues.U_C_INT, v1.getValue(C_INT));
					assertEquals(TestValues.U_C_ENM, v1.getValue(C_ENM));
					assertEquals(TestValues.U_C_LNG, v1.getValue(C_LNG));
					assertFalse(v1.getValue(NBL));
				}).thenCompose(v -> getDatastore().query(TARGET).filter(ID.eq(oid3)).findOne(SET1)).thenApply(v3 -> {
					assertNotNull(v3);
					assertTrue(v3.isPresent());
					return v3.get();
				}).thenAccept(v3 -> {
					assertEquals(oid3, v3.getValue(ID));
					assertEquals(TestValues.U_STR, v3.getValue(STR));
					assertEquals(TestValues.U_BOOL, v3.getValue(BOOL));
					assertEquals(TestValues.U_INT, v3.getValue(INT));
					assertEquals(TestValues.U_LNG, v3.getValue(LNG));
					assertEquals(TestValues.U_DBL, v3.getValue(DBL));
					assertEquals(TestValues.U_FLT, v3.getValue(FLT));
					assertEquals(TestValues.U_SHR, v3.getValue(SHR));
					assertEquals(TestValues.U_BYT, v3.getValue(BYT));
					assertEquals(TestValues.U_BGD, v3.getValue(BGD));
					assertEquals(TestValues.U_ENM, v3.getValue(ENM));
					assertEquals(TestValues.U_DAT, v3.getValue(DAT));
					assertEquals(TestValues.U_TMS, v3.getValue(TMS));
					assertEquals(TestValues.U_LDAT, v3.getValue(LDAT));
					assertEquals(TestValues.U_LTMS, v3.getValue(LTMS));
					assertEquals(TestValues.U_LTM, v3.getValue(LTM));
					assertTrue(Arrays.equals(TestValues.U_A_STR, v3.getValue(A_STR)));
					assertTrue(Arrays.equals(TestValues.U_A_INT, v3.getValue(A_INT)));
					assertTrue(Arrays.equals(TestValues.U_A_ENM, v3.getValue(A_ENM)));
					assertTrue(Arrays.equals(TestValues.U_A_CHR, v3.getValue(A_CHR)));
					assertTrue(Arrays.equals(TestValues.U_A_BYT, v3.getValue(A_BYT)));
					assertEquals(TestValues.U_C_STR, v3.getValue(C_STR));
					assertEquals(TestValues.U_C_INT, v3.getValue(C_INT));
					assertEquals(TestValues.U_C_ENM, v3.getValue(C_ENM));
					assertEquals(TestValues.U_C_LNG, v3.getValue(C_LNG));
					assertFalse(v3.getValue(NBL));
				})
				.thenCompose(v -> getDatastore().bulkUpdate(TARGET).filter(ID.in(oid1, oid2, oid3)).set(DBL, 77.99d)
						.setNull(INT).execute())
				.thenAccept(r -> assertEquals(3, r.getAffectedCount()))
				.thenCompose(v -> getDatastore().query(TARGET).filter(ID.eq(oid1)).findOne(INT)).thenAccept(r -> {
					assertFalse(r.isPresent());
				}).thenCompose(v -> getDatastore().query(TARGET).filter(ID.eq(oid2)).findOne(INT)).thenAccept(r -> {
					assertFalse(r.isPresent());
				}).thenCompose(v -> getDatastore().query(TARGET).filter(ID.eq(oid3)).findOne(INT)).thenAccept(r -> {
					assertFalse(r.isPresent());
				}).thenCompose(v -> getDatastore().query(TARGET).filter(ID.eq(oid1)).findOne(DBL)).thenAccept(dv -> {
					assertEquals(Double.valueOf(77.99d), dv.orElse(null));
				}).thenCompose(v -> getDatastore().query(TARGET).filter(ID.eq(oid2)).findOne(DBL)).thenAccept(dv -> {
					assertEquals(Double.valueOf(77.99d), dv.orElse(null));
				}).thenCompose(v -> getDatastore().query(TARGET).filter(ID.eq(oid3)).findOne(DBL)).thenAccept(dv -> {
					assertEquals(Double.valueOf(77.99d), dv.orElse(null));
				}).thenCompose(v -> getDatastore().bulkDelete(TARGET).filter(ID.in(oid1, oid2, oid3)).execute())
				.thenApply(r -> r.getAffectedCount()).toCompletableFuture().join();

		assertEquals(3, count);

	}

	@Test
	public void testBulkUpdateDate() {

		final ObjectId oid = new ObjectId();

		long count = getDatastore().insert(TARGET, PropertyBox.builder(SET1).set(ID, oid).set(STR, "bkuv10").build())
				.thenAccept(r -> assertEquals(1, r.getAffectedCount()))
				.thenCompose(v -> getDatastore().bulkUpdate(TARGET).filter(ID.eq(oid)).set(DAT, CurrentDate.create())
						.set(LDAT, CurrentLocalDate.create()).set(TMS, CurrentTimestamp.create())
						.set(LTMS, CurrentLocalDateTime.create()).execute())
				.thenAccept(r -> assertEquals(1, r.getAffectedCount()))
				.thenCompose(v -> getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET1)).thenApply(uvalue -> {
					assertTrue(uvalue.isPresent());
					return uvalue.get();
				}).thenAccept(uvalue -> {

					final Calendar now = Calendar.getInstance();
					final LocalDate today = LocalDate.now();

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
				}).thenCompose(v -> getDatastore().bulkDelete(TARGET).filter(ID.eq(oid)).execute())
				.thenApply(r -> r.getAffectedCount()).toCompletableFuture().join();

		assertEquals(1, count);

	}

}
