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
package com.holonplatform.datastore.mongo.core.internal.document.converter;

import org.bson.Document;

import com.holonplatform.core.ConstantConverterExpression;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.expression.Value;

/**
 * A document converter which returns a document value without performing any conversion.
 * 
 * @param <T> Expression type
 * 
 * @since 5.2.0
 */
public class ValueDocumentConverter<T> implements DocumentConverter<T> {

	/**
	 * Value type
	 */
	private final Class<T> valueType;

	/**
	 * Selection field name
	 */
	private final String selection;

	/**
	 * Constructor
	 * @param valueType Value type (not null)
	 * @param selection Selection field name (not null)
	 */
	public ValueDocumentConverter(Class<T> valueType, String selection) {
		super();
		ObjectUtils.argumentNotNull(valueType, "Value type must be not null");
		ObjectUtils.argumentNotNull(selection, "Selection must be not null");
		this.valueType = valueType;
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.DocumentConverter#getConversionType()
	 */
	@Override
	public Class<? extends T> getConversionType() {
		return valueType;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.document.DocumentConverter#convert(com.holonplatform.datastore.mongo.core.
	 * context.MongoResolutionContext, org.bson.Document)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T convert(MongoResolutionContext context, Document document) {
		if (document != null) {

			final Object fieldValue = document.get(selection);

			Object value = context
					.resolveOrFail(FieldValue.create(fieldValue,
							ConstantConverterExpression.create(fieldValue, getConversionType())), Value.class)
					.getValue();

			if (value != null && !TypeUtils.isAssignable(value.getClass(), getConversionType())) {
				throw new DataAccessException("Failed to convert document value [" + value + "] to required type ["
						+ getConversionType() + "]: value type [" + value.getClass().getName() + "] is not compatible");
			}

			return (T) value;
		}
		return null;
	}

}
