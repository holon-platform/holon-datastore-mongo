/*
 * Copyright 2016-2019 Axioma srl.
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
package com.holonplatform.datastore.mongo.core.internal.support;

import java.io.Serializable;

import com.holonplatform.core.Path;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.property.Property;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;

/**
 * Helper class for document id resolution.
 * 
 * @since 5.2.0
 */
public final class DocumentIdHelper implements Serializable {

	private static final long serialVersionUID = -3223984014715022926L;

	private DocumentIdHelper() {
	}

	/**
	 * Check if given expression acts as document id property or path and the property/path name id the default "_id"
	 * name.
	 * @param context Resolution context
	 * @param expression The expression to check
	 * @return <code>true</code> if given property acts as document id property, <code>false</code> otherwise
	 */
	public static boolean isDefaultDocumentIdProperty(MongoResolutionContext<?> context,
			TypedExpression<?> expression) {
		boolean documentIdProperty = false;
		if (Property.class.isAssignableFrom(expression.getClass())) {
			documentIdProperty = MongoDocumentContext.isDocumentContext(context)
					.map(ctx -> ctx.isDocumentIdProperty((Property<?>) expression)).orElse(false);
		} else if (Path.class.isAssignableFrom(expression.getClass())) {
			documentIdProperty = MongoDocumentContext.isDocumentContext(context)
					.map(ctx -> ctx.isDocumentIdPath((Path<?>) expression).isPresent()).orElse(false);
		}
		if (documentIdProperty) {
			return isDocumentIdPropertyPath(expression);
		}
		return false;
	}

	/**
	 * Check if given expression represents the default document id attribute.
	 * @param expression The expression to check
	 * @return Whether the given expression represents the default document id attribute
	 */
	public static boolean isDocumentIdPropertyPath(TypedExpression<?> expression) {
		if (Path.class.isAssignableFrom(expression.getClass())) {
			return MongoDocumentContext.ID_FIELD_NAME.equals(((Path<?>) expression).relativeName());
		}
		if (Property.class.isAssignableFrom(expression.getClass())) {
			return MongoDocumentContext.ID_FIELD_NAME.equals(((Property<?>) expression).getName());
		}
		return false;
	}

}
