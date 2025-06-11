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
package com.holonplatform.datastore.mongo.async.test.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TestValues {

	public static final String STR1 = "test";
	public static final String STR2 = "test2";
	public static final Boolean BOOL = Boolean.TRUE;
	public static final Integer INT = Integer.valueOf(1234567);
	public static final Long LNG = Long.valueOf(778877L);
	public static final Double DBL = Double.valueOf(4576.768d);
	public static final Float FLT = Float.valueOf(332.68f);
	public static final BigInteger BGI = BigInteger.valueOf(3344662774586903848L);
	public static final BigDecimal BGD = BigDecimal.valueOf(145678.7632d);
	public static final Short SHR = Short.valueOf((short) 583);
	public static final Byte BYT = Byte.valueOf((byte) 32);
	public static final EnumValue ENM = EnumValue.SECOND;
	public static final Date DAT;
	public static final Date TMS;
	public static final LocalDate LDAT = LocalDate.of(2018, Month.FEBRUARY, 7);
	public static final LocalDateTime LTMS = LocalDateTime.of(2018, Month.FEBRUARY, 7, 16, 30, 15);
	public static final LocalTime LTM = LocalTime.of(18, 45, 30);
	public static final String[] A_STR = { "a", "b", "c" };
	public static final int[] A_INT = { 1, 2, 3 };
	public static final EnumValue[] A_ENM = { EnumValue.THIRD, EnumValue.SECOND };
	public static final char[] A_CHR = { 'a', 'b', 'c' };
	public static final byte[] A_BYT = { 1, 2, 3 };
	public static final List<String> C_STR = Arrays.asList("d", "e", "f");
	public static final Set<Integer> C_INT = new HashSet<>(Arrays.asList(4, 5, 6));
	public static final Set<EnumValue> C_ENM = new HashSet<>(
			Arrays.asList(EnumValue.FIRST, EnumValue.SECOND, EnumValue.THIRD));
	public static final List<Long> C_LNG = Arrays.asList(10L, 11L, 12L);

	public static final String U_STR = "test_u";
	public static final String U_STR2 = "test2_u";
	public static final Boolean U_BOOL = Boolean.FALSE;
	public static final Integer U_INT = Integer.valueOf(8910);
	public static final Long U_LNG = Long.valueOf(99775566L);
	public static final Double U_DBL = Double.valueOf(789.21d);
	public static final Float U_FLT = Float.valueOf(655.7f);
	public static final BigInteger U_BGI = BigInteger.valueOf(6744621755586903248L);
	public static final BigDecimal U_BGD = BigDecimal.valueOf(45973.1234d);
	public static final Short U_SHR = Short.valueOf((short) 214);
	public static final Byte U_BYT = Byte.valueOf((byte) 16);
	public static final EnumValue U_ENM = EnumValue.FIRST;
	public static final Date U_DAT;
	public static final Date U_TMS;
	public static final LocalDate U_LDAT = LocalDate.of(2019, Month.JUNE, 4);
	public static final LocalDateTime U_LTMS = LocalDateTime.of(2019, Month.JUNE, 4, 11, 15, 30);
	public static final LocalTime U_LTM = LocalTime.of(7, 30, 15);
	public static final String[] U_A_STR = { "d", "e", "f" };
	public static final int[] U_A_INT = { 4, 5 };
	public static final EnumValue[] U_A_ENM = { EnumValue.FIRST, EnumValue.SECOND, EnumValue.THIRD };
	public static final char[] U_A_CHR = { 'd', 'e', 'f' };
	public static final byte[] U_A_BYT = { 4, 5, 6, 7 };
	public static final List<String> U_C_STR = Arrays.asList("g", "h", "i");
	public static final Set<Integer> U_C_INT = new HashSet<>(Arrays.asList(7, 8, 9, 10));
	public static final Set<EnumValue> U_C_ENM = new HashSet<>(Arrays.asList(EnumValue.FIRST));
	public static final List<Long> U_C_LNG = Arrays.asList(20L, 21L, 22L);

	static {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2018);
		c.set(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 7);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		DAT = c.getTime();

		c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2018);
		c.set(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 7);
		c.set(Calendar.HOUR_OF_DAY, 16);
		c.set(Calendar.MINUTE, 30);
		c.set(Calendar.SECOND, 15);
		c.set(Calendar.MILLISECOND, 0);
		TMS = c.getTime();

		c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2019);
		c.set(Calendar.MONTH, 5);
		c.set(Calendar.DAY_OF_MONTH, 4);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		U_DAT = c.getTime();

		c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2019);
		c.set(Calendar.MONTH, 4);
		c.set(Calendar.DAY_OF_MONTH, 5);
		c.set(Calendar.HOUR_OF_DAY, 11);
		c.set(Calendar.MINUTE, 15);
		c.set(Calendar.SECOND, 30);
		c.set(Calendar.MILLISECOND, 0);
		U_TMS = c.getTime();
	}

}
