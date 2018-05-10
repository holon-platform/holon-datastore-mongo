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
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultFieldNameExpression;

/**
 * Mongo document field name expression, with <em>dot notation</em> support.
 *
 * @since 5.2.0
 */
public interface FieldNameExpression extends Expression {

	/**
	 * Get the document field name. The <em>dot notation</em> convention can be used to declare a fields path.
	 * @return The field name
	 */
	String getFieldName();

	/**
	 * Create a new {@link FieldNameExpression} using given field name.
	 * @param fieldName The field name expression
	 * @return A new {@link FieldNameExpression} instance
	 */
	static FieldNameExpression create(String fieldName) {
		return new DefaultFieldNameExpression(fieldName);
	}

}
