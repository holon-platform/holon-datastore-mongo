/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.datastore.mongo.sync.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.datastore.mongo.sync.MongoDatastore;

public class TestStringIdUT extends AbstractMongoDBTest {

	private static final StringProperty ID = StringProperty.create("_id");
	private static final StringProperty TEXT = StringProperty.create("text");

	private static final PropertySet<?> SET = PropertySet.builderOf(ID, TEXT).withIdentifier(ID).build();

	private static final DataTarget<?> TARGET = DataTarget.named("test_string_ids");

	@Test
	public void testStringIdQuery() {

		final Datastore datastore = MongoDatastore.builder().client(getMongo()).database("testsid").traceEnabled(true)
				.build();

		PropertyBox value = PropertyBox.builder(SET).set(TEXT, "test1").build();

		OperationResult result = datastore.save(TARGET, value);
		assertEquals(1, result.getAffectedCount());

		String id = result.getInsertedKey(ID).orElse(null);
		assertNotNull(id);

		PropertyBox found = datastore.query(TARGET).filter(ID.eq(id)).findOne(SET).orElse(null);
		assertNotNull(found);
	}

}
