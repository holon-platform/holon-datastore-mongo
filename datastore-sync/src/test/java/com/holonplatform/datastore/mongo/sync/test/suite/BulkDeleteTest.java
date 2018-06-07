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
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static org.junit.Assert.assertEquals;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.PropertyBox;

public class BulkDeleteTest extends AbstractDatastoreOperationTest {

	@Test
	public void testBulkDelete() {

		final ObjectId oid1 = new ObjectId();
		final ObjectId oid2 = new ObjectId();

		PropertyBox value1 = PropertyBox.builder(SET1).set(ID, oid1).set(STR, "v1").build();
		PropertyBox value2 = PropertyBox.builder(SET1).set(ID, oid2).set(STR, "v2").build();

		OperationResult result = getDatastore().insert(TARGET, value1);
		assertEquals(1, result.getAffectedCount());
		result = getDatastore().insert(TARGET, value2);
		assertEquals(1, result.getAffectedCount());

		result = getDatastore().bulkDelete(TARGET).filter(STR.eq("v1")).execute();
		assertEquals(1, result.getAffectedCount());

		result = getDatastore().bulkDelete(TARGET).filter(ID.eq(oid2)).execute();
		assertEquals(1, result.getAffectedCount());

		long count = getDatastore().query(TARGET).filter(ID.eq(oid1)).count();
		assertEquals(0, count);
		count = getDatastore().query(TARGET).filter(ID.eq(oid2)).count();
		assertEquals(0, count);

	}

}
