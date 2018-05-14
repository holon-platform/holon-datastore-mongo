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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import org.bson.types.ObjectId;

import com.holonplatform.core.property.BooleanProperty;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.property.TemporalProperty;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.core.temporal.TemporalType;

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

	public final static PathProperty<Boolean> NBL = PathProperty.create("nbl", boolean.class)
			.converter(PropertyValueConverter.numericBoolean(Integer.class));

	public static final VirtualProperty<String> VRT = VirtualProperty.create(String.class,
			pb -> "STR:" + pb.getValue(STR));

	public static final PropertySet<?> SET1 = PropertySet.of(ID, STR, STR2, BOOL, INT, LNG, DBL, FLT, BGD, SHR, BYT,
			ENM, DAT, TMS, LDAT, LTMS, LTM, A_STR, A_INT, A_ENM, A_CHR, A_BYT, NBL, VRT);

}
