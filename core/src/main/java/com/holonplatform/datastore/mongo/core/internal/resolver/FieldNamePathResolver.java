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
package com.holonplatform.datastore.mongo.core.internal.resolver;

import java.util.Optional;

import jakarta.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.Path;
import com.holonplatform.core.property.PathPropertySetAdapter;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * {@link FieldName} to {@link Path} default expression resolver.
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum FieldNamePathResolver implements MongoExpressionResolver<FieldName, Path> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<Path> resolve(FieldName expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// field name
		final String fieldName = expression.getFieldName();

		// check document context or use field name
		return Optional.of(getPathUsingDocumentContext(context, fieldName).orElse(Path.of(fieldName, Object.class)));
	}

	private static Optional<Path> getPathUsingDocumentContext(MongoResolutionContext context, String fieldName) {
		PathPropertySetAdapter adapter = MongoDocumentContext.isDocumentContext(context)
				.map(dc -> PathPropertySetAdapter.create(dc.getPropertySet())).orElse(null);
		if (adapter != null) {
			Optional<Path> path = adapter.getProperty(fieldName).flatMap(p -> adapter.getPath(p)).map(p -> (Path) p);
			if (path.isPresent()) {
				return path;
			}
			// check data path
			return adapter.propertyPaths()
					.filter(pp -> fieldName.equals(pp.getPath().relativeName(p -> p.getDataPath().orElse(p.getName()))))
					.map(pp -> (Path) pp.getPath()).findFirst();
		}
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends FieldName> getExpressionType() {
		return FieldName.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends Path> getResolvedType() {
		return Path.class;
	}

}
