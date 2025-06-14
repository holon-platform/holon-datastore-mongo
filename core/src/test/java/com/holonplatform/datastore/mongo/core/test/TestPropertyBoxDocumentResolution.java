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
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.CP_ENM;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.CP_LIST;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.CP_SET;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.C_PBX;
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
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET10;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET2;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET3;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET4;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET5;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET6;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET7;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET8;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SET9;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.SHR;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.STR1;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.TMS;
import static com.holonplatform.datastore.mongo.core.test.data.ModelTest.VRT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;
import com.holonplatform.datastore.mongo.core.test.context.MongoTestContext;
import com.holonplatform.datastore.mongo.core.test.data.EnumValue;
import com.holonplatform.datastore.mongo.core.test.data.TestValues;
import com.mongodb.MongoClientSettings;

public class TestPropertyBoxDocumentResolution {

	private static MongoResolutionContext<?> context;

	@BeforeAll
	public static void init() {
		context = MongoResolutionContext.create(new MongoTestContext());
		context.addExpressionResolvers(MongoExpressionResolver.getDefaultResolvers());
	}

	@Test
	public void testPropertyBoxResolutionObjectId() {

		final ObjectId oid = new ObjectId();

		PropertyBox pb = PropertyBox.builder(SET1).set(ID, oid).set(STR1, TestValues.STR1).set(BOOL, TestValues.BOOL)
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
		assertEquals(TestValues.STR1, pb.getValue(STR1));
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
		assertEquals("STR1:" + TestValues.STR1, pb.getValue(VRT));
	}

