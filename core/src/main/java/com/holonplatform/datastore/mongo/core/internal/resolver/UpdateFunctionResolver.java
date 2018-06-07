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
import com.holonplatform.core.query.QueryFunction;
import com.holonplatform.core.query.TemporalFunction.CurrentDate;
import com.holonplatform.core.query.TemporalFunction.CurrentLocalDate;
import com.holonplatform.core.query.TemporalFunction.CurrentLocalDateTime;
import com.holonplatform.core.query.TemporalFunction.CurrentTimestamp;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.resolver.BsonExpressionResolver;
import com.mongodb.client.model.Updates;

/**
 * A {@link QueryFunction} to {@link BsonExpression} resolver for update type operations.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE - 1000)
public enum UpdateFunctionResolver implements BsonExpressionResolver<QueryFunction> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<BsonExpression> resolve(QueryFunction expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		if (context.isForUpdate()) {
			final Class<? extends QueryFunction> functionType = expression.getClass();

			if (CurrentDate.class.isAssignableFrom(functionType)
					|| CurrentLocalDate.class.isAssignableFrom(functionType)) {
				return context.getUpdatePath().map(path -> {
					// resolve field name
					final String fieldName = context.resolveOrFail(path, FieldName.class).getFieldName();
					return BsonExpression.create(Updates.currentDate(fieldName));
				});
			} else if (CurrentTimestamp.class.isAssignableFrom(functionType)
					|| CurrentLocalDateTime.class.isAssignableFrom(functionType)) {
				return context.getUpdatePath().map(path -> {
					// resolve field name
					final String fieldName = context.resolveOrFail(path, FieldName.class).getFieldName();
					return BsonExpression.create(Updates.currentTimestamp(fieldName));
				});
			}
		}

		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends QueryFunction> getExpressionType() {
		return QueryFunction.class;
	}

}
