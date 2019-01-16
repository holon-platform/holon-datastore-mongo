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
 * Resolver to detect a suitable {@link Path} type {@link Property} within a {@link PropertySet} which can act as
 * <em>document</em> identifier property.
 *
 * @since 5.2.0
 */
public interface DocumentIdResolver {

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
	 * Try to resolve the {@link Path} within given {@link PropertySet} which can act as <em>document</em> identifier
	 * path property.
	 * @param propertySet The property set (not null)
	 * @return The document identifier path, if available
	 * @throws InvalidDocumentIdentifierException If an error occurred during document identifier property resolution
	 */
	Optional<Path<?>> resolveDocumentIdPath(PropertySet<?> propertySet) throws InvalidDocumentIdentifierException;

	/**
	 * Encode given document id as an {@link ObjectId}.
	 * @param documentId The document id to encode
	 * @return The encoded {@link ObjectId}, <code>null</code> if given document id was <code>null</code>
	 * @throws InvalidDocumentIdentifierException If the given document id is not encodable
	 */
	ObjectId encode(Object documentId) throws InvalidDocumentIdentifierException;

	/**
	 * Decode given {@link ObjectId} using given type.
	 * @param <T> Expected value type
	 * @param id The id to decode
	 * @param type The decoding type (not null)
	 * @return The decoded document id, <code>null</code> if given id was <code>null</code>
	 * @throws InvalidDocumentIdentifierException If the given type is not decodable
	 */
	<T> T decode(ObjectId id, Class<? extends T> type) throws InvalidDocumentIdentifierException;

	/**
	 * Checks if the type of given property is a valid document id type.
	 * @param property Property to check
	 * @return <code>true</code> if the type of given property is a valid document id type, <code>false</code> otherwise
	 */
	boolean isValidDocumentIdType(Property<?> property);

	/**
	 * Checks if the given type is a valid document id type.
	 * @param type Type to check
	 * @return <code>true</code> if the given type is a valid document id type, <code>false</code> otherwise
	 */
	boolean isValidDocumentIdType(Class<?> type);

	// ------- Default resolver

	/**
	 * Get the default {@link DocumentIdResolver} implementation.
	 * <p>
	 * The admitted identifier property types are: {@link String}, {@link BigInteger} and {@link ObjectId}.
	 * </p>
	 * <p>
	 * The default {@link DocumentIdResolver} implementation looks for a {@link Path} type {@link Property} with the
	 * <code>_id</code> path name, first of all in the <em>identifier</em> property set, if available (see
	 * {@link PropertySet#getIdentifiers()}), or in the overall property set.
	 * </p>
	 * <p>
	 * If more than one property with the <code>_id</code> path name is found, or if the identifier property type is not
	 * admitted, an {@link InvalidDocumentIdentifierException} is thrown.
	 * </p>
	 * @return The default {@link DocumentIdResolver} implementation
	 */
	static DocumentIdResolver getDefault() {
		return DefaultDocumentIdResolver.INSTANCE;
	}

}
