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

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;

/**
 * Default {@link PropertyBoxValue} implementation.
 *
 * @since 5.2.0
 */
public class DefaultPropertyBoxValue implements PropertyBoxValue {

	private final PropertyBox value;
	private final PropertySet<?> propertySet;
	
	/**
	 * Constructor.
	 * @param value The PropertyBox value
	 * @param propertySet Optional property set
	 */
	public DefaultPropertyBoxValue(PropertyBox value, PropertySet<?> propertySet) {
		super();
		this.value = value;
		this.propertySet = propertySet;
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.ExpressionValue#getValue()
	 */
	@Override
	public PropertyBox getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue#getPropertySet()
	 */
	@Override
	public PropertySet<?> getPropertySet() {
		return (propertySet != null) ? propertySet : getValue();
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {}

}
