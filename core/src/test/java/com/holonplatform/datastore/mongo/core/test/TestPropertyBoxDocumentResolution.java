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
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ENM2;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.FLT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID3;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID4;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.ID5;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LNG;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N1_V1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N1_V2;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N1_V3;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N2_V1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N2_V2;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N3_V1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.N3_V2;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NBL;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NESTED;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NESTED_SET;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NESTED_V1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.NESTED_V2;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET2;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET3;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET4;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET5;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET6;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET7;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET8;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SHR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.TMS;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.VRT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.internal.resolver.DocumentPropertyBoxResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.FieldNamePathResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.FieldValuePathResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.PathFieldNameResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.PathValueFieldResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.PropertyBoxDocumentResolver;
import com.holonplatform.datastore.mongo.core.test.context.MongoTestContext;
import com.holonplatform.datastore.mongo.core.test.data.EnumValue;
import com.holonplatform.datastore.mongo.core.test.data.TestValues;
import com.mongodb.MongoClientSettings;

public class TestPropertyBoxDocumentResolution {

	private static MongoResolutionContext context;

	@BeforeClass
	public static void init() {
		context = MongoResolutionContext.create(new MongoTestContext());
		context.addExpressionResolver(PathFieldNameResolver.INSTANCE);
		context.addExpressionResolver(FieldNamePathResolver.INSTANCE);
		context.addExpressionResolver(FieldValuePathResolver.INSTANCE);
		context.addExpressionResolver(PathValueFieldResolver.INSTANCE);
		context.addExpressionResolver(PropertyBoxDocumentResolver.INSTANCE);
		context.addExpressionResolver(DocumentPropertyBoxResolver.INSTANCE);
	}

