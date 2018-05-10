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

import org.bson.types.ObjectId;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.Path;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.FieldNameExpression;
import com.holonplatform.datastore.mongo.core.expression.FieldValueExpression;
import com.holonplatform.datastore.mongo.core.expression.PathValueExpression;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * Resolver to resolve a {@link PathValueExpression} into a {@link FieldValueExpression}.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum FieldValueExpressionResolver implements MongoExpressionResolver<FieldValueExpression, PathValueExpression> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<PathValueExpression> resolve(FieldValueExpression expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// resolve path
		Path path = context.resolveOrFail(FieldNameExpression.create(expression.getFieldName()), Path.class);

		// value
		Object value = expression.getValue();

		// check document id value
		if (value != null && ObjectId.class.isAssignableFrom(value.getClass())) {
			Optional<Path<?>> idPath = MongoDocumentContext.isDocumentContext(context)
					.flatMap(ctx -> ctx.isDocumentIdPath(path));
			if (idPath.isPresent()) {
				// decode from ObjectId
				value = context.getDocumentIdResolver().decode((ObjectId) value, idPath.get().getType());
			}
		}

		return Optional.of(PathValueExpression.create(path, value));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends FieldValueExpression> getExpressionType() {
		return FieldValueExpression.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends PathValueExpression> getResolvedType() {
		return PathValueExpression.class;
	}

}
