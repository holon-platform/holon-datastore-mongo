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

import org.bson.Document;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.query.ConstantExpression;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
import com.holonplatform.datastore.mongo.core.expression.BsonProjection;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * {@link ConstantExpression} projection resolver.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE - 500)
public enum ConstantExpressionProjectionResolver
		implements MongoExpressionResolver<ConstantExpression, BsonProjection> {

	/**
	 * Singleton instance
	 */
	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends ConstantExpression> getExpressionType() {
		return ConstantExpression.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends BsonProjection> getResolvedType() {
		return BsonProjection.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<BsonProjection> resolve(ConstantExpression expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		final ConstantExpression<?> ce = expression;

		// literal value
		return context.resolve(ce, FieldValue.class).map(fv -> fv.getValue()).map(value -> {
			// projection field name
			final String name = context.getNextProjectionFieldName();
			// set AGGREGATE type
			context.setQueryOperationType(QueryOperationType.AGGREGATE);
			// literal projection
			return BsonProjection.builder(value.getClass())
					.field(name, new Document(name, new Document("$literal", value)))
					.converter(DocumentConverter.create((Class) value.getClass(), (c, d) -> value)).build();
		});
	}

}
