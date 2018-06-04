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
package com.holonplatform.datastore.mongo.core.test.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import org.bson.types.ObjectId;

import com.holonplatform.core.Path;
import com.holonplatform.core.property.BooleanProperty;
import com.holonplatform.core.property.ListPathProperty;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyBoxProperty;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.core.property.SetPathProperty;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.property.TemporalProperty;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;

public interface ModelTest {

	public static final PathProperty<ObjectId> ID = PathProperty.create("_id", ObjectId.class);

	public static final StringProperty STR = StringProperty.create("str");
	public static final StringProperty STR2 = StringProperty.create("str2");
	public static final BooleanProperty BOOL = BooleanProperty.create("bool");
	public static final NumericProperty<Integer> INT = NumericProperty.integerType("int");
	public static final NumericProperty<Long> LNG = NumericProperty.longType("lng");
	public static final NumericProperty<Double> DBL = NumericProperty.doubleType("dbl");
	public static final NumericProperty<Float> FLT = NumericProperty.floatType("flt");
	// public static final NumericProperty<BigInteger> BGI = NumericProperty.bigIntegerType("bgi");
	public static final NumericProperty<BigDecimal> BGD = NumericProperty.bigDecimalType("bgd");
	public static final NumericProperty<Short> SHR = NumericProperty.shortType("shr");
	public static final NumericProperty<Byte> BYT = NumericProperty.byteType("byt");
	public static final PathProperty<EnumValue> ENM = PathProperty.create("enm", EnumValue.class);
	public static final TemporalProperty<Date> DAT = TemporalProperty.create("dat", Date.class)
			.temporalType(TemporalType.DATE);
	public static final TemporalProperty<Date> TMS = TemporalProperty.create("tms", Date.class)
			.temporalType(TemporalType.DATE_TIME);
	public static final TemporalProperty<LocalDate> LDAT = TemporalProperty.localDate("ldat");
	public static final TemporalProperty<LocalDateTime> LTMS = TemporalProperty.localDateTime("ltms");
	public static final TemporalProperty<LocalTime> LTM = TemporalProperty.localTime("ltm");

	public static final PathProperty<String[]> A_STR = PathProperty.create("astr", String[].class);
	public static final PathProperty<int[]> A_INT = PathProperty.create("aint", int[].class);
	public static final PathProperty<EnumValue[]> A_ENM = PathProperty.create("aenm", EnumValue[].class);
	public static final PathProperty<char[]> A_CHR = PathProperty.create("achr", char[].class);
	public static final PathProperty<byte[]> A_BYT = PathProperty.create("abyt", byte[].class);

	public static ListPathProperty<String> C_STR = ListPathProperty.create("cstr", String.class);
	public static SetPathProperty<Integer> C_INT = SetPathProperty.create("cint", Integer.class);
	public static SetPathProperty<EnumValue> C_ENM = SetPathProperty.create("cenm", EnumValue.class);
	public static ListPathProperty<Long> C_LNG = ListPathProperty.create("clng", Long.class)
			.elementConverter(String.class, Long::valueOf, String::valueOf);

	public final static PathProperty<Boolean> NBL = PathProperty.create("nbl", boolean.class)
			.converter(PropertyValueConverter.numericBoolean(Integer.class));

	public static final VirtualProperty<String> VRT = VirtualProperty.create(String.class,
			pb -> "STR:" + pb.getValue(STR));

	public static final PropertySet<?> SET1 = PropertySet.of(ID, STR, STR2, BOOL, INT, LNG, DBL, FLT, BGD, SHR, BYT,
			ENM, DAT, TMS, LDAT, LTMS, LTM, A_STR, A_INT, A_ENM, A_CHR, A_BYT, C_STR, C_INT, C_ENM, C_LNG, NBL, VRT);

	public static final PathProperty<EnumValue> ENM2 = PathProperty.create("enm2", EnumValue.class)
			.configuration(EnumCodecStrategy.CONFIG_PROPERTY, EnumCodecStrategy.ORDINAL);

	public static final PropertySet<?> SET2 = PropertySet.of(ID, ENM2);

	public static final PathProperty<ObjectId> ID3 = PathProperty.create("code", ObjectId.class);

	public static final PropertySet<?> SET3 = PropertySet.builderOf(ID3, STR).identifier(ID3).build();

	public static final StringProperty ID4 = StringProperty.create("code");

	public static final PropertySet<?> SET4 = PropertySet.builderOf(ID4, STR).identifier(ID4).build();

	public static final NumericProperty<BigInteger> ID5 = NumericProperty.bigIntegerType("code");

	public static final PropertySet<?> SET5 = PropertySet.builderOf(ID5, STR).identifier(ID5).build();

	// nested

	public static final StringProperty N1_V1 = StringProperty.create("n1.v1");
	public static final StringProperty N1_V2 = StringProperty.create("n1.v2");
	public static final BooleanProperty N1_V3 = BooleanProperty.create("v3").parent(Path.of("n1", Object.class));
	public static final NumericProperty<Integer> N2_V1 = NumericProperty.integerType("n2.v1");
	public static final StringProperty N2_V2 = StringProperty.create("n2.v2");
	public static final StringProperty N3_V1 = StringProperty.create("n2.n3.v1");
	public static final NumericProperty<Double> N3_V2 = NumericProperty.doubleType("n2.n3.v2");

	public static final PropertySet<?> SET6 = PropertySet.of(ID, STR, ENM, N1_V1, N1_V2, N1_V3, N2_V1, N2_V2, N3_V1,
			N3_V2);

	public static final StringProperty NESTED_V1 = StringProperty.create("v1");
	public static final StringProperty NESTED_V2 = StringProperty.create("v2");

	public static final PropertySet<?> NESTED_SET = PropertySet.of(NESTED_V1, NESTED_V2);

	public static final PropertyBoxProperty NESTED = PropertyBoxProperty.create("n1", NESTED_SET);

	public static final PropertySet<?> SET7 = PropertySet.of(ID, STR, ENM, NESTED);

	public static final PropertySet<?> SET8 = PropertySet.of(ID, STR, ENM, NESTED, N2_V1, N2_V2, N3_V1, N3_V2);

	// collections

	public static final ListPathProperty<String> CP_LIST = ListPathProperty.create("cplist", String.class);
	public static final SetPathProperty<Integer> CP_SET = SetPathProperty.create("cpset", Integer.class);
	public static final SetPathProperty<EnumValue> CP_ENM = SetPathProperty.create("cpenm", EnumValue.class);

	public static final PropertySet<?> SET9 = PropertySet.of(ID, STR, CP_LIST, CP_SET, CP_ENM);

	// collection of PropertyBox

	public static final ListPathProperty<PropertyBox> C_PBX = ListPathProperty.propertyBox("cpbx", NESTED_SET);

	public static final PropertySet<?> SET10 = PropertySet.of(ID, STR, C_PBX);

}
