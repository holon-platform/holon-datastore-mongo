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
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Priority;

import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyValueConverter;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
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
public enum FieldValuePathResolver implements MongoExpressionResolver<FieldValue, PathValue> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<PathValue> resolve(FieldValue expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// check property
		return Optional.of(expression.getProperty()
				.map(p -> PathValue.create(decode(context, p, expression.getValue()), (Property<Object>) p))
				.orElse(PathValue.create(checkBsonType(expression.getValue()))));
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
	 * Decode the value bound to the specified property.
	 * @param context Resolution context
	 * @param property Property
	 * @param value Value
	 * @return Decoded value
	 * @throws InvalidExpressionException If an error occurred
	 */
	@SuppressWarnings("unchecked")
	private static Object decode(MongoResolutionContext context, Property<?> property, Object value)
			throws InvalidExpressionException {

		try {
			// expected type
			Class<?> targetType = property.getConverter().map(c -> (Class) c.getModelType()).orElse(property.getType());

			// check type
			Object decoded = (isDocumentIdProperty(context, property)
					&& ObjectId.class.isAssignableFrom(value.getClass()))
							? context.getDocumentIdResolver().decode((ObjectId) value, targetType)
							: checkType(targetType, value);

			// check converter
			if (property.getConverter().isPresent()) {
				if (decoded == null
						|| TypeUtils.isAssignable(decoded.getClass(), property.getConverter().get().getModelType())) {
					decoded = ((PropertyValueConverter) property.getConverter().get()).fromModel(decoded, property);
				}
			}

			return decoded;
		} catch (InvalidExpressionException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidExpressionException(
					"Failed to decode value [" + value + "] for Property [" + property + "]", e);
		}
	}

	/**
	 * Check specific Bson types conversion.
	 * @param value Value to check
	 * @return Checked value
	 */
	private static Object checkBsonType(Object value) {
		if (value != null) {
			// binary
			if (Binary.class.isAssignableFrom(value.getClass())) {
				return ((Binary) value).getData();
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
		final Object value = checkBsonType(v);

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
	public Class<? extends PathValue> getResolvedType() {
		return PathValue.class;
	}

}
