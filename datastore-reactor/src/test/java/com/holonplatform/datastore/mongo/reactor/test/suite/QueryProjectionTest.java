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

import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.A_BYT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.A_CHR;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.A_ENM;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.A_INT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.A_STR;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.BGD;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.BOOL;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.BYT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.C_ENM;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.C_INT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.C_LNG;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.C_STR;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.DAT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.DBL;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.ENM;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.FLT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.ID;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.ID3;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.ID4;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.ID5;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LDAT;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LNG;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LTM;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.LTMS;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.N1_V1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.N1_V2;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.N1_V3;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.N2_V1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.N2_V2;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.N3_V1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.N3_V2;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.NBL;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET3;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET4;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET5;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SET6;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.SHR;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.STR1;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.TMS;
import static com.holonplatform.datastore.mongo.reactor.test.data.ModelTest.VRT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.BeanProjection;
import com.holonplatform.core.query.ConstantExpression;
import com.holonplatform.core.query.SelectAllProjection;
import com.holonplatform.datastore.mongo.reactor.test.data.EnumValue;
import com.holonplatform.datastore.mongo.reactor.test.data.TestProjectionBean;
import com.holonplatform.datastore.mongo.reactor.test.data.TestProjectionBean2;
import com.holonplatform.datastore.mongo.reactor.test.data.TestProjectionBean3;
import com.holonplatform.datastore.mongo.reactor.test.data.TestValues;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class QueryProjectionTest extends AbstractDatastoreOperationTest {

	@Test
	public void testPropertySetProjection() {

		final ObjectId oid = new ObjectId();
		final ObjectId oid2 = new ObjectId();

		final Mono<Long> op = getDatastore()
				.insert(TARGET,
						PropertyBox.builder(SET1).set(ID, oid).set(STR1, TestValues.STR1).set(BOOL, TestValues.BOOL)
								.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL)
								.set(FLT, TestValues.FLT).set(SHR, TestValues.SHR).set(BYT, TestValues.BYT)
								.set(BGD, TestValues.BGD).set(ENM, TestValues.ENM).set(DAT, TestValues.DAT)
								.set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
								.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
								.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
								.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
								.set(C_LNG, TestValues.C_LNG).set(NBL, true).build())
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET1)).doOnSuccess(value -> {
					assertEquals(oid, value.getValue(ID));
					assertEquals(TestValues.STR1, value.getValue(STR1));
					assertEquals(TestValues.BOOL, value.getValue(BOOL));
					assertEquals(TestValues.INT, value.getValue(INT));
					assertEquals(TestValues.LNG, value.getValue(LNG));
					assertEquals(TestValues.DBL, value.getValue(DBL));
					assertEquals(TestValues.FLT, value.getValue(FLT));
					assertEquals(TestValues.SHR, value.getValue(SHR));
					assertEquals(TestValues.BYT, value.getValue(BYT));
					assertEquals(TestValues.BGD, value.getValue(BGD));
					assertEquals(TestValues.ENM, value.getValue(ENM));
					assertEquals(TestValues.DAT, value.getValue(DAT));
					assertEquals(TestValues.TMS, value.getValue(TMS));
					assertEquals(TestValues.LDAT, value.getValue(LDAT));
					assertEquals(TestValues.LTMS, value.getValue(LTMS));
					assertEquals(TestValues.LTM, value.getValue(LTM));
					assertTrue(Arrays.equals(TestValues.A_STR, value.getValue(A_STR)));
					assertTrue(Arrays.equals(TestValues.A_INT, value.getValue(A_INT)));
					assertTrue(Arrays.equals(TestValues.A_ENM, value.getValue(A_ENM)));
					assertTrue(Arrays.equals(TestValues.A_CHR, value.getValue(A_CHR)));
					assertTrue(Arrays.equals(TestValues.A_BYT, value.getValue(A_BYT)));
					assertEquals(TestValues.C_STR, value.getValue(C_STR));
					assertEquals(TestValues.C_INT, value.getValue(C_INT));
					assertEquals(TestValues.C_ENM, value.getValue(C_ENM));
					assertEquals(TestValues.C_LNG, value.getValue(C_LNG));
					assertTrue(value.getValue(NBL));
					assertEquals("STR1:" + TestValues.STR1, value.getValue(VRT));
				})
				.then(getDatastore().insert(TARGET,
						PropertyBox.builder(SET1).set(ID, oid2).set(STR1, "str2").set(BOOL, TestValues.BOOL)
								.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL)
								.set(FLT, TestValues.FLT).set(SHR, TestValues.SHR).set(BYT, TestValues.BYT)
								.set(BGD, TestValues.BGD).set(ENM, TestValues.ENM).set(DAT, TestValues.DAT)
								.set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
								.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
								.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
								.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
								.set(C_LNG, TestValues.C_LNG).set(NBL, true).build()))
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.flatMapMany(r -> getDatastore().query(TARGET).filter(ID.eq(oid).or(ID.eq(oid2))).stream(SET1))
				.collectList().doOnSuccess(values -> {
					assertEquals(2, values.size());

					PropertyBox value = values.get(0);

					assertEquals(oid, value.getValue(ID));
					assertEquals(TestValues.STR1, value.getValue(STR1));
					assertEquals(TestValues.BOOL, value.getValue(BOOL));
					assertEquals(TestValues.INT, value.getValue(INT));
					assertEquals(TestValues.LNG, value.getValue(LNG));
					assertEquals(TestValues.DBL, value.getValue(DBL));
					assertEquals(TestValues.FLT, value.getValue(FLT));
					assertEquals(TestValues.SHR, value.getValue(SHR));
					assertEquals(TestValues.BYT, value.getValue(BYT));
					assertEquals(TestValues.BGD, value.getValue(BGD));
					assertEquals(TestValues.ENM, value.getValue(ENM));
					assertEquals(TestValues.DAT, value.getValue(DAT));
					assertEquals(TestValues.TMS, value.getValue(TMS));
					assertEquals(TestValues.LDAT, value.getValue(LDAT));
					assertEquals(TestValues.LTMS, value.getValue(LTMS));
					assertEquals(TestValues.LTM, value.getValue(LTM));
					assertTrue(Arrays.equals(TestValues.A_STR, value.getValue(A_STR)));
					assertTrue(Arrays.equals(TestValues.A_INT, value.getValue(A_INT)));
					assertTrue(Arrays.equals(TestValues.A_ENM, value.getValue(A_ENM)));
					assertTrue(Arrays.equals(TestValues.A_CHR, value.getValue(A_CHR)));
					assertTrue(Arrays.equals(TestValues.A_BYT, value.getValue(A_BYT)));
					assertEquals(TestValues.C_STR, value.getValue(C_STR));
					assertEquals(TestValues.C_INT, value.getValue(C_INT));
					assertEquals(TestValues.C_ENM, value.getValue(C_ENM));
					assertEquals(TestValues.C_LNG, value.getValue(C_LNG));
					assertTrue(value.getValue(NBL));
					assertEquals("STR1:" + TestValues.STR1, value.getValue(VRT));

					value = values.get(1);

					assertEquals(oid2, value.getValue(ID));
					assertEquals("str2", value.getValue(STR1));
					assertEquals(TestValues.BOOL, value.getValue(BOOL));
					assertEquals(TestValues.INT, value.getValue(INT));
					assertEquals(TestValues.LNG, value.getValue(LNG));
					assertEquals(TestValues.DBL, value.getValue(DBL));
					assertEquals(TestValues.FLT, value.getValue(FLT));
					assertEquals(TestValues.SHR, value.getValue(SHR));
					assertEquals(TestValues.BYT, value.getValue(BYT));
					assertEquals(TestValues.BGD, value.getValue(BGD));
					assertEquals(TestValues.ENM, value.getValue(ENM));
					assertEquals(TestValues.DAT, value.getValue(DAT));
					assertEquals(TestValues.TMS, value.getValue(TMS));
					assertEquals(TestValues.LDAT, value.getValue(LDAT));
					assertEquals(TestValues.LTMS, value.getValue(LTMS));
					assertEquals(TestValues.LTM, value.getValue(LTM));
					assertTrue(Arrays.equals(TestValues.A_STR, value.getValue(A_STR)));
					assertTrue(Arrays.equals(TestValues.A_INT, value.getValue(A_INT)));
					assertTrue(Arrays.equals(TestValues.A_ENM, value.getValue(A_ENM)));
					assertTrue(Arrays.equals(TestValues.A_CHR, value.getValue(A_CHR)));
					assertTrue(Arrays.equals(TestValues.A_BYT, value.getValue(A_BYT)));
					assertEquals(TestValues.C_STR, value.getValue(C_STR));
					assertEquals(TestValues.C_INT, value.getValue(C_INT));
					assertEquals(TestValues.C_ENM, value.getValue(C_ENM));
					assertEquals(TestValues.C_LNG, value.getValue(C_LNG));
					assertTrue(value.getValue(NBL));
					assertEquals("STR1:str2", value.getValue(VRT));
				}).then(getDatastore().bulkDelete(TARGET).filter(ID.eq(oid).or(ID.eq(oid2))).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(2L).expectComplete().verify();

	}

	@Test
	public void testPropertyProjection() {

		final Mono<Long> op = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp1").set(INT, 1).build())
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp2").set(INT, 2).build())
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp3").set(INT, 3).build()).execute()
				.doOnSuccess(r -> assertEquals(3, r.getAffectedCount()))
				.flatMapMany(x -> getDatastore().query(TARGET).sort(INT.asc()).stream(STR1, INT)).collectList()
				.doOnSuccess(values -> {
					assertEquals(3, values.size());
					assertEquals("bktfp1", values.get(0).getValue(STR1));
					assertEquals(Integer.valueOf(1), values.get(0).getValue(INT));
					assertEquals("bktfp2", values.get(1).getValue(STR1));
					assertEquals(Integer.valueOf(2), values.get(1).getValue(INT));
					assertEquals("bktfp3", values.get(2).getValue(STR1));
					assertEquals(Integer.valueOf(3), values.get(2).getValue(INT));
				}).then(getDatastore().bulkDelete(TARGET).filter(STR1.in("bktfp1", "bktfp2", "bktfp3")).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(3L).expectComplete().verify();

	}

	@Test
	public void testLiteralProjection() {

		final Mono<Long> op = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp1").build())
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp2").build())
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp3").build()).execute()
				.doOnSuccess(r -> assertEquals(3, r.getAffectedCount()))
				.then(getDatastore().query(TARGET).list(ConstantExpression.create(1))).doOnSuccess(literals -> {
					assertEquals(3, literals.size());
					literals.forEach(l -> {
						assertEquals(Integer.valueOf(1), l);
					});
				}).then(getDatastore().query(TARGET).list(ConstantExpression.create("S"))).doOnSuccess(sliterals -> {
					assertEquals(3, sliterals.size());
					sliterals.forEach(l -> {
						assertEquals("S", l);
					});
				}).then(getDatastore().bulkDelete(TARGET).filter(STR1.in("bktfp1", "bktfp2", "bktfp3")).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(3L).expectComplete().verify();
	}

	@Test
	public void testTemporalFunctionProjection() {

		final Mono<Long> op = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp1").set(LDAT, LocalDate.of(2016, Month.JANUARY, 3))
						.set(LTMS, LocalDateTime.of(2016, Month.JANUARY, 3, 10, 30)).build())
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp2").set(LDAT, LocalDate.of(2017, Month.FEBRUARY, 4))
						.set(LTMS, LocalDateTime.of(2016, Month.JANUARY, 3, 12, 30)).build())
				.add(PropertyBox.builder(SET1).set(STR1, "bktfp3").set(LDAT, LocalDate.of(2018, Month.MARCH, 5))
						.set(LTMS, LocalDateTime.of(2016, Month.JANUARY, 3, 18, 30)).build())
				.execute().doOnSuccess(r -> assertEquals(3, r.getAffectedCount()))
				.then(getDatastore().query(TARGET).list(LDAT.year())).doOnSuccess(years -> {
					assertEquals(3, years.size());
					assertTrue(years.contains(2016));
					assertTrue(years.contains(2017));
					assertTrue(years.contains(2018));
				}).then(getDatastore().query(TARGET).list(LDAT.month())).doOnSuccess(months -> {
					assertEquals(3, months.size());
					assertTrue(months.contains(1));
					assertTrue(months.contains(2));
					assertTrue(months.contains(3));
				}).then(getDatastore().query(TARGET).list(LDAT.day())).doOnSuccess(days -> {
					assertEquals(3, days.size());
					assertTrue(days.contains(3));
					assertTrue(days.contains(4));
					assertTrue(days.contains(5));
				}).then(getDatastore().query(TARGET).list(LTMS.hour())).doOnSuccess(hours -> {
					assertEquals(3, hours.size());
					assertTrue(hours.contains(10));
					assertTrue(hours.contains(12));
					assertTrue(hours.contains(18));
				}).then(getDatastore().bulkDelete(TARGET).filter(STR1.in("bktfp1", "bktfp2", "bktfp3")).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(3L).expectComplete().verify();

	}

	@Test
	public void testDocumentIdProjection() {

		final ObjectId oid = new ObjectId();

		final Mono<Long> op = getDatastore()
				.insert(TARGET, PropertyBox.builder(SET3).set(ID3, oid).set(STR1, "test1").build())
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().query(TARGET).filter(ID3.eq(oid)).findOne(SET3)).doOnSuccess(value -> {
					assertNotNull(value);
					assertEquals(oid, value.getValue(ID3));
					assertEquals("test1", value.getValue(STR1));
				}).then(getDatastore().bulkDelete(TARGET).filter(ID3.eq(oid)).execute().map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(1L).expectComplete().verify();

	}

	@Test
	public void testDocumentIdProjectionString() {

		final ObjectId oid = new ObjectId();
		final String code = oid.toHexString();

		final Mono<Long> op = getDatastore()
				.insert(TARGET, PropertyBox.builder(SET4).set(ID4, code).set(STR1, "test1").build())
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().query(TARGET).filter(ID4.eq(code)).findOne(SET4)).doOnSuccess(value -> {
					assertNotNull(value);
					assertEquals(code, value.getValue(ID4));
					assertEquals("test1", value.getValue(STR1));
				})
				.then(getDatastore().bulkDelete(TARGET).filter(ID4.eq(code)).execute().map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(1L).expectComplete().verify();

	}

	@Test
	public void testDocumentIdProjectionBigInteger() {

		final ObjectId oid = new ObjectId();
		final BigInteger code = new BigInteger(oid.toHexString(), 16);

		final Mono<Long> op = getDatastore()
				.insert(TARGET, PropertyBox.builder(SET5).set(ID5, code).set(STR1, "test1").build())
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().query(TARGET).filter(ID5.eq(code)).findOne(SET5)).doOnSuccess(value -> {
					assertNotNull(value);
					assertEquals(code, value.getValue(ID5));
					assertEquals("test1", value.getValue(STR1));
				})
				.then(getDatastore().bulkDelete(TARGET).filter(ID5.eq(code)).execute().map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(1L).expectComplete().verify();

	}

	@Test
	public void testPropertyConversion() {

		final Mono<Long> op = getDatastore().bulkInsert(TARGET, SET1)
				.add(PropertyBox.builder(SET1).set(STR1, "nbktfp1").set(NBL, true).build())
				.add(PropertyBox.builder(SET1).set(STR1, "nbktfp2").set(NBL, false).build())
				.add(PropertyBox.builder(SET1).set(STR1, "nbktfp3").build()).execute()
				.doOnSuccess(r -> assertEquals(3, r.getAffectedCount()))
				.then(getDatastore().query().target(TARGET).sort(STR1.asc()).list(NBL)).doOnSuccess(values -> {
					assertNotNull(values);
					assertEquals(3, values.size());
					assertEquals(Boolean.TRUE, values.get(0));
					assertEquals(Boolean.FALSE, values.get(1));
					assertEquals(Boolean.FALSE, values.get(2));
				}).then(getDatastore().bulkDelete(TARGET).filter(STR1.in("nbktfp1", "nbktfp2", "nbktfp3")).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(3L).expectComplete().verify();

	}

	@Test
	public void testProjectionBean() {

		final ObjectId oid1 = new ObjectId();
		final ObjectId oid2 = new ObjectId();

		final Mono<Long> op = getDatastore()
				.insert(TARGET, PropertyBox.builder(SET1).set(ID, oid1).set(STR1, "One").build())
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().insert(TARGET, PropertyBox.builder(SET1).set(ID, oid2).set(STR1, "Two").build()))
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount())).then(getDatastore().query().target(TARGET)
						.sort(STR1.asc()).list(BeanProjection.of(TestProjectionBean.class)))
				.doOnSuccess(results -> {
					assertNotNull(results);
					assertEquals(2, results.size());
					assertEquals(oid1.toHexString(), results.get(0).getId());
					assertEquals("One", results.get(0).getText());
					assertEquals(oid2.toHexString(), results.get(1).getId());
					assertEquals("Two", results.get(1).getText());
				}).then(getDatastore().query().target(TARGET).sort(STR1.asc())
						.list(BeanProjection.of(TestProjectionBean2.class)))
				.doOnSuccess(results2 -> {
					assertNotNull(results2);
					assertEquals(2, results2.size());
					assertEquals(oid1, new ObjectId(results2.get(0).getCode().toString(16)));
					assertEquals("One", results2.get(0).getText());
					assertEquals(oid2, new ObjectId(results2.get(1).getCode().toString(16)));
					assertEquals("Two", results2.get(1).getText());
				}).then(getDatastore().bulkDelete(TARGET).filter(ID.in(oid1, oid2)).execute()
						.map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(2L).expectComplete().verify();
	}

	@Test
	public void testNestedProjectionBean() {

		final ObjectId oid = new ObjectId();

		final Mono<Long> op = getDatastore()
				.insert(TARGET,
						PropertyBox.builder(SET6).set(ID, oid).set(STR1, "testn").set(ENM, EnumValue.FIRST)
								.set(N1_V1, "n1v1").set(N1_V2, "n1v2").set(N1_V3, true).set(N2_V1, 52)
								.set(N2_V2, "n2v2").set(N3_V1, "n3v1").set(N3_V2, 12.97d).build())
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SET6)).doOnSuccess(value -> {
					assertNotNull(value);
					assertEquals(oid, value.getValue(ID));
					assertEquals("testn", value.getValue(STR1));
					assertEquals(EnumValue.FIRST, value.getValue(ENM));
					assertEquals("n1v1", value.getValue(N1_V1));
					assertEquals("n1v2", value.getValue(N1_V2));
					assertEquals(Boolean.TRUE, value.getValue(N1_V3));
					assertEquals(Integer.valueOf(52), value.getValue(N2_V1));
					assertEquals("n2v2", value.getValue(N2_V2));
					assertEquals("n3v1", value.getValue(N3_V1));
					assertEquals(Double.valueOf(12.97d), value.getValue(N3_V2));
				}).then(getDatastore().query(TARGET).filter(ID.eq(oid))
						.findOne(BeanProjection.of(TestProjectionBean3.class)))
				.doOnSuccess(bean -> {
					assertNotNull(bean);
					assertEquals(oid.toHexString(), bean.getId());
					assertEquals("testn", bean.getStr());
					assertEquals(EnumValue.FIRST, bean.getEnm());
					assertNotNull(bean.getN1());
					assertNotNull(bean.getN2());
					assertEquals("n1v1", bean.getN1().getV1());
					assertEquals("n1v2", bean.getN1().getValue2());
					assertTrue(bean.getN1().isV3());
					assertEquals(Integer.valueOf(52), bean.getN2().getV1());
					assertEquals("n2v2", bean.getN2().getV2());
					assertNotNull(bean.getN2().getNested());
					assertEquals("n3v1", bean.getN2().getNested().getV1());
					assertEquals(Double.valueOf(12.97d), bean.getN2().getNested().getV2());
				}).then(getDatastore().bulkDelete(TARGET).filter(ID.eq(oid)).execute().map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(1L).expectComplete().verify();
	}

	@Test
	public void testSelectAll() {

		final ObjectId oid = new ObjectId();

		final Mono<Long> op = getDatastore()
				.insert(TARGET,
						PropertyBox.builder(SET1).set(ID, oid).set(STR1, TestValues.STR1).set(BOOL, TestValues.BOOL)
								.set(INT, TestValues.INT).set(LNG, TestValues.LNG).set(DBL, TestValues.DBL)
								.set(FLT, TestValues.FLT).set(SHR, TestValues.SHR).set(BYT, TestValues.BYT)
								.set(BGD, TestValues.BGD).set(ENM, TestValues.ENM).set(DAT, TestValues.DAT)
								.set(TMS, TestValues.TMS).set(LDAT, TestValues.LDAT).set(LTMS, TestValues.LTMS)
								.set(LTM, TestValues.LTM).set(A_STR, TestValues.A_STR).set(A_INT, TestValues.A_INT)
								.set(A_ENM, TestValues.A_ENM).set(A_CHR, TestValues.A_CHR).set(A_BYT, TestValues.A_BYT)
								.set(C_STR, TestValues.C_STR).set(C_INT, TestValues.C_INT).set(C_ENM, TestValues.C_ENM)
								.set(C_LNG, TestValues.C_LNG).set(NBL, true).build())
				.doOnSuccess(r -> assertEquals(1, r.getAffectedCount()))
				.then(getDatastore().query(TARGET).filter(ID.eq(oid)).findOne(SelectAllProjection.create()))
				.doOnSuccess(values -> {
					assertNotNull(values);
					assertTrue(values.containsKey("_id"));
					assertEquals(oid, values.get("_id"));
					assertTrue(values.containsKey("str"));
					assertEquals(TestValues.STR1, values.get("str"));
				}).then(getDatastore().bulkDelete(TARGET).filter(ID.eq(oid)).execute().map(r -> r.getAffectedCount()));

		StepVerifier.create(op).expectNext(1L).expectComplete().verify();

	}

	@Test
	public void testCountNone() {

		final Mono<Long> op = getDatastore().query(TARGET).filter(STR1.eq("--xxx---xxxx---xxx")).count()
				.doOnSuccess(r -> {
					assertNotNull(r);
				});

		StepVerifier.create(op).expectNext(0L).expectComplete().verify();
	}

}
