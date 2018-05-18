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

import com.holonplatform.core.TypedExpression;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;

/**
 * Default {@link FieldValue} implementation.
 *
 * @since 5.2.0
 */
public class DefaultFieldValue implements FieldValue {

	private final Object value;
	private final TypedExpression<?> expression;
	private final EnumCodecStrategy enumCodecStrategy;

	public DefaultFieldValue(Object value, TypedExpression<?> expression, EnumCodecStrategy enumCodecStrategy) {
		super();
		this.value = value;
		this.expression = expression;
		this.enumCodecStrategy = enumCodecStrategy;
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
	 * @see com.holonplatform.datastore.mongo.core.expression.FieldValue#getEnumCodecStrategy()
	 */
	@Override
	public Optional<EnumCodecStrategy> getEnumCodecStrategy() {
		return Optional.ofNullable(enumCodecStrategy);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.FieldValue#getExpression()
	 */
	@Override
	public Optional<TypedExpression<?>> getExpression() {
		return Optional.ofNullable(expression);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.expression.DefaultFieldValue#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
	}

}
