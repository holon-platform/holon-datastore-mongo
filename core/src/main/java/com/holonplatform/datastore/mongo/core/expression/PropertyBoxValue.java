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

import com.holonplatform.core.Expression;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultPropertyBoxValue;

/**
 * Expression which represents a {@link PropertyBox} value.
 *
 * @since 5.2.0
 */
public interface PropertyBoxValue extends Expression {

	/**
	 * Get the {@link PropertyBox} value.
	 * @return The value
	 */
	PropertyBox getValue();

	/**
	 * Get the expression property set.
	 * @return the expression property set
	 */
	default PropertySet<?> getPropertySet() {
		return getValue();
	}

	/**
	 * Create a new {@link PropertyBoxValue}.
	 * @param value The value
	 * @return A new {@link PropertyBoxValue} instance
	 */
	static PropertyBoxValue create(PropertyBox value) {
		return new DefaultPropertyBoxValue(value, value);
	}

	/**
	 * Create a new {@link PropertyBoxValue}.
	 * @param value The value
	 * @param propertySet The property set to use
	 * @return A new {@link PropertyBoxValue} instance
	 */
	static PropertyBoxValue create(PropertyBox value, PropertySet<?> propertySet) {
		return new DefaultPropertyBoxValue(value, propertySet);
	}

}
