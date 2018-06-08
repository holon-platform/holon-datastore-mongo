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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Priority;

import org.bson.Document;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.Path;
import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.datastore.mongo.core.ReadOperationConfiguration;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;
import com.mongodb.client.model.Filters;

/**
 * {@link QueryConfiguration} to {@link BsonQueryDefinition} resolver.
 *
 * @since 5.2.0
 */
@Priority(Integer.MAX_VALUE)
public enum QueryConfigurationResolver implements MongoExpressionResolver<QueryConfiguration, BsonQueryDefinition> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<BsonQueryDefinition> resolve(QueryConfiguration expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		final BsonQueryDefinition.Builder builder = BsonQueryDefinition.builder();

		// target
		expression.getTarget().ifPresent(t -> {
			builder.collectionName(context.resolveOrFail(t, CollectionName.class).getName());
		});

		// filters
		final List<QueryFilter> filters = new LinkedList<>();

		expression.getFilter().ifPresent(f -> {
			filters.add(f);
		});
		expression.getAggregation().flatMap(a -> a.getAggregationFilter()).ifPresent(f -> {
			filters.add(f);
		});

		if (!filters.isEmpty()) {
			if (filters.size() == 1) {
				builder.filter(context.resolveOrFail(filters.get(0), BsonExpression.class).getValue());
			} else {
				builder.filter(
						Filters.and(filters.stream().map(f -> context.resolveOrFail(f, BsonExpression.class).getValue())
								.collect(Collectors.toList())));
			}
		}

		// sort
		expression.getSort().ifPresent(s -> {
			builder.sort(context.resolveOrFail(s, BsonExpression.class).getValue());
		});

		// group by
		expression.getAggregation().map(a -> a.getAggregationPaths()).filter(p -> (p != null && p.length > 0))
				.ifPresent(paths -> {
					if (paths.length == 1) {
						final String fieldName = context.resolveOrFail(paths[0], FieldName.class).getFieldName();
						builder.group(new Document(fieldName, "$" + fieldName));
					} else {
						final Document groups = new Document();
						for (Path<?> path : paths) {
							final String fieldName = context.resolveOrFail(path, FieldName.class).getFieldName();
							groups.append(fieldName, "$" + fieldName);
						}
						// TODO multiple fields group id
						builder.group(new Document("_id", groups));
					}
				});

		// limit and offset
		expression.getLimit().ifPresent(l -> builder.limit(l));
		expression.getOffset().ifPresent(o -> builder.offset(o));

		// parameters
		expression.getParameter(ReadOperationConfiguration.QUERY_TIMEOUT).ifPresent(p -> {
			builder.timeout(p,
					expression.getParameter(ReadOperationConfiguration.QUERY_TIMEOUT_UNIT, TimeUnit.MILLISECONDS));
		});
		expression.getParameter(ReadOperationConfiguration.QUERY_CURSOR_TYPE).ifPresent(p -> {
			builder.cursorType(p);
		});
		expression.getParameter(ReadOperationConfiguration.QUERY_BATCH_SIZE).ifPresent(p -> {
			builder.batchSize(p);
		});
		expression.getParameter(ReadOperationConfiguration.QUERY_COLLATION).ifPresent(p -> {
			builder.collation(p);
		});
		expression.getParameter(ReadOperationConfiguration.QUERY_COMMENT).ifPresent(p -> {
			builder.comment(p);
		});
		expression.getParameter(ReadOperationConfiguration.QUERY_HINT).ifPresent(p -> {
			builder.hint(p);
		});
		expression.getParameter(ReadOperationConfiguration.QUERY_MAX).ifPresent(p -> {
			builder.max(p);
		});
		expression.getParameter(ReadOperationConfiguration.QUERY_MIN).ifPresent(p -> {
			builder.min(p);
		});
		expression.getParameter(ReadOperationConfiguration.QUERY_MAX_SCAN).ifPresent(p -> {
			builder.maxScan(p);
		});
		expression.getParameter(ReadOperationConfiguration.QUERY_PARTIAL).ifPresent(p -> {
			builder.partial(p);
		});
		expression.getParameter(ReadOperationConfiguration.QUERY_RETURN_KEY).ifPresent(p -> {
			builder.returnKey(p);
		});
		expression.getParameter(ReadOperationConfiguration.QUERY_SHOW_RECORD_ID).ifPresent(p -> {
			builder.showRecordId(p);
		});
		expression.getParameter(ReadOperationConfiguration.QUERY_SNAPSHOT).ifPresent(p -> {
			builder.snapshot(p);
		});

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
	public Class<? extends BsonQueryDefinition> getResolvedType() {
		return BsonQueryDefinition.class;
	}

}
