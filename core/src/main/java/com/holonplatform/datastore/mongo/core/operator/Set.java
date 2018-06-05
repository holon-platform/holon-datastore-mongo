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
package com.holonplatform.datastore.mongo.core.operator;

import com.holonplatform.core.TypedExpression;
import com.holonplatform.datastore.mongo.core.expression.FieldOperatorExpression;
import com.holonplatform.datastore.mongo.core.internal.operator.DefaultSet;

/**
 * <code>$set</code> operator expression.
 * 
 * @param <T> Expression type
 *
 * @since 5.2.0
 */
public interface Set<T> extends FieldOperatorExpression<T> {

	/**
	 * Get the expression value.
	 * @return the expression value
	 */
	T getValue();

	/**
	 * Create a new {@link Set} operator expression.
	 * @param <T> Expression type
	 * @param fieldExpression The expression which represents the document field (not null)
	 * @param value The value to set (not null)
	 * @return A new {@link Set} instance
	 */
	public static <T> Set<T> create(TypedExpression<T> fieldExpression, T value) {
		return new DefaultSet<>(fieldExpression, value);
	}

}
