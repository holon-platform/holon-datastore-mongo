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

import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.STR1;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.STR2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;

public class StringFunctionsTest extends AbstractDatastoreOperationTest {

	@Test
	public void testLower() {

		final ObjectId oid = new ObjectId();

		PropertyBox value1 = PropertyBox.builder(SET1).set(ID, oid).set(STR1, "One").set(INT, 1).set(STR2, "TEST")
				.build();
		OperationResult result = getDatastore().insert(TARGET, value1);
		assertEquals(1, result.getAffectedCount());

		String str = getDatastore().query().target(TARGET).filter(ID.eq(oid)).findOne(STR1.lower()).orElse(null);
		assertNotNull(str);
		assertEquals("one", str);

		final Property<?> LSTR = STR1.lower();

		PropertyBox pb = getDatastore().query().target(TARGET).filter(ID.eq(oid)).findOne(LSTR, INT).orElse(null);
		assertNotNull(pb);
		assertEquals("one", pb.getValue(LSTR));
		assertEquals(Integer.valueOf(1), pb.getValue(INT));

		ObjectId id = getDatastore().query().target(TARGET).filter(STR1.lower().eq("one")).findOne(ID).orElse(null);
		assertNotNull(id);
		assertEquals(oid, id);

		result = getDatastore().delete(TARGET, value1);
		assertEquals(1, result.getAffectedCount());

	}

	@Test
	public void testUpper() {

		final ObjectId oid = new ObjectId();

		PropertyBox value1 = PropertyBox.builder(SET1).set(ID, oid).set(STR1, "One").set(INT, 1).build();
		OperationResult result = getDatastore().insert(TARGET, value1);
		assertEquals(1, result.getAffectedCount());

		String str = getDatastore().query().target(TARGET).filter(ID.eq(oid)).findOne(STR1.upper()).orElse(null);
		assertNotNull(str);
		assertEquals("ONE", str);

		ObjectId id = getDatastore().query().target(TARGET).filter(STR1.upper().eq("ONE")).findOne(ID).orElse(null);
		assertNotNull(id);
		assertEquals(oid, id);

		result = getDatastore().delete(TARGET, value1);
		assertEquals(1, result.getAffectedCount());
	}

}
