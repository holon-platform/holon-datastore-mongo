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
package com.holonplatform.datastore.mongo.core.internal.document;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PathPropertySetAdapter;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.document.PropertyBoxConverter;
import com.holonplatform.json.exceptions.JsonDeserializationException;
import com.holonplatform.json.exceptions.JsonSerializationException;
import com.holonplatform.json.model.PropertySetSerializationNode;
import com.holonplatform.json.model.PropertySetSerializationTree;

/**
 * Default {@link PropertyBoxConverter} implementation.
 *
 * @since 5.2.0
 */
public enum DefaultPropertyBoxConverter implements PropertyBoxConverter {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.PropertyBoxConverter#encode(com.holonplatform.core.property.
	 * PropertyBox)
	 */
	@Override
	public Document encode(PropertyBox value) {
		if (value != null) {
			final Document document = new Document();
			// get the serialization tree
			final PropertySetSerializationTree tree = MongoPropertySetSerializationTreeResolver.getDefault()
					.resolve(value);
			// encode
			encodePropertyBoxNodes(document, value, tree);
			return document;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.PropertyBoxConverter#decode(org.bson.Document,
	 * com.holonplatform.core.property.PropertySet)
	 */
	@Override
	public PropertyBox decode(Document document, PropertySet<?> propertySet) {
		return decodePropertyBox(document, propertySet);
	}

	// ------- encode

	private static void encodePropertyBoxNodes(Document document, PropertyBox propertyBox,
			Iterable<PropertySetSerializationNode> nodes) throws JsonSerializationException {
		for (PropertySetSerializationNode node : nodes) {
			encodePropertyBoxNode(document, propertyBox, node);
		}
	}

	private static void encodePropertyBoxNode(Document document, PropertyBox propertyBox,
			PropertySetSerializationNode node) throws JsonSerializationException {
		if (node.getProperty().isPresent()) {
			encodePropertyBoxProperty(document, propertyBox, node.getProperty().get(), node.getName());
		} else {
			try {
				// nested
				final Document nested = new Document();
				document.put(node.getName(), nested);
				encodePropertyBoxNodes(nested, propertyBox, node.getChildren());
			} catch (JsonSerializationException se) {
				throw se;
			} catch (Exception e) {
				throw new JsonSerializationException(
						"Failed to encode PropertyBox [" + propertyBox + "] for field name [" + node.getName() + "]",
						e);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void encodePropertyBoxProperty(final Document document, PropertyBox propertyBox,
			Property<?> property, String name) throws JsonSerializationException {
		try {
			propertyBox.getValueIfPresent(property).ifPresent(value -> {
				document.put(name, ((Property) property).getConvertedValue(value));
			});
		} catch (Exception e) {
			throw new JsonSerializationException(
					"Failed to encode Property [" + property + "] using field name [" + name + "]", e);
		}
	}

	// ------- decode

	private static PropertyBox decodePropertyBox(Map<String, Object> document, PropertySet<?> propertySet) {
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		if (document != null) {
			PropertyBox.Builder builder = PropertyBox.builder(propertySet).invalidAllowed(true);

			final PathPropertySetAdapter adapter = PathPropertySetAdapter.create(propertySet);
			for (Property property : propertySet) {
				Optional<Path> propertyPath = adapter.getPath(property);
				propertyPath.ifPresent(path -> {
					Optional<?> value = deserializePath(document, (Path & Property) path);
					if (value.isPresent()) {
						builder.setIgnoreReadOnly(property, value.get());
					}
				});
			}

			return builder.build();
		}
		return null;
	}

	private static List<String> getPathNameHierarchy(Path<?> path) {
		final String pathName = path.relativeName();
		if (pathName == null) {
			return Collections.emptyList();
		}
		if (pathName.indexOf('.') < 1) {
			return Collections.singletonList(pathName);
		}
		return Arrays.asList(pathName.split("\\."));
	}

	private static <T, P extends Path<T> & Property<T>> Optional<T> deserializePath(final Map<String, Object> document,
			P path) throws JsonDeserializationException {
		final List<String> pathNames = getPathNameHierarchy(path);
		if (!pathNames.isEmpty()) {
			return getDocumentNode(document, pathNames)
					.flatMap(n -> decodeField(n, path, pathNames.get(pathNames.size() - 1)));
		}
		return Optional.empty();
	}

	private static Optional<Map<String, Object>> getDocumentNode(final Map<String, Object> document,
			List<String> pathNames) {
		Map<String, Object> currentNode = document;
		for (String name : pathNames) {
			if (currentNode == null) {
				break;
			}
			Object value = currentNode.get(name);
			// TODO error otherwise?
			if (value != null && Map.class.isAssignableFrom(value.getClass())) {
				currentNode = (Map<String, Object>) value;
			} else {
				return Optional.empty();
			}
		}
		return Optional.ofNullable(currentNode);
	}

	private static <T, P extends Path<T> & Property<T>> Optional<T> decodeField(final Map<String, Object> document,
			P path, String fieldName) throws JsonDeserializationException {
		Object value = document.get(fieldName);
		if (value != null) {
			if (PropertyBox.class.isAssignableFrom(path.getType())) {

				// TODO
				Map<String, Object> nested = (Map<String, Object>) value;

				return Optional.ofNullable((T) decodePropertyBox(nested, path.getConfiguration()
						.getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE)
						.orElseThrow(() -> new JsonDeserializationException(
								"Failed to deserialize PropertyBox type path [" + path + "] for JSON field ["
										+ fieldName + "]: missing PropertySet. Check property configuration attribute ["
										+ PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE.getKey() + "]"))));
			} else {
				return Optional.ofNullable(decodeValue(path, value));
			}
		}
		return Optional.empty();
	}

	// TODO
	private static <T, P extends Path<T> & Property<T>> T decodeValue(P path, Object value)
			throws JsonDeserializationException {
		// TODO convert from model and deserialize correctly
		return (T) value;
	}

}
