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
package com.holonplatform.datastore.mongo.async.test.suite;

import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.ID4;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.SET4;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.STR1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.DocumentWriteOption;

public class BulkInsertTest extends AbstractDatastoreOperationTest {

	@Test
	public void testBulkInsert() {

		long result = getDatastore().bulkInsert(TARGET, SET1).add(PropertyBox.builder(SET1).set(STR1, "bkiv1").build())
				.add(PropertyBox.builder(SET1).set(STR1, "bkiv2").build())
				.add(PropertyBox.builder(SET1).set(STR1, "bkiv3").build()).execute()
				.thenAccept(r -> assertEquals(3, r.getAffectedCount()))
				.thenCompose(v -> getDatastore().query(TARGET).filter(STR1.in("bkiv1", "bkiv2", "bkiv3")).count())
				.thenAccept(count -> assertEquals(Long.valueOf(3), count))
				.thenCompose(
						v -> getDatastore().bulkDelete(TARGET).filter(STR1.in("bkiv1", "bkiv2", "bkiv3")).execute())
				.thenApply(r -> r.getAffectedCount()).toCompletableFuture().join();
		assertEquals(3, result);

	}

	@Test
	public void testBulkInsertIds() {

		final PropertyBox v1 = PropertyBox.builder(SET1).set(STR1, "bkiv10").build();
		final PropertyBox v2 = PropertyBox.builder(SET1).set(STR1, "bkiv11").build();
		final PropertyBox v3 = PropertyBox.builder(SET1).set(STR1, "bkiv12").build();

		getDatastore().bulkInsert(TARGET, SET1, DefaultWriteOption.BRING_BACK_GENERATED_IDS).add(v1).add(v2).add(v3)
				.execute().thenAccept(r -> assertEquals(3, r.getAffectedCount())).thenAccept(v -> {
					assertTrue(v1.containsValue(ID));
					assertTrue(v2.containsValue(ID));
					assertTrue(v3.containsValue(ID));
				}).thenCompose(v -> getDatastore().delete(TARGET, v1))
				.thenAccept(r -> assertEquals(1, r.getAffectedCount()))
				.thenCompose(v -> getDatastore().delete(TARGET, v2))
				.thenAccept(r -> assertEquals(1, r.getAffectedCount()))
				.thenCompose(v -> getDatastore().delete(TARGET, v3))
				.thenAccept(r -> assertEquals(1, r.getAffectedCount())).toCompletableFuture().join();

	}

	@Test
	public void testBulkInsertOptions() {

		long result = getDatastore()
				.bulkInsert(TARGET, SET1, DocumentWriteOption.BYPASS_VALIDATION, DocumentWriteOption.UNORDERED)
				.add(PropertyBox.builder(SET1).set(STR1, "bkiv20").build())
				.add(PropertyBox.builder(SET1).set(STR1, "bkiv21").build()).execute()
				.thenAccept(r -> assertEquals(2, r.getAffectedCount())).thenCompose(v -> getDatastore()
						.bulkDelete(TARGET).filter(STR1.eq("bkiv20").or(STR1.eq("bkiv21"))).execute())
				.thenApply(r -> r.getAffectedCount()).toCompletableFuture().join();

		assertEquals(2, result);

	}

	@Test
	public void testUpdateIdPropertyValue() {

		long count = getDatastore().bulkInsert(TARGET, SET4)
				.add(PropertyBox.builder(SET4).set(STR1, "ubkiv200").build())
				.add(PropertyBox.builder(SET4).set(STR1, "ubkiv201").build()).execute()
				.thenAccept(r -> assertEquals(2, r.getAffectedCount()))
				.thenCompose(x -> getDatastore().query(TARGET).filter(STR1.in("ubkiv200", "ubkiv201")).list(ID4))
				.thenAccept(codes -> {
					assertNotNull(codes);
					assertEquals(2, codes.size());
					assertNotNull(codes.get(0));
					assertNotNull(codes.get(1));
				}).thenCompose(r -> getDatastore().bulkDelete(TARGET)
						.filter(STR1.eq("ubkiv200").or(STR1.eq("ubkiv201"))).execute())
				.thenApply(r -> r.getAffectedCount()).toCompletableFuture().join();

		assertEquals(2, count);
	}

	@Test(expected = InvalidExpressionException.class)
	public void testBulkInsertNoValues() {

		getDatastore().bulkInsert(TARGET, SET1).execute().toCompletableFuture().join();

	}

}
