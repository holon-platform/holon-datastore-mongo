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

import org.bson.Document;

import com.holonplatform.datastore.mongo.core.expression.DocumentValue;

/**
 * Default {@link DocumentValue} implementation.
 *
 * @since 5.2.0
 */
public class DefaultDocumentValue implements DocumentValue {

	private final Document document;

	/**
	 * Constructor.
	 * @param document The document instance
	 */
	public DefaultDocumentValue(Document document) {
		super();
		this.document = document;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.DocumentValue#getValue()
	 */
	@Override
	public Document getValue() {
		return document;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getValue() == null) {
			throw new InvalidExpressionException("Null document value");
		}
	}

}
