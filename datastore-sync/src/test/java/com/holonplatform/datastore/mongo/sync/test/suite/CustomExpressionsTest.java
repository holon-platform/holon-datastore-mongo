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


import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.A_STR;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.sync.test.data.ModelTest.STR1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.BsonFilter;
import com.holonplatform.datastore.mongo.core.BsonSort;
import com.holonplatform.datastore.mongo.sync.test.expression.IntIsFilter;
import com.holonplatform.datastore.mongo.sync.test.expression.StrIntSort;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

public class CustomExpressionsTest extends AbstractDatastoreOperationTest {

	@Test
	public void testResolvers() {

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "cext1").set(INT, 1).build())
				.add(PropertyBox.builder(SET1).set(STR1, "cext2").set(INT, 2).build())
				.add(PropertyBox.builder(SET1).set(STR1, "cext1").set(INT, 3).build()).execute();
		assertEquals(3, result.getAffectedCount());

		List<String> vals = getDatastore().query().withExpressionResolver(IntIsFilter.RESOLVER).target(TARGET)
				.filter(new IntIsFilter(2)).list(STR1);
		assertNotNull(vals);
		assertEquals(1, vals.size());
		assertEquals("cext2", vals.get(0));

		vals = getDatastore().query().withExpressionResolver(StrIntSort.RESOLVER).target(TARGET).sort(new StrIntSort())
				.list(STR1);
		assertNotNull(vals);
		assertEquals(3, vals.size());
		assertEquals("cext2", vals.get(0));
		assertEquals("cext1", vals.get(1));
		assertEquals("cext1", vals.get(2));

		result = getDatastore().bulkDelete(TARGET).filter(STR1.in("cext1", "cext2")).execute();
		assertEquals(3, result.getAffectedCount());

	}

	@Test
	public void testBsonFilterSort() {

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "cext1").set(INT, 1).set(A_STR, new String[] { "a", "b" })
						.build())
				.add(PropertyBox.builder(SET1).set(STR1, "cext2").set(INT, 2).set(A_STR, new String[] { "e", "f", "g" })
						.build())
				.add(PropertyBox.builder(SET1).set(STR1, "cext3").set(INT, 3).set(A_STR, new String[] { "a", "b", "c" })
						.build())
				.execute();
		assertEquals(3, result.getAffectedCount());

		List<String> vals = getDatastore().query().target(TARGET).filter(BsonFilter.create(Filters.eq("int", 2)))
				.list(STR1);
		assertNotNull(vals);
		assertEquals(1, vals.size());
		assertEquals("cext2", vals.get(0));

		vals = getDatastore().query().target(TARGET).filter(BsonFilter.create(Filters.size("astr", 3))).sort(INT.asc())
				.list(STR1);
		assertNotNull(vals);
		assertEquals(2, vals.size());
		assertEquals("cext2", vals.get(0));
		assertEquals("cext3", vals.get(1));

		vals = getDatastore().query().target(TARGET).sort(BsonSort.create(Sorts.descending("int"))).list(STR1);
		assertNotNull(vals);
		assertEquals(3, vals.size());
		assertEquals("cext3", vals.get(0));

		result = getDatastore().bulkDelete(TARGET).filter(STR1.in("cext1", "cext2", "cext3")).execute();
		assertEquals(3, result.getAffectedCount());

	}

}
