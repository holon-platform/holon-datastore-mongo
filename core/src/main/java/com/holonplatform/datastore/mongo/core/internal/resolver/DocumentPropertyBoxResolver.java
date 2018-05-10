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
import com.holonplatform.core.property.PathPropertyBoxAdapter;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.FieldValueExpression;
import com.holonplatform.datastore.mongo.core.expression.PathValueExpression;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
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

		// PropertyBox builder
		final PropertyBox propertyBox = PropertyBox.builder(documentContext.getPropertySet()).invalidAllowed(true)
				.build();

		// Adapter
		final PathPropertyBoxAdapter adapter = PathPropertyBoxAdapter.create(propertyBox);

		// decode PropertyBox
		decodeDocument(documentContext, null, expression.getValue(), adapter);

		return Optional.ofNullable(PropertyBoxValue.create(propertyBox));
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

	private static void decodeDocument(final MongoResolutionContext context, final String parent,
			Map<String, Object> document, PathPropertyBoxAdapter adapter) throws InvalidExpressionException {
		// TODO parallel -> thread-safe PropertyBox
		document.entrySet().stream().forEach(entry -> {
			decodeDocumentField(context, adapter, parent, entry.getKey(), entry.getValue());
		});
	}

	private static void decodeDocumentField(MongoResolutionContext context, PathPropertyBoxAdapter adapter,
			String parent, String name, Object value) throws InvalidExpressionException {
		if (value != null && Map.class.isAssignableFrom(value.getClass())) {
			// nested value
			Map<String, Object> nested = (Map<String, Object>) value;

			decodeDocument(context.childContext(), composeFieldPath(parent, name), nested, adapter);
		} else {
			PathValueExpression e = context.resolveOrFail(FieldValueExpression.create(name, value),
					PathValueExpression.class);
			adapter.setValue(e.getPath(), e.getValue());
		}

	}

	private static String composeFieldPath(String parent, String name) {
		return (parent == null) ? name : parent + "." + name;
	}

	/*
	 * private static List<String> getPathNameHierarchy(Path<?> path) { final String pathName = path.relativeName(); if
	 * (pathName == null) { return Collections.emptyList(); } if (pathName.indexOf('.') < 1) { return
	 * Collections.singletonList(pathName); } return Arrays.asList(pathName.split("\\.")); } private static <P extends
	 * Path & Property> Optional<Object> deserializePath(MongoDocumentContext context, final Map<String, Object>
	 * document, P path) throws InvalidExpressionException { final List<String> pathNames = getPathNameHierarchy(path);
	 * if (!pathNames.isEmpty()) { return getDocumentNode(document, pathNames) .flatMap(n -> decodeField(context, n,
	 * path, pathNames.get(pathNames.size() - 1))); } return Optional.empty(); } private static Optional<Map<String,
	 * Object>> getDocumentNode(final Map<String, Object> document, List<String> pathNames) { Map<String, Object>
	 * currentNode = document; for (String name : pathNames) { if (currentNode == null) { break; } Object value =
	 * currentNode.get(name); // TODO error otherwise? if (value != null &&
	 * Map.class.isAssignableFrom(value.getClass())) { currentNode = (Map<String, Object>) value; } else { return
	 * Optional.empty(); } } return Optional.ofNullable(currentNode); } private static <P extends Path & Property>
	 * Optional<Object> decodeField(MongoDocumentContext context, final Map<String, Object> document, P path, String
	 * fieldName) throws InvalidExpressionException { Object value = document.get(fieldName); // check PropertyBox type
	 * if (PropertyBox.class.isAssignableFrom(path.getType()) && value != null) { // get the PropertySet final
	 * PropertySet<?> propertySet = path.getConfiguration() .getParameter(PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE)
	 * .orElseThrow(() -> new InvalidExpressionException( "Failed to deserialize PropertyBox type path [" + path +
	 * "] for document field [" + fieldName + "]: missing PropertySet. Check the property configuration attribute [" +
	 * PropertySet.PROPERTY_CONFIGURATION_ATTRIBUTE.getKey() + "]")); // TODO Map<String, Object> nested = (Map<String,
	 * Object>) value; return Optional.ofNullable(decodePropertyBox(context.documentContext(propertySet, false),
	 * nested)); } // decode document value return Optional.ofNullable(decodeValue(context, path, value)); } // TODO
	 * private static <P extends Path & Property> Object decodeValue(MongoDocumentContext context, String fieldName,
	 * Object value) throws InvalidExpressionException { return context.resolveOrFail(FieldValueExpression.create(path,
	 * value), PathValueExpression.class).getValue(); }
	 */

}
