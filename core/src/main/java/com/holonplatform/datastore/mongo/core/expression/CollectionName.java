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
package com.holonplatform.datastore.mongo.core.expression;

import com.holonplatform.core.Expression;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultCollectionName;

/**
 * Mongo collection name expression.
 *
 * @since 5.2.0
 */
public interface CollectionName extends Expression {

	/**
	 * Get the database collection name.
	 * @return The collection name
	 */
	String getName();

	/**
	 * Create a new {@link CollectionName} using given collection name.
	 * @param name The collection name
	 * @return A new {@link CollectionName} instance
	 */
	static CollectionName create(String name) {
		return new DefaultCollectionName(name);
	}

}
