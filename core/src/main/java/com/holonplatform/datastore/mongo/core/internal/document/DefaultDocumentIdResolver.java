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
package com.holonplatform.datastore.mongo.core.internal.document;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.types.ObjectId;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.document.DocumentIdHandler;
import com.holonplatform.datastore.mongo.core.document.DocumentIdPropertyResolver;
import com.holonplatform.datastore.mongo.core.exceptions.InvalidDocumentIdentifierException;

/**
 * Default {@link DocumentIdPropertyResolver} implementation.
 *
 * @since 5.2.0
 */
public enum DefaultDocumentIdResolver implements DocumentIdPropertyResolver {

	/**
	 * Singleton instance
	 */
	INSTANCE;

	/**
	 * Cache
	 */
	private static final Map<DocumentIdHandler, Map<PropertySet<?>, Supplier<Property<?>>>> DOCUMENT_IDS = new WeakHashMap<>();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.DocumentIdPropertyResolver#resolveDocumentIdProperty(com.
	 * holonplatform.core.property.PropertySet, com.holonplatform.datastore.mongo.core.document.DocumentIdHandler)
	 */
	@Override
	public Optional<Property<?>> resolveDocumentIdProperty(PropertySet<?> propertySet,
			DocumentIdHandler documentIdHandler) throws InvalidDocumentIdentifierException {
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		ObjectUtils.argumentNotNull(documentIdHandler, "DocumentIdHandler must be not null");
		return Optional.ofNullable(DOCUMENT_IDS.computeIfAbsent(documentIdHandler, handler -> new HashMap<>())
				.computeIfAbsent(propertySet, ps -> () -> findDocumentIdProperty(ps, documentIdHandler)).get());
	}

	/**
	 * Lookup for a property which can act as document identifier.
	 * @param <P> Property type
	 * @param propertySet Property set (not null)
	 * @param documentIdHandler Document id handler
	 * @return The document identifier property, if available
	 * @throws InvalidDocumentIdentifierException If more than one suitable property is found or if an identifier
	 *         property has invalid type
	 */
	@SuppressWarnings("rawtypes")
	private static <P extends Property> Property<?> findDocumentIdProperty(PropertySet<P> propertySet,
			DocumentIdHandler documentIdHandler) throws InvalidDocumentIdentifierException {
		// check identifiers for the default id path name
		Property<?> identifier = findDefaultDocumentIdPathProperty(propertySet, propertySet.identifiers(),
				documentIdHandler);
		if (identifier != null) {
			return identifier;
		}
		// check a suitable (unique) identifier property with a different path name
		if (propertySet.getIdentifiers().size() == 1) {
			identifier = propertySet.getIdentifiers().iterator().next();
			if (isValidDocumentIdType(identifier, documentIdHandler)) {
				return identifier;
			}
		}
		// check all properties for the default id path name
		return findDefaultDocumentIdPathProperty(propertySet, propertySet.stream(), documentIdHandler);
	}

	/**
	 * Lookup for a property with the {@link DocumentIdPropertyResolver#DOCUMENT_ID_PATH_NAME} path name and which can
	 * act as document identifier.
	 * @param <P> Property type
	 * @param propertySet Property set
	 * @param properties Properties to take into account
	 * @param documentIdHandler Document id handler
	 * @return The document identifier property, if available
	 * @throws InvalidDocumentIdentifierException
	 */
	@SuppressWarnings("rawtypes")
	private static <P extends Property> Property<?> findDefaultDocumentIdPathProperty(PropertySet<?> propertySet,
			Stream<P> properties, DocumentIdHandler documentIdHandler) throws InvalidDocumentIdentifierException {
		List<Property<?>> documentIdProperties = properties.filter(p -> Path.class.isAssignableFrom(p.getClass()))
				.map(p -> (Path<?>) p)
				.filter(p -> DocumentIdPropertyResolver.DOCUMENT_ID_PATH_NAME.equals(p.relativeName()))
				.map(p -> (Property<?>) p).collect(Collectors.toList());
		if (!documentIdProperties.isEmpty()) {
			if (documentIdProperties.size() > 1) {
				throw new InvalidDocumentIdentifierException("More than one property [" + documentIdProperties.size()
						+ "] with the default document id path name ["
						+ DocumentIdPropertyResolver.DOCUMENT_ID_PATH_NAME + "] found in property set [" + propertySet
						+ "]");
			}
			Property<?> property = documentIdProperties.get(0);
			// check type
			if (!isValidDocumentIdType(property, documentIdHandler)) {
				throw new InvalidDocumentIdentifierException("The id property [" + property + "] type ["
						+ property.getType()
						+ "] is not an admitted document id type. Admitted types are: ObjectId, String, BigInteger.");
			}
			return property;
		}
		// not found
		return null;
	}

	/**
	 * Checks if the type of given {@link Property} is admitted as document identifier value type. Admitted types are:
	 * {@link String}, {@link BigInteger} and {@link ObjectId}.
	 * @param property Property to check
	 * @param documentIdHandler Document id handler
	 * @return <code>true</code> if given type is admitted as document identifier type, <code>false</code> otherwise
	 */
	@SuppressWarnings("rawtypes")
	private static boolean isValidDocumentIdType(Property<?> property, DocumentIdHandler documentIdHandler) {
		final Class<?> type = property.getConverter().map(c -> (Class) c.getModelType()).orElse(property.getType());
		return documentIdHandler.isAdmittedType(type);
	}

}
