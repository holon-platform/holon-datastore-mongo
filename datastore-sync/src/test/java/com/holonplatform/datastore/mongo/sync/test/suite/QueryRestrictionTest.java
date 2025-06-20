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

import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.STR1;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.PropertyBox;

public class QueryRestrictionTest extends AbstractDatastoreOperationTest {

	@Test
	public void testRestrictions() {

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp1").set(INT, 1).build())
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp2").set(INT, 2).build())
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp3").set(INT, 3).build())
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp4").set(INT, 4).build())
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp5").set(INT, 5).build()).execute();
		assertEquals(5, result.getAffectedCount());

		List<String> values = getDatastore().query(TARGET).sort(INT.asc()).limit(2).list(STR1);
		assertEquals(2, values.size());
		assertEquals("bktfp1", values.get(0));
		assertEquals("bktfp2", values.get(1));

		values = getDatastore().query(TARGET).sort(INT.asc()).limit(3).offset(2).list(STR1);
		assertEquals(3, values.size());
		assertEquals("bktfp3", values.get(0));
		assertEquals("bktfp4", values.get(1));
		assertEquals("bktfp5", values.get(2));

		values = getDatastore().query(TARGET).sort(INT.asc()).limit(2).offset(4).list(STR1);
		assertEquals(1, values.size());
		assertEquals("bktfp5", values.get(0));

		result = getDatastore().bulkDelete(TARGET).filter(STR1.in("bktfp1", "bktfp2", "bktfp3", "bktfp4", "bktfp5"))
				.execute();
		assertEquals(5, result.getAffectedCount());

	}

}
