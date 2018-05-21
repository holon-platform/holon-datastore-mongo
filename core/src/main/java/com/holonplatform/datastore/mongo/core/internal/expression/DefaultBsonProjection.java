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
import java.util.List;
import java.util.Optional;

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
	private List<String> fields;
	private DocumentConverter<R> converter;

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
		return (fields != null) ? Collections.unmodifiableList(fields) : Collections.emptyList();
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
	 * Set the projection field names.
	 * @param fields the fields to set
	 */
	public void setFields(List<String> fields) {
		this.fields = fields;
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
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoProjection.Builder#fields(java.util.List)
		 */
		@Override
		public Builder<T> fields(List<String> fields) {
			instance.setFields(fields);
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
