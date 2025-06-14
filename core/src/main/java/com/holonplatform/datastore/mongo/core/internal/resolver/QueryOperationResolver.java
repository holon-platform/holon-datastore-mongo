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

import java.util.List;
import java.util.Optional;

import jakarta.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.query.QueryOperation;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
import com.holonplatform.datastore.mongo.core.expression.BsonProjection;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * {@link QueryOperation} to {@link BsonQuery} resolver.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum QueryOperationResolver implements MongoExpressionResolver<QueryOperation, BsonQuery> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<BsonQuery> resolve(QueryOperation expression, MongoResolutionContext<?> context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// resolve projection
		BsonProjection<?> projection = context.resolveOrFail(expression.getProjection(), BsonProjection.class);

		// resolve query configuration
		final BsonQueryDefinition definition = context.resolveOrFail(expression.getConfiguration(),
				BsonQueryDefinition.class);

		// build query
		final BsonQuery.Builder builder = BsonQuery.builder(definition);

		// check projection
		if (projection.isCountAllProjection()
				&& context.getQueryOperationType().orElse(QueryOperationType.FIND) != QueryOperationType.COUNT) {
			projection = BsonProjection.builder(Long.class, true)
					.converter(DocumentConverter.value(Long.class, "count")).build();
		}

		builder.projection(projection);

		// check distinct
		if (definition.isDistinct()) {
			final List<String> filedNames = projection.getFieldNames();
			if (filedNames.size() == 1) {
				builder.distinct(filedNames.get(0));
				// set DISTINCT type
				context.setQueryOperationType(QueryOperationType.DISTINCT);
			}
		}

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
	public Class<? extends BsonQuery> getResolvedType() {
		return BsonQuery.class;
	}

}
