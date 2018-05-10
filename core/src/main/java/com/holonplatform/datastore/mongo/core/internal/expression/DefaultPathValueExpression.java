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

import com.holonplatform.core.ExpressionValueConverter;
import com.holonplatform.core.Path;
import com.holonplatform.core.property.Property;
import com.holonplatform.datastore.mongo.core.expression.PathValueExpression;

/**
 * Default {@link PathValueExpression} implementation.
 *
 * @param <T> Path type
 *
 * @since 5.2.0
 */
public class DefaultPathValueExpression<T> implements PathValueExpression<T> {

	private final Path<T> path;
	private final T value;

	private final ExpressionValueConverter<T, ?> converter;

	/**
	 * Constructor.
	 * @param path The path to which the value is bound
	 * @param value The path value
	 */
	@SuppressWarnings("unchecked")
	public DefaultPathValueExpression(Path<T> path, T value) {
		super();
		this.path = path;
		this.value = value;
		if (path != null && Property.class.isAssignableFrom(path.getClass())) {
			converter = ((Property<T>) path).getConverter()
					.map(c -> ExpressionValueConverter.fromProperty(((Property<T>) path), c)).orElse(null);
		} else {
			converter = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.PathValueExpression#getPath()
	 */
	@Override
	public Path<T> getPath() {
		return path;
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
	 * @see com.holonplatform.core.TypedExpression#getType()
	 */
	@Override
	public Class<? extends T> getType() {
		return (path != null) ? path.getType() : null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ConverterExpression#getExpressionValueConverter()
	 */
	@Override
	public Optional<ExpressionValueConverter<T, ?>> getExpressionValueConverter() {
		return Optional.ofNullable(converter);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getPath() == null) {
			throw new InvalidExpressionException("Null path");
		}
	}

}
