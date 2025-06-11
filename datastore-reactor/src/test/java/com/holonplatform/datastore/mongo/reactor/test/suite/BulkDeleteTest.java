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
package com.holonplatform.datastore.mongo.reactor.test.suite;

import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR1;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class BulkDeleteTest extends AbstractDatastoreOperationTest {

	@Test
	public void testBulkDelete() {

		final ObjectId oid1 = new ObjectId();
		final ObjectId oid2 = new ObjectId();

		final PropertyBox value1 = PropertyBox.builder(SET1).set(ID, oid1).set(STR1, "v1").build();
		final PropertyBox value2 = PropertyBox.builder(SET1).set(ID, oid2).set(STR1, "v2").build();

		final Flux<Long> op = getDatastore().insert(TARGET, value1).map(r -> r.getAffectedCount())
				.concatWith(getDatastore().insert(TARGET, value2).map(r -> r.getAffectedCount())
						.concatWith(getDatastore().bulkDelete(TARGET).filter(STR1.eq("v1")).execute()
								.map(r -> r.getAffectedCount())
								.concatWith(getDatastore().query(TARGET).filter(ID.eq(oid1)).count())
								.concatWith(getDatastore().bulkDelete(TARGET).filter(ID.eq(oid2)).execute()
										.map(r -> r.getAffectedCount())
										.concatWith(getDatastore().query(TARGET).filter(ID.eq(oid2)).count()))));

		StepVerifier.create(op).expectNext(1L).expectNext(1L).expectNext(1L).expectNext(0L).expectNext(1L)
				.expectNext(0L).expectComplete().verify();

	}

}
