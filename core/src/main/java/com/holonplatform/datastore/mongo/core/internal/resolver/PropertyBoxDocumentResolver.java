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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Priority;

import org.bson.Document;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.Path;
import com.holonplatform.core.property.CollectionProperty;
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
import com.holonplatform.datastore.mongo.core.internal.document.MongoPropertySetSerializationTreeResolver;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;
import com.holonplatform.json.model.PropertySetSerializationNode;
import com.holonplatform.json.model.PropertySetSerializationTree;

/**
 * Resolver to resolve a {@link PropertyBoxValue} into a {@link DocumentValue} expression.
 *
 * @since 5.2.0
 */
@Priority(Integer.MAX_VALUE)
public enum PropertyBoxDocumentResolver implements MongoExpressionResolver<PropertyBoxValue, DocumentValue> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<DocumentValue> resolve(PropertyBoxValue expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// get the serialization tree
		final PropertySetSerializationTree tree = MongoPropertySetSerializationTreeResolver.getDefault()
				.resolve(expression.getPropertySet());

		// Document context
		final MongoDocumentContext documentContext = context.documentContext(expression.getPropertySet());

		// Encode document
		final Document document = new Document(encodePropertyBoxNodes(documentContext, expression.getValue(), tree));

		return Optional.of(DocumentValue.create(document));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends PropertyBoxValue> getExpressionType() {
		return PropertyBoxValue.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends DocumentValue> getResolvedType() {
		return DocumentValue.class;
	}

	/**
	 * Encode a set of {@link PropertySetSerializationNode} into a document.
	 * @param context Resolution context
	 * @param propertyBox The PropertyBox to encode
	 * @param nodes The property set serialization nodes
	 * @return The document field-value map
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static Map<String, Object> encodePropertyBoxNodes(MongoResolutionContext context, PropertyBox propertyBox,
			Iterable<PropertySetSerializationNode> nodes) throws InvalidExpressionException {
		return StreamSupport.stream(nodes.spliterator(), false)
				.map(node -> encodePropertyBoxNode(context, propertyBox, node)).map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> u));
	}

	/**
	 * Encode a {@link PropertySetSerializationNode} into a document fragment.
	 * @param context Resolution context
	 * @param propertyBox The PropertyBox to encode
	 * @param node The property set serialization node
	 * @return The document fragment field-value map
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static Map<String, Object> encodePropertyBoxNode(MongoResolutionContext context, PropertyBox propertyBox,
			PropertySetSerializationNode node) throws InvalidExpressionException {
		return isValidNodeProperty(node).map(p -> encodeProperty(context, propertyBox, p, node.getName()))
				.orElseGet(() -> {
					// nested
					Map<String, Object> nested = encodePropertyBoxNodes(context.childContext(), propertyBox,
							node.getChildren());
					return Collections.singletonMap(node.getName(), nested);
				});
	}

	/**
	 * Checks if a {@link PropertySetSerializationNode} property is valid, i.e. it is a {@link Path} type property.
	 * @param <T> Property type
	 * @param <P> Path & Property type
	 * @param node The node to check
	 * @return If valid, returns the property as a {@link Path} and {@link Property} type, otherwise an empty Optional
	 *         is returned
	 */
	@SuppressWarnings("unchecked")
	private static <T, P extends Path<T> & Property<T>> Optional<P> isValidNodeProperty(
			PropertySetSerializationNode node) {
		return node.getProperty().filter(p -> Path.class.isAssignableFrom(p.getClass())).map(p -> (P) p);
	}

	/**
	 * Encode a PropertyBox property into a field name and value pair.
	 * @param <T> Property type
	 * @param <P> Path & Property type
	 * @param context Resolution context
	 * @param propertyBox PropertyBox value
	 * @param property The property to encode
	 * @param name The path name
	 * @return Encoded field name and value
	 * @throws InvalidExpressionException If an error occurred
	 */
	@SuppressWarnings("unchecked")
	private static <T, P extends Path<T> & Property<T>> Map<String, Object> encodeProperty(
			MongoResolutionContext context, final PropertyBox propertyBox, P property, String name)
			throws InvalidExpressionException {
		final T value = propertyBox.getValue(property);
		if (value == null) {
			return Collections.emptyMap();
		}
		try {
			// resolve field name
			String fieldName = context.resolveOrFail(Path.of(name, Object.class), FieldName.class).getFieldName();
			// resolve field value
			final Object fieldValue;
			if (PropertyBox.class.isAssignableFrom(property.getType())) {
				// nested PropertyBox
				final PropertyBox pb = (PropertyBox) value;
				fieldValue = context
						.documentContext(property.getConfiguration()
								.getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE).orElse(pb), false)
						.resolveOrFail(PropertyBoxValue.create(pb), DocumentValue.class).getValue();
			} else if (CollectionProperty.class.isAssignableFrom(property.getClass())
					&& PropertyBox.class.isAssignableFrom(((CollectionProperty<?, ?>) property).getElementType())
					&& Collection.class.isAssignableFrom(value.getClass())) {
				final Collection<PropertyBox> values = (Collection<PropertyBox>) value;
				if (values.isEmpty()) {
					return Collections.emptyMap();
				}
				fieldValue = new ArrayList<Document>(values.size());
				for (PropertyBox pb : values) {
					Document doc = context
							.documentContext(
									property.getConfiguration()
											.getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE).orElse(pb),
									false)
							.resolveOrFail(PropertyBoxValue.create(pb), DocumentValue.class).getValue();
					if (doc != null) {
						((List<Document>) fieldValue).add(doc);
					}
				}
			} else {
				fieldValue = context
						.resolveOrFail(
								Value.create(value, property, property.getConfiguration()
										.getParameter(EnumCodecStrategy.CONFIG_PROPERTY).orElse(null)),
								FieldValue.class)
						.getValue();
			}
			return Collections.singletonMap(fieldName, fieldValue);
		} catch (Exception e) {
			throw new InvalidExpressionException(
					"Failed to encode Property [" + property + "] using field name [" + name + "]", e);
		}
	}

}
