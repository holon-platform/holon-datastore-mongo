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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.Path;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.property.BooleanProperty;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.internal.resolver.DataTargetCollectionNameResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.FieldNamePathResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.PathFieldNameResolver;
import com.holonplatform.datastore.mongo.core.test.context.MongoTestContext;

public class TestPathResolution {

	@Test
	public void testDataTarget() {
		final MongoResolutionContext<?> ctx = MongoResolutionContext.create(new MongoTestContext());
		ctx.addExpressionResolver(DataTargetCollectionNameResolver.INSTANCE);

		Optional<CollectionName> cn = ctx.resolve(DataTarget.named("test"), CollectionName.class);
		assertTrue(cn.isPresent());
		assertEquals("test", cn.get().getName());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testPathExpressions() {
		final MongoResolutionContext<?> ctx = MongoResolutionContext.create(new MongoTestContext());
		ctx.addExpressionResolver(PathFieldNameResolver.INSTANCE);
		ctx.addExpressionResolver(FieldNamePathResolver.INSTANCE);

		Optional<Path> path = ctx.resolve(FieldName.create("test"), Path.class);
		assertTrue(path.isPresent());
		assertEquals("test", path.get().relativeName());

		path = ctx.resolve(FieldName.create("test1.test2.test3"), Path.class);
		assertTrue(path.isPresent());
		assertEquals("test1.test2.test3", path.get().relativeName());

		Optional<FieldName> name = ctx.resolve(Path.of("test", Object.class), FieldName.class);
		assertTrue(name.isPresent());
		assertEquals("test", name.get().getFieldName());

		name = ctx.resolve(Path.of("test1.test2", Object.class), FieldName.class);
		assertTrue(name.isPresent());
		assertEquals("test1.test2", name.get().getFieldName());

		name = ctx.resolve(Path.of("test2", Object.class).parent(Path.of("test1", Object.class)), FieldName.class);
		assertTrue(name.isPresent());
		assertEquals("test1.test2", name.get().getFieldName());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testDocumentIdPath() {

		final StringProperty ID = StringProperty.create("code");
		final BooleanProperty BOOL = BooleanProperty.create("bool");

		final PropertySet<?> SET = PropertySet.builderOf(ID, BOOL).identifier(ID).build();

		final MongoResolutionContext<?> ctx = MongoResolutionContext.create(new MongoTestContext());
		ctx.addExpressionResolver(PathFieldNameResolver.INSTANCE);
		ctx.addExpressionResolver(FieldNamePathResolver.INSTANCE);

		MongoDocumentContext<?> dctx = ctx.documentContext(SET);

		Optional<Path> path = ctx.resolve(FieldName.create("bool"), Path.class);
		assertTrue(path.isPresent());
		assertEquals("bool", path.get().relativeName());

		path = ctx.resolve(FieldName.create("_id"), Path.class);
		assertTrue(path.isPresent());
		assertEquals("_id", path.get().relativeName());

		path = dctx.resolve(FieldName.create("_id"), Path.class);
		assertTrue(path.isPresent());
		assertEquals("_id", path.get().relativeName());

		Optional<FieldName> name = dctx.resolve(BOOL, FieldName.class);
		assertTrue(name.isPresent());
		assertEquals("bool", name.get().getFieldName());

		name = dctx.resolve(ID, FieldName.class);
		assertTrue(name.isPresent());
		assertEquals("code", name.get().getFieldName());

	}

}
