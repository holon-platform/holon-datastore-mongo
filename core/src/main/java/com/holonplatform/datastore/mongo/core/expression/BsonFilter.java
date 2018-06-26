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
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultBsonFilter;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultFilterAggregationPipeline;

/**
 * {@link Bson} filter expression.
 *
 * @since 5.2.0
 */
public interface BsonFilter extends Expression {

	/**
	 * Get the filter expression.
	 * @return The filter expression
	 */
	Bson getExpression();

	/**
	 * If the filter must be represented using an aggregation pipeline, returns the pipeline.
	 * @return Optional filter aggregation pipeline
	 */
	Optional<FilterAggregationPipeline> getPipeline();

	/**
	 * Create a new {@link BsonFilter}.
	 * @param expression Filter expression
	 * @return A new {@link BsonFilter}
	 */
	static BsonFilter create(Bson expression) {
		return new DefaultBsonFilter(expression);
	}

	/**
	 * Create a new {@link BsonFilter}.
	 * @param match Filter aggregation pipeline match stage
	 * @param projection Optional filter aggregation pipeline projection stage
	 * @return A new {@link BsonFilter}
	 */
	static BsonFilter create(Bson match, Bson projection) {
		return new DefaultBsonFilter(new DefaultFilterAggregationPipeline(match, projection));
	}

	/**
	 * Filter aggregation pipeline stages.
	 */
	public interface FilterAggregationPipeline {

		/**
		 * Get the match stage.
		 * @return Match stage
		 */
		Bson getMatch();

		/**
		 * Get the projection stage.
		 * @return Optional projection stage
		 */
		Optional<Bson> getProjection();

	}

}
