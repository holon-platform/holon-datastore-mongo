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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
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

	private QueryOperationType operationType;
	private DocumentConverter<R> converter;

	private final Map<String, TypedExpression<?>> fields = new LinkedHashMap<>();

	public DefaultBsonProjection(Class<R> projectionType) {
		super();
		this.projectionType = projectionType;
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
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoProjection#getFields()
	 */
	@Override
	public List<String> getFields() {
		return new ArrayList<>(fields.keySet());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonProjection#getFieldExpession(java.lang.String)
	 */
	@Override
	public Optional<TypedExpression<?>> getFieldExpression(String fieldName) {
		ObjectUtils.argumentNotNull(fieldName, "Field name must be not null");
		return Optional.ofNullable(fields.get(fieldName));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoProjection#getOperationType()
	 */
	@Override
	public Optional<QueryOperationType> getOperationType() {
		return Optional.ofNullable(operationType);
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
	 * Set the {@link QueryOperationType}.
	 * @param operationType the operation type to set
	 */
	public void setOperationType(QueryOperationType operationType) {
		this.operationType = operationType;
	}

	/**
	 * Add a projection field name.
	 * @param fieldName Field name (not null)
	 * @param expression Optional field expression
	 */
	public void addField(String fieldName, TypedExpression<?> expression) {
		ObjectUtils.argumentNotNull(fieldName, "Field name must be not null");
		this.fields.put(fieldName, expression);
	}

	/**
	 * Set the projection result converter.
	 * @param converter the converter to set
	 */
	public void setConverter(DocumentConverter<R> converter) {
		this.converter = converter;
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

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.expression.MongoProjection.Builder#operationType(com.holonplatform.
		 * datastore.mongo.core.document.QueryOperationType)
		 */
		@Override
		public Builder<T> operationType(QueryOperationType operationType) {
			instance.setOperationType(operationType);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.BsonProjection.Builder#field(java.lang.String)
		 */
		@Override
		public Builder<T> field(String fieldName) {
			return fieldExpression(fieldName, null);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.expression.BsonProjection.Builder#fieldExpression(java.lang.String,
		 * com.holonplatform.core.TypedExpression)
		 */
		@Override
		public Builder<T> fieldExpression(String fieldName, TypedExpression<?> expression) {
			instance.addField(fieldName, expression);
			return this;
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
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoProjection.Builder#build()
		 */
		@Override
		public BsonProjection<T> build() {
			return instance;
		}

	}

}