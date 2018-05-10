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
import java.util.WeakHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	 * Default document identifier property path name
	 */
	private static final String DOCUMENT_ID_PATH_NAME = "_id";

	/**
	 * Admitted document id types
	 */
	private static final Class<?>[] ADMITTED_TYPES = new Class<?>[] { ObjectId.class, String.class, BigInteger.class,
			byte[].class };

	/**
	 * Cache
	 */
	private static final Map<PropertySet<?>, Supplier<Object>> DOCUMENT_IDS = new WeakHashMap<>();

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.document.DocumentIdResolver#resolveDocumentIdProperty(com.holonplatform.
	 * core.property.PropertySet)
	 */
	@Override
	public Optional<Property<?>> resolveDocumentIdProperty(PropertySet<?> propertySet)
			throws InvalidDocumentIdentifierException {
		return Optional.ofNullable(resolveDocumentId(propertySet));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.document.DocumentIdResolver#resolveDocumentIdPath(com.holonplatform.core.
	 * property.PropertySet)
	 */
	@Override
	public Optional<Path<?>> resolveDocumentIdPath(PropertySet<?> propertySet)
			throws InvalidDocumentIdentifierException {
		return Optional.ofNullable(resolveDocumentId(propertySet));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.DocumentIdResolver#encode(java.lang.Object)
	 */
	@Override
	public ObjectId encode(Object documentId) throws InvalidDocumentIdentifierException {
		if (documentId != null) {
			if (!isAdmittedDocumentIdType(documentId.getClass())) {
				throw new InvalidDocumentIdentifierException(
						"Invalid document id type [" + documentId.getClass() + "]");
			}
			if (ObjectId.class.isAssignableFrom(documentId.getClass())) {
				return (ObjectId) documentId;
			}
			if (String.class.isAssignableFrom(documentId.getClass())) {
				if (((String) documentId).length() > 0) {
					return new ObjectId((String) documentId);
				}
			}
			if (byte[].class == documentId.getClass()) {
				return new ObjectId((byte[]) documentId);
			}
			if (BigInteger.class.isAssignableFrom(documentId.getClass())) {
				return new ObjectId(((BigInteger) documentId).toString(16));
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.DocumentIdResolver#decode(org.bson.types.ObjectId,
	 * java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(ObjectId id, Class<? extends T> type) throws InvalidDocumentIdentifierException {
		ObjectUtils.argumentNotNull(type, "Document id type must be not null");
		if (id != null) {
			if (!isAdmittedDocumentIdType(type)) {
				throw new InvalidDocumentIdentifierException("Invalid document id type [" + type.getName() + "]");
			}
			if (ObjectId.class.isAssignableFrom(type)) {
				return (T) id;
			}
			if (String.class.isAssignableFrom(type)) {
				return (T) id.toHexString();
			}
			if (byte[].class == type) {
				return (T) id.toByteArray();
			}
			if (BigInteger.class.isAssignableFrom(type)) {
				return (T) new BigInteger(id.toHexString(), 16);
			}
		}
		return null;
	}

	// ------- internal

	/**
	 * Checks if given type is an admitted document id type.
	 * @param type The type to check
	 * @return <code>true</code> if type is an admitted document id type
	 */
	private static boolean isAdmittedDocumentIdType(Class<?> type) {
		if (type != null) {
			for (Class<?> admittedType : ADMITTED_TYPES) {
				if (admittedType == type || admittedType.isAssignableFrom(type)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Lookup for a {@link Property} which is a {@link Path} and which can act as document identifier.
	 * @param <P> Path and Property type
	 * @param propertySet Property set (not null)
	 * @return The document identifier property, <code>null</code> if not found
	 * @throws InvalidDocumentIdentifierException If more than one suitable property is found or if an identifier
	 *         property has invalid type
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <P extends Path & Property> P resolveDocumentId(PropertySet<?> propertySet)
			throws InvalidDocumentIdentifierException {
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		return (P) DOCUMENT_IDS.computeIfAbsent(propertySet, ps -> () -> findDocumentIdProperty(ps)).get();
	}

	/**
	 * Lookup for a property which can act as document identifier.
	 * @param <S> PropertySet property type
	 * @param <P> Path and Property type
	 * @param propertySet Property set (not null)
	 * @return The document identifier property, if available
	 * @throws InvalidDocumentIdentifierException If more than one suitable property is found or if an identifier
	 *         property has invalid type
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <S extends Property, P extends Path & Property> P findDocumentIdProperty(PropertySet<S> propertySet)
			throws InvalidDocumentIdentifierException {
		// check identifiers for the default id path name
		P identifier = findDefaultDocumentIdPathProperty(propertySet, propertySet.identifiers());
		if (identifier != null) {
			return identifier;
		}
		// check a suitable (unique) identifier property with a different path name
		if (propertySet.getIdentifiers().size() == 1) {
			S id = propertySet.getIdentifiers().iterator().next();
			if (id != null && Path.class.isAssignableFrom(id.getClass())) {
				if (isValidDocumentIdType(id)) {
					return (P) id;
				}
			}
		}
		// check all properties for the default id path name
		return findDefaultDocumentIdPathProperty(propertySet, propertySet.stream());
	}

	/**
	 * Lookup for a property with the {@link DocumentIdResolver#DOCUMENT_ID_PATH_NAME} path name and which can act as
	 * document identifier.
	 * @param <S> PropertySet property type
	 * @param <P> Path and Property type
	 * @param propertySet Property set
	 * @param properties Properties to take into account
	 * @return The document identifier property, if available
	 * @throws InvalidDocumentIdentifierException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <S extends Property, P extends Path & Property> P findDefaultDocumentIdPathProperty(
			PropertySet<S> propertySet, Stream<S> properties) throws InvalidDocumentIdentifierException {
		List<P> documentIdProperties = properties.filter(p -> Path.class.isAssignableFrom(p.getClass())).map(p -> (P) p)
				.filter(p -> DOCUMENT_ID_PATH_NAME.equals(p.relativeName())).collect(Collectors.toList());
		if (!documentIdProperties.isEmpty()) {
			if (documentIdProperties.size() > 1) {
				throw new InvalidDocumentIdentifierException("More than one property [" + documentIdProperties.size()
						+ "] with the default document id path name [" + DOCUMENT_ID_PATH_NAME
						+ "] found in property set [" + propertySet + "]");
			}
			P property = documentIdProperties.get(0);
			// check type
			if (!isValidDocumentIdType(property)) {
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
	 * @return <code>true</code> if given type is admitted as document identifier type, <code>false</code> otherwise
	 */
	@SuppressWarnings("rawtypes")
	private static boolean isValidDocumentIdType(Property<?> property) {
		final Class<?> type = property.getConverter().map(c -> (Class) c.getModelType()).orElse(property.getType());
		return isAdmittedDocumentIdType(type);
	}

}
