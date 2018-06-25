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

import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.PropertyBox;

public class StringFunctionsTest extends AbstractDatastoreOperationTest {

	@Test
	public void testLower() {
		
		final ObjectId oid = new ObjectId();

		PropertyBox value1 = PropertyBox.builder(SET1).set(ID, oid).set(STR, "One").set(INT, 1).build();
		OperationResult result = getDatastore().insert(TARGET, value1);
		assertEquals(1, result.getAffectedCount());
		
		System.err.println(getDatastore().query().target(TARGET).filter(ID.eq(oid)).count());
		
		String str = getDatastore().query().target(TARGET).filter(ID.eq(oid)).findOne(STR.lower()).orElse(null);
		assertNotNull(str);
		assertEquals("one", str);
		
		// TODO

		/*
		ObjectId id = getDatastore().query().target(TARGET).filter(STR.lower().eq("one")).findOne(ID).orElse(null);
		assertNotNull(id);
		assertEquals(oid, id);

		result = getDatastore().bulkUpdate(TARGET).set(STR, STR.lower()).filter(ID.eq(oid))
					.execute();
		assertEquals(1, result.getAffectedCount());

		String v = getDatastore().query().target(TARGET).filter(ID.eq(oid)).findOne(STR).orElse(null);
		assertNotNull(v);
		assertEquals("one", v);
		
		result = getDatastore().delete(TARGET, value1);
		assertEquals(1, result.getAffectedCount());
		*/
	}

	// TODO
	/*
	@Test
	public void testUpper() {
		String str = getDatastore().query().target(NAMED_TARGET).filter(KEY.eq(1L)).findOne(STR.upper()).orElse(null);
		assertNotNull(str);
		assertEquals("ONE", str);

		Long key = getDatastore().query().target(NAMED_TARGET).filter(STR.upper().eq("ONE")).findOne(KEY).orElse(null);
		assertNotNull(key);
		assertEquals(Long.valueOf(1L), key);

		inTransaction(() -> {

			OperationResult result = getDatastore().bulkUpdate(NAMED_TARGET).set(STR, STR.upper()).filter(KEY.eq(1L))
					.execute();
			assertEquals(1, result.getAffectedCount());

			String v = getDatastore().query().target(NAMED_TARGET).filter(KEY.eq(1L)).findOne(STR).orElse(null);
			assertNotNull(v);
			assertEquals("ONE", v);

		});
	}
	*/

}
