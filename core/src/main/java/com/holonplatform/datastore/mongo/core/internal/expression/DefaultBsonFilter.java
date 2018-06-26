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
package com.holonplatform.datastore.mongo.core.internal.expression;

import java.util.Optional;

import org.bson.conversions.Bson;

import com.holonplatform.datastore.mongo.core.expression.BsonFilter;

/**
 * Default {@link BsonFilter} implementation.
 *
 * @since 5.2.0
 */
public class DefaultBsonFilter implements BsonFilter {

	private final Bson expression;
	private final FilterAggregationPipeline pipeline;

	public DefaultBsonFilter(Bson expression) {
		this(expression, null);
	}

	public DefaultBsonFilter(FilterAggregationPipeline pipeline) {
		this((pipeline != null) ? pipeline.getMatch() : null, pipeline);
	}

	public DefaultBsonFilter(Bson expression, FilterAggregationPipeline pipeline) {
		super();
		this.expression = expression;
		this.pipeline = pipeline;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonFilter#getExpression()
	 */
	@Override
	public Bson getExpression() {
		return expression;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonFilter#getPipeline()
	 */
	@Override
	public Optional<FilterAggregationPipeline> getPipeline() {
		return Optional.ofNullable(pipeline);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getExpression() == null) {
			throw new InvalidExpressionException("Null filter expresion");
		}
	}

}
