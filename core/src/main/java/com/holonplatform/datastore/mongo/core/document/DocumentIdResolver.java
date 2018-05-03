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
public interface DocumentIdResolver {

	/**
	 * Default document identifier propety path name
	 */
	public static final String DEFAULT_DOCUMENT_ID_PATH_NAME = "_id";

	/**
	 * Try to resolve the {@link Property} within given {@link PropertySet} which can act as <em>document</em>
	 * identifier property.
	 * @param propertySet The property set (not null)
	 * @return The document identifier property, if available
	 * @throws InvalidDocumentIdentifierException If an error occurred during document identifier property resolution
	 */
	Optional<Property<?>> resolveDocumentIdProperty(PropertySet<?> propertySet)
			throws InvalidDocumentIdentifierException;

	/**
	 * Get the default {@link DocumentIdResolver} implementation.
	 * <p>
	 * The admitted identifier property types are: {@link String}, {@link BigInteger} and {@link ObjectId}.
	 * </p>
	 * <p>
	 * The default {@link DocumentIdResolver} implementation adopt the following strategy to resolve the document
	 * identifier property:
	 * <ul>
	 * <li>If the property set declares one <em>identifier</em> property (see {@link PropertySet#getIdentifiers()}),
	 * that property will be used as document identifier. If more than one identifier property is declared, or if the
	 * single identifier property is not of an admitted type, an {@link InvalidDocumentIdentifierException} is
	 * thrown.</li>
	 * <li>If no identifier property is available, the resolver looks for a property with the
	 * {@link #DEFAULT_DOCUMENT_ID_PATH_NAME} path name and of a suitable document identifier type. If more than one
	 * property with the {@link #DEFAULT_DOCUMENT_ID_PATH_NAME} path name is found in property set, an
	 * {@link InvalidDocumentIdentifierException} is thrown.</li>
	 * <li>At last, the resolver checks if one and only one property of {@link ObjectId} type is available in the
	 * property set: if found, this one is returned.</li>
	 * </ul>
	 * @return The default {@link DocumentIdResolver} implementation
	 */
	static DocumentIdResolver getDefault() {
		return DefaultDocumentIdResolver.INSTANCE;
	}

}
