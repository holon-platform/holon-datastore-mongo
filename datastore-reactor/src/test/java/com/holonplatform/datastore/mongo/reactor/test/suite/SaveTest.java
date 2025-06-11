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

import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.A_BYT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.A_CHR;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.A_ENM;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.A_INT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.A_STR;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.BGD;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.BOOL;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.BYT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.DAT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.DBL;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.ENM;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.FLT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LNG;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.NBL;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SHR;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.TMS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.reactor.test.data.TestValues;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class SaveTest extends AbstractDatastoreOperationTest {

	@Test
	public void testSaveNoId() {

		final Mono<Long> op = getDatastore().save(TARGET,
				PropertyBox.builder(SET1).set(STR1, TestValues.STR1).set(BOOL, TestValues.BOOL).set(INT, TestValues.INT)
						.set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
						.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD)
						.set(ENM, TestValues.ENM).set(DAT, TestValues.DAT).set(TMS, TestValues.TMS)
						.set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS).set(LTM, TestValues.LTM)
						.set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT).set(A_ENM, TestValues.A_ENM)
						.set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT).set(NBL, true).build())
				.map(r -> {
					assertEquals(1, r.getAffectedCount());
					assertEquals(OperationType.INSERT, r.getOperationType().orElse(null));

					assertEquals(1, r.getInsertedKeys().size());
					assertTrue(r.getFirstInsertedKey().isPresent());

					ObjectId oid = r.getFirstInsertedKey(ObjectId.class).orElse(null);
					assertNotNull(oid);

					return oid;
				}).flatMap(oid -> getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET1)).map(value -> {
					assertEquals(TestValues.STR1, value.getValue(STR1));
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
					assertTrue(value.getValue(NBL));
					return value;
				}).flatMap(value -> getDatastore().delete(TARGET, value).map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(1L).expectComplete().verify();

	}

	@Test
	public void testSaveInsert() {

		ObjectId oid = new ObjectId();

		final Mono<Long> op = getDatastore().save(TARGET, PropertyBox.builder(SET1).set(ID, oid)
				.set(STR1, TestValues.STR1).set(BOOL, TestValues.BOOL).set(INT, TestValues.INT).set(LNG, TestValues.LNG)
				.set(DBL, TestValues.DBL).set(FLT, TestValues.FLT).set(SHR, TestValues.SHR).set(BYT, TestValues.BYT)
				.set(BGD, TestValues.BGD).set(ENM, TestValues.ENM).set(DAT, TestValues.DAT).set(TMS, TestValues.TMS)
				.set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS).set(LTM, TestValues.LTM)
				.set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT).set(A_ENM, TestValues.A_ENM)
				.set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT).set(NBL, true).build()).map(r -> {
					assertEquals(1, r.getAffectedCount());
					assertEquals(OperationType.INSERT, r.getOperationType().orElse(null));

					assertEquals(1, r.getInsertedKeys().size());
					assertTrue(r.getFirstInsertedKey().isPresent());

					ObjectId oidx = r.getFirstInsertedKey(ObjectId.class).orElse(null);
					assertNotNull(oidx);
					assertEquals(oid, oidx);

					return oidx;
				}).flatMap(oidx -> getDatastore().query(TARGET).filter(ID.eq(oidx)).findOne(SET1)).map(value -> {
					assertEquals(oid, value.getValue(ID));
					assertEquals(TestValues.STR1, value.getValue(STR1));
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
					assertTrue(value.getValue(NBL));
					return value;
				}).flatMap(value -> getDatastore().delete(TARGET, value).map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(1L).expectComplete().verify();

	}

	@Test
	public void testSaveUpdate() {

		ObjectId oid = new ObjectId();

		final Mono<Long> op = getDatastore()
				.insert(TARGET,
						PropertyBox.builder(SET1).set(ID, oid).set(STR1, TestValues.STR1).set(BOOL, TestValues.BOOL)
								.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL)
								.set(FLT, TestValues.FLT).set(SHR, TestValues.SHR).set(BYT, TestValues.BYT)
								.set(BGD, TestValues.BGD).set(ENM, TestValues.ENM).set(DAT, TestValues.DAT)
								.set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
								.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
								.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
								.set(NBL, true).build())
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().save(TARGET,
						PropertyBox.builder(SET1).set(ID, oid).set(STR1, "upd").set(BOOL, TestValues.BOOL)
								.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL)
								.set(FLT, TestValues.FLT).set(SHR, TestValues.SHR).set(BYT, TestValues.BYT)
								.set(BGD, TestValues.BGD).set(ENM, TestValues.ENM).set(DAT, TestValues.DAT)
								.set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
								.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
								.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
								.set(NBL, true).build()))
				.doOnSuccess(result -> {
					assertEquals(OperationType.UPDATE, result.getOperationType().orElse(null));
					assertEquals(0, result.getInsertedKeys().size());
				}).then(getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET1)).map(value -> {
					assertEquals(oid, value.getValue(ID));
					assertEquals("upd", value.getValue(STR1));
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
					assertTrue(value.getValue(NBL));
					return value;
				}).flatMap(value -> getDatastore().delete(TARGET, value).map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(1L).expectComplete().verify();

	}

}
