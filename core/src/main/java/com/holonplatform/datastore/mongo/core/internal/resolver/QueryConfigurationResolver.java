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
import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * {@link QueryConfiguration} to {@link MongoQueryDefinition} resolver.
 *
 * @since 5.2.0
 */
@Priority(Integer.MAX_VALUE)
public enum QueryConfigurationResolver implements MongoExpressionResolver<QueryConfiguration, MongoQueryDefinition> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<MongoQueryDefinition> resolve(QueryConfiguration expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		final MongoQueryDefinition.Builder builder = MongoQueryDefinition.builder();

		// filter
		expression.getFilter().ifPresent(f -> {
			builder.filter(context.resolveOrFail(f, BsonExpression.class).getValue());
		});
		// sort
		expression.getSort().ifPresent(s -> {
			builder.sort(context.resolveOrFail(s, BsonExpression.class).getValue());
		});

		// TODO aggregation

		// parameters
		// TODO

		// resolved
		return Optional.of(builder.build());

	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends QueryConfiguration> getExpressionType() {
		return QueryConfiguration.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends MongoQueryDefinition> getResolvedType() {
		return MongoQueryDefinition.class;
	}

}
