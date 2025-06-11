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
package com.holonplatform.datastore.mongo.core.internal.resolver.projection;

import java.util.List;
import java.util.Optional;

import jakarta.annotation.Priority;

import org.bson.conversions.Bson;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.query.QueryFunction;
import com.holonplatform.core.query.QueryFunction.Avg;
import com.holonplatform.core.query.QueryFunction.Count;
import com.holonplatform.core.query.QueryFunction.Max;
import com.holonplatform.core.query.QueryFunction.Min;
import com.holonplatform.core.query.QueryFunction.Sum;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.BsonProjection;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * Aggregation {@link QueryFunction} projection resolver.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE - 100)
public enum AggregationFunctionProjectionResolver implements MongoExpressionResolver<QueryFunction, BsonProjection> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<BsonProjection> resolve(QueryFunction expression, MongoResolutionContext<?> context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		final Class<? extends QueryFunction> functionType = expression.getClass();

		// check Count
		if (Count.class.isAssignableFrom(functionType)) {
			// get field
			String fieldName = null;
			List<TypedExpression> args = expression.getExpressionArguments();
			if (args != null && args.size() == 1) {
				TypedExpression arg = args.get(0);
				fieldName = context.resolve(arg, FieldName.class).map(f -> f.getFieldName()).orElse(null);
			}
			if (fieldName == null) {
				fieldName = context.getOrCreateAlias(expression);
			}
			context.setQueryOperationType(QueryOperationType.AGGREGATE);
			// projection
			return Optional.of(BsonProjection.builder(expression.getType()).countByField(fieldName)
					.converter(DocumentConverter.expression(expression, "count")).build());
		}

		// check Min, Max, Avg, Sum
		if (Avg.class.isAssignableFrom(functionType) || Min.class.isAssignableFrom(functionType)
				|| Max.class.isAssignableFrom(functionType) || Sum.class.isAssignableFrom(functionType)) {

			// resolve field name
			final String fieldName = context.resolve(expression, FieldName.class).map(fn -> fn.getFieldName())
					.orElse(context.getOrCreateAlias(expression));
			// resolve expression
			final Bson bson = context.resolveOrFail(expression, BsonExpression.class).getValue();

			// projection
			return Optional.of(
					BsonProjection.builder(expression.getType()).field(fieldName, bson).hasAggregationFunctions(true)
							.converter(DocumentConverter.expression(expression, fieldName)).build());
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

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends BsonProjection> getResolvedType() {
		return BsonProjection.class;
	}

}
