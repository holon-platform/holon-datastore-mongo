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

import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.test.data.TestValues;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class DeleteTest extends AbstractDatastoreOperationTest {

	@Test
	public void testDelete() {

		final ObjectId oid = new ObjectId();
		final PropertyBox value = PropertyBox.builder(SET1).set(ID, oid).set(STR, TestValues.STR).build();

		final Flux<Long> op = getDatastore().insert(TARGET, value).map(r -> r.getAffectedCount())
				.concatWith(getDatastore().query(TARGET).filter(ID.eq(oid)).count())
				.concatWith(getDatastore().delete(TARGET, value).map(r -> r.getAffectedCount())
						.concatWith(getDatastore().query(TARGET).filter(ID.eq(oid)).count()));

		StepVerifier.create(op).expectNext(1L).expectNext(1L).expectNext(1L).expectNext(0L).expectComplete().verify();

	}

}
