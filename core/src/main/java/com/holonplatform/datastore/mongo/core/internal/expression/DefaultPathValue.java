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

import com.holonplatform.core.property.Property;
import com.holonplatform.datastore.mongo.core.expression.PathValue;

/**
 * Default {@link PathValue} implementation.
 *
 * @param <T> Path type
 *
 * @since 5.2.0
 */
public class DefaultPathValue<T> implements PathValue<T> {

	private final T value;
	private final Property<T> property;

	/**
	 * Constructor.
	 * @param value The value
	 */
	public DefaultPathValue(T value) {
		this(value, null);
	}

	/**
	 * Constructor.
	 * @param value The value
	 * @param property The property to which the value is bound
	 */
	public DefaultPathValue(T value, Property<T> property) {
		super();
		this.value = value;
		this.property = property;
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
	 * @see com.holonplatform.datastore.mongo.core.expression.PathValueExpression#getProperty()
	 */
	@Override
	public Optional<Property<T>> getProperty() {
		return Optional.ofNullable(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
	}

}
