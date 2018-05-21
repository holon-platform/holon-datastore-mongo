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

import com.holonplatform.core.datastore.DatastoreOperations.WriteOption;
import com.holonplatform.datastore.mongo.core.internal.DefaultCollationOption;
import com.mongodb.client.model.Collation;

/**
 * A Datastore {@link WriteOption} to configure the MongoDB {@link Collation} to use with a specific write operation.
 *
 * @since 5.2.0
 */
public interface CollationOption extends WriteOption {

	/**
	 * Get the MongoDB {@link Collation} to use.
	 * @return the MongoDB {@link Collation} value
	 */
	Collation getCollation();

	/**
	 * Create a new {@link CollationOption}.
	 * @param collation The MongoDB {@link Collation} to use (not null)
	 * @return A new {@link CollationOption} instance
	 */
	static CollationOption create(Collation collation) {
		return new DefaultCollationOption(collation);
	}

}
