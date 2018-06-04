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

/**
 * Document operations {@link WriteOption}.
 *
 * @since 5.2.0
 */
public enum DocumentWriteOption implements WriteOption {

	/**
	 * A {@link WriteOption} to bypass document level validation.
	 * <p>
	 * Applies to the <code>insert</code> and <code>update</code> operations.
	 * </p>
	 */
	BYPASS_VALIDATION,

	/**
	 * A {@link WriteOption} to create a new document if there are no matches to the update filter.
	 * <p>
	 * Applies to the <code>update</code> operation.
	 * </p>
	 */
	UPSERT,

	/**
	 * A {@link WriteOption} which applies to multiple document insertion, to specify to not insert the documents in the
	 * order provided (stopping on the first failed insertion). When this option is provided, the server will attempt to
	 * insert all the documents regardless of any failure.
	 * <p>
	 * Applies to the <code>bulk insert</code> operation.
	 * </p>
	 */
	UNORDERED;

}