	@Test
	public void testPropertyBoxResolutionObjectId() {

		final ObjectId oid = new ObjectId();

		PropertyBox pb = PropertyBox.builder(SET1).set(ID, oid).set(STR, TestValues.STR).set(BOOL, TestValues.BOOL)
				.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL).set(FLT, TestValues.FLT)
				.set(SHR, TestValues.SHR).set(BYT, TestValues.BYT).set(BGD, TestValues.BGD).set(ENM, TestValues.ENM)
				.set(DAT, TestValues.DAT).set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
				.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
				.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT).set(NBL, true)
				.build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));

		assertEquals(oid, doc.get(ID.getName()));

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET1).resolve(DocumentValue.create(doc),
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
		assertEquals("STR:" + TestValues.STR, pb.getValue(VRT));
	}

	@Test
	public void testEnumCodec() {

		final ObjectId oid = new ObjectId();

		PropertyBox pb = PropertyBox.builder(SET2).set(ID, oid).set(ENM2, EnumValue.THIRD).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));
		assertTrue(doc.get(ENM2.getName()) instanceof Number);

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET2).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);

		assertEquals(EnumValue.THIRD, pb.getValue(ENM2));
	}

	@Test
	public void testObjectIdName() {

		final ObjectId oid = new ObjectId();

		PropertyBox pb = PropertyBox.builder(SET3).set(ID3, oid).set(STR, TestValues.STR).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));
		assertTrue(doc.containsKey("_id"));
		assertTrue(doc.get("_id") instanceof ObjectId);
		assertEquals(oid, doc.getObjectId("_id"));

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET3).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);

		assertEquals(oid, pb.getValue(ID3));
		assertEquals(TestValues.STR, pb.getValue(STR));
	}

	@Test
	public void testObjectIdString() {

		final ObjectId oid = new ObjectId();

		PropertyBox pb = PropertyBox.builder(SET4).set(ID4, oid.toHexString()).set(STR, TestValues.STR).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));
		assertTrue(doc.containsKey("_id"));
		assertTrue(doc.get("_id") instanceof ObjectId);
		assertEquals(oid, doc.getObjectId("_id"));

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET4).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);

		assertEquals(oid.toHexString(), pb.getValue(ID4));
		assertEquals(TestValues.STR, pb.getValue(STR));
	}

	@Test
	public void testObjectIdBigInteger() {

		final ObjectId oid = new ObjectId();
		final BigInteger biv = context.getDocumentIdResolver().decode(oid, BigInteger.class);

		PropertyBox pb = PropertyBox.builder(SET5).set(ID5, biv).set(STR, TestValues.STR).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));
		assertTrue(doc.containsKey("_id"));
		assertTrue(doc.get("_id") instanceof ObjectId);
		assertEquals(oid, doc.getObjectId("_id"));

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET5).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);

		assertEquals(biv, pb.getValue(ID5));
		assertEquals(TestValues.STR, pb.getValue(STR));
	}

	@Test
	public void testNestedPaths() {

		final ObjectId oid = new ObjectId();

		PropertyBox pb = PropertyBox.builder(SET6).set(ID, oid).set(STR, "testn").set(ENM, EnumValue.FIRST)
				.set(N1_V1, "n1v1").set(N1_V2, "n1v2").set(N1_V3, false).set(N2_V1, 52).set(N2_V2, "n2v2")
				.set(N3_V1, "n3v1").set(N3_V2, 12.97d).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));

		assertTrue(doc.containsKey("n1"));
		assertTrue(doc.get("n1") instanceof Map);
		assertEquals(3, doc.get("n1", Map.class).size());

		assertTrue(doc.containsKey("n2"));
		assertTrue(doc.get("n2") instanceof Map);
		assertEquals(3, doc.get("n2", Map.class).size());

		assertTrue(doc.get("n2", Map.class).containsKey("n3"));
		assertTrue(doc.get("n2", Map.class).get("n3") instanceof Map);
		assertEquals(2, ((Map<?, ?>) doc.get("n2", Map.class).get("n3")).size());

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET6).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);

		assertEquals(oid, pb.getValue(ID));
		assertEquals("testn", pb.getValue(STR));
		assertEquals(EnumValue.FIRST, pb.getValue(ENM));
		assertEquals("n1v1", pb.getValue(N1_V1));
		assertEquals("n1v2", pb.getValue(N1_V2));
		assertEquals(Boolean.FALSE, pb.getValue(N1_V3));
		assertEquals(Integer.valueOf(52), pb.getValue(N2_V1));
		assertEquals("n2v2", pb.getValue(N2_V2));
		assertEquals("n3v1", pb.getValue(N3_V1));
		assertEquals(Double.valueOf(12.97d), pb.getValue(N3_V2));
	}

	@Test
	public void testNestedPropertyBox() {

		final ObjectId oid = new ObjectId();

		PropertyBox nested = PropertyBox.builder(NESTED_SET).set(NESTED_V1, "nestedv1").set(NESTED_V2, "nestedv2")
				.build();

		PropertyBox pb = PropertyBox.builder(SET7).set(ID, oid).set(STR, "testn").set(ENM, EnumValue.FIRST)
				.set(NESTED, nested).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));

		assertTrue(doc.containsKey("n1"));
		assertTrue(doc.get("n1") instanceof Map);
		assertEquals(2, doc.get("n1", Map.class).size());

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET7).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);

		assertEquals(oid, pb.getValue(ID));
		assertEquals("testn", pb.getValue(STR));
		assertEquals(EnumValue.FIRST, pb.getValue(ENM));

		nested = pb.getValue(NESTED);
		assertNotNull(nested);

		assertEquals("nestedv1", nested.getValue(NESTED_V1));
		assertEquals("nestedv2", nested.getValue(NESTED_V2));
	}

	@Test
	public void testNestedMixed() {

		final ObjectId oid = new ObjectId();

		PropertyBox nested = PropertyBox.builder(NESTED_SET).set(NESTED_V1, "n1v1").set(NESTED_V2, "n1v2").build();

		PropertyBox pb = PropertyBox.builder(SET8).set(ID, oid).set(STR, "testn").set(ENM, EnumValue.FIRST)
				.set(NESTED, nested).set(N2_V1, 52).set(N2_V2, "n2v2").set(N3_V1, "n3v1").set(N3_V2, 12.97d).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));

		assertTrue(doc.containsKey("n1"));
		assertTrue(doc.get("n1") instanceof Map);
		assertEquals(2, doc.get("n1", Map.class).size());

		assertTrue(doc.containsKey("n2"));
		assertTrue(doc.get("n2") instanceof Map);
		assertEquals(3, doc.get("n2", Map.class).size());

		assertTrue(doc.get("n2", Map.class).containsKey("n3"));
		assertTrue(doc.get("n2", Map.class).get("n3") instanceof Map);
		assertEquals(2, ((Map<?, ?>) doc.get("n2", Map.class).get("n3")).size());

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET8).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);

		assertEquals(oid, pb.getValue(ID));
		assertEquals("testn", pb.getValue(STR));
		assertEquals(EnumValue.FIRST, pb.getValue(ENM));
		assertEquals(Integer.valueOf(52), pb.getValue(N2_V1));
		assertEquals("n2v2", pb.getValue(N2_V2));
		assertEquals("n3v1", pb.getValue(N3_V1));
		assertEquals(Double.valueOf(12.97d), pb.getValue(N3_V2));

		nested = pb.getValue(NESTED);
		assertNotNull(nested);

		assertEquals("n1v1", nested.getValue(NESTED_V1));
		assertEquals("n1v2", nested.getValue(NESTED_V2));
	}

	private static String checkJson(Document document) {
		return document.toJson(JsonWriterSettings.builder().build(),
				new DocumentCodec(MongoClientSettings.getDefaultCodecRegistry()));
	}

}
