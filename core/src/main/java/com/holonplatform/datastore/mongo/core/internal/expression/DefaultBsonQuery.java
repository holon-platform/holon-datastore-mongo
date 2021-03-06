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

import com.holonplatform.datastore.mongo.core.expression.BsonProjection;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition;

/**
 * Default {@link BsonQuery} implementation.
 *
 * @since 5.2.0
 */
public class DefaultBsonQuery implements BsonQuery {

	private final BsonQueryDefinition definition;

	private BsonProjection<?> projection;

	private String distinctFieldName;

	public DefaultBsonQuery(BsonQueryDefinition definition) {
		super();
		this.definition = definition;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQuery#getDefinition()
	 */
	@Override
	public BsonQueryDefinition getDefinition() {
		return definition;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQuery#getProjection()
	 */
	@Override
	public Optional<BsonProjection<?>> getProjection() {
		return Optional.ofNullable(projection);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonQuery#getDistinctFieldName()
	 */
	@Override
	public Optional<String> getDistinctFieldName() {
		return Optional.ofNullable(distinctFieldName);
	}

	/**
	 * Set the query projection.
	 * @param projection the projection to set
	 */
	public void setProjection(BsonProjection<?> projection) {
		this.projection = projection;
	}

	/**
	 * Set the query as distinct.
	 * @param fieldName Field name
	 */
	public void setDistinct(String fieldName) {
		this.distinctFieldName = fieldName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getDefinition() == null) {
			throw new InvalidExpressionException("Null query definition");
		}
	}

	public static class DefaultBuilder implements Builder {

		private final DefaultBsonQuery instance;

		public DefaultBuilder(BsonQueryDefinition definition) {
			super();
			this.instance = new DefaultBsonQuery(definition);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.expression.MongoQuery.Builder#projection(org.bson.conversions.Bson)
		 */
		@Override
		public Builder projection(BsonProjection<?> projection) {
			instance.setProjection(projection);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.BsonQuery.Builder#distinct(java.lang.String)
		 */
		@Override
		public Builder distinct(String fieldName) {
			instance.setDistinct(fieldName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoQuery.Builder#build()
		 */
		@Override
		public BsonQuery build() {
			return instance;
		}

	}

}
