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
package com.holonplatform.datastore.mongo.core.internal.operator;

import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.internal.utils.ObjectUtils;

/**
 * Base operator with value expression.
 *
 * @param <T> Value type
 *
 * @since 5.2.0
 */
public class AbstractValueOperatorExpression<T> extends AbstractFieldOperatorExpression<T> {

	private final T value;

	public AbstractValueOperatorExpression(TypedExpression<T> fieldExpression, T value) {
		super(fieldExpression);
		ObjectUtils.argumentNotNull(value, "Value must be not null");
		this.value = value;
	}

	/**
	 * Get the expression value.
	 * @return the expression value
	 */
	public T getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		super.validate();
		if (getValue() == null) {
			throw new InvalidExpressionException("Value must be not null");
		}
	}

}
