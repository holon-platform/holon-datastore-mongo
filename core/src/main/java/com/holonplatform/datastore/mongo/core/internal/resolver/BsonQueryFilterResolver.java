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

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonFilter;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * {@link QueryFilter} to {@link BsonFilter} expression resolver.
 *
 * @since 5.2.0
 */
@Priority(Integer.MAX_VALUE)
public enum BsonQueryFilterResolver implements MongoExpressionResolver<QueryFilter, BsonFilter> {

	/**
	 * Singleton instance.
	 */
	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends QueryFilter> getExpressionType() {
		return QueryFilter.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends BsonFilter> getResolvedType() {
		return BsonFilter.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<BsonFilter> resolve(QueryFilter expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// intermediate resolution
		return context.resolve(expression, QueryFilter.class).flatMap(filter -> {
			// resolve as BsonFilter
			return context.resolve(filter, BsonFilter.class);
		});

	}

}
