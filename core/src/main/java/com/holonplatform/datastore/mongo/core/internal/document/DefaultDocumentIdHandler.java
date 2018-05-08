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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.document.DocumentIdHandler;
import com.holonplatform.datastore.mongo.core.exceptions.InvalidDocumentIdentifierException;

/**
 * Default {@link DocumentIdHandler} implementation.
 *
 * @since 5.2.0
 */
public enum DefaultDocumentIdHandler implements DocumentIdHandler {

	/**
	 * Singleton instance
	 */
	INSTANCE;

	/**
	 * Admitted document id types
	 */
	private static final Set<Class<?>> ADMITTED_TYPES = new HashSet<>(4);

	static {
		ADMITTED_TYPES.add(ObjectId.class);
		ADMITTED_TYPES.add(String.class);
		ADMITTED_TYPES.add(BigInteger.class);
		ADMITTED_TYPES.add(byte[].class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.DocumentIdCodec#isAdmittedType(java.lang.Class)
	 */
	@Override
	public boolean isAdmittedType(Class<?> type) {
		return (type != null && ADMITTED_TYPES.contains(type));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.DocumentIdCodec#getAdmittedTypes()
	 */
	@Override
	public Set<Class<?>> getAdmittedTypes() {
		return Collections.unmodifiableSet(ADMITTED_TYPES);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.DocumentIdCodec#encode(java.lang.Object)
	 */
	@Override
	public ObjectId encode(Object documentId) throws InvalidDocumentIdentifierException {
		if (documentId != null) {
			if (!isAdmittedType(documentId.getClass())) {
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
	 * @see com.holonplatform.datastore.mongo.core.document.DocumentIdCodec#decode(org.bson.types.ObjectId,
	 * java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(ObjectId id, Class<? extends T> type) throws InvalidDocumentIdentifierException {
		ObjectUtils.argumentNotNull(type, "Document id type must be not null");
		if (id != null) {
			if (!isAdmittedType(type)) {
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

}
