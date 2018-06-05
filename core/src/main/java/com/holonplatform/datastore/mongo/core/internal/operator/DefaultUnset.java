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
import com.holonplatform.datastore.mongo.core.operator.Unset;

/**
 * Default {@link Unset} implementation.
 *
 * @param <T> Expression type
 *
 * @since 5.2.0
 */
public class DefaultUnset<T> extends AbstractFieldOperatorExpression<T> implements Unset<T> {

	public DefaultUnset(TypedExpression<T> fieldExpression) {
		super(fieldExpression);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "$unset operator [field=" + getFieldExpression() + "]";
	}

}
