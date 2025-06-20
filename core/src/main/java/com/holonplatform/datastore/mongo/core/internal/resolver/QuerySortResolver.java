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
package com.holonplatform.datastore.mongo.core.internal.resolver;

import java.util.Optional;

import jakarta.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.resolver.BsonExpressionResolver;

/**
 * {@link QuerySort} expression resolver.
 *
 * @since 5.2.0
 */
@Priority(Integer.MAX_VALUE)
public enum QuerySortResolver implements BsonExpressionResolver<QuerySort> {

	/**
	 * Singleton instance.
	 */
	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends QuerySort> getExpressionType() {
		return QuerySort.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<BsonExpression> resolve(QuerySort expression, MongoResolutionContext<?> context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// intermediate resolution
		return context.resolve(expression, QuerySort.class).flatMap(filter -> {
			// resolve as BsonExpression
			return context.resolve(filter, BsonExpression.class);
		});

	}

}
