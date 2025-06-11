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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import jakarta.annotation.Priority;

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

	private static final Object NO_VALUE = "";

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<DocumentValue> resolve(PropertyBoxValue expression, MongoResolutionContext<?> context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// Document context
		final MongoDocumentContext<?> documentContext = MongoDocumentContext.isDocumentContext(context)
				.orElse(context.documentContext(expression.getPropertySet()));

		// Encode document
		final Map<String, Object> nodes = encodePropertyBox(documentContext, expression.getValue(), null,
				documentContext.isForUpdate());

		if (documentContext.isForUpdate()) {
			// update operators
			final Map<String, Object> unset = new HashMap<>();
			final Map<String, Object> set = processDocumentForUpdate(nodes, unset);

			Document document = new Document();
			if (!set.isEmpty()) {
				document.append("$set", set);
			}
			if (!unset.isEmpty()) {
				document.append("$unset", unset);
			}
			return Optional.of(DocumentValue.create(document));
		}

		return Optional.of(DocumentValue.create(new Document(nodes)));
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
	 * Process given document to create a $set/$unset update operation model.
	 * @param document Document to process
	 * @param unset $unset fields global map
	 * @return The processed document
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> processDocumentForUpdate(Map<String, Object> document,
			Map<String, Object> unset) {
		final Map<String, Object> set = new HashMap<>(document.size());
		for (Entry<String, Object> entry : document.entrySet()) {
			if (entry.getValue() == null || NO_VALUE.equals(entry.getValue())) {
				unset.put(entry.getKey(), "");
			} else {
				if (Map.class.isAssignableFrom(entry.getValue().getClass())) {
					set.put(entry.getKey(), processDocumentForUpdate((Map<String, Object>) entry.getValue(), unset));
				} else {
					set.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return set;
	}

	private static Document encodePropertyBox(MongoDocumentContext<?> context, PropertyBox propertyBox,
			String parentPath, boolean forUpdate) throws InvalidExpressionException {
		// get the serialization tree
		final PropertySetSerializationTree nodes = MongoPropertySetSerializationTreeResolver.getDefault()
				.resolve(context.getPropertySet());
		return new Document(encodePropertyBoxNodes(context, propertyBox, nodes, parentPath, forUpdate));
	}

	/**
	 * Encode a set of {@link PropertySetSerializationNode} into a document.
	 * @param context Resolution context
	 * @param propertyBox The PropertyBox to encode
	 * @param nodes The property set serialization nodes
	 * @param parentPath Parent nodes path
	 * @return The document field-value map
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static Map<String, Object> encodePropertyBoxNodes(MongoDocumentContext<?> context, PropertyBox propertyBox,
			Iterable<PropertySetSerializationNode> nodes, String parentPath, boolean forUpdate)
			throws InvalidExpressionException {
		return StreamSupport.stream(nodes.spliterator(), false)
				.map(node -> encodePropertyBoxNode(context, propertyBox, node, parentPath, forUpdate))
				.map(Map::entrySet).flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> u));
	}

	/**
	 * Encode a {@link PropertySetSerializationNode} into a document fragment.
	 * @param context Resolution context
	 * @param propertyBox The PropertyBox to encode
	 * @param node The property set serialization node
	 * @param parentPath Parent nodes path
	 * @return The document fragment field-value map
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static Map<String, Object> encodePropertyBoxNode(MongoDocumentContext<?> context, PropertyBox propertyBox,
			PropertySetSerializationNode node, String parentPath, boolean forUpdate) throws InvalidExpressionException {
		return isValidNodeProperty(node)
				.map(p -> encodeProperty(context, propertyBox, p, node.getName(), parentPath, forUpdate))
				.orElseGet(() -> {
					// nested
					String parent = composeFieldPath(parentPath, node.getName());
					Map<String, Object> nested = encodePropertyBoxNodes(context, propertyBox, node.getChildren(),
							parent, forUpdate);
					return forUpdate ? nested : Collections.singletonMap(node.getName(), nested);
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
	 * Compose a field path using an optional parent path.
	 * @param parent Optional parent path
	 * @param name Field name
	 * @return Full path
	 */
	private static String composeFieldPath(String parent, String name) {
		return (parent == null) ? name : parent + "." + name;
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
			MongoDocumentContext<?> context, final PropertyBox propertyBox, P property, String name, String parentPath,
			boolean forUpdate) throws InvalidExpressionException {
		final T value = propertyBox.getValue(property);
		if (value == null) {
			if (forUpdate) {
				// exclude document id
				if (context.isDocumentIdProperty(property)) {
					return Collections.emptyMap();
				}
				// resolve field name
				String fieldName = context.resolveOrFail(Path.of(name, Object.class), FieldName.class).getFieldName();
				// return empty value for $unset
				return Collections.singletonMap(composeFieldPath(parentPath, fieldName), NO_VALUE);
			}
			return Collections.emptyMap();
		}
		try {
			// check nested PropertyBox
			if (PropertyBox.class.isAssignableFrom(property.getType())) {
				return encodePropertyBoxTypeProperty(context, property, (PropertyBox) value, name, parentPath,
						forUpdate);
			}
			// check PropertyBox collection property
			if (isPropertyBoxCollectionProperty(property, value)) {
				return encodePropertyBoxTypeCollectionProperty(context, property, (Collection<PropertyBox>) value, name,
						parentPath, forUpdate);
			}
			// resolve field name
			String fieldName = context.resolveOrFail(Path.of(name, Object.class), FieldName.class).getFieldName();
			// resolve field value
			final Object fieldValue = context.resolveOrFail(
					Value.create(value, property,
							property.getConfiguration().getParameter(EnumCodecStrategy.CONFIG_PROPERTY).orElse(null)),
					FieldValue.class).getValue();

			// check document id property
			if (!forUpdate && parentPath == null && fieldValue != null) {
				if (context.isDocumentIdProperty(property)
						&& !MongoDocumentContext.ID_FIELD_NAME.equals(property.relativeName())) {
					// add the default _id field
					Map<String, Object> encoded = new HashMap<>(2);
					encoded.put(fieldName, fieldValue);
					encoded.put(MongoDocumentContext.ID_FIELD_NAME, context.getDocumentIdResolver().encode(value));
					return encoded;
				}
			}
			// check for update
			return Collections.singletonMap(forUpdate ? composeFieldPath(parentPath, fieldName) : fieldName,
					(fieldValue != null) ? fieldValue : NO_VALUE);
		} catch (Exception e) {
			throw new InvalidExpressionException(
					"Failed to encode Property [" + property + "] using field name [" + name + "]", e);
		}
	}

	private static <T, P extends Path<T> & Property<T>> Map<String, Object> encodePropertyBoxTypeProperty(
			MongoDocumentContext<?> context, P property, PropertyBox value, String name, String parentPath,
			boolean forUpdate) throws InvalidExpressionException {
		// resolve field name
		String fieldName = context.resolveOrFail(Path.of(name, Object.class), FieldName.class).getFieldName();
		// encode nested document
		Document encoded = encodePropertyBox(
				context.documentContext(property.getConfiguration()
						.getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE).orElse(value), false),
				value, forUpdate ? composeFieldPath(parentPath, fieldName) : parentPath, forUpdate);
		if (forUpdate) {
			return encoded;
		} else {
			return Collections.singletonMap(fieldName, (encoded != null) ? encoded : NO_VALUE);
		}
	}

	private static <T, P extends Path<T> & Property<T>> boolean isPropertyBoxCollectionProperty(P property, T value) {
		return (CollectionProperty.class.isAssignableFrom(property.getClass())
				&& PropertyBox.class.isAssignableFrom(((CollectionProperty<?, ?>) property).getElementType())
				&& Collection.class.isAssignableFrom(value.getClass()));
	}

	private static <T, P extends Path<T> & Property<T>> Map<String, Object> encodePropertyBoxTypeCollectionProperty(
			MongoDocumentContext<?> context, P property, Collection<PropertyBox> values, String name, String parentPath,
			boolean forUpdate) throws InvalidExpressionException {
		// resolve field name
		String fieldName = context.resolveOrFail(Path.of(name, Object.class), FieldName.class).getFieldName();

		// encode collection elements
		if (values == null || values.isEmpty()) {
			return Collections.emptyMap();
		}
		List<Document> encoded = new ArrayList<>(values.size());
		for (PropertyBox pb : values) {
			Document doc = encodePropertyBox(
					context.documentContext(property.getConfiguration()
							.getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE).orElse(pb), false),
					pb, parentPath, forUpdate);
			if (doc != null) {
				encoded.add(doc);
			}
		}

		return Collections.singletonMap(forUpdate ? composeFieldPath(parentPath, fieldName) : fieldName, encoded);
	}

}
