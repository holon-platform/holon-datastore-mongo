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

import java.util.Optional;

import javax.annotation.Priority;

import org.bson.Document;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.query.QueryFunction;
import com.holonplatform.core.query.StringFunction.Lower;
import com.holonplatform.core.query.StringFunction.Upper;
import com.holonplatform.core.query.TemporalFunction.Day;
import com.holonplatform.core.query.TemporalFunction.Hour;
import com.holonplatform.core.query.TemporalFunction.Month;
import com.holonplatform.core.query.TemporalFunction.Year;
import com.holonplatform.datastore.mongo.core.context.MongoQueryContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.BsonProjection;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * A {@link QueryFunction} to {@link BsonExpression} resolver for temporal type functions.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE - 1000)
public enum QueryFunctionProjectionResolver implements MongoExpressionResolver<QueryFunction, BsonProjection> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<BsonProjection> resolve(QueryFunction expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		final Class<? extends QueryFunction> functionType = expression.getClass();

		// String
		if (Lower.class.isAssignableFrom(functionType)) {
			return Optional.of(resolveFunction(context, expression, "$toLower",
					((QueryFunction<?, ?>) expression).getExpressionArguments().get(0)));
		}
		if (Upper.class.isAssignableFrom(functionType)) {
			return Optional.of(resolveFunction(context, expression, "$toUpper",
					((QueryFunction<?, ?>) expression).getExpressionArguments().get(0)));
		}

		// Temporal

		if (Year.class.isAssignableFrom(functionType)) {
			return Optional.of(resolveFunction(context, expression, "$year",
					((QueryFunction<?, ?>) expression).getExpressionArguments().get(0)));
		}
		if (Month.class.isAssignableFrom(functionType)) {
			return Optional.of(resolveFunction(context, expression, "$month",
					((QueryFunction<?, ?>) expression).getExpressionArguments().get(0)));
		}

		if (Day.class.isAssignableFrom(functionType)) {
			return Optional.of(resolveFunction(context, expression, "$dayOfMonth",
					((QueryFunction<?, ?>) expression).getExpressionArguments().get(0)));
		}

		if (Hour.class.isAssignableFrom(functionType)) {
			return Optional.of(resolveFunction(context, expression, "$hour",
					((QueryFunction<?, ?>) expression).getExpressionArguments().get(0)));
		}

		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	private static BsonProjection resolveFunction(MongoResolutionContext context, QueryFunction expression,
			String function, TypedExpression<?> argument) throws InvalidExpressionException {

		final String fieldName = context.resolveOrFail(argument, FieldName.class).getFieldName();

		final Document projection = new Document(fieldName, new Document(function, "$" + fieldName));

		// set AGGREGATE type
		MongoQueryContext.isQueryContext(context)
				.ifPresent(qc -> qc.setQueryOperationType(QueryOperationType.AGGREGATE));

		return BsonProjection.builder(expression.getType()).field(fieldName, projection)
				.converter(DocumentConverter.expression(expression, fieldName)).build();
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
