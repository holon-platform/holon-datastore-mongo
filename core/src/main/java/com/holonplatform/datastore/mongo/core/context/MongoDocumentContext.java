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
import com.holonplatform.datastore.mongo.core.internal.context.DefaultMongoDocumentContext;
import com.mongodb.session.ClientSession;

/**
 * A {@link MongoResolutionContext} which is bound to a document resolution operation.
 * 
 * @param <S> Concrete ClientSession type
 *
 * @since 5.2.0
 */
public interface MongoDocumentContext<S extends ClientSession> extends MongoResolutionContext<S> {

	/**
	 * Default MongoDB document id field name
	 */
	public static final String ID_FIELD_NAME = "_id";

	/**
	 * Get the {@link PropertySet} to which the document resolution is bound.
	 * @param <P> Property type
	 * @return The document {@link PropertySet}
	 */
	<P extends Property<?>> PropertySet<P> getPropertySet();

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
	 * Checks if given property is the document id property.
	 * @param property The property to check
	 * @return <code>true</code> if the property acts as document id property
	 */
	default boolean isDocumentIdProperty(Property<?> property) {
		return getDocumentIdProperty().filter(p -> p.equals(property)).isPresent();
	}

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
	static Optional<MongoDocumentContext<?>> isDocumentContext(ResolutionContext context) {
		return Optional
				.ofNullable((context instanceof MongoDocumentContext) ? (MongoDocumentContext<?>) context : null);
	}

	/**
	 * Create a new {@link MongoDocumentContext}.
	 * @param <S> Concrete client session type
	 * @param context Mongo context (not null)
	 * @param propertySet The {@link PropertySet} to which the document is bound (not null)
	 * @return A new {@link MongoDocumentContext} instance
	 */
	static <S extends ClientSession> MongoDocumentContext<S> create(MongoContext<S> context,
			PropertySet<?> propertySet) {
		return new DefaultMongoDocumentContext<>(context, propertySet, false);
	}

	/**
	 * Create a new {@link MongoDocumentContext} for an update type operation.
	 * @param <S> Concrete client session type
	 * @param context Mongo context (not null)
	 * @param propertySet The {@link PropertySet} to which the document is bound (not null)
	 * @return A new {@link MongoDocumentContext} instance
	 */
	static <S extends ClientSession> MongoDocumentContext<S> createForUpdate(MongoContext<S> context,
			PropertySet<?> propertySet) {
		return new DefaultMongoDocumentContext<>(context, propertySet, true);
	}

}
