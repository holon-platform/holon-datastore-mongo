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
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.C_PBX;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.C_STR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.DAT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.DBL;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ENM;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.FLT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID4;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LNG;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N1_V1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N1_V2;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N1_V3;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N2_V1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N2_V2;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N3_V1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N3_V2;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NBL;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NESTED;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NESTED_SET;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NESTED_V1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NESTED_V2;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET10;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET4;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET6;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET7;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET8;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SHR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.TMS;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.VRT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.test.data.EnumValue;
import com.holonplatform.datastore.mongo.core.test.data.TestValues;

public class InsertTest extends AbstractDatastoreOperationTest {

	@Test
	public void testInsert() {

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
		assertEquals(OperationType.INSERT, result.getOperationType().orElse(null));

		long count = getDatastore().query(TARGET).filter(ID.eq(oid)).count();
		assertEquals(1, count);

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

		getDatastore().delete(TARGET, value);
		count = getDatastore().query(TARGET).filter(ID.eq(oid)).count();
		assertEquals(0, count);

	}

	@Test
	public void testInsertNulls() {
		final ObjectId oid = new ObjectId();

		PropertyBox value = PropertyBox.builder(SET1).set(ID, oid).set(STR, TestValues.STR).build();

		OperationResult result = getDatastore().insert(TARGET, value);

		assertEquals(1, result.getAffectedCount());

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET1).orElse(null);
		assertNotNull(value);

