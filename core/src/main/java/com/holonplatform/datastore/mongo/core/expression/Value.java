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
package com.holonplatform.datastore.mongo.core.expression;

import java.util.Optional;

import com.holonplatform.core.Expression;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.property.Property;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultValue;

/**
 * Expression which represents a value which can be bound to a {@link Property}.
 *
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public interface Value<T> extends Expression {

	/**
	 * Get the expression value.
	 * @return The expression value
	 */
	T getValue();

	/**
	 * Get the {@link EnumCodecStrategy} to use to encode/decode {@link Enum} type values.
	 * @return The enum codec strategy, if specified
	 */
	Optional<EnumCodecStrategy> getEnumCodecStrategy();

	/**
	 * Get the {@link TypedExpression} to which the value is bound, if any.
	 * @return Optional expression to which the value is bound
	 */
	Optional<TypedExpression<T>> getExpression();

	/**
	 * Create a new {@link Value}.
	 * @param <T> Value type
	 * @param value The path value
	 * @return A new {@link Value} instance
	 */
	static <T> Value<T> create(T value) {
		return new DefaultValue<>(value, null, null);
	}

	/**
	 * Create a new {@link Value}.
	 * @param <T> Value type
	 * @param value The path value
	 * @param expression The expression to which the value is bound
	 * @return A new {@link Value} instance
	 */
	static <T> Value<T> create(T value, TypedExpression<T> expression) {
		return new DefaultValue<>(value, expression, null);
	}

	/**
	 * Create a new {@link Value}.
	 * @param <T> Value type
	 * @param value The path value
	 * @param enumCodecStrategy The {@link EnumCodecStrategy} to use
	 * @return A new {@link Value} instance
	 */
	static <T> Value<T> create(T value, EnumCodecStrategy enumCodecStrategy) {
		return new DefaultValue<>(value, null, enumCodecStrategy);
	}

	/**
	 * Create a new {@link Value}.
	 * @param <T> Value type
	 * @param value The path value
	 * @param expression The expression to which the value is bound
	 * @param enumCodecStrategy The {@link EnumCodecStrategy} to use
	 * @return A new {@link Value} instance
	 */
	static <T> Value<T> create(T value, TypedExpression<T> expression, EnumCodecStrategy enumCodecStrategy) {
		return new DefaultValue<>(value, expression, enumCodecStrategy);
	}

}
