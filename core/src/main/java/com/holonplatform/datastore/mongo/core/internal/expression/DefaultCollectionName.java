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
package com.holonplatform.datastore.mongo.core.internal.expression;

import com.holonplatform.datastore.mongo.core.expression.CollectionName;

/**
 * Default {@link CollectionName} implementation.
 *
 * @since 5.2.0
 */
public class DefaultCollectionName implements CollectionName {

	private final String name;

	/**
	 * Constructor
	 * @param name Collection name
	 */
	public DefaultCollectionName(String name) {
		super();
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoCollectionName#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getName() == null || getName().trim().length() == 0) {
			throw new InvalidExpressionException("Null or blank collection name");
		}
	}

}
