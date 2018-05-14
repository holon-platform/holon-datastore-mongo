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

import java.util.Map;
import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.Path;
import com.holonplatform.core.property.PathPropertyBoxAdapter;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.expression.PathValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentPathMatcher;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * @author BODSI08
 *
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum DocumentPropertyBoxResolver implements MongoExpressionResolver<DocumentValue, PropertyBoxValue> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<PropertyBoxValue> resolve(DocumentValue expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// Document context
		final MongoDocumentContext documentContext = MongoDocumentContext.isDocumentContext(context).orElseThrow(
				() -> new InvalidExpressionException("The resolution context must be a MongoDocumentContext"));

		return Optional.ofNullable(PropertyBoxValue.create(
				decodePropertyBox(documentContext, null, expression.getValue(), documentContext.getPropertySet())));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends DocumentValue> getExpressionType() {
		return DocumentValue.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends PropertyBoxValue> getResolvedType() {
		return PropertyBoxValue.class;
	}

	private static PropertyBox decodePropertyBox(final MongoResolutionContext context, final String parent,
			Map<String, Object> document, PropertySet<?> propertySet) throws InvalidExpressionException {
		// PropertyBox builder
		final PropertyBox propertyBox = PropertyBox.builder(propertySet).invalidAllowed(true).build();

		// Adapter
		final PathPropertyBoxAdapter adapter = PathPropertyBoxAdapter.builder(propertyBox)
				.pathMatcher(DocumentPathMatcher.INSTANCE).build();

		// decode
		decodeDocument(context, parent, document, adapter);

		return propertyBox;
	}

	private static void decodeDocument(final MongoResolutionContext context, final String parent,
			Map<String, Object> document, PathPropertyBoxAdapter adapter) throws InvalidExpressionException {
		document.entrySet().stream().forEach(entry -> {
			decodeDocumentField(context, adapter, parent, entry.getKey(), entry.getValue());
		});
	}

	private static void decodeDocumentField(MongoResolutionContext context, PathPropertyBoxAdapter adapter,
			String parent, String name, Object value) throws InvalidExpressionException {
		if (value != null && Map.class.isAssignableFrom(value.getClass())) {
			// nested value
			Map<String, Object> nested = (Map<String, Object>) value;
			// full path
			final String path = composeFieldPath(parent, name);
			// decode property using full path name
			decodeDocument(context.childContext(), path, nested, adapter);
			// check PropertyBox type property into which to decode the nested map
			final Path<?> nestedPath = Path.of(path, Object.class);
			adapter.getProperty(nestedPath).filter(p -> PropertyBox.class.isAssignableFrom(p.getType()))
					.ifPresent(p -> {
						final PropertySet<?> propertySet = p.getConfiguration()
								.getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE)
								.orElseThrow(() -> new InvalidExpressionException(
										"Failed to deserialize PropertyBox type path [" + path
												+ "]: missing PropertySet. Check property configuration attribute ["
												+ PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE.getKey() + "]"));
						PropertyBox pb = decodePropertyBox(context.childContext(), parent, nested, propertySet);
						adapter.setValue((Path) nestedPath, pb);
					});
		} else {
			// resolve Path
			final Path<?> path = context.resolveOrFail(FieldName.create(name), Path.class);

			adapter.getProperty(path).ifPresent(p -> {
				// resolve value
				Object resolvedValue = context
						.resolveOrFail(FieldValue.create(value, p), PathValue.class).getValue();
				adapter.setValue((Path) path, resolvedValue);
			});
		}

	}

	private static String composeFieldPath(String parent, String name) {
		return (parent == null) ? name : parent + "." + name;
	}

}
