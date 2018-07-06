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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.Path;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.property.CollectionProperty;
import com.holonplatform.core.property.PathPropertyBoxAdapter;
import com.holonplatform.core.property.Property;
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
 * Resolver to resolve a {@link DocumentValue} into a {@link PropertyBoxValue}.
 *
 * @since 5.2.0
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

		return Optional
				.ofNullable(PropertyBoxValue.create(decodePropertyBox(documentContext, null, expression.getValue())));
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
	 * @return Decoded PropertyBox instance
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static PropertyBox decodePropertyBox(final MongoDocumentContext context, final String parent,
			Map<String, Object> document) throws InvalidExpressionException {
		// PropertyBox builder
		final PropertyBox propertyBox = PropertyBox.builder(context.getPropertySet()).invalidAllowed(true).build();

		// Adapter
		final PathPropertyBoxAdapter adapter = PathPropertyBoxAdapter.builder(propertyBox)
				.pathMatcher(DocumentPathMatcher.INSTANCE).build();

		// decode
		decodeDocument(context, parent, document, propertyBox, adapter);

		return propertyBox;
	}

	/**
	 * Decode given document into a {@link PropertyBox} using given adapter to match the document field names and
	 * property paths.
	 * @param context Resolution context
	 * @param parent Optional parent field name expression
	 * @param document Document to decode
	 * @param propertyBox PropertyBox to use to collect the decoded values
	 * @param adapter PropertyBox adapter
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static void decodeDocument(final MongoDocumentContext context, final String parent,
			Map<String, Object> document, PropertyBox propertyBox, PathPropertyBoxAdapter adapter)
			throws InvalidExpressionException {
		final List<Property<?>> properties = new ArrayList<>(context.getPropertySet().asList());
		document.entrySet().stream().forEach(entry -> {
			decodeDocumentField(context, propertyBox, adapter, parent, entry.getKey(), entry.getValue())
					.ifPresent(p -> {
						properties.remove(p);
					});
		});
		// check properties with converter and force value set
		for (Property<?> property : properties) {
			if (property.getConverter().isPresent() && propertyBox.contains(property)) {
				propertyBox.setValue(property, null);
			}
		}
	}

	/**
	 * Decode a document field.
	 * @param context Resolution context
	 * @param propertyBox PropertyBox to use to collect the decoded values
	 * @param adapter PropertyBox adapter to use to set the decoded field value
	 * @param parent Optional parent field name expression
	 * @param name Field name
	 * @param value Field value
	 * @return If a matching property into which to decode the field value is found, it is returned
	 * @throws InvalidExpressionException If an error occurred
	 */
	@SuppressWarnings("unchecked")
	private static Optional<Property<?>> decodeDocumentField(MongoDocumentContext context, PropertyBox propertyBox,
			PathPropertyBoxAdapter adapter, String parent, String name, Object value)
			throws InvalidExpressionException {

		// full path
		final String fieldName = composeFieldPath(parent, name);

		// check nested document
		if (value != null && Map.class.isAssignableFrom(value.getClass())) {
			// nested value
			Map<String, Object> nested = (Map<String, Object>) value;
			// decode property using full path name
			decodeDocument(context.documentContext(context.getPropertySet()), fieldName, nested, propertyBox, adapter);
			// check PropertyBox type property into which to decode the nested map
			final Path<?> nestedPath = Path.of(fieldName, Object.class);
			return adapter.getProperty(nestedPath).filter(p -> PropertyBox.class.isAssignableFrom(p.getType()))
					.map(p -> {
						final PropertySet<?> propertySet = p.getConfiguration()
								.getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE)
								.orElseThrow(() -> new InvalidExpressionException(
										"Failed to deserialize PropertyBox type path [" + fieldName
												+ "]: missing PropertySet. Check property configuration attribute ["
												+ PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE.getKey() + "]"));
						PropertyBox pb = decodePropertyBox(context.documentContext(propertySet), parent, nested);
						adapter.setValue((Path) nestedPath, pb);
						return p;
					});
		}

		// resolve Path
		final Path<?> path = context.resolveOrFail(FieldName.create(fieldName), Path.class);

		// check collection of nested documents
		if (value != null && Collection.class.isAssignableFrom(value.getClass()) && !((Collection) value).isEmpty()) {
			Object firstElement = ((Collection) value).iterator().next();
			if (firstElement != null && Map.class.isAssignableFrom(firstElement.getClass())) {
				// check PropertyBox CollectionProperty type
				CollectionProperty<PropertyBox, Collection<PropertyBox>> collectionProperty = adapter.getProperty(path)
						.filter(p -> CollectionProperty.class.isAssignableFrom(p.getClass()))
						.map(p -> (CollectionProperty<?, ?>) p)
						.filter(cp -> PropertyBox.class.isAssignableFrom(cp.getElementType()))
						.map(cp -> (CollectionProperty<PropertyBox, Collection<PropertyBox>>) cp).orElse(null);
				if (collectionProperty != null) {
					// check property set
					final PropertySet<?> propertySet = collectionProperty.getConfiguration()
							.getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE)
							.orElseThrow(() -> new InvalidExpressionException(
									"Failed to deserialize PropertyBox type path [" + fieldName
											+ "]: missing PropertySet. Check property configuration attribute ["
											+ PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE.getKey() + "]"));
					// collection
					final int size = ((Collection) value).size();
					boolean isList = adapter.getProperty(path).filter(p -> List.class.isAssignableFrom(p.getType()))
							.orElse(null) != null;
					Collection<PropertyBox> propertyValues = isList ? new ArrayList<>(size) : new HashSet<>(size);
					for (Object element : ((Collection) value)) {
						// decode
						PropertyBox pb = decodePropertyBox(context.documentContext(propertySet), parent,
								(Map<String, Object>) element);
						if (pb != null) {
							propertyValues.add(pb);
						}
					}
					propertyBox.setValue(collectionProperty, propertyValues);
					return Optional.of(collectionProperty);
				}
			}
		}

		// check alias
		if (context.maybeAliasName(name)) {
			Optional<TypedExpression<Object>> aliasExpression = context.getExpression(name);
			if (aliasExpression.isPresent()) {
				final TypedExpression<?> exp = aliasExpression.get();
				Object resolvedValue = context.resolveOrFail(FieldValue.create(value, exp), Value.class).getValue();
				// check property
				if (Property.class.isAssignableFrom(exp.getClass())) {
					final Property<Object> property = (Property) exp;
					if (propertyBox.contains(property)) {
						propertyBox.setValue(property, resolvedValue);
					}
					return Optional.of(property);
				}
				return Optional.empty();
			}
		}

		// resolve value
		return adapter.getProperty(path).map(p -> {
			Object resolvedValue = context.resolveOrFail(
					FieldValue.create(value, p,
							p.getConfiguration().getParameter(EnumCodecStrategy.CONFIG_PROPERTY).orElse(null)),
					Value.class).getValue();
			adapter.setValue((Path) path, resolvedValue);
			return p;
		});
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
