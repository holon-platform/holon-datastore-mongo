/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.datastore.mongo.core.internal.resolver.projection;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.property.PathPropertySetAdapter;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.query.PropertySetProjection;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.expression.BsonProjection;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * Property set projection resolver.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE - 100)
public enum PropertySetProjectionResolver implements MongoExpressionResolver<PropertySetProjection, BsonProjection> {

	/**
	 * Singleton instance
	 */
	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends PropertySetProjection> getExpressionType() {
		return PropertySetProjection.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends BsonProjection> getResolvedType() {
		return BsonProjection.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<BsonProjection> resolve(PropertySetProjection expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		final BsonProjection.Builder builder = BsonProjection.builder(PropertyBox.class);

		// projection fields
		PathPropertySetAdapter adapter = PathPropertySetAdapter.create(expression.getPropertySet());
		builder.fields(adapter.paths().map(path -> context.resolveOrFail(path, FieldName.class))
				.map(fn -> fn.getFieldName()).collect(Collectors.toList()));

		// converter
		builder.converter(DocumentConverter.propertyBox(expression.getPropertySet()));

		return Optional.of(builder.build());
	}

}