		assertEquals(oid, value.getValue(ID));
		assertEquals(TestValues.STR, value.getValue(STR));
		assertFalse(value.getValueIfPresent(BOOL).isPresent());
		assertFalse(value.getValueIfPresent(INT).isPresent());
		assertFalse(value.getValueIfPresent(LNG).isPresent());
		assertFalse(value.getValueIfPresent(DBL).isPresent());
		assertFalse(value.getValueIfPresent(FLT).isPresent());
		assertFalse(value.getValueIfPresent(SHR).isPresent());
		assertFalse(value.getValueIfPresent(BYT).isPresent());
		assertFalse(value.getValueIfPresent(BGD).isPresent());
		assertFalse(value.getValueIfPresent(ENM).isPresent());
		assertFalse(value.getValueIfPresent(DAT).isPresent());
		assertFalse(value.getValueIfPresent(TMS).isPresent());
		assertFalse(value.getValueIfPresent(LDAT).isPresent());
		assertFalse(value.getValueIfPresent(LTMS).isPresent());
		assertFalse(value.getValueIfPresent(LTM).isPresent());
		assertFalse(value.getValueIfPresent(A_STR).isPresent());
		assertFalse(value.getValueIfPresent(A_INT).isPresent());
		assertFalse(value.getValueIfPresent(A_ENM).isPresent());
		assertFalse(value.getValueIfPresent(A_CHR).isPresent());
		assertFalse(value.getValueIfPresent(A_BYT).isPresent());
		assertFalse(value.getValue(NBL));

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());
	}

	@Test
	public void testInsertOid() {

		PropertyBox value = PropertyBox.builder(SET1).set(STR, TestValues.STR).set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT).set(NBL, true)
				.build();

		OperationResult result = getDatastore().insert(TARGET, value);

		assertEquals(1, result.getAffectedCount());
		assertEquals(1, result.getInsertedKeys().size());
		assertTrue(result.getFirstInsertedKey().isPresent());

		ObjectId oid = result.getFirstInsertedKey(ObjectId.class).orElse(null);
		assertNotNull(oid);

		long count = getDatastore().query(TARGET).filter(ID.eq(oid)).count();
		assertEquals(1, count);

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
		assertTrue(value.getValue(NBL));
		assertEquals("STR:" + TestValues.STR, value.getValue(VRT));

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());

	}

	@Test
	public void testInsertIdType() {

		final ObjectId oid = new ObjectId();

		PropertyBox value = PropertyBox.builder(SET4).set(ID4, oid.toString()).set(STR, TestValues.STR).build();
		OperationResult result = getDatastore().insert(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());
	}

	@Test
	public void testInsertBringBackIdType() {

		PropertyBox value = PropertyBox.builder(SET4).set(STR, TestValues.STR).build();
		OperationResult result = getDatastore().insert(TARGET, value);

		assertEquals(1, result.getAffectedCount());
		assertTrue(result.getFirstInsertedKey().isPresent());
		String oid = result.getFirstInsertedKey(String.class).orElse(null);
		assertNotNull(oid);

		value.setValue(ID4, oid);
		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = PropertyBox.builder(SET4).set(STR, TestValues.STR).build();
		result = getDatastore().insert(TARGET, value, DefaultWriteOption.BRING_BACK_GENERATED_IDS);

		assertEquals(1, result.getAffectedCount());
		assertTrue(result.getFirstInsertedKey().isPresent());

		oid = result.getFirstInsertedKey(String.class).orElse(null);
		assertNotNull(oid);

		assertNotNull(value.getValue(ID4));
		assertEquals(oid, value.getValue(ID4));

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());

	}

	@Test
	public void testBringBackIds() {

		PropertyBox value = PropertyBox.builder(SET1).set(STR, TestValues.STR).build();

		OperationResult result = getDatastore().insert(TARGET, value, DefaultWriteOption.BRING_BACK_GENERATED_IDS);

		assertEquals(1, result.getAffectedCount());
		assertTrue(result.getFirstInsertedKey().isPresent());

		ObjectId oid = result.getFirstInsertedKey(ObjectId.class).orElse(null);
		assertNotNull(oid);

		assertNotNull(value.getValue(ID));

		assertEquals(oid, value.getValue(ID));

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());
	}

	@Test
	public void testInsertNested() {

		final ObjectId oid = new ObjectId();

		PropertyBox value = PropertyBox.builder(SET6).set(ID, oid).set(STR, "testn").set(ENM, EnumValue.FIRST)
				.set(N1_V1, "n1v1").set(N1_V2, "n1v2").set(N1_V3, false).set(N2_V1, 52).set(N2_V2, "n2v2")
				.set(N3_V1, "n3v1").set(N3_V2, 12.97d).build();

		OperationResult result = getDatastore().insert(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET6).orElse(null);
		assertNotNull(value);

		assertEquals(oid, value.getValue(ID));
		assertEquals("testn", value.getValue(STR));
		assertEquals(EnumValue.FIRST, value.getValue(ENM));
		assertEquals("n1v1", value.getValue(N1_V1));
		assertEquals("n1v2", value.getValue(N1_V2));
		assertEquals(Boolean.FALSE, value.getValue(N1_V3));
		assertEquals(Integer.valueOf(52), value.getValue(N2_V1));
		assertEquals("n2v2", value.getValue(N2_V2));
		assertEquals("n3v1", value.getValue(N3_V1));
		assertEquals(Double.valueOf(12.97d), value.getValue(N3_V2));

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());
	}

	@Test
	public void testInsertNestedPropertyBox() {

		final ObjectId oid = new ObjectId();

		PropertyBox nested = PropertyBox.builder(NESTED_SET).set(NESTED_V1, "nestedv1").set(NESTED_V2, "nestedv2")
				.build();
		PropertyBox value = PropertyBox.builder(SET7).set(ID, oid).set(STR, "testn").set(ENM, EnumValue.FIRST)
				.set(NESTED, nested).build();

		OperationResult result = getDatastore().insert(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET7).orElse(null);
		assertNotNull(value);

		assertEquals(oid, value.getValue(ID));
		assertEquals("testn", value.getValue(STR));
		assertEquals(EnumValue.FIRST, value.getValue(ENM));

		nested = value.getValue(NESTED);
		assertNotNull(nested);

		assertEquals("nestedv1", nested.getValue(NESTED_V1));
		assertEquals("nestedv2", nested.getValue(NESTED_V2));

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());

	}

	@Test
	public void testInsertNestedMixed() {

		final ObjectId oid = new ObjectId();

		PropertyBox nested = PropertyBox.builder(NESTED_SET).set(NESTED_V1, "n1v1").set(NESTED_V2, "n1v2").build();
		PropertyBox value = PropertyBox.builder(SET8).set(ID, oid).set(STR, "testn").set(ENM, EnumValue.FIRST)
				.set(NESTED, nested).set(N2_V1, 52).set(N2_V2, "n2v2").set(N3_V1, "n3v1").set(N3_V2, 12.97d).build();

		OperationResult result = getDatastore().insert(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET8).orElse(null);
		assertNotNull(value);

		assertEquals(oid, value.getValue(ID));
		assertEquals("testn", value.getValue(STR));
		assertEquals(EnumValue.FIRST, value.getValue(ENM));
		assertEquals(Integer.valueOf(52), value.getValue(N2_V1));
		assertEquals("n2v2", value.getValue(N2_V2));
		assertEquals("n3v1", value.getValue(N3_V1));
		assertEquals(Double.valueOf(12.97d), value.getValue(N3_V2));

		nested = value.getValue(NESTED);
		assertNotNull(nested);

		assertEquals("n1v1", nested.getValue(NESTED_V1));
		assertEquals("n1v2", nested.getValue(NESTED_V2));

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());
	}

	@Test
	public void testInsertPropertyBoxCollections() {

		final ObjectId oid = new ObjectId();

		List<PropertyBox> nesteds = new LinkedList<>();
		nesteds.add(PropertyBox.builder(NESTED_SET).set(NESTED_V1, "n1v1").set(NESTED_V2, "n1v2").build());
		nesteds.add(PropertyBox.builder(NESTED_SET).set(NESTED_V1, "n2v1").set(NESTED_V2, "n2v2").build());
		nesteds.add(PropertyBox.builder(NESTED_SET).set(NESTED_V1, "n3v1").set(NESTED_V2, "n3v2").build());

		PropertyBox value = PropertyBox.builder(SET10).set(ID, oid).set(STR, "testnpb").set(C_PBX, nesteds).build();

		OperationResult result = getDatastore().insert(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		value = getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET10).orElse(null);
		assertNotNull(value);

		assertEquals(oid, value.getValue(ID));
		assertEquals("testnpb", value.getValue(STR));

		List<PropertyBox> nvs = value.getValue(C_PBX);
		assertNotNull(nvs);
		assertEquals(3, nvs.size());

		result = getDatastore().delete(TARGET, value);
		assertEquals(1, result.getAffectedCount());
	}

}
