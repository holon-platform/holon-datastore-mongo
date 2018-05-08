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

import com.holonplatform.core.property.PropertySet;
import com.holonplatform.json.model.PropertySetSerializationTree;
import com.holonplatform.json.model.PropertySetSerializationTreeResolver;

/**
 * MongoDB {@link PropertySetSerializationTreeResolver} with caching support.
 *
 * @since 5.2.0
 */
public interface MongoPropertySetSerializationTreeResolver {

	/**
	 * Resolve the {@link PropertySetSerializationTree} if given {@link PropertySet} definition.
	 * @param propertySet The property set for which to obtain the serialization tree (not null)
	 * @return The resolved {@link PropertySetSerializationTree}
	 */
	PropertySetSerializationTree resolve(PropertySet<?> propertySet);

	/**
	 * Get the default {@link MongoPropertySetSerializationTreeResolver}.
	 * @return The default {@link MongoPropertySetSerializationTreeResolver}
	 */
	static MongoPropertySetSerializationTreeResolver getDefault() {
		return DefaultMongoPropertySetSerializationTreeResolver.INSTANCE;
	}

}
