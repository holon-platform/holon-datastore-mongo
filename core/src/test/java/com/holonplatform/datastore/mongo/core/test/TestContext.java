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
package com.holonplatform.datastore.mongo.core.test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.Path;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.test.context.MongoTestContext;

public class TestContext {

	@Test
	public void testDocumentContext() {

		final MongoResolutionContext<?> ctx = MongoResolutionContext.create(new MongoTestContext());

		assertFalse(MongoDocumentContext.isDocumentContext(ctx).isPresent());

		Path<?> path = Path.of("test", String.class);

		assertFalse(MongoDocumentContext.isDocumentContext(ctx).flatMap(c -> c.isDocumentIdPath(path)).isPresent());

	}

}
