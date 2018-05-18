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

import org.bson.conversions.Bson;

import com.holonplatform.core.Expression;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultBsonExpression;

/**
 * Expression which represents a {@link Bson} value.
 *
 * @since 5.2.0
 */
public interface BsonExpression extends Expression {

	/**
	 * Get the {@link Bson} value.
	 * @return the Bson value
	 */
	Bson getValue();

	/**
	 * Create a new {@link BsonExpression}.
	 * @param value Bson value
	 * @return A new {@link BsonExpression} instance
	 */
	static BsonExpression create(Bson value) {
		return new DefaultBsonExpression(value);
	}

}
