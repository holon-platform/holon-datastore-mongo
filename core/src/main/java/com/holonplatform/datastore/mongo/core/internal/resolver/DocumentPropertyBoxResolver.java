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
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.expression.Value;
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

	/**
	 * Decode a document into a {@link PropertyBox} value using given property set.
	 * @param context Resolution context
	 * @param parent Optional parent field name expression
	 * @param document Document to decode
	 * @param propertySet Property set to use
	 * @return Decoded PropertyBox instance
	 * @throws InvalidExpressionException If an error occurred
	 */
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

	/**
	 * Decode given document into a {@link PropertyBox} using given adapter to match the document field names and
	 * property paths.
	 * @param context Resolution context
	 * @param parent Optional parent field name expression
	 * @param document Document to decode
	 * @param adapter PropertyBox adapter
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static void decodeDocument(final MongoResolutionContext context, final String parent,
			Map<String, Object> document, PathPropertyBoxAdapter adapter) throws InvalidExpressionException {
		document.entrySet().stream().forEach(entry -> {
			decodeDocumentField(context, adapter, parent, entry.getKey(), entry.getValue());
		});
	}

	/**
	 * Decode a document field.
	 * @param context Resolution context
	 * @param adapter PropertyBox adapter to use to set the decoded field value
	 * @param parent Optional parent field name expression
	 * @param name Field name
	 * @param value Field value
	 * @throws InvalidExpressionException If an error occurred
	 */
	@SuppressWarnings("unchecked")
	private static void decodeDocumentField(MongoResolutionContext context, PathPropertyBoxAdapter adapter,
			String parent, String name, Object value) throws InvalidExpressionException {
		// full path
		final String fieldName = composeFieldPath(parent, name);

		if (value != null && Map.class.isAssignableFrom(value.getClass())) {
			// nested value
			Map<String, Object> nested = (Map<String, Object>) value;
			// decode property using full path name
			decodeDocument(context.childContext(), fieldName, nested, adapter);
			// check PropertyBox type property into which to decode the nested map
			final Path<?> nestedPath = Path.of(fieldName, Object.class);
			adapter.getProperty(nestedPath).filter(p -> PropertyBox.class.isAssignableFrom(p.getType()))
					.ifPresent(p -> {
						final PropertySet<?> propertySet = p.getConfiguration()
								.getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE)
								.orElseThrow(() -> new InvalidExpressionException(
										"Failed to deserialize PropertyBox type path [" + fieldName
												+ "]: missing PropertySet. Check property configuration attribute ["
												+ PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE.getKey() + "]"));
						PropertyBox pb = decodePropertyBox(context.childContext(), parent, nested, propertySet);
						adapter.setValue((Path) nestedPath, pb);
					});
		} else {
			// resolve Path
			final Path<?> path = context.resolveOrFail(FieldName.create(fieldName), Path.class);
			// resolve value
			adapter.getProperty(path).ifPresent(p -> {
				Object resolvedValue = context.resolveOrFail(
						FieldValue.create(value, p,
								p.getConfiguration().getParameter(EnumCodecStrategy.CONFIG_PROPERTY).orElse(null)),
						Value.class).getValue();
				adapter.setValue((Path) path, resolvedValue);
			});
		}

	}

	/**
	 * Compose a field path using an optional parent path.
	 * @param parent Optional parent path
	 * @param name Field name
	 * @return Full path
	 */
	private static String composeFieldPath(String parent, String name) {
		return (parent == null) ? name : parent + "." + name;
	}

}
