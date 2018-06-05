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
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.TMS;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.VRT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.property.PropertyBox;
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
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT).set(NBL, true)
				.build();

		CompletionStage<OperationResult> asyncResult = getDatastore().insert(TARGET, value);
		
		OperationResult result = asyncResult.toCompletableFuture().join();
		
		assertEquals(1, result.getAffectedCount());

		assertEquals(OperationType.INSERT, result.getOperationType().orElse(null));
		assertEquals(1, result.getAffectedCount());

		// TODO
		/*
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

		getDatastore().delete(TARGET, value);
		count = getDatastore().query(TARGET).filter(ID.eq(oid)).count();
		assertEquals(0, count);
		
		*/

	}

}