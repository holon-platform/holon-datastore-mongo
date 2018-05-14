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
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultPathValue;

/**
 * Expression which represents a {@link TypedExpression} value.
 *
 * @param <T> Path type
 *
 * @since 5.2.0
 */
public interface PathValue<T> extends Expression {

	/**
	 * Get the expression value.
	 * @return The expression value
	 */
	T getValue();

	/**
	 * Get the {@link Property} to which the value is bound, if any.
	 * @return Optional property to which the value is bound
	 */
	Optional<Property<T>> getProperty();
	
	/**
	 * Create a new {@link PathValue}.
	 * @param <T> Value type
	 * @param value The path value
	 * @return A new {@link PathValue} instance
	 */
	static <T> PathValue<T> create(T value) {
		return new DefaultPathValue<>(value);
	}

	/**
	 * Create a new {@link PathValue}.
	 * @param <T> Value type
	 * @param value The path value
	 * @param property The property to which the value is bound
	 * @return A new {@link PathValue} instance
	 */
	static <T> PathValue<T> create(T value, Property<T> property) {
		return new DefaultPathValue<>(value, property);
	}

}
