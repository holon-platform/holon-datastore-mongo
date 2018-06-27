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
package com.holonplatform.datastore.mongo.core.internal.context;

import java.util.Optional;

import com.holonplatform.core.TypedExpression;

/**
 * Provider for context expression aliases.
 *
 * @since 5.2.0
 */
public interface ExpressionAliasProvider {

	public static final String DEFAULT_PROJECTION_FIELD_PREFIX = "_!";

	/**
	 * For generated projection field names, get the next sequence number.
	 * @return The next projection field sequence number
	 */
	int getNextProjectionFieldSequence();

	/**
	 * For generated projection field names, get the next name.
	 * @return The next projection field name
	 */
	default String getNextProjectionFieldName() {
		return DEFAULT_PROJECTION_FIELD_PREFIX + getNextProjectionFieldSequence();
	}

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
	 * Get the expression bound to given alias, if any.
	 * @param <E> Expression type
	 * @param alias Expression alias
	 * @return Optional expression bound to given alias
	 */
	<E> Optional<TypedExpression<E>> getExpression(String alias);

	/**
	 * Create a new {@link ExpressionAliasProvider}.
	 * @return A new {@link ExpressionAliasProvider}
	 */
	static ExpressionAliasProvider create() {
		return new DefaultExpressionAliasProvider();
	}

}
