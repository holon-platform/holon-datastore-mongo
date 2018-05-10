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

import com.holonplatform.core.ConverterExpression;
import com.holonplatform.core.Path;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultPathValueExpression;

/**
 * Expression which represents a {@link TypedExpression} value.
 *
 * @param <T> Path type
 *
 * @since 5.2.0
 */
public interface PathValueExpression<T> extends ConverterExpression<T> {

	/**
	 * Get the path to which the value is bound.
	 * @return The value path (not null)
	 */
	Path<T> getPath();

	/**
	 * Get the expression value.
	 * @return The expression value
	 */
	T getValue();

	/**
	 * Create a new {@link PathValueExpression}.
	 * @param <T> Path type
	 * @param path The path to which the value is bound
	 * @param value The path value
	 * @return A new {@link PathValueExpression} instance
	 */
	static <T> PathValueExpression<T> create(Path<T> path, T value) {
		return new DefaultPathValueExpression<>(path, value);
	}

}
