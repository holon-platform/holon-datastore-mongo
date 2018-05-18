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

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.expression.LiteralValue;
import com.holonplatform.datastore.mongo.core.expression.PathValue;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * Resolver to resolve a {@link PathValue} into a {@link FieldValue}.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum PathValueFieldResolver implements MongoExpressionResolver<PathValue, FieldValue> {

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

		// Temporal type
		TemporalType temporalType = exp.getProperty().flatMap(p -> p.getConfiguration().getTemporalType()).orElse(null);

		// check type conversion
		try {
			Object encoded = exp.getProperty().filter(p -> isDocumentIdProperty(context, p))
					.map(p -> (Object) context.getDocumentIdResolver().encode(value)).orElse(
							// resolve as literal value
							context.resolveOrFail(LiteralValue.create(value, strategy, temporalType), FieldValue.class).getValue());

			return Optional.of(FieldValue.create(encoded, exp.getProperty().orElse(null)));
		} catch (InvalidExpressionException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidExpressionException("Failed to encode value [" + expression.getValue() + "]"
					+ expression.getProperty().map(p -> " for Property [" + p + "]").orElse(""), e);
		}
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
