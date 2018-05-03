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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.document.DocumentIdResolver;
import com.holonplatform.datastore.mongo.core.exceptions.InvalidDocumentIdentifierException;

/**
 * Default {@link DocumentIdResolver} implementation.
 *
 * @since 5.2.0
 */
public enum DefaultDocumentIdResolver implements DocumentIdResolver {

	/**
	 * Singleton instance
	 */
	INSTANCE;

	/**
	 * Cache
	 */
	private static final Map<PropertySet<?>, Supplier<Property<?>>> DOCUMENT_IDS = new WeakHashMap<>();

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.document.DocumentIdResolver#resolveDocumentIdProperty(com.holonplatform.
	 * core.property.PropertySet)
	 */
	@Override
	public Optional<Property<?>> resolveDocumentIdProperty(PropertySet<?> propertySet)
			throws InvalidDocumentIdentifierException {
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");

		return Optional
				.ofNullable(DOCUMENT_IDS.computeIfAbsent(propertySet, ps -> () -> findDocumentIdProperty(ps)).get());
	}

	/**
	 * Lookup for a property which can act as document identifier.
	 * @param <P> Property type
	 * @param propertySet Property set (not null)
	 * @return The document identifier property, if available
	 * @throws InvalidDocumentIdentifierException If more than one suitable property is found or if an identifier
	 *         property has invalid type
	 */
	@SuppressWarnings("rawtypes")
	private static <P extends Property> Property<?> findDocumentIdProperty(PropertySet<P> propertySet)
			throws InvalidDocumentIdentifierException {
		// check identifiers
		Set<P> identifiers = propertySet.getIdentifiers();
		if (!identifiers.isEmpty()) {
			if (identifiers.size() > 1) {
				throw new InvalidDocumentIdentifierException(
						"More than one identifier property [" + identifiers.size() + "] declared in PropertySet ["
								+ propertySet + "] but only one is admitted to act as document id");
			}
			P identifier = identifiers.iterator().next();
			if (identifier != null) {
				if (!isValidDocumentIdProperty(identifier)) {
					throw new InvalidDocumentIdentifierException("The identifier property [" + identifier
							+ "] cannot be used as document id: the Property type must be either String, BigInteger or ObjectId");
				}
				return identifier;
			}
		}

		// look for the default document id property name
		List<Property<?>> documentIdProperties = propertySet.stream()
				.filter(p -> Path.class.isAssignableFrom(p.getClass())).map(p -> (Path<?>) p)
				.filter(p -> DocumentIdResolver.DEFAULT_DOCUMENT_ID_PATH_NAME.equals(p.relativeName()))
				.map(p -> (Property<?>) p).filter(p -> isValidDocumentIdType(p.getType())).collect(Collectors.toList());
		if (!documentIdProperties.isEmpty()) {
			if (documentIdProperties.size() > 1) {
				throw new InvalidDocumentIdentifierException("More than one property [" + identifiers.size()
						+ "]  with default document id path name [" + DocumentIdResolver.DEFAULT_DOCUMENT_ID_PATH_NAME
						+ "] found in PropertySet [" + propertySet
						+ "]: declare the right document id property to use as PropertySet identifier");
			}
			return documentIdProperties.get(0);
		}

		// look for a ObjectId type property
		List<P> objectIdProperties = propertySet.stream().filter(p -> ObjectId.class.isAssignableFrom(p.getType()))
				.collect(Collectors.toList());
		if (objectIdProperties.size() == 1) {
			return objectIdProperties.get(0);
		}

		// not found
		return null;
	}

	/**
	 * Checks if given property can be used as document identifier.
	 * @param property The property to check
	 * @return <code>true</code> if given property can be used as document identifie, <code>false</code> otherwise
	 */
	private static boolean isValidDocumentIdProperty(Property<?> property) {
		if (property != null) {
			return isValidDocumentIdType(property.getType());
		}
		return false;
	}

	/**
	 * Checks if given type is admitted as document identifier value type. Admitted types are: {@link String},
	 * {@link BigInteger} and {@link ObjectId}.
	 * @param type The type to check
	 * @return <code>true</code> if given type is admitted as document identifier type, <code>false</code> otherwise
	 */
	private static boolean isValidDocumentIdType(Class<?> type) {
		return type != null && (String.class.isAssignableFrom(type) || BigInteger.class.isAssignableFrom(type)
				|| ObjectId.class.isAssignableFrom(type));
	}

}
