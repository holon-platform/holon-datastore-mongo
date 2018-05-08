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
import java.util.Set;

import org.bson.types.ObjectId;

import com.holonplatform.datastore.mongo.core.exceptions.InvalidDocumentIdentifierException;
import com.holonplatform.datastore.mongo.core.internal.document.DefaultDocumentIdHandler;

/**
 * A handler to perform {@link ObjectId} to document id conversions, relying on a set of admitted document id types.
 * 
 * @since 5.2.0
 */
public interface DocumentIdHandler {

	/**
	 * Checks if given type is an admitted document id type.
	 * @param type The type to check
	 * @return <code>true</code> if given type is an admitted document id typ, <code>false</code> otherwise
	 */
	boolean isAdmittedType(Class<?> type);

	/**
	 * Get the admitted document id types.
	 * @return The admitted document id types (not null)
	 */
	Set<Class<?>> getAdmittedTypes();

	/**
	 * Encode given document id as an {@link ObjectId}.
	 * @param documentId The document id to encode
	 * @return The encoded {@link ObjectId}, <code>null</code> if given document id was <code>null</code>
	 * @throws InvalidDocumentIdentifierException If the given document id is not of an admitted type
	 * @see #getAdmittedTypes()
	 */
	ObjectId encode(Object documentId) throws InvalidDocumentIdentifierException;

	/**
	 * Decode given {@link ObjectId} using given type.
	 * @param id The id to decode
	 * @param type The decoding type (not null)
	 * @return The decoded document id, <code>null</code> if given id was <code>null</code>
	 * @throws InvalidDocumentIdentifierException If the given type is not an admitted type
	 * @see #getAdmittedTypes()
	 */
	<T> T decode(ObjectId id, Class<? extends T> type) throws InvalidDocumentIdentifierException;

	/**
	 * Get the default {@link DocumentIdHandler} implementation.
	 * <p>
	 * The admitted document id types for the default implementation are: {@link ObjectId}, {@link String},
	 * {@link BigInteger} and <code>byte[]</code>.
	 * </p>
	 * @return The default {@link DocumentIdHandler} implementation
	 */
	static DocumentIdHandler getDefault() {
		return DefaultDocumentIdHandler.INSTANCE;
	}

}
