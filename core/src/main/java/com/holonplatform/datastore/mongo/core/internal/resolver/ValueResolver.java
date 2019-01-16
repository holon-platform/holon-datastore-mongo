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
package com.holonplatform.datastore.mongo.core.internal.resolver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Priority;

import org.bson.types.Binary;
import org.bson.types.Decimal128;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.internal.utils.CalendarUtils;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.expression.Value;
import com.holonplatform.datastore.mongo.core.internal.support.DocumentIdHelper;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * Resolver to resolve a {@link Value} into a {@link FieldValue}.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum ValueResolver implements MongoExpressionResolver<Value, FieldValue> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<FieldValue> resolve(Value expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		final Value<Object> exp = expression;

		// check converter
		final Object value = exp.getExpression().flatMap(e -> e.isConverterExpression())
				.map(ce -> ce.getModelValue(exp.getValue())).orElse(exp.getValue());

		// Enum codec strategy
		final EnumCodecStrategy strategy = exp.getEnumCodecStrategy().orElse(context.getDefaultEnumCodecStrategy());

		// Temporal type
		TemporalType temporalType = exp.getExpression().flatMap(e -> e.getTemporalType()).orElse(null);

		// check type conversion
		try {
			Object encoded = exp.getExpression().filter(p -> DocumentIdHelper.isDefaultDocumentIdProperty(context, p))
					.map(p -> (Object) context.getDocumentIdResolver().encode(value))
					.orElse(checkType(strategy, temporalType, value));

			return Optional.of(FieldValue.create(encoded, exp.getExpression().orElse(null), strategy));
		} catch (InvalidExpressionException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidExpressionException("Failed to encode value [" + expression.getValue() + "]", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends Value> getExpressionType() {
		return Value.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends FieldValue> getResolvedType() {
		return FieldValue.class;
	}

	/**
	 * Check the value type, applying suitable conversions if required.
	 * @param strategy Enum codec strategy
	 * @param temporalType Optional value temporal type
	 * @param value The value to encode
	 * @return Checked value
	 */
	private static Object checkType(EnumCodecStrategy strategy, TemporalType temporalType, Object value) {
		if (value != null) {
			// collections
			if (Collection.class.isAssignableFrom(value.getClass())) {
				final Collection collection = (Collection) value;
				Collection<Object> values = Set.class.isAssignableFrom(value.getClass())
						? new HashSet<>(collection.size())
						: new ArrayList<>(collection.size());
				for (Object collectionValue : collection) {
					final Object encodedValue = checkType(strategy, temporalType, collectionValue);
					if (encodedValue != null) {
						values.add(encodedValue);
					}
				}
				return values;
			}
			// dates
			if (Date.class.isAssignableFrom(value.getClass()) && temporalType != null) {
				if (TemporalType.DATE == temporalType) {
					// reset time fields
					return CalendarUtils.floorTime((Date) value);
				}
			}
			// enums
			if (value.getClass().isEnum()) {
				return encodeEnum(strategy, (Enum<?>) value);
			}
			// binary
			if (byte[].class == value.getClass()) {
				return new Binary((byte[]) value);
			}
			// BigInteger
			if (BigInteger.class.isAssignableFrom(value.getClass())) {
				return new Decimal128(new BigDecimal((BigInteger) value));
			}
			// BigDecimal
			if (BigDecimal.class.isAssignableFrom(value.getClass())) {
				return new Decimal128((BigDecimal) value);
			}
			// check arrays
			if (value.getClass().isArray()) {
				// check primitive types
				if (char[].class == value.getClass()) {
					return new String((char[]) value);
				}
				// convert arrays into Lists
				if (int[].class == value.getClass()) {
					return toList((int[]) value);
				}
				if (long[].class == value.getClass()) {
					return toList((long[]) value);
				}
				if (double[].class == value.getClass()) {
					return toList((double[]) value);
				}
				if (float[].class == value.getClass()) {
					return toList((float[]) value);
				}
				if (short[].class == value.getClass()) {
					return toList((short[]) value);
				}
				if (boolean[].class == value.getClass()) {
					return toList((boolean[]) value);
				}
				return arrayToList(strategy, temporalType, (Object[]) value);
			}
		}
		return value;
	}

	/**
	 * Encode given {@link Enum} type value using the provided Enum codec strategy.
	 * @param strategy Enum codec strategy to use
	 * @param value The value to encode
	 * @return The encoded enum value according to the specified strategy
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static Object encodeEnum(EnumCodecStrategy strategy, Enum<?> value) throws InvalidExpressionException {
		if (value != null) {
			try {
				switch (strategy) {
				case ORDINAL:
					return Integer.valueOf(value.ordinal());
				case NAME:
				default:
					return value.name();
				}
			} catch (Exception e) {
				throw new InvalidExpressionException(
						"Failed to encode value [" + value + "] using strategy [" + strategy + "]", e);
			}
		}
		return null;
	}

	/**
	 * Convert an array of values into a {@link List} of the same values.
	 * @param strategy Enum codec strategy
	 * @param temporalType Optional value temporal type
	 * @param value The array value
	 * @return The array converted into a List
	 */
	@SuppressWarnings("unchecked")
	private static <T> List<T> arrayToList(EnumCodecStrategy strategy, TemporalType temporalType, T[] value) {
		if (value != null) {
			List<T> list = new ArrayList<>(value.length);
			for (T v : value) {
				list.add((T) checkType(strategy, temporalType, v));
			}
			return list;
		}
		return null;
	}

	/**
	 * Convert an int array into a {@link List}.
	 * @param value Value to convert
	 * @return Converted value
	 */
	private static List<Integer> toList(int[] value) {
		if (value != null) {
			List<Integer> list = new ArrayList<>(value.length);
			for (int i = 0; i < value.length; i++) {
				list.add(Integer.valueOf(value[i]));
			}
			return list;
		}
		return null;
	}

	/**
	 * Convert a long array into a {@link List}.
	 * @param value Value to convert
	 * @return Converted value
	 */
	private static List<Long> toList(long[] value) {
		if (value != null) {
			List<Long> list = new ArrayList<>(value.length);
			for (int i = 0; i < value.length; i++) {
				list.add(Long.valueOf(value[i]));
			}
			return list;
		}
		return null;
	}

	/**
	 * Convert a double array into a {@link List}.
	 * @param value Value to convert
	 * @return Converted value
	 */
	private static List<Double> toList(double[] value) {
		if (value != null) {
			List<Double> list = new ArrayList<>(value.length);
			for (int i = 0; i < value.length; i++) {
				list.add(Double.valueOf(value[i]));
			}
			return list;
		}
		return null;
	}

	/**
	 * Convert a float array into a {@link List}.
	 * @param value Value to convert
	 * @return Converted value
	 */
	private static List<Float> toList(float[] value) {
		if (value != null) {
			List<Float> list = new ArrayList<>(value.length);
			for (int i = 0; i < value.length; i++) {
				list.add(Float.valueOf(value[i]));
			}
			return list;
		}
		return null;
	}

	/**
	 * Convert a short array into a {@link List}.
	 * @param value Value to convert
	 * @return Converted value
	 */
	private static List<Short> toList(short[] value) {
		if (value != null) {
			List<Short> list = new ArrayList<>(value.length);
			for (int i = 0; i < value.length; i++) {
				list.add(Short.valueOf(value[i]));
			}
			return list;
		}
		return null;
	}

	/**
	 * Convert a boolean array into a {@link List}.
	 * @param value Value to convert
	 * @return Converted value
	 */
	private static List<Boolean> toList(boolean[] value) {
		if (value != null) {
			List<Boolean> list = new ArrayList<>(value.length);
			for (int i = 0; i < value.length; i++) {
				list.add(Boolean.valueOf(value[i]));
			}
			return list;
		}
		return null;
	}

}
