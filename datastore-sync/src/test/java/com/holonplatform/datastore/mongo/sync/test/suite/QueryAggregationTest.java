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

import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.QueryAggregation;

public class QueryAggregationTest extends AbstractDatastoreOperationTest {

	@Test
	public void testQueryAggregation() {

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "g1").set(INT, 1).build())
				.add(PropertyBox.builder(SET1).set(STR, "g2").set(INT, 2).build())
				.add(PropertyBox.builder(SET1).set(STR, "g3").set(INT, 3).build())
				.add(PropertyBox.builder(SET1).set(STR, "g1").set(INT, 9).build())
				.add(PropertyBox.builder(SET1).set(STR, "g2").set(INT, 18).build()).execute();
		assertEquals(5, result.getAffectedCount());

		List<Integer> values = getDatastore().query(TARGET).aggregate(STR).list(INT.sum());
		assertNotNull(values);
		assertEquals(3, values.size());
		assertTrue(values.contains(Integer.valueOf(10)));
		assertTrue(values.contains(Integer.valueOf(20)));
		assertTrue(values.contains(Integer.valueOf(3)));

		values = getDatastore().query(TARGET).filter(STR.in("g1", "g2")).aggregate(STR).list(INT.sum());
		assertNotNull(values);
		assertEquals(2, values.size());
		assertTrue(values.contains(Integer.valueOf(10)));
		assertTrue(values.contains(Integer.valueOf(20)));

		values = getDatastore().query(TARGET)
				.aggregate(QueryAggregation.builder().path(STR).filter(INT.sum().gt(10)).build()).list(INT.sum());
		assertNotNull(values);
		assertEquals(1, values.size());
		assertTrue(values.contains(Integer.valueOf(20)));

		values = getDatastore().query(TARGET).filter(INT.lt(10))
				.aggregate(QueryAggregation.builder().path(STR).filter(INT.sum().goe(10)).build()).list(INT.sum());
		assertNotNull(values);
		assertEquals(1, values.size());
		assertTrue(values.contains(Integer.valueOf(10)));

		result = getDatastore().bulkDelete(TARGET).filter(STR.in("g1", "g2", "g3")).execute();
		assertEquals(5, result.getAffectedCount());

	}

}
