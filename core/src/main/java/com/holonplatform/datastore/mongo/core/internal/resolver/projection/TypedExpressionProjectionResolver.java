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
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.property.PathPropertySetAdapter;
import com.holonplatform.core.property.Property;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.expression.BsonProjection;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * Generic {@link TypedExpression} projection resolver.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE - 10)
public enum TypedExpressionProjectionResolver implements MongoExpressionResolver<TypedExpression, BsonProjection> {

	/**
	 * Singleton instance
	 */
	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends TypedExpression> getExpressionType() {
		return TypedExpression.class;
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
	@Override
	public Optional<BsonProjection> resolve(TypedExpression expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// check document context
		Optional<BsonProjection> projection = MongoDocumentContext.isDocumentContext(context)
				.flatMap(documentContext -> resolveDocumentExpression(documentContext, expression));
		if (projection.isPresent()) {
			return projection;
		}

		return resolveExpression(context, expression);
	}

	@SuppressWarnings("unchecked")
	private static Optional<BsonProjection> resolveDocumentExpression(MongoDocumentContext context,
			TypedExpression<?> expression) {

		// check property
		return context.getPropertySet().stream().filter(p -> p.equals(expression)).map(p -> (Property<?>) p).findFirst()
				.flatMap(property -> {
					// check path
					return PathPropertySetAdapter.create(context.getPropertySet()).getPath(property).map(path -> {
						// resolve field name
						final String fieldName = context.resolveOrFail(path, FieldName.class).getFieldName();
						// build projection
						return BsonProjection.builder(property.getType()).field(fieldName)
								.converter((DocumentConverter) DocumentConverter.expression(property, fieldName))
								.build();
					});
				});
	}

	@SuppressWarnings("unchecked")
	private static Optional<BsonProjection> resolveExpression(MongoResolutionContext context,
			TypedExpression<?> expression) {
		return context.resolve(expression, FieldName.class)
				.map(fn -> BsonProjection.builder(expression.getType()).field(fn.getFieldName())
						.converter((DocumentConverter) DocumentConverter.expression(expression, fn.getFieldName()))
						.build());
	}

}
