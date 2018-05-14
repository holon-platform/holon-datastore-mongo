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
import com.holonplatform.core.property.Property;
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
	 * Get the {@link Property} to which the field value is bound, if any.
	 * @return Optional property to which the field value is bound
	 */
	Optional<Property<?>> getProperty();

	/**
	 * Create a new {@link FieldValue}.
	 * @param value The field value
	 * @return A new {@link FieldValue} instance
	 */
	static FieldValue create(Object value) {
		return new DefaultFieldValue(value);
	}

	/**
	 * Create a new {@link FieldValue}.
	 * @param value The field value
	 * @param property The property to which the value is bound
	 * @return A new {@link FieldValue} instance
	 */
	static FieldValue create(Object value, Property<?> property) {
		return new DefaultFieldValue(value, property);
	}

}
