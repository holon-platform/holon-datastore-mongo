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

import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.A_BYT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.A_CHR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.A_ENM;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.A_INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.A_STR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.BGD;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.BOOL;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.BYT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.DAT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.DBL;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ENM;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.FLT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LNG;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NBL;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SHR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.TMS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.internal.resolver.DocumentPropertyBoxResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.FieldNamePathResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.FieldValueExpressionResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.PathFieldNameResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.PathValueExpressionResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.PropertyBoxDocumentResolver;
import com.holonplatform.datastore.mongo.core.test.context.MongoTestContext;
import com.holonplatform.datastore.mongo.core.test.data.TestValues;
import com.mongodb.MongoClientSettings;

public class TestPropertyBoxDocumentResolution {

	@Test
	public void testPropertyBoxResolutionObjectId() {

		final MongoResolutionContext ctx = MongoResolutionContext.create(new MongoTestContext());
		ctx.addExpressionResolver(PathFieldNameResolver.INSTANCE);
		ctx.addExpressionResolver(FieldNamePathResolver.INSTANCE);
		ctx.addExpressionResolver(FieldValueExpressionResolver.INSTANCE);
		ctx.addExpressionResolver(PathValueExpressionResolver.INSTANCE);
		ctx.addExpressionResolver(PropertyBoxDocumentResolver.INSTANCE);
		ctx.addExpressionResolver(DocumentPropertyBoxResolver.INSTANCE);

		final ObjectId oid = new ObjectId();

		PropertyBox pb = PropertyBox.builder(SET1).set(ID, oid).set(STR, TestValues.STR).set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT).set(NBL, true)
				.build();

		Optional<DocumentValue> value = ctx.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));

		assertEquals(oid, doc.get(ID.getName()));

		Optional<PropertyBoxValue> pbValue = ctx.documentContext(SET1).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);
		
		assertEquals(oid, pb.getValue(ID));
		assertEquals(TestValues.STR, pb.getValue(STR));
		assertEquals(TestValues.BOOL, pb.getValue(BOOL));
		assertEquals(TestValues.INT, pb.getValue(INT));
		assertEquals(TestValues.LNG, pb.getValue(LNG));
		assertEquals(TestValues.DBL, pb.getValue(DBL));
		assertEquals(TestValues.FLT, pb.getValue(FLT));
		assertEquals(TestValues.SHR, pb.getValue(SHR));
		assertEquals(TestValues.BYT, pb.getValue(BYT));
		assertEquals(TestValues.BGD, pb.getValue(BGD));
		assertEquals(TestValues.ENM, pb.getValue(ENM));
		assertEquals(TestValues.DAT, pb.getValue(DAT));
		assertEquals(TestValues.TMS, pb.getValue(TMS));
		assertEquals(TestValues.LDAT, pb.getValue(LDAT));
		assertEquals(TestValues.LTMS, pb.getValue(LTMS));
		assertEquals(TestValues.LTM, pb.getValue(LTM));
		assertTrue(Arrays.equals(TestValues.A_STR, pb.getValue(A_STR)));
		assertTrue(Arrays.equals(TestValues.A_INT, pb.getValue(A_INT)));
		assertTrue(Arrays.equals(TestValues.A_ENM, pb.getValue(A_ENM)));
		assertTrue(Arrays.equals(TestValues.A_CHR, pb.getValue(A_CHR)));
		assertTrue(Arrays.equals(TestValues.A_BYT, pb.getValue(A_BYT)));
		assertTrue(pb.getValue(NBL));
	}

	private static String checkJson(Document document) {
		return document.toJson(JsonWriterSettings.builder().build(),
				new DocumentCodec(MongoClientSettings.getDefaultCodecRegistry()));
	}

}
