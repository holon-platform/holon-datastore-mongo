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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Priority;

import org.bson.Document;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.query.QueryFunction;
import com.holonplatform.core.query.QueryFunction.Avg;
import com.holonplatform.core.query.QueryFunction.Count;
import com.holonplatform.core.query.QueryFunction.Max;
import com.holonplatform.core.query.QueryFunction.Min;
import com.holonplatform.core.query.QueryFunction.Sum;
import com.holonplatform.core.query.StringFunction.Lower;
import com.holonplatform.core.query.StringFunction.Upper;
import com.holonplatform.core.query.TemporalFunction.Day;
import com.holonplatform.core.query.TemporalFunction.Hour;
import com.holonplatform.core.query.TemporalFunction.Month;
import com.holonplatform.core.query.TemporalFunction.Year;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.resolver.BsonExpressionResolver;

/**
 * {@link QueryFunction} expression resolver.
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum QueryFunctionResolver implements BsonExpressionResolver<QueryFunction> {

	/**
	 * Singleton instance.
	 */
	INSTANCE;

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
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<BsonExpression> resolve(QueryFunction expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// intermediate resolution
		final QueryFunction function = context.resolve(expression, QueryFunction.class).orElse(expression);

		// resolve as BsonExpression
		return getFunctionName(function).map(name -> {
			// Count as sum of document instances
			if (Count.class.isAssignableFrom(function.getClass())) {
				return BsonExpression.create(new Document(name, 1));
			}
			// resolve arguments as field names
			final List<String> arguments = new LinkedList<>();
			if (function.getExpressionArguments() != null) {
				for (TypedExpression<?> argument : ((QueryFunction<?, ?>) function).getExpressionArguments()) {
					// resolve argument
					arguments.add(context.resolveOrFail(argument, FieldName.class).getFieldName());
				}
			}
			// add $ prefix
			List<String> names = arguments.stream().map(a -> "$" + a).collect(Collectors.toList());
			if (names.size() == 1) {
				return BsonExpression.create(new Document(name, names.get(0)));
			}
			return BsonExpression.create(new Document(name, names));
		});
	}

	private static Optional<String> getFunctionName(QueryFunction expression) {

		final Class<? extends QueryFunction> functionType = expression.getClass();

		// aggregate
		if (Count.class.isAssignableFrom(functionType))
			return Optional.of("$sum");
		if (Avg.class.isAssignableFrom(functionType))
			return Optional.of("$avg");
		if (Min.class.isAssignableFrom(functionType))
			return Optional.of("$min");
		if (Max.class.isAssignableFrom(functionType))
			return Optional.of("$max");
		if (Sum.class.isAssignableFrom(functionType))
			return Optional.of("$sum");

		// string
		if (Lower.class.isAssignableFrom(functionType))
			return Optional.of("$toLower");
		if (Upper.class.isAssignableFrom(functionType))
			return Optional.of("$toUpper");

		// temporal
		if (Year.class.isAssignableFrom(functionType))
			return Optional.of("$year");
		if (Month.class.isAssignableFrom(functionType))
			return Optional.of("$month");
		if (Day.class.isAssignableFrom(functionType))
			return Optional.of("$dayOfMonth");
		if (Hour.class.isAssignableFrom(functionType))
			return Optional.of("$hour");

		return Optional.empty();
	}

}
