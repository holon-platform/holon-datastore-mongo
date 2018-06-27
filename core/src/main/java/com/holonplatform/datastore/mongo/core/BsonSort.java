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
package com.holonplatform.datastore.mongo.core;

import org.bson.conversions.Bson;

import com.holonplatform.core.query.QuerySort;
import com.holonplatform.datastore.mongo.core.internal.DefaultBsonSort;

/**
 * A {@link QuerySort} which uses a {@link Bson} expression as sort expression.
 *
 * @since 5.2.0
 */
public interface BsonSort extends QuerySort {

	/**
	 * Get the sort expression as a {@link Bson} expression.
	 * @return {@link Bson} sort expression (must be not null)
	 */
	Bson getBson();

	/**
	 * Create a new {@link BsonSort}.
	 * @param bson Sort expression as {@link Bson}
	 * @return A new {@link BsonSort} instance
	 */
	static BsonSort create(Bson bson) {
		return new DefaultBsonSort(bson);
	}

}
