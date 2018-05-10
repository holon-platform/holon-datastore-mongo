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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Priority;

import org.bson.Document;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.Path;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.FieldValueExpression;
import com.holonplatform.datastore.mongo.core.expression.PathValueExpression;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
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

	private static Map<String, Object> encodePropertyBoxNodes(MongoResolutionContext context, PropertyBox propertyBox,
			Iterable<PropertySetSerializationNode> nodes) throws InvalidExpressionException {
		return StreamSupport.stream(nodes.spliterator(), true)
				.map(node -> encodePropertyBoxNode(context, propertyBox, node)).map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
	}

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

	@SuppressWarnings("unchecked")
	private static <T, P extends Path<T> & Property<T>> Optional<P> isValidNodeProperty(
			PropertySetSerializationNode node) {
		return node.getProperty().filter(p -> Path.class.isAssignableFrom(p.getClass())).map(p -> (P) p);
	}

	private static <T, P extends Path<T> & Property<T>> Map<String, Object> encodeProperty(
			MongoResolutionContext context, final PropertyBox propertyBox, P property, String name)
			throws InvalidExpressionException {
		try {
			// resolve
			FieldValueExpression fieldValue = context.resolveOrFail(
					PathValueExpression.create(property, propertyBox.getValue(property)), FieldValueExpression.class);
			// check id
			if (MongoDocumentContext.isDocumentContext(context).flatMap(ctx -> ctx.isDocumentIdPath(property))
					.isPresent()) {
				return Collections.singletonMap(fieldValue.getFieldName(), fieldValue.getValue());
			}
			return Collections.singletonMap(name, fieldValue.getValue());
		} catch (Exception e) {
			throw new InvalidExpressionException(
					"Failed to encode Property [" + property + "] using field name [" + name + "]", e);
		}
	}

}
