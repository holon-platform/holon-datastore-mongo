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

import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.*;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.DocumentWriteOption;

public class BulkInsertTest extends AbstractDatastoreOperationTest {

	@Test
	public void testBulkInsert() {

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "bkiv1").build())
				.add(PropertyBox.builder(SET1).set(STR, "bkiv2").build())
				.add(PropertyBox.builder(SET1).set(STR, "bkiv3").build()).execute();

		assertEquals(3, result.getAffectedCount());

		long count = getDatastore().query(TARGET).filter(STR.in("bkiv1", "bkiv2", "bkiv3")).count();
		assertEquals(3, count);

		result = getDatastore().bulkDelete(TARGET).filter(STR.in("bkiv1", "bkiv2", "bkiv3")).execute();
		assertEquals(3, result.getAffectedCount());

	}

	@Test
	public void testBulkInsertIds() {

		final PropertyBox v1 = PropertyBox.builder(SET1).set(STR, "bkiv10").build();
		final PropertyBox v2 = PropertyBox.builder(SET1).set(STR, "bkiv11").build();
		final PropertyBox v3 = PropertyBox.builder(SET1).set(STR, "bkiv12").build();

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1, DefaultWriteOption.BRING_BACK_GENERATED_IDS)
				.add(v1).add(v2).add(v3).execute();
		assertEquals(3, result.getAffectedCount());

		assertTrue(v1.containsValue(ID));
		assertTrue(v2.containsValue(ID));
		assertTrue(v3.containsValue(ID));

		result = getDatastore().delete(TARGET, v1);
		assertEquals(1, result.getAffectedCount());
		result = getDatastore().delete(TARGET, v2);
		assertEquals(1, result.getAffectedCount());
		result = getDatastore().delete(TARGET, v3);
		assertEquals(1, result.getAffectedCount());

	}

	@Test
	public void testBulkInsertOptions() {

		OperationResult result = getDatastore()
				.bulkInsert(TARGET, SET1, DocumentWriteOption.BYPASS_VALIDATION, DocumentWriteOption.UNORDERED)
				.add(PropertyBox.builder(SET1).set(STR, "bkiv20").build())
				.add(PropertyBox.builder(SET1).set(STR, "bkiv21").build()).execute();
		assertEquals(2, result.getAffectedCount());

		result = getDatastore().bulkDelete(TARGET).filter(STR.eq("bkiv20").or(STR.eq("bkiv21"))).execute();
		assertEquals(2, result.getAffectedCount());

	}

	@Test
	public void testUpdateIdPropertyValue() {
		OperationResult result = getDatastore().bulkInsert(TARGET, SET4)
				.add(PropertyBox.builder(SET4).set(STR, "ubkiv200").build())
				.add(PropertyBox.builder(SET4).set(STR, "ubkiv201").build()).execute();
		assertEquals(2, result.getAffectedCount());

		List<String> codes = getDatastore().query(TARGET).filter(STR.in("ubkiv200", "ubkiv201")).list(ID4);
		assertNotNull(codes);
		assertEquals(2, codes.size());

		result = getDatastore().bulkDelete(TARGET).filter(STR.eq("ubkiv200").or(STR.eq("ubkiv201"))).execute();
		assertEquals(2, result.getAffectedCount());
	}

	@Test(expected = DataAccessException.class)
	public void testBulkInsertNoValues() {

		getDatastore().bulkInsert(TARGET, SET1).execute();

	}

}
