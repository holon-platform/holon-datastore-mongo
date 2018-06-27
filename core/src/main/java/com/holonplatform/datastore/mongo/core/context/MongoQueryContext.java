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
package com.holonplatform.datastore.mongo.core.context;

import java.util.Optional;

import com.holonplatform.core.ExpressionResolver.ResolutionContext;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
import com.holonplatform.datastore.mongo.core.internal.context.DefaultMongoQueryContext;

/**
 * A {@link MongoResolutionContext} which is used to resolve a query.
 *
 * @since 5.2.0
 */
public interface MongoQueryContext extends MongoResolutionContext {

	/**
	 * Get the {@link QueryOperationType}.
	 * @return Optional query operation type
	 */
	Optional<QueryOperationType> getQueryOperationType();

	/**
	 * Set the {@link QueryOperationType}.
	 * @param queryOperationType The query operation type to set
	 */
	void setQueryOperationType(QueryOperationType queryOperationType);

	/**
	 * Get the alias associated to given expression, if any.
	 * @param expression The expression to get the alias for (not null)
	 * @return Optional expression alias
	 */
	Optional<String> getAlias(TypedExpression<?> expression);

	/**
	 * Get the alias for given expression or create one if does not exist.
	 * @param expression The expression to get the alias for (not null)
	 * @return The alias for given expression or a newly created one
	 */
	String getOrCreateAlias(TypedExpression<?> expression);

	/**
	 * Checks if given resolution context is a {@link MongoQueryContext} type.
	 * @param context The context to check
	 * @return If the given context is a {@link MongoQueryContext}, returns the context itself as a
	 *         {@link MongoQueryContext} type. Otherwise an empty Optional is returned.
	 */
	static Optional<MongoQueryContext> isQueryContext(ResolutionContext context) {
		return Optional.ofNullable((context instanceof MongoQueryContext) ? (MongoQueryContext) context : null);
	}

	/**
	 * Create a new {@link MongoQueryContext}.
	 * @param context MongoContext to use (not null)
	 * @return A new {@link MongoQueryContext}
	 */
	static MongoQueryContext create(MongoContext context) {
		return new DefaultMongoQueryContext(context);
	}

}
