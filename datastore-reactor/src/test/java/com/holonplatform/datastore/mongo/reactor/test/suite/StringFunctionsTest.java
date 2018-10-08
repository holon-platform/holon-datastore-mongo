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
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class StringFunctionsTest extends AbstractDatastoreOperationTest {

	@Test
	public void testLower() {

		final Property<?> LSTR = STR.lower();

		final ObjectId oid = new ObjectId();

		final Mono<Long> op = getDatastore()
				.insert(TARGET,
						PropertyBox.builder(SET1).set(ID, oid).set(STR, "One").set(INT, 1).set(STR2, "TEST").build())
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().query().target(TARGET).filter(ID.eq(oid)).findOne(STR.lower()))
				.doOnSuccess(str -> {
					assertNotNull(str);
					assertEquals("one", str);
				}).then(getDatastore().query().target(TARGET).filter(ID.eq(oid)).findOne(LSTR, INT)).doOnSuccess(pb -> {
					assertNotNull(pb);
					assertEquals("one", pb.getValue(LSTR));
					assertEquals(Integer.valueOf(1), pb.getValue(INT));
				}).then(getDatastore().query().target(TARGET).filter(STR.lower().eq("one")).findOne(ID))
				.doOnSuccess(id -> {
					assertNotNull(id);
					assertEquals(oid, id);
				}).then(getDatastore().bulkDelete(TARGET).filter(ID.eq(oid)).execute().map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(1L).expectComplete().verify();

	}

	@Test
	public void testUpper() {

		final ObjectId oid = new ObjectId();

		final Mono<Long> op = getDatastore()
				.insert(TARGET, PropertyBox.builder(SET1).set(ID, oid).set(STR, "One").set(INT, 1).build())
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().query().target(TARGET).filter(ID.eq(oid)).findOne(STR.upper()))
				.doOnSuccess(str -> {
					assertNotNull(str);
					assertEquals("ONE", str);
				}).then(getDatastore().query().target(TARGET).filter(STR.upper().eq("ONE")).findOne(ID))
				.doOnSuccess(id -> {
					assertNotNull(id);
					assertEquals(oid, id);
				}).then(getDatastore().bulkDelete(TARGET).filter(ID.eq(oid)).execute().map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(1L).expectComplete().verify();
	}

}
