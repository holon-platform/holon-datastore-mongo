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
package com.holonplatform.datastore.mongo.core.internal;

import org.bson.conversions.Bson;

import com.holonplatform.datastore.mongo.core.BsonSort;

/**
 * Default {@link BsonSort} implementation.
 *
 * @since 5.2.0
 */
public class DefaultBsonSort implements BsonSort {

	private static final long serialVersionUID = -6720398895950240806L;

	private final Bson bson;

	/**
	 * Constructor.
	 * @param bson Sort expression
	 */
	public DefaultBsonSort(Bson bson) {
		super();
		this.bson = bson;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.BsonFilter#getBson()
	 */
	@Override
	public Bson getBson() {
		return bson;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getBson() == null) {
			throw new InvalidExpressionException("Null Bson sort value");
		}
	}

}
