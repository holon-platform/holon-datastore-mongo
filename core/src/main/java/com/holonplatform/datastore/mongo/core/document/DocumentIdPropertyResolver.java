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
package com.holonplatform.datastore.mongo.core.document;

import java.math.BigInteger;
import java.util.Optional;

import org.bson.types.ObjectId;

import com.holonplatform.core.Path;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.exceptions.InvalidDocumentIdentifierException;
import com.holonplatform.datastore.mongo.core.internal.document.DefaultDocumentIdResolver;

/**
 * Resolver to detect a suitable {@link Property} within a {@link PropertySet} which can act as <em>document</em>
 * identifier property.
 *
 * @since 5.2.0
 */
public interface DocumentIdPropertyResolver {

	/**
	 * Default document identifier property path name
	 */
	public static final String DOCUMENT_ID_PATH_NAME = "_id";

	/**
	 * Try to resolve the {@link Property} within given {@link PropertySet} which can act as <em>document</em>
	 * identifier property.
	 * @param propertySet The property set (not null)
	 * @return The document identifier property, if available
	 * @throws InvalidDocumentIdentifierException If an error occurred during document identifier property resolution
	 */
	Optional<Property<?>> resolveDocumentIdProperty(PropertySet<?> propertySet, DocumentIdHandler documentIdHandler)
			throws InvalidDocumentIdentifierException;

	/**
	 * Get the default {@link DocumentIdPropertyResolver} implementation.
	 * <p>
	 * The admitted identifier property types are: {@link String}, {@link BigInteger} and {@link ObjectId}.
	 * </p>
	 * <p>
	 * The default {@link DocumentIdPropertyResolver} implementation looks for a {@link Path} type {@link Property} with
	 * the {@link #DOCUMENT_ID_PATH_NAME} path name, first of all in the <em>identifier</em> property set, if available
	 * (see {@link PropertySet#getIdentifiers()}), or in the overall property set.
	 * </p>
	 * <p>
	 * If more than one property with the {@link #DOCUMENT_ID_PATH_NAME} path name is found, or if the identifier
	 * property type is not admitted, an {@link InvalidDocumentIdentifierException} is thrown.
	 * </p>
	 * @return The default {@link DocumentIdPropertyResolver} implementation
	 */
	static DocumentIdPropertyResolver getDefault() {
		return DefaultDocumentIdResolver.INSTANCE;
	}

}
