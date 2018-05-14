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

import java.util.Optional;

import javax.annotation.Priority;

import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyValueConverter;
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
public enum FieldValueExpressionResolver implements MongoExpressionResolver<FieldValue, PathValue> {

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
				.orElse(PathValue.create(expression.getValue())));
	}

	@SuppressWarnings("unchecked")
	private static Object decode(MongoResolutionContext context, Property<?> property, Object value)
			throws InvalidExpressionException {

		// expected type
		Class<?> targetType = property.getConverter().map(c -> (Class) c.getModelType()).orElse(property.getType());

		// check type
		Object decoded = checkType(context, targetType, value);

		// check converter
		if (property.getConverter().isPresent()) {
			if (decoded == null
					|| TypeUtils.isAssignable(decoded.getClass(), property.getConverter().get().getModelType())) {
				decoded = ((PropertyValueConverter) property.getConverter().get()).fromModel(decoded, property);
			}
		}

		return decoded;
	}

	private static Object checkBsonType(Object value) throws InvalidExpressionException {
		if (value != null) {
			// binary
			if (Binary.class.isAssignableFrom(value.getClass())) {
				return ((Binary) value).getData();
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	private static Object checkType(MongoResolutionContext context, Class<?> targetType, Object v)
			throws InvalidExpressionException {

		// check Bson types
		final Object value = checkBsonType(v);

		if (value != null) {

			// check ObjectId type
			if (ObjectId.class.isAssignableFrom(value.getClass()) && !ObjectId.class.isAssignableFrom(targetType)) {
				return context.getDocumentIdResolver().decode((ObjectId) value, targetType);
			}

			// enum
			if (TypeUtils.isEnum(targetType)) {
				return ConversionUtils.convertEnumValue((Class<Enum>) targetType, value);
			}

			// number
			if (TypeUtils.isNumber(targetType) && TypeUtils.isNumber(value.getClass())) {
				return ConversionUtils.convertNumberToTargetClass((Number) value, (Class<Number>) targetType);
			}

		}
		return value;
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
