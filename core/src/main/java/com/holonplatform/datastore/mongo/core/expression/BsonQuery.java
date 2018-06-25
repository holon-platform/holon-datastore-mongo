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

import java.util.Optional;

import com.holonplatform.core.Expression;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultBsonQuery;

/**
 * MongoDB query expression.
 *
 * @since 5.2.0
 */
public interface BsonQuery extends Expression {

	/**
	 * Get the query definition.
	 * @return The query definition
	 */
	BsonQueryDefinition getDefinition();

	/**
	 * Get the query projection.
	 * @return Optional query projection
	 */
	Optional<BsonProjection<?>> getProjection();

	/**
	 * If the query is a <em>distinct</em> field value query, get the field name.
	 * @return Distinct field name, or empty if not a distinct field value query
	 */
	Optional<String> getDistinctFieldName();

	/**
	 * Get a new {@link BsonQuery} builder.
	 * @param definition Query definition
	 * @return A new {@link BsonQuery} builder
	 */
	static Builder builder(BsonQueryDefinition definition) {
		return new DefaultBsonQuery.DefaultBuilder(definition);
	}

	/**
	 * {@link BsonQuery} builder.
	 */
	public interface Builder {

		/**
		 * Set the query projection.
		 * @param projection The query projection
		 * @return this
		 */
		Builder projection(BsonProjection<?> projection);

		/**
		 * Mark the query as a <em>distinct</em> field value query.
		 * @param fieldName Distinct field name (not null)
		 * @return this
		 */
		Builder distinct(String fieldName);

		/**
		 * Build the {@link BsonQuery} instance.
		 * @return The {@link BsonQuery} instance
		 */
		BsonQuery build();

	}

}
