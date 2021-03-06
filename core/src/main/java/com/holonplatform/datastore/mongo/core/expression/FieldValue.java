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
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultFieldValue;

/**
 * Expression which represents a MongoDB document field value.
 *
 * @since 5.2.0
 */
public interface FieldValue extends Expression {

	/**
	 * Get the field value.
	 * @return The field value (may be null)
	 */
	Object getValue();

	/**
	 * Get the {@link EnumCodecStrategy} to use to encode/decode {@link Enum} type values.
	 * @return The enum codec strategy, if specified
	 */
	Optional<EnumCodecStrategy> getEnumCodecStrategy();

	/**
	 * Get the {@link TypedExpression} to which the field value is bound, if any.
	 * @return Optional expression to which the field value is bound
	 */
	Optional<TypedExpression<?>> getExpression();

	/**
	 * Create a new {@link FieldValue}.
	 * @param value The field value
	 * @return A new {@link FieldValue} instance
	 */
	static FieldValue create(Object value) {
		return new DefaultFieldValue(value, null, null);
	}

	/**
	 * Create a new {@link FieldValue}.
	 * @param value The field value
	 * @param expression The expression to which the value is bound
	 * @return A new {@link FieldValue} instance
	 */
	static FieldValue create(Object value, TypedExpression<?> expression) {
		return new DefaultFieldValue(value, expression, null);
	}

	/**
	 * Create a new {@link FieldValue}.
	 * @param value The field value
	 * @param enumCodecStrategy The {@link EnumCodecStrategy} to use
	 * @return A new {@link FieldValue} instance
	 */
	static FieldValue create(Object value, EnumCodecStrategy enumCodecStrategy) {
		return new DefaultFieldValue(value, null, enumCodecStrategy);
	}

	/**
	 * Create a new {@link FieldValue}.
	 * @param value The field value
	 * @param expression The expression to which the value is bound
	 * @param enumCodecStrategy The {@link EnumCodecStrategy} to use
	 * @return A new {@link FieldValue} instance
	 */
	static FieldValue create(Object value, TypedExpression<?> expression, EnumCodecStrategy enumCodecStrategy) {
		return new DefaultFieldValue(value, expression, enumCodecStrategy);
	}

}
