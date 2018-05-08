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
package com.holonplatform.datastore.mongo.core.document;

import org.bson.Document;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.internal.document.DefaultPropertyBoxConverter;

/**
 * {@link PropertyBox} to and from {@link Document} converter.
 *
 * @since 5.2.0
 */
public interface PropertyBoxConverter {

	/**
	 * Encode given {@link PropertyBox} value into a Bson {@link Document}.
	 * @param value The {@link PropertyBox} value to encode
	 * @return The encoded Bson {@link Document}, <code>null</code> if given value was <code>null</code>
	 */
	Document encode(PropertyBox value);

	/**
	 * Decode given Bson {@link Document} into a {@link PropertyBox} value, using given <code>propertySet</code>.
	 * @param document The Bson {@link Document} to decode
	 * @param propertySet The {@link PropertySet} to use as {@link PropertyBox} property set (not null)
	 * @return The decoded {@link PropertyBox} value, <code>null</code> if given document was <code>null</code>
	 */
	PropertyBox decode(Document document, PropertySet<?> propertySet);

	static PropertyBoxConverter getDefault() {
		return DefaultPropertyBoxConverter.INSTANCE;
	}

}
