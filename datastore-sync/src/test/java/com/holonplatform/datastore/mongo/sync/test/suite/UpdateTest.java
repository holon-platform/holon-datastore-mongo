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

import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.A_BYT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.A_CHR;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.A_ENM;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.A_INT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.A_STR;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.BGD;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.BOOL;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.BYT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.C_ENM;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.C_INT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.C_LNG;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.C_STR;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.DAT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.DBL;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.ENM;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.FLT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.LNG;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.N1_V1;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.N1_V2;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.N1_V3;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.N2_V1;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.N2_V2;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.N3_V1;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.N3_V2;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.NBL;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.NESTED;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.NESTED_SET;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.NESTED_V1;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.NESTED_V2;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.SET6;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.SET7;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.SHR;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.STR1;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.STR2;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.TMS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.sync.test.data.EnumValue;
import com.holonplatform.datastore.mongo.sync.test.data.TestValues;

public class UpdateTest extends AbstractDatastoreOperationTest {

	@Test
	public void testUpdate() {

		final ObjectId oid = new ObjectId();

		PropertyBox value = PropertyBox.builder(SET1).set(ID, oid).set(STR1, TestValues.STR1).set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
				.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
				.set(C_LNG, TestValues.C_LNG).set(NBL, true).build();

		OperationResult result = getDatastore().insert(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = PropertyBox.builder(SET1).set(ID, oid).set(STR1, TestValues.U_STR).set(STR2, TestValues.U_STR2)
				.set(BOOL, TestValues.U_BOOL).set(INT, TestValues.U_INT).set(LNG, TestValues.U_LNG)
				.set(DBL, TestValues.U_DBL).set(FLT, TestValues.U_FLT).set(SHR, TestValues.U_SHR)
				.set(BYT, TestValues.U_BYT).set(BGD, TestValues.U_BGD).set(ENM, TestValues.U_ENM)
				.set(DAT, TestValues.U_DAT).set(TMS, TestValues.U_TMS).set(LDAT, TestValues.U_LDAT)
				.set(LTMS, TestValues.U_LTMS).set(LTM, TestValues.U_LTM).set(A_STR, TestValues.U_A_STR)
				.set(A_INT, TestValues.U_A_INT).set(A_ENM, TestValues.U_A_ENM).set(A_CHR, TestValues.U_A_CHR)
				.set(A_BYT, TestValues.U_A_BYT).set(C_STR, TestValues.U_C_STR).set(C_INT, TestValues.U_C_INT)
				.set(C_ENM, TestValues.U_C_ENM).set(C_LNG, TestValues.U_C_LNG).set(NBL, false).build();

		result = getDatastore().update(TARGET, value);
		assertEquals(OperationType.UPDATE, result.getOperationType().orElse(null));
		assertEquals(1, result.getAffectedCount());

		long count = getDatastore().query(TARGET).filter(ID.eq(oid)).count();
		assertEquals(1, count);

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET1).orElse(null);
		assertNotNull(value);

		assertEquals(oid, value.getValue(ID));
		assertEquals(TestValues.U_STR, value.getValue(STR1));
		assertEquals(TestValues.U_BOOL, value.getValue(BOOL));
		assertEquals(TestValues.U_INT, value.getValue(INT));
		assertEquals(TestValues.U_LNG, value.getValue(LNG));
		assertEquals(TestValues.U_DBL, value.getValue(DBL));
		assertEquals(TestValues.U_FLT, value.getValue(FLT));
		assertEquals(TestValues.U_SHR, value.getValue(SHR));
		assertEquals(TestValues.U_BYT, value.getValue(BYT));
		assertEquals(TestValues.U_BGD, value.getValue(BGD));
		assertEquals(TestValues.U_ENM, value.getValue(ENM));
		assertEquals(TestValues.U_DAT, value.getValue(DAT));
		assertEquals(TestValues.U_TMS, value.getValue(TMS));
		assertEquals(TestValues.U_LDAT, value.getValue(LDAT));
		assertEquals(TestValues.U_LTMS, value.getValue(LTMS));
		assertEquals(TestValues.U_LTM, value.getValue(LTM));
		assertTrue(Arrays.equals(TestValues.U_A_STR, value.getValue(A_STR)));
		assertTrue(Arrays.equals(TestValues.U_A_INT, value.getValue(A_INT)));
		assertTrue(Arrays.equals(TestValues.U_A_ENM, value.getValue(A_ENM)));
		assertTrue(Arrays.equals(TestValues.U_A_CHR, value.getValue(A_CHR)));
		assertTrue(Arrays.equals(TestValues.U_A_BYT, value.getValue(A_BYT)));
		assertEquals(TestValues.U_C_STR, value.getValue(C_STR));
		assertEquals(TestValues.U_C_INT, value.getValue(C_INT));
		assertEquals(TestValues.U_C_ENM, value.getValue(C_ENM));
		assertEquals(TestValues.U_C_LNG, value.getValue(C_LNG));
		assertFalse(value.getValue(NBL));

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());
	}

	@Test
	public void testUpdateUnset() {

		final ObjectId oid = new ObjectId();

		PropertyBox value = PropertyBox.builder(SET1).set(ID, oid).set(STR1, TestValues.STR1).set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
				.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
				.set(C_LNG, TestValues.C_LNG).set(NBL, true).build();

		OperationResult result = getDatastore().insert(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = PropertyBox.builder(SET1).set(ID, oid).set(STR2, TestValues.U_STR2).set(BOOL, TestValues.U_BOOL)
				.set(INT, TestValues.U_INT).set(DBL, TestValues.U_DBL).set(FLT, TestValues.U_FLT)
				.set(SHR, TestValues.U_SHR).set(BYT, TestValues.U_BYT).set(BGD, TestValues.U_BGD)
				.set(DAT, TestValues.U_DAT).set(TMS, TestValues.U_TMS).set(LDAT, TestValues.U_LDAT)
				.set(LTMS, TestValues.U_LTMS).set(LTM, TestValues.U_LTM).set(A_STR, TestValues.U_A_STR)
				.set(A_CHR, TestValues.U_A_CHR).set(A_BYT, TestValues.U_A_BYT).set(C_INT, TestValues.U_C_INT)
				.set(C_ENM, TestValues.U_C_ENM).set(C_LNG, TestValues.U_C_LNG).build();

		result = getDatastore().update(TARGET, value);
		assertEquals(OperationType.UPDATE, result.getOperationType().orElse(null));
		assertEquals(1, result.getAffectedCount());

		long count = getDatastore().query(TARGET).filter(ID.eq(oid)).count();
		assertEquals(1, count);

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET1).orElse(null);
		assertNotNull(value);

		assertEquals(oid, value.getValue(ID));
		assertNull(value.getValue(STR1));
		assertEquals(TestValues.U_BOOL, value.getValue(BOOL));
		assertEquals(TestValues.U_INT, value.getValue(INT));
		assertNull(value.getValue(LNG));
		assertEquals(TestValues.U_DBL, value.getValue(DBL));
		assertEquals(TestValues.U_FLT, value.getValue(FLT));
		assertEquals(TestValues.U_SHR, value.getValue(SHR));
		assertEquals(TestValues.U_BYT, value.getValue(BYT));
		assertEquals(TestValues.U_BGD, value.getValue(BGD));
		assertNull(value.getValue(ENM));
		assertEquals(TestValues.U_DAT, value.getValue(DAT));
		assertEquals(TestValues.U_TMS, value.getValue(TMS));
		assertEquals(TestValues.U_LDAT, value.getValue(LDAT));
		assertEquals(TestValues.U_LTMS, value.getValue(LTMS));
		assertEquals(TestValues.U_LTM, value.getValue(LTM));
		assertTrue(Arrays.equals(TestValues.U_A_STR, value.getValue(A_STR)));
		assertNull(value.getValue(A_INT));
		assertNull(value.getValue(A_ENM));
		assertTrue(Arrays.equals(TestValues.U_A_CHR, value.getValue(A_CHR)));
		assertTrue(Arrays.equals(TestValues.U_A_BYT, value.getValue(A_BYT)));
		assertNull(value.getValue(C_STR));
		assertEquals(TestValues.U_C_INT, value.getValue(C_INT));
		assertEquals(TestValues.U_C_ENM, value.getValue(C_ENM));
		assertEquals(TestValues.U_C_LNG, value.getValue(C_LNG));
		assertFalse(value.getValue(NBL));

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());
	}

	@Test(expected = DataAccessException.class)
	public void testUpdateMissingId() {

		PropertyBox value = PropertyBox.builder(SET1).set(STR1, TestValues.STR1).build();

		getDatastore().update(TARGET, value);

	}

	@Test
	public void testUpdateNested() {

		final ObjectId oid = new ObjectId();

		PropertyBox value = PropertyBox.builder(SET6).set(ID, oid).set(STR1, "testn").set(ENM, EnumValue.FIRST)
				.set(N1_V1, "n1v1").set(N1_V2, "n1v2").set(N1_V3, false).set(N2_V1, 52).set(N2_V2, "n2v2")
				.set(N3_V1, "n3v1").set(N3_V2, 12.97d).build();

		OperationResult result = getDatastore().insert(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET6).orElse(null);
		assertNotNull(value);

		value.setValue(STR1, "upd");
		value.setValue(N1_V1, "n1v1_upd");
		value.setValue(N1_V2, null);

		result = getDatastore().update(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET6).orElse(null);
		assertNotNull(value);

		assertEquals(oid, value.getValue(ID));
		assertEquals("upd", value.getValue(STR1));
		assertEquals(EnumValue.FIRST, value.getValue(ENM));
		assertEquals("n1v1_upd", value.getValue(N1_V1));
		assertNull(value.getValue(N1_V2));
		assertEquals(Boolean.FALSE, value.getValue(N1_V3));
		assertEquals(Integer.valueOf(52), value.getValue(N2_V1));
		assertEquals("n2v2", value.getValue(N2_V2));
		assertEquals("n3v1", value.getValue(N3_V1));
		assertEquals(Double.valueOf(12.97d), value.getValue(N3_V2));

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());
	}

	@Test
	public void testUpdateNestedPropertyBox() {

		final ObjectId oid = new ObjectId();

		PropertyBox nested = PropertyBox.builder(NESTED_SET).set(NESTED_V1, "nestedv1").set(NESTED_V2, "nestedv2")
				.build();
		PropertyBox value = PropertyBox.builder(SET7).set(ID, oid).set(STR1, "testn").set(ENM, EnumValue.FIRST)
				.set(NESTED, nested).build();

		OperationResult result = getDatastore().insert(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET7).orElse(null);
		assertNotNull(value);

		value.setValue(STR1, "upd");
		nested.setValue(NESTED_V1, "nestedv1_upd");
		value.setValue(NESTED, nested);

		result = getDatastore().update(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET7).orElse(null);
		assertNotNull(value);

		assertEquals(oid, value.getValue(ID));
		assertEquals("upd", value.getValue(STR1));
		assertEquals(EnumValue.FIRST, value.getValue(ENM));
		nested = value.getValue(NESTED);
		assertNotNull(nested);
		assertEquals("nestedv1_upd", nested.getValue(NESTED_V1));
		assertEquals("nestedv2", nested.getValue(NESTED_V2));

		nested.setValue(NESTED_V2, null);
		value.setValue(NESTED, nested);

		result = getDatastore().update(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET7).orElse(null);
		assertNotNull(value);

		assertEquals(oid, value.getValue(ID));
		assertEquals("upd", value.getValue(STR1));
		assertEquals(EnumValue.FIRST, value.getValue(ENM));
		nested = value.getValue(NESTED);
		assertNotNull(nested);
		assertEquals("nestedv1_upd", nested.getValue(NESTED_V1));
		assertNull(nested.getValue(NESTED_V2));

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());

	}

}
