/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.datastore.mongo.core.internal.resolver.projection;

import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.query.ConstantExpression;
import com.holonplatform.core.query.ConstantExpressionProjection;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.expression.LiteralValue;
import com.holonplatform.datastore.mongo.core.expression.MongoProjection;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * {@link ConstantExpressionProjection} resolver.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE - 1000)
public enum ConstantExpressionProjectionResolver
		implements MongoExpressionResolver<ConstantExpressionProjection, MongoProjection> {

	/**
	 * Singleton instance
	 */
	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends ConstantExpressionProjection> getExpressionType() {
		return ConstantExpressionProjection.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends MongoProjection> getResolvedType() {
		return MongoProjection.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<MongoProjection> resolve(ConstantExpressionProjection expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		final ConstantExpression<?> ce = expression;

		// literal value
		return context
				.resolve(LiteralValue.create(ce.getModelValue(), ce.getModelType(), ce.getTemporalType().orElse(null)),
						FieldValue.class)
				.map(fv -> fv.getValue()).map(value -> {
					return MongoProjection.builder(value.getClass())
							.converter(DocumentConverter.create((Class) value.getClass(), (c, d) -> value)).build();
				});
	}

}
