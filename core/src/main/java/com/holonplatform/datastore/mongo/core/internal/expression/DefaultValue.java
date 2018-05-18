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
package com.holonplatform.datastore.mongo.core.internal.expression;

import java.util.Optional;

import com.holonplatform.core.TypedExpression;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.expression.Value;

/**
 * Default {@link Value} implementation.
 *
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class DefaultValue<T> implements Value<T> {

	private final T value;
	private final TypedExpression<T> expression;
	private final EnumCodecStrategy enumCodecStrategy;

	public DefaultValue(T value, TypedExpression<T> expression, EnumCodecStrategy enumCodecStrategy) {
		super();
		this.value = value;
		this.expression = expression;
		this.enumCodecStrategy = enumCodecStrategy;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.ExpressionValue#getValue()
	 */
	@Override
	public T getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.PropertyValue#getEnumCodecStrategy()
	 */
	@Override
	public Optional<EnumCodecStrategy> getEnumCodecStrategy() {
		return Optional.ofNullable(enumCodecStrategy);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.PropertyValue#getExpression()
	 */
	@Override
	public Optional<TypedExpression<T>> getExpression() {
		return Optional.ofNullable(expression);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
	}

}
