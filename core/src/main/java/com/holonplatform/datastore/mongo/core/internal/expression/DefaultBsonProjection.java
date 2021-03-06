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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.expression.BsonProjection;

/**
 * Default {@link BsonProjection} implementation.
 *
 * @param <R> Projection result type
 *
 * @since 5.2.0
 */
public class DefaultBsonProjection<R> implements BsonProjection<R> {

	private final Class<R> projectionType;

	private final boolean countAllProjection;

	private String countByField;

	private boolean hasAggregationFunctions = false;

	private DocumentConverter<R> converter;

	private final Map<String, Bson> fields = new LinkedHashMap<>();

	public DefaultBsonProjection(Class<R> projectionType) {
		this(projectionType, false);
	}

	public DefaultBsonProjection(Class<R> projectionType, boolean countAllProjection) {
		super();
		this.projectionType = projectionType;
		this.countAllProjection = countAllProjection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.TypedExpression#getType()
	 */
	@Override
	public Class<? extends R> getType() {
		return projectionType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonProjection#isCountAllProjection()
	 */
	@Override
	public boolean isCountAllProjection() {
		return countAllProjection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonProjection#isCountFieldProjection()
	 */
	@Override
	public Optional<String> isCountFieldProjection() {
		return Optional.ofNullable(countByField);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonProjection#hasAggregationFunctions()
	 */
	@Override
	public boolean hasAggregationFunctions() {
		return hasAggregationFunctions;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonProjection#getFields()
	 */
	@Override
	public Map<String, Bson> getFields() {
		return Collections.unmodifiableMap(fields);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonProjection#getFieldNames()
	 */
	@Override
	public List<String> getFieldNames() {
		return getFields().entrySet().stream().map(entry -> entry.getKey()).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonProjection#getFieldProjections()
	 */
	@Override
	public List<Bson> getFieldProjections() {
		return getFields().entrySet().stream().map(entry -> {
			if (entry.getValue() == null) {
				return new Document(entry.getKey(), Integer.valueOf(1));
			}
			return new Document(entry.getKey(), entry.getValue());
		}).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoProjection#getConverter()
	 */
	@Override
	public Optional<DocumentConverter<R>> getConverter() {
		return Optional.ofNullable(converter);
	}

	/**
	 * Add a projection field name.
	 * @param fieldName Field name (not null)
	 * @param bson Bson representation (not null)
	 */
	public void addField(String fieldName, Bson bson) {
		ObjectUtils.argumentNotNull(fieldName, "Field name must be not null");
		this.fields.put(fieldName, bson);
	}

	/**
	 * Set the projection result converter.
	 * @param converter the converter to set
	 */
	public void setConverter(DocumentConverter<R> converter) {
		this.converter = converter;
	}

	/**
	 * Set whether this projection include one or more <em>aggregation functions</em>.
	 * @param hasAggregationFunctions whether this projection include one or more <em>aggregation functions</em>
	 */
	public void setHasAggregationFunctions(boolean hasAggregationFunctions) {
		this.hasAggregationFunctions = hasAggregationFunctions;
	}

	/**
	 * Set the field for which to count results
	 * @param countByField the field to set
	 */
	public void setCountByField(String countByField) {
		this.countByField = countByField;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getType() == null) {
			throw new InvalidExpressionException("Null projection type");
		}
	}

	public static class DefaultBuilder<T> implements Builder<T> {

		private final DefaultBsonProjection<T> instance;

		public DefaultBuilder(Class<T> projectionType) {
			super();
			this.instance = new DefaultBsonProjection<>(projectionType);
		}

		public DefaultBuilder(Class<T> projectionType, boolean countAllProjection) {
			super();
			this.instance = new DefaultBsonProjection<>(projectionType, countAllProjection);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.BsonProjection.Builder#field(java.lang.String,
		 * org.bson.conversions.Bson)
		 */
		@Override
		public Builder<T> field(String fieldName, Bson bson) {
			instance.addField(fieldName, bson);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.BsonProjection.Builder#field(java.lang.String)
		 */
		@Override
		public Builder<T> field(String fieldName) {
			return field(fieldName, null);
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoProjection.Builder#converter(com.holonplatform.
		 * datastore.mongo.core.document.DocumentConverter)
		 */
		@Override
		public Builder<T> converter(DocumentConverter<T> converter) {
			instance.setConverter(converter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.expression.BsonProjection.Builder#hasAggregationFunctions(boolean)
		 */
		@Override
		public Builder<T> hasAggregationFunctions(boolean hasAggregationFunctions) {
			instance.setHasAggregationFunctions(hasAggregationFunctions);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.BsonProjection.Builder#countByField(java.lang.String)
		 */
		@Override
		public Builder<T> countByField(String fieldName) {
			instance.setCountByField(fieldName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoProjection.Builder#build()
		 */
		@Override
		public BsonProjection<T> build() {
			return instance;
		}

	}

}
