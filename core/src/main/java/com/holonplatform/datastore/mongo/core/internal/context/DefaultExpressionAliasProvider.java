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

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.internal.utils.ObjectUtils;

/**
 * Default {@link ExpressionAliasProvider} implementation.
 *
 * @since 5.2.0
 */
public class DefaultExpressionAliasProvider implements ExpressionAliasProvider {

	/**
	 * Projection sequence
	 */
	private final AtomicInteger projectionSequence = new AtomicInteger(0);

	/**
	 * Expression aliases
	 */
	private final Map<TypedExpression<?>, String> expressionAlias = new ConcurrentHashMap<>();

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.context.ExpressionAliasProvider#getNextProjectionFieldSequence()
	 */
	@Override
	public int getNextProjectionFieldSequence() {
		return projectionSequence.incrementAndGet();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.context.ExpressionAliasProvider#getAlias(com.holonplatform.core.
	 * TypedExpression)
	 */
	@Override
	public Optional<String> getAlias(TypedExpression<?> expression) {
		if (expression != null) {
			return Optional.ofNullable(expressionAlias.get(expression));
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.context.ExpressionAliasProvider#getOrCreateAlias(com.
	 * holonplatform.core.TypedExpression)
	 */
	@Override
	public String getOrCreateAlias(TypedExpression<?> expression) {
		ObjectUtils.argumentNotNull(expression, "Expression must be not null");
		return expressionAlias.computeIfAbsent(expression, e -> getNextProjectionFieldName());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.context.ExpressionAliasProvider#getExpression(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <E> Optional<TypedExpression<E>> getExpression(String alias) {
		if (alias != null) {
			return expressionAlias.entrySet().stream().filter(e -> alias.equals(e.getValue()))
					.map(e -> (TypedExpression<E>) e.getKey()).findFirst();
		}
		return Optional.empty();
	}

}
