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
package com.holonplatform.datastore.mongo.core.context;

import java.util.Optional;

import com.holonplatform.core.ExpressionResolver.ResolutionContext;
import com.holonplatform.core.Path;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertySet;

/**
 * A {@link MongoResolutionContext} which is bound to a document resolution operation.
 *
 * @since 5.2.0
 */
public interface MongoDocumentContext extends MongoResolutionContext {

	/**
	 * Default MongoDB document id field name
	 */
	public static final String ID_FIELD_NAME = "_id";

	/**
	 * Get the {@link PropertySet} to which the document resolution is bound.
	 * @return The document {@link PropertySet}
	 */
	PropertySet<?> getPropertySet();

	/**
	 * Get the {@link Path} which acts as document id, if available.
	 * @return Optional document id path
	 */
	Optional<Path<?>> getDocumentIdPath();

	/**
	 * Get the {@link Property} which acts as document id, if available.
	 * @return Optional document id property
	 */
	Optional<Property<?>> getDocumentIdProperty();

	/**
	 * Checks if given path matches the document id path, if available.
	 * @param path The path to check
	 * @return If a document id path is available and matches the given path, returns the actual document id path
	 */
	Optional<Path<?>> isDocumentIdPath(Path<?> path);

	/**
	 * Checks if given resolution context is a {@link MongoDocumentContext} type.
	 * @param context The context to check
	 * @return If the given context is a {@link MongoDocumentContext}, returns the context itself as a
	 *         {@link MongoDocumentContext} type. Otherwise an empty Optional is returned.
	 */
	static Optional<MongoDocumentContext> isDocumentContext(ResolutionContext context) {
		return Optional.ofNullable((context instanceof MongoDocumentContext) ? (MongoDocumentContext) context : null);
	}

}
