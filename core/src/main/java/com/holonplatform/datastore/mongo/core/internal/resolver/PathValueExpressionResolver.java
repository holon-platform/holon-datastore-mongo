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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;

import org.bson.types.Binary;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.property.Property;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.expression.PathValue;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * Resolver to resolve a {@link PathValue} into a {@link FieldValue}.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum PathValueExpressionResolver implements MongoExpressionResolver<PathValue, FieldValue> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<FieldValue> resolve(PathValue expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		final PathValue<Object> exp = expression;

		// check converter
		final Object value = exp.getProperty().map(p -> p.getConvertedValue(exp.getValue())).orElse(exp.getValue());

		// Enum codec strategy
		final EnumCodecStrategy strategy = exp.getProperty()
				.flatMap(p -> p.getConfiguration().getParameter(EnumCodecStrategy.CONFIG_PROPERTY))
				.orElse(context.getDefaultEnumCodecStrategy());

		// check type conversion
		Object encoded = exp.getProperty().filter(p -> isDocumentIdProperty(context, p))
				.map(p -> (Object) context.getDocumentIdResolver().encode(value)).orElse(checkType(strategy, value));

		return Optional.of(FieldValue.create(encoded, exp.getProperty().orElse(null)));
	}

	/**
	 * Check if given property acts as document id property.
	 * @param context Resolution context
	 * @param property The property to check
	 * @return <code>true</code> if given property acts as document id property, <code>false</code> otherwise
	 */
	private static boolean isDocumentIdProperty(MongoResolutionContext context, Property<?> property) {
		return MongoDocumentContext.isDocumentContext(context).map(ctx -> ctx.isDocumentIdProperty(property))
				.orElse(false);
	}

	/**
	 * Check the value type, applying suitable conversions if required.
	 * @param strategy Enum codec strategy
	 * @param value The value to encode
	 * @return Checked value
	 */
	private static Object checkType(EnumCodecStrategy strategy, Object value) {
		if (value != null) {
			// enums
			if (value.getClass().isEnum()) {
				return encodeEnum(strategy, (Enum<?>) value);
			}
			// binary
			if (byte[].class == value.getClass()) {
				return new Binary((byte[]) value);
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
				return arrayToList(strategy, (Object[]) value);
			}
		}
		return value;
	}

	/**
	 * Encode given {@link Enum} type value using the provided Enum codec strategy.
	 * @param strategy Enum codec strategy to use
	 * @param value The value to encode
	 * @return The encoded enum value according to the specified strategy
	 */
	private static Object encodeEnum(EnumCodecStrategy strategy, Enum<?> value) {
		if (value != null) {
			switch (strategy) {
			case ORDINAL:
				return Integer.valueOf(value.ordinal());
			case NAME:
			default:
				return value.name();
			}
		}
		return null;
	}

	/**
	 * Convert an array of values into a {@link List} of the same values.
	 * @param strategy Enum codec strategy
	 * @param value The array value
	 * @return The array converted into a List
	 */
	@SuppressWarnings("unchecked")
	private static <T> List<T> arrayToList(EnumCodecStrategy strategy, T[] value) {
		if (value != null) {
			List<T> list = new ArrayList<>(value.length);
			for (T v : value) {
				list.add((T) checkType(strategy, v));
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends PathValue> getExpressionType() {
		return PathValue.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends FieldValue> getResolvedType() {
		return FieldValue.class;
	}

}
