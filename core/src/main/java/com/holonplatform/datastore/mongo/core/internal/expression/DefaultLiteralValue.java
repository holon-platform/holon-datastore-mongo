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

import com.holonplatform.core.temporal.TemporalType;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.expression.LiteralValue;

/**
 * Default {@link LiteralValue} implementation.
 *
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class DefaultLiteralValue<T> implements LiteralValue<T> {

	private final T value;
	private final Class<? extends T> type;
	private final EnumCodecStrategy enumCodecStrategy;
	private final TemporalType temporalType;

	public DefaultLiteralValue(T value) {
		this(value, (EnumCodecStrategy) null);
	}

	public DefaultLiteralValue(T value, TemporalType temporalType) {
		this(value, (EnumCodecStrategy) null, temporalType);
	}

	public DefaultLiteralValue(T value, EnumCodecStrategy enumCodecStrategy) {
		this(value, enumCodecStrategy, null);
	}

	@SuppressWarnings("unchecked")
	public DefaultLiteralValue(T value, EnumCodecStrategy enumCodecStrategy, TemporalType temporalType) {
		this(value,
				(value != null) ? (Class<? extends T>) value.getClass() : (Class<? extends T>) (Class<?>) Void.class,
				enumCodecStrategy, temporalType);
	}

	public DefaultLiteralValue(T value, Class<? extends T> type) {
		this(value, type, null, null);
	}

	public DefaultLiteralValue(T value, Class<? extends T> type, TemporalType temporalType) {
		this(value, type, null, temporalType);
	}

	public DefaultLiteralValue(T value, Class<? extends T> type, EnumCodecStrategy enumCodecStrategy) {
		this(value, type, enumCodecStrategy, null);
	}

	public DefaultLiteralValue(T value, Class<? extends T> type, EnumCodecStrategy enumCodecStrategy,
			TemporalType temporalType) {
		super();
		this.value = value;
		this.type = type;
		this.enumCodecStrategy = enumCodecStrategy;
		this.temporalType = temporalType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.TypedExpression#getTemporalType()
	 */
	@Override
	public Optional<TemporalType> getTemporalType() {
		if (temporalType != null) {
			return Optional.of(temporalType);
		}
		return LiteralValue.super.getTemporalType();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.LiteralValue#getValue()
	 */
	@Override
	public T getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.TypedExpression#getType()
	 */
	@Override
	public Class<? extends T> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.LiteralValue#getEnumCodecStrategy()
	 */
	@Override
	public Optional<EnumCodecStrategy> getEnumCodecStrategy() {
		return Optional.ofNullable(enumCodecStrategy);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getType() == null) {
			throw new InvalidExpressionException("Null value type");
		}
	}

}
