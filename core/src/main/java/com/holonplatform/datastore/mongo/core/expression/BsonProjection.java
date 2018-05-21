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
package com.holonplatform.datastore.mongo.core.expression;

import java.util.List;
import java.util.Optional;

import com.holonplatform.core.TypedExpression;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultBsonProjection;

/**
 * Mongo query projection expression.
 *
 * @param <R> Projection result type
 *
 * @since 5.2.0
 */
public interface BsonProjection<R> extends TypedExpression<R> {

	/**
	 * Get the {@link QueryOperationType}.
	 * @return Optional query operation type
	 */
	Optional<QueryOperationType> getOperationType();

	/**
	 * Get the projection fields.
	 * @return the projection field names
	 */
	List<String> getFields();

	/**
	 * Get the projection {@link DocumentConverter}.
	 * @return Oprional document converter
	 */
	Optional<DocumentConverter<R>> getConverter();

	/**
	 * Get a new {@link BsonProjection} builder.
	 * @param <R> Projection result type
	 * @param projectionType Projection result type
	 * @return A new {@link BsonProjection} builder
	 */
	static <R> Builder<R> builder(Class<R> projectionType) {
		return new DefaultBsonProjection.DefaultBuilder<>(projectionType);
	}

	/**
	 * Builder.
	 *
	 * @param <R> Projection result type
	 */
	public interface Builder<R> {

		/**
		 * Set the {@link QueryOperationType}.
		 * @param operationType The query operation type
		 * @return this
		 */
		Builder<R> operationType(QueryOperationType operationType);

		/**
		 * Set the projection fields names.
		 * @param fields Projection fields
		 * @return this
		 */
		Builder<R> fields(List<String> fields);

		/**
		 * Set the document results converter.
		 * @param converter The converter to set
		 * @return this
		 */
		Builder<R> converter(DocumentConverter<R> converter);

		/**
		 * Build the {@link BsonProjection}.
		 * @return A new {@link BsonProjection}
		 */
		BsonProjection<R> build();

	}
}
