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
import com.holonplatform.datastore.mongo.core.expression.FieldValue;

/**
 * Default {@link FieldValue} implementation.
 *
 * @since 5.2.0
 */
public class DefaultFieldValue implements FieldValue {

	private final Object value;
	private final Property<?> property;

	/**
	 * Constructor.
	 * @param value The field value
	 */
	public DefaultFieldValue(Object value) {
		this(value, null);
	}

	/**
	 * Constructor with property.
	 * @param value The field value
	 * @param property The property to which the field value is bound
	 */
	public DefaultFieldValue(Object value, Property<?> property) {
		super();
		this.value = value;
		this.property = property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.FieldValueExpression#getValue()
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.FieldValueExpression#getProperty()
	 */
	@Override
	public Optional<Property<?>> getProperty() {
		return Optional.ofNullable(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.expression.DefaultFieldValue#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
	}

}
