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

import com.holonplatform.datastore.mongo.core.expression.FieldValueExpression;

/**
 * Default {@link FieldValueExpression} implementation.
 *
 * @since 5.2.0
 */
public class DefaultFieldValueExpression implements FieldValueExpression {

	private final String fieldName;
	private final Object value;

	/**
	 * Constructor
	 * @param fieldName The field name
	 * @param value The field value
	 */
	public DefaultFieldValueExpression(String fieldName, Object value) {
		super();
		this.fieldName = fieldName;
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.FieldValueExpression#getFieldName()
	 */
	@Override
	public String getFieldName() {
		return fieldName;
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
	 * @see com.holonplatform.datastore.mongo.core.internal.expression.DefaultFieldValue#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getFieldName() == null) {
			throw new InvalidExpressionException("Null field name");
		}
	}

}
