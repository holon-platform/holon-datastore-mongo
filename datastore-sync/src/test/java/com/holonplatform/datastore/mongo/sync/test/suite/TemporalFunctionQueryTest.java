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

import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.PropertyBox;

public class TemporalFunctionQueryTest extends AbstractDatastoreOperationTest {

	@Test
	public void testTemporalFunctionProjection() {

		OperationResult result = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR, "bktfp1").set(LDAT, LocalDate.of(2016, Month.JANUARY, 3)).build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp2").set(LDAT, LocalDate.of(2017, Month.JANUARY, 3)).build())
				.add(PropertyBox.builder(SET1).set(STR, "bktfp3").set(LDAT, LocalDate.of(2018, Month.JANUARY, 3)).build()).execute();
		assertEquals(3, result.getAffectedCount());
		
		List<Integer> years = getDatastore().query(TARGET).stream(LDAT.year()).collect(Collectors.toList());
		assertEquals(3, years.size());
		assertTrue(years.contains(2016));
		assertTrue(years.contains(2017));
		assertTrue(years.contains(2018));
		
		result = getDatastore().bulkDelete(TARGET).filter(STR.in("bktfp1", "bktfp2", "bktfp3")).execute();
		assertEquals(3, result.getAffectedCount());
		
	}

}