	@Test
	public void testPropertyBoxResolutionConverters() {
		final ObjectId oid = new ObjectId();

		PropertyBox pb = PropertyBox.builder(SET1).set(ID, oid).set(STR1, TestValues.STR1).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET1).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);
		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();

		assertNotNull(pb);

		assertEquals(oid, pb.getValue(ID));
		assertEquals(TestValues.STR1, pb.getValue(STR1));
		assertNotNull(pb.getValue(NBL));
		assertFalse(pb.getValue(NBL));
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

		PropertyBox pb = PropertyBox.builder(SET3).set(ID3, oid).set(STR1, TestValues.STR1).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));
		assertTrue(doc.containsKey("_id"));
		assertTrue(doc.get("_id") instanceof ObjectId);
		assertEquals(oid, doc.getObjectId("_id"));
		assertTrue(doc.containsKey(ID3.getName()));
		assertTrue(doc.get(ID3.getName()) instanceof ObjectId);
		assertEquals(oid, doc.getObjectId(ID3.getName()));

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET3).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);

		assertEquals(oid, pb.getValue(ID3));
		assertEquals(TestValues.STR1, pb.getValue(STR1));
	}

	@Test
	public void testObjectIdString() {

		final ObjectId oid = new ObjectId();

		PropertyBox pb = PropertyBox.builder(SET4).set(ID4, oid.toHexString()).set(STR1, TestValues.STR1).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));
		assertTrue(doc.containsKey("_id"));
		assertTrue(doc.get("_id") instanceof ObjectId);
		assertEquals(oid, doc.getObjectId("_id"));
		assertTrue(doc.containsKey(ID4.getName()));
		assertTrue(doc.get(ID4.getName()) instanceof String);
		assertEquals(oid.toHexString(), doc.getString(ID4.getName()));

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET4).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);

		assertEquals(oid.toHexString(), pb.getValue(ID4));
		assertEquals(TestValues.STR1, pb.getValue(STR1));
	}

	@Test
	public void testObjectIdBigInteger() {

		final ObjectId oid = new ObjectId();
		final BigInteger biv = context.getDocumentIdResolver().decode(oid, BigInteger.class);

		PropertyBox pb = PropertyBox.builder(SET5).set(ID5, biv).set(STR1, TestValues.STR1).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));
		assertTrue(doc.containsKey("_id"));
		assertTrue(doc.get("_id") instanceof ObjectId);
		assertEquals(oid, doc.getObjectId("_id"));
		assertTrue(doc.containsKey(ID5.getName()));

		ObjectId idv = context.getDocumentIdResolver().encode(biv);
		assertEquals(oid, idv);

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET5).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);

		assertEquals(biv, pb.getValue(ID5));
		assertEquals(TestValues.STR1, pb.getValue(STR1));
	}

	@Test
	public void testNestedPaths() {

		final ObjectId oid = new ObjectId();

		PropertyBox pb = PropertyBox.builder(SET6).set(ID, oid).set(STR1, "testn").set(ENM, EnumValue.FIRST)
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
		assertEquals("testn", pb.getValue(STR1));
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

		PropertyBox pb = PropertyBox.builder(SET7).set(ID, oid).set(STR1, "testn").set(ENM, EnumValue.FIRST)
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
		assertEquals("testn", pb.getValue(STR1));
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

		PropertyBox pb = PropertyBox.builder(SET8).set(ID, oid).set(STR1, "testn").set(ENM, EnumValue.FIRST)
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
		assertEquals("testn", pb.getValue(STR1));
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

	@Test
	public void testCollectionProperties() {

		final ObjectId oid = new ObjectId();

		final List<String> lvals = Arrays.asList("a", "b", "c");
		final Set<Integer> svals = new HashSet<>(Arrays.asList(1, 2, 3));
		final Set<EnumValue> evals = new HashSet<>(Arrays.asList(EnumValue.FIRST, EnumValue.SECOND));

		PropertyBox pb = PropertyBox.builder(SET9).set(ID, oid).set(STR1, "test").set(CP_LIST, lvals).set(CP_SET, svals)
				.set(CP_ENM, evals).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));

		assertTrue(doc.containsKey(CP_LIST.getName()));
		assertTrue(doc.containsKey(CP_SET.getName()));
		assertTrue(doc.containsKey(CP_ENM.getName()));

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET9).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);

		assertEquals(oid, pb.getValue(ID));
		assertEquals("test", pb.getValue(STR1));

		List<String> lvs = pb.getValue(CP_LIST);
		assertNotNull(lvs);
		assertEquals(3, lvs.size());
		assertTrue(lvs.contains("a"));
		assertTrue(lvs.contains("b"));
		assertTrue(lvs.contains("c"));

		Set<Integer> svs = pb.getValue(CP_SET);
		assertNotNull(svs);
		assertEquals(3, svs.size());
		assertTrue(svs.contains(1));
		assertTrue(svs.contains(2));
		assertTrue(svs.contains(3));

		Set<EnumValue> sevs = pb.getValue(CP_ENM);
		assertNotNull(sevs);
		assertEquals(2, sevs.size());
		assertTrue(sevs.contains(EnumValue.FIRST));
		assertTrue(sevs.contains(EnumValue.SECOND));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPropertyBoxCollection() {

		final ObjectId oid = new ObjectId();

		List<PropertyBox> nestedList = new LinkedList<>();
		nestedList.add(PropertyBox.builder(NESTED_SET).set(NESTED_V1, "n1v1").set(NESTED_V2, "n1v2").build());
		nestedList.add(PropertyBox.builder(NESTED_SET).set(NESTED_V1, "n2v1").set(NESTED_V2, "n2v2").build());
		nestedList.add(PropertyBox.builder(NESTED_SET).set(NESTED_V1, "n3v1").set(NESTED_V2, "n3v2").build());

		PropertyBox pb = PropertyBox.builder(SET10).set(ID, oid).set(STR1, "testn").set(C_PBX, nestedList).build();

		Optional<DocumentValue> value = context.resolve(PropertyBoxValue.create(pb), DocumentValue.class);
		assertTrue(value.isPresent());

		Document doc = value.get().getValue();
		assertNotNull(doc);

		assertNotNull(checkJson(doc));

		assertTrue(doc.containsKey("cpbx"));
		assertTrue(doc.get("cpbx") instanceof Collection);
		assertEquals(3, doc.get("cpbx", Collection.class).size());

		Collection<Object> vs = doc.get("cpbx", Collection.class);
		for (Object v : vs) {
			assertTrue(v instanceof Map);
			Map<String, Object> element = (Map<String, Object>) v;
			assertEquals(2, element.size());
			assertTrue(element.containsKey("v1"));
			assertTrue(element.containsKey("v2"));
		}

		Optional<PropertyBoxValue> pbValue = context.documentContext(SET10).resolve(DocumentValue.create(doc),
				PropertyBoxValue.class);

		assertTrue(pbValue.isPresent());

		pb = pbValue.get().getValue();
		assertNotNull(pb);

		assertEquals(oid, pb.getValue(ID));
		assertEquals("testn", pb.getValue(STR1));

		List<PropertyBox> values = pb.getValue(C_PBX);
		assertNotNull(values);
		assertEquals(3, values.size());

		for (PropertyBox vpb : values) {
			assertNotNull(vpb.getValue(NESTED_V1));
			assertNotNull(vpb.getValue(NESTED_V2));
		}

	}

	private static String checkJson(Document document) {
		return document.toJson(JsonWriterSettings.builder().build(),
				new DocumentCodec(MongoClientSettings.getDefaultCodecRegistry()));
	}

}
