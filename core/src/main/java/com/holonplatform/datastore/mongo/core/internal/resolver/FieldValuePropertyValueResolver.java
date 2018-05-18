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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Priority;

import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import org.bson.types.Symbol;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionValueConverter;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.expression.Value;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * Resolver to resolve a {@link Value} into a {@link FieldValue}.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum FieldValuePropertyValueResolver implements MongoExpressionResolver<FieldValue, Value> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<Value> resolve(FieldValue expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// check property
		return Optional.of(expression.getExpression()
				.map(expr -> Value.create(decode(context, expr, expression.getValue()), (TypedExpression) expr,
						expression.getEnumCodecStrategy().orElse(null)))
				.orElse(Value.create(expression.getValue())));
	}

	/**
	 * Decode the value bound to the specified property.
	 * @param context Resolution context
	 * @param expression Field value expression
	 * @param value Value
	 * @return Decoded value
	 * @throws InvalidExpressionException If an error occurred
	 */
	@SuppressWarnings("unchecked")
	private static Object decode(MongoResolutionContext context, TypedExpression<?> expression, Object value)
			throws InvalidExpressionException {

		try {
			// expected type
			Class<?> targetType = expression.isConverterExpression().map(c -> (Class) c.getModelType())
					.orElse(expression.getType());

			// check Bson types
			Object decoded = checkBsonType(targetType, value);

			// check converter
			if (expression.isConverterExpression().flatMap(c -> c.getExpressionValueConverter()).isPresent()) {
				final ExpressionValueConverter converter = expression.isConverterExpression()
						.flatMap(c -> c.getExpressionValueConverter()).get();
				if (decoded == null || TypeUtils.isAssignable(decoded.getClass(), converter.getModelType())) {
					decoded = converter.fromModel(decoded);
				}
			}

			// check document id property value conversion
			if (ObjectId.class.isAssignableFrom(value.getClass()) && !ObjectId.class.isAssignableFrom(targetType)) {
				decoded = context.getDocumentIdResolver().decode((ObjectId) value, targetType);
			}

			final Object v = decoded;

			// check type
			return expression.isCollectionExpression().map(c -> checkCollectionType(targetType, c.getElementType(), v))
					.orElse(checkType(targetType, v));

		} catch (InvalidExpressionException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidExpressionException(
					"Failed to decode value [" + value + "] for Property [" + expression + "]", e);
		}
	}

	/**
	 * Check specific Bson types conversion.
	 * @param value Value to check
	 * @return Checked value
	 */
	private static Object checkBsonType(Class<?> targetType, Object value) {
		if (value != null) {
			// binary
			if (Binary.class.isAssignableFrom(value.getClass()) && !Binary.class.isAssignableFrom(targetType)) {
				return ((Binary) value).getData();
			}
			// code
			if (Code.class.isAssignableFrom(value.getClass()) && String.class.isAssignableFrom(targetType)) {
				return ((Code) value).getCode();
			}
			// symbol
			if (Symbol.class.isAssignableFrom(value.getClass()) && String.class.isAssignableFrom(targetType)) {
				return ((Symbol) value).getSymbol();
			}
		}
		return value;
	}

	/**
	 * Check given value type, applying suitable conversions if required.
	 * @param targetType Expected type
	 * @param v Value to decode
	 * @return Checked value
	 * @throws InvalidExpressionException If an error occurred
	 */
	@SuppressWarnings("unchecked")
	private static Object checkType(Class<?> targetType, Object v) throws InvalidExpressionException {

		// check Bson types
		final Object value = checkBsonType(targetType, v);

		if (value != null) {

			// check array
			if (targetType.isArray() && !value.getClass().isArray()) {
				if (Collection.class.isAssignableFrom(value.getClass())) {
					if (int[].class == targetType) {
						return toIntArray((Collection<?>) value);
					}
					if (long[].class == targetType) {
						return toLongArray((Collection<?>) value);
					}
					if (double[].class == targetType) {
						return toDoubleArray((Collection<?>) value);
					}
					if (float[].class == targetType) {
						return toFloatArray((Collection<?>) value);
					}
					if (short[].class == targetType) {
						return toShortArray((Collection<?>) value);
					}
					if (boolean[].class == targetType) {
						return toBooleanArray((Collection<?>) value);
					}
					return collectionToArray(targetType, (Collection<?>) value);
				}
			}

			// enum
			if (TypeUtils.isEnum(targetType)) {
				return ConversionUtils.convertEnumValue((Class<Enum>) targetType, value);
			}

			// number
			if (TypeUtils.isNumber(targetType) && TypeUtils.isNumber(value.getClass())) {
				return ConversionUtils.convertNumberToTargetClass((Number) value, (Class<Number>) targetType);
			}

			// char[]
			if (char[].class == targetType && TypeUtils.isString(value.getClass())) {
				return ((String) value).toCharArray();
			}

		}
		return value;
	}

	/**
	 * Check collection types.
	 * @param targetType Target collection type
	 * @param targetElementType Target collection element type
	 * @param value Value to decode
	 * @return Decoded value
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static Object checkCollectionType(Class<?> targetType, Class<?> targetElementType, Object value)
			throws InvalidExpressionException {
		if (value != null) {
			if (Collection.class.isAssignableFrom(targetType) && Collection.class.isAssignableFrom(value.getClass())) {
				final Collection<?> collection = (Collection) value;
				if (collection.isEmpty()) {
					return checkType(targetType, value);
				}

				// check collection element type
				Object fe = collection.iterator().next();
				Class<?> valueElementType = (fe != null) ? fe.getClass() : null;
				if (valueElementType != null && targetElementType != null
						&& !TypeUtils.isAssignable(valueElementType, targetElementType)) {

					Collection<Object> values = Set.class.isAssignableFrom(targetType)
							? new HashSet<>(collection.size())
							: new ArrayList<>(collection.size());
					for (Object collectionValue : collection) {
						values.add(checkType(targetElementType, collectionValue));
					}
					return values;

				}

				// check collection type
				if (Set.class.isAssignableFrom(targetType) && !Set.class.isAssignableFrom(value.getClass())) {
					return new HashSet<>(collection);
				}
			}
		}
		return checkType(targetType, value);
	}

	/**
	 * Decode a {@link Collection} type value into an expected array type.
	 * @param targetType Expected type
	 * @param value Collection value
	 * @return Array of values
	 */
	private static Object collectionToArray(Class<?> targetType, Collection<?> value) {
		if (value != null) {
			final Class<?> elementType = targetType.getComponentType();
			Object array = Array.newInstance(elementType, value.size());
			int idx = 0;
			for (Object v : value) {
				Array.set(array, idx++, checkType(elementType, v));
			}
			return array;
		}
		return null;
	}

	private static int[] toIntArray(Collection<?> value) {
		if (value != null) {
			int[] a = new int[value.size()];
			int idx = 0;
			for (Object v : value) {
				a[idx++] = ConversionUtils.convertNumberToTargetClass((Number) v, int.class);
			}
			return a;
		}
		return null;
	}

	private static long[] toLongArray(Collection<?> value) {
		if (value != null) {
			long[] a = new long[value.size()];
			int idx = 0;
			for (Object v : value) {
				a[idx++] = ConversionUtils.convertNumberToTargetClass((Number) v, long.class);
			}
			return a;
		}
		return null;
	}

	private static double[] toDoubleArray(Collection<?> value) {
		if (value != null) {
			double[] a = new double[value.size()];
			int idx = 0;
			for (Object v : value) {
				a[idx++] = ConversionUtils.convertNumberToTargetClass((Number) v, double.class);
			}
			return a;
		}
		return null;
	}

	private static float[] toFloatArray(Collection<?> value) {
		if (value != null) {
			float[] a = new float[value.size()];
			int idx = 0;
			for (Object v : value) {
				a[idx++] = ConversionUtils.convertNumberToTargetClass((Number) v, float.class);
			}
			return a;
		}
		return null;
	}

	private static short[] toShortArray(Collection<?> value) {
		if (value != null) {
			short[] a = new short[value.size()];
			int idx = 0;
			for (Object v : value) {
				a[idx++] = ConversionUtils.convertNumberToTargetClass((Number) v, short.class);
			}
			return a;
		}
		return null;
	}

	private static boolean[] toBooleanArray(Collection<?> value) {
		if (value != null) {
			boolean[] a = new boolean[value.size()];
			int idx = 0;
			for (Object v : value) {
				a[idx++] = (v == null) ? Boolean.FALSE : Boolean.valueOf(v.toString());
			}
			return a;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends FieldValue> getExpressionType() {
		return FieldValue.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends Value> getResolvedType() {
		return Value.class;
	}

}
