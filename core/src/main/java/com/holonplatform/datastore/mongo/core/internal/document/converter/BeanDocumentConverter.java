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
package com.holonplatform.datastore.mongo.core.internal.document.converter;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import com.holonplatform.core.Path;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.expression.Value;

/**
 * Bean {@link DocumentConverter}.
 * 
 * @param <T> Bean type
 *
 * @since 5.2.0
 */
public class BeanDocumentConverter<T> implements DocumentConverter<T> {

	/**
	 * Bean property set
	 */
	private final BeanPropertySet<T> beanPropertySet;

	/**
	 * Constructor
	 * @param beanPropertySet Bean property set (not null)
	 */
	public BeanDocumentConverter(BeanPropertySet<T> beanPropertySet) {
		super();
		ObjectUtils.argumentNotNull(beanPropertySet, "Bean property set must be not null");
		this.beanPropertySet = beanPropertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.DocumentConverter#getConversionType()
	 */
	@Override
	public Class<? extends T> getConversionType() {
		return beanPropertySet.getBeanClass();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.DocumentConverter#convert(com.holonplatform.
	 * datastore.mongo.core. context.MongoResolutionContext, org.bson.Document)
	 */
	@Override
	public T convert(MongoResolutionContext<?> context, Document document) {
		return (document == null) ? null
				: decodeDocument(context.documentContext(beanPropertySet), null, document, beanPropertySet);
	}

	private static <T> T decodeDocument(final MongoDocumentContext<?> context, final String parent,
			Map<String, Object> document, BeanPropertySet<T> beanPropertySet) throws DataAccessException {

		// new instance
		T instance;
		try {
			instance = beanPropertySet.getBeanClass().getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new DataAccessException("Failed to istantiate bean class [" + beanPropertySet.getBeanClass() + "]",
					e);
		}

		// decode fields
		document.entrySet().stream().forEach(entry -> {
			decodeDocumentField(context, beanPropertySet, instance, parent, entry.getKey(), entry.getValue());
		});

		return instance;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> void decodeDocumentField(MongoDocumentContext<?> context, BeanPropertySet<T> beanPropertySet,
			T instance, String parent, String name, Object value) throws DataAccessException {
		// full path
		final String fieldName = composeFieldPath(parent, name);

		if (value != null && Map.class.isAssignableFrom(value.getClass())) {
			// nested value
			final Map<String, Object> nested = (Map<String, Object>) value;
			for (Entry<String, Object> entry : nested.entrySet()) {
				decodeDocumentField(context, beanPropertySet, instance, fieldName, entry.getKey(), entry.getValue());
			}

		} else {
			// check id property
			if (MongoDocumentContext.ID_FIELD_NAME.equals(fieldName)) {
				PathProperty identifier = beanPropertySet.getFirstIdentifier().orElse(null);
				if (identifier != null && context.getDocumentIdResolver().isValidDocumentIdType(identifier)) {
					Object resolvedValue = context.resolveOrFail(FieldValue.create(value, identifier), Value.class)
							.getValue();
					beanPropertySet.write(identifier, resolvedValue, instance);
					return;
				}
			}

			// resolve Path
			final Path path = context.resolveOrFail(FieldName.create(fieldName), Path.class);
			// resolve value
			beanPropertySet.getProperty(path.relativeName()).ifPresent(p -> {
				Object resolvedValue = context.resolveOrFail(FieldValue.create(value, p), Value.class).getValue();
				beanPropertySet.write(path, resolvedValue, instance);
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
