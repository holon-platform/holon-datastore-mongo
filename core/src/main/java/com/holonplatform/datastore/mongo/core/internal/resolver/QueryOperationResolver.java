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
import com.holonplatform.core.query.QueryOperation;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.MongoProjection;
import com.holonplatform.datastore.mongo.core.expression.MongoQuery;
import com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;
import com.mongodb.client.model.Projections;

/**
 * {@link QueryOperation} to {@link MongoQuery} resolver.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum QueryOperationResolver implements MongoExpressionResolver<QueryOperation, MongoQuery> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<MongoQuery> resolve(QueryOperation expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// resolve query configuration
		final MongoQueryDefinition definition = context.resolveOrFail(expression.getConfiguration(),
				MongoQueryDefinition.class);

		// resolve projection
		final MongoProjection<?> projection = context.resolveOrFail(expression.getProjection(), MongoProjection.class);

		// build query
		final MongoQuery.Builder builder = MongoQuery.builder(definition);

		projection.getOperationType().ifPresent(ot -> builder.operationType(ot));

		if (projection.getFields() != null && !projection.getFields().isEmpty()) {
			builder.projection(Projections.include(projection.getFields()));
		}

		projection.getConverter().ifPresent(c -> builder.converter(c));

		return Optional.of(builder.build());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends QueryOperation> getExpressionType() {
		return QueryOperation.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends MongoQuery> getResolvedType() {
		return MongoQuery.class;
	}

}
