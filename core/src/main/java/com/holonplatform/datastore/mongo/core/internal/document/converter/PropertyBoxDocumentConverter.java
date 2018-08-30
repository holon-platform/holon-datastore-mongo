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

import org.bson.Document;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;

/**
 * {@link PropertyBox} document converter.
 *
 * @since 5.2.0
 */
public class PropertyBoxDocumentConverter implements DocumentConverter<PropertyBox> {

	private final PropertySet<?> propertySet;

	/**
	 * Constructor.
	 * @param propertySet Property set (not null)
	 */
	public PropertyBoxDocumentConverter(PropertySet<?> propertySet) {
		super();
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		this.propertySet = propertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.DocumentConverter#getConversionType()
	 */
	@Override
	public Class<? extends PropertyBox> getConversionType() {
		return PropertyBox.class;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.document.DocumentConverter#convert(com.holonplatform.datastore.mongo.core.
	 * context.MongoResolutionContext, org.bson.Document)
	 */
	@Override
	public PropertyBox convert(MongoResolutionContext<?> context, Document document) {
		return (document == null) ? null : context.documentContext(propertySet)
				.resolveOrFail(DocumentValue.create(document), PropertyBoxValue.class).getValue();
	}

}
