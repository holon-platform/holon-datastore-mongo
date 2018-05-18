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

import org.bson.conversions.Bson;

import com.holonplatform.core.Expression;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultMongoQuery;

/**
 * MongoDB query expression.
 *
 * @since 5.2.0
 */
public interface MongoQuery extends Expression {

	/**
	 * Get the query definition.
	 * @return The query definition
	 */
	MongoQueryDefinition getDefinition();

	/**
	 * Get the {@link QueryOperationType}.
	 * @return the query operation type
	 */
	QueryOperationType getOperationType();

	/**
	 * Get the query projection document.
	 * @return Optional query projection
	 */
	Optional<Bson> getProjection();

	/**
	 * Get the {@link DocumentConverter} to use to convert the query results.
	 * @return Optional document results converter
	 */
	Optional<DocumentConverter<?>> getConverter();

	/**
	 * Get a new {@link MongoQuery} builder.
	 * @param definition Query definition
	 * @return A new {@link MongoQuery} builder
	 */
	static Builder builder(MongoQueryDefinition definition) {
		return new DefaultMongoQuery.DefaultBuilder(definition);
	}

	/**
	 * {@link MongoQuery} builder.
	 */
	public interface Builder {

		/**
		 * Set the query operation type.
		 * @param operationType The query operation type
		 * @return this
		 */
		Builder operationType(QueryOperationType operationType);

		/**
		 * Set the query prjection document.
		 * @param projection The query prjection document
		 * @return this
		 */
		Builder projection(Bson projection);

		/**
		 * Set the query results converter.
		 * @param converter The query results converter
		 * @return this
		 */
		Builder converter(DocumentConverter<?> converter);

		/**
		 * Build the {@link MongoQuery} instance.
		 * @return The {@link MongoQuery} instance
		 */
		MongoQuery build();

	}

}
