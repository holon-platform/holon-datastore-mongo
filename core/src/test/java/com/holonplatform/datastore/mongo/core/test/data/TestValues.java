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
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

public final class TestValues {

	public static final String STR = "test";
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
	}

}
