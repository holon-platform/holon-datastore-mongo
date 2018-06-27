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

import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.datastore.mongo.core.internal.DefaultBsonFilter;

/**
 * A {@link QueryFilter} which uses a {@link Bson} expression as filter expression.
 *
 * @since 5.2.0
 */
public interface BsonFilter extends QueryFilter {

	/**
	 * Get the filter expression as a {@link Bson} expression.
	 * @return {@link Bson} filter expression (must be not null)
	 */
	Bson getBson();

	/**
	 * Create a new {@link BsonFilter}.
	 * @param bson Filter expression as {@link Bson}
	 * @return A new {@link BsonFilter} instance
	 */
	static BsonFilter create(Bson bson) {
		return new DefaultBsonFilter(bson);
	}

}
