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

import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.DBL;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.PropertyBox;

public class AggregationFunctionsTest extends AbstractDatastoreOperationTest {

	@Test
	public void testAggregationFunctions() {

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "tmpft1").set(INT, 1).set(DBL, 1d).build())
				.add(PropertyBox.builder(SET1).set(STR, "tmpft2").set(INT, 2).build())
				.add(PropertyBox.builder(SET1).set(STR, "tmpft3").set(DBL, 2d).build()).execute();
		assertEquals(3, result.getAffectedCount());

		Integer mv = getDatastore().query().target(TARGET).findOne(INT.max()).orElse(null);
		assertNotNull(mv);
		assertEquals(Integer.valueOf(2), mv);

		mv = getDatastore().query().target(TARGET).findOne(INT.min()).orElse(null);
		assertNotNull(mv);
		assertEquals(Integer.valueOf(1), mv);

		mv = getDatastore().query().target(TARGET).findOne(INT.sum()).orElse(null);
		assertNotNull(mv);
		assertEquals(Integer.valueOf(3), mv);

		Double dv = getDatastore().query().target(TARGET).findOne(DBL.avg()).orElse(null);
		assertNotNull(dv);
		assertEquals(Double.valueOf(1.5), dv);

		Long ct = getDatastore().query().target(TARGET).findOne(INT.count()).orElse(null);
		assertNotNull(ct);
		assertEquals(Long.valueOf(2), ct);

		ct = getDatastore().query().target(TARGET).distinct().findOne(INT.count()).orElse(null);
		assertNotNull(ct);
		assertEquals(Long.valueOf(2), ct);

		result = getDatastore().bulkDelete(TARGET).filter(STR.in("tmpft1", "tmpft2", "tmpft3")).execute();
		assertEquals(3, result.getAffectedCount());

	}

}
