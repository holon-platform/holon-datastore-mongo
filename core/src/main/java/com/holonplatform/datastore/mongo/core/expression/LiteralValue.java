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

import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultLiteralValue;

/**
 * Expression which represents a literal value.
 *
 * @param <T> Value type
 * 
 * @since 5.2.0
 */
public interface LiteralValue<T> extends TypedExpression<T> {

	/**
	 * Get the value.
	 * @return the value (may be null)
	 */
	T getValue();

	/**
	 * Get the {@link EnumCodecStrategy} to use to encode/decode {@link Enum} type values.
	 * @return The enum codec strategy, if specified
	 */
	Optional<EnumCodecStrategy> getEnumCodecStrategy();

	/**
	 * Create a new {@link LiteralValue}.
	 * @param value The value
	 * @return A new {@link LiteralValue} instance
	 */
	static <T> LiteralValue<T> create(T value) {
		return new DefaultLiteralValue<>(value);
	}

	/**
	 * Create a new {@link LiteralValue}.
	 * @param value The value
	 * @param temporalType Value temporal type
	 * @return A new {@link LiteralValue} instance
	 */
	static <T> LiteralValue<T> create(T value, TemporalType temporalType) {
		return new DefaultLiteralValue<>(value, temporalType);
	}

	/**
	 * Create a new {@link LiteralValue}.
	 * @param value The value
	 * @param enumCodecStrategy Enum codec strategy
	 * @return A new {@link LiteralValue} instance
	 */
	static <T> LiteralValue<T> create(T value, EnumCodecStrategy enumCodecStrategy) {
		return new DefaultLiteralValue<>(value, enumCodecStrategy);
	}

	/**
	 * Create a new {@link LiteralValue}.
	 * @param value The value
	 * @param enumCodecStrategy Enum codec strategy
	 * @param temporalType Value temporal type
	 * @return A new {@link LiteralValue} instance
	 */
	static <T> LiteralValue<T> create(T value, EnumCodecStrategy enumCodecStrategy, TemporalType temporalType) {
		return new DefaultLiteralValue<>(value, enumCodecStrategy, temporalType);
	}

	/**
	 * Create a new {@link LiteralValue}.
	 * @param value The value
	 * @param type Value type
	 * @return A new {@link LiteralValue} instance
	 */
	static <T> LiteralValue<T> create(T value, Class<? extends T> type) {
		return new DefaultLiteralValue<>(value, type);
	}

	/**
	 * Create a new {@link LiteralValue}.
	 * @param value The value
	 * @param type Value type
	 * @param temporalType Value temporal type
	 * @return A new {@link LiteralValue} instance
	 */
	static <T> LiteralValue<T> create(T value, Class<? extends T> type, TemporalType temporalType) {
		return new DefaultLiteralValue<>(value, type, temporalType);
	}

	/**
	 * Create a new {@link LiteralValue}.
	 * @param value The value
	 * @param type Value type
	 * @param enumCodecStrategy Enum codec strategy
	 * @return A new {@link LiteralValue} instance
	 */
	static <T> LiteralValue<T> create(T value, Class<? extends T> type, EnumCodecStrategy enumCodecStrategy) {
		return new DefaultLiteralValue<>(value, type, enumCodecStrategy);
	}

	/**
	 * Create a new {@link LiteralValue}.
	 * @param value The value
	 * @param type Value type
	 * @param enumCodecStrategy Enum codec strategy
	 * @param temporalType Value temporal type
	 * @return A new {@link LiteralValue} instance
	 */
	static <T> LiteralValue<T> create(T value, Class<? extends T> type, EnumCodecStrategy enumCodecStrategy,
			TemporalType temporalType) {
		return new DefaultLiteralValue<>(value, type, enumCodecStrategy, temporalType);
	}

}
