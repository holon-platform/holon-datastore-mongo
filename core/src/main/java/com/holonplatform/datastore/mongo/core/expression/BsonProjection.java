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
import java.util.Map;
import java.util.Optional;

import org.bson.conversions.Bson;

import com.holonplatform.core.TypedExpression;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
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
	 * Get whether the projection is empty.
	 * @return whether the projection is empty
	 */
	default boolean isEmpty() {
		return getFields().isEmpty();
	}

	/**
	 * Get the projection fields as field name - Bson expression map.
	 * @return the projection fields
	 */
	Map<String, Bson> getFields();

	/**
	 * Get the field names.
	 * @return field names
	 */
	List<String> getFieldNames();

	/**
	 * Get the field projection values.
	 * @return field projection values
	 */
	List<Bson> getFieldProjections();

	/**
	 * Get the projection {@link DocumentConverter}.
	 * @return Oprional document converter
	 */
	Optional<DocumentConverter<R>> getConverter();

	/**
	 * Get whether this projection is a <em>count all </em> projection.
	 * @return Whether this projection is a <em>count all </em> projection
	 */
	boolean isCountAllProjection();

	/**
	 * Get whether this projection is a <em>count</em> projection on a specific field name.
	 * @return If this projection is a <em>count</em> projection on a specific field name, return the field name,
	 *         otherwise an empty Optional
	 */
	Optional<String> isCountFieldProjection();

	/**
	 * Get whether this projection include one or more <em>aggregation functions</em> (for example min, man, sum, avg,
	 * count).
	 * @return Whether this projection include one or more <em>aggregation functions</em>
	 */
	boolean hasAggregationFunctions();

	/**
	 * Create a new <em>count all</em> {@link BsonProjection}.
	 * @return A new <em>count all</em> {@link BsonProjection}
	 */
	static BsonProjection<Long> countAll() {
		return new DefaultBsonProjection<>(Long.class, true);
	}

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
	 * Get a new {@link BsonProjection} builder.
	 * @param <R> Projection result type
	 * @param projectionType Projection result type
	 * @param countAllProjection Whether the projection is a <em>count all </em> projection
	 * @return A new {@link BsonProjection} builder
	 */
	static <R> Builder<R> builder(Class<R> projectionType, boolean countAllProjection) {
		return new DefaultBsonProjection.DefaultBuilder<>(projectionType, countAllProjection);
	}

	/**
	 * Builder.
	 *
	 * @param <R> Projection result type
	 */
	public interface Builder<R> {

		/**
		 * Add a projection field.
		 * @param fieldName Projection field name (not null)
		 * @param bson Field projection (not null)
		 * @return this
		 */
		Builder<R> field(String fieldName, Bson bson);

		/**
		 * Add a projection field name.
		 * @param fieldName Projection field name (not null)
		 * @return this
		 */
		Builder<R> field(String fieldName);

		/**
		 * Set the document results converter.
		 * @param converter The converter to set
		 * @return this
		 */
		Builder<R> converter(DocumentConverter<R> converter);

		/**
		 * Set whether this projection include one or more <em>aggregation functions</em>.
		 * @param hasAggregationFunctions whether this projection include one or more <em>aggregation functions</em>
		 * @return this
		 */
		Builder<R> hasAggregationFunctions(boolean hasAggregationFunctions);

		/**
		 * Set that this projection is a count projection on given field name.
		 * @param fieldName Field name
		 * @return this
		 */
		Builder<R> countByField(String fieldName);

		/**
		 * Build the {@link BsonProjection}.
		 * @return A new {@link BsonProjection}
		 */
		BsonProjection<R> build();

	}
}
