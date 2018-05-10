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

import org.bson.Document;

import com.holonplatform.core.Expression;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultDocumentValue;

/**
 * Expression which represents a MongoDB {@link Document} value.
 *
 * @since 5.2.0
 */
public interface DocumentValue extends Expression {

	/**
	 * Get the actual {@link Document} value.
	 * @return The {@link Document} value
	 */
	Document getValue();

	/**
	 * Create a new {@link DocumentValue} using given document.
	 * @param document The document instance
	 * @return A new {@link DocumentValue} instance
	 */
	static DocumentValue create(Document document) {
		return new DefaultDocumentValue(document);
	}

}
