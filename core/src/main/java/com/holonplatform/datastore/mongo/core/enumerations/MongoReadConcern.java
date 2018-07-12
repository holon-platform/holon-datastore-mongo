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
package com.holonplatform.datastore.mongo.core.enumerations;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;

/**
 * MongoDB {@link ReadConcern} enumeration.
 *
 * @since 5.2.0
 */
public enum MongoReadConcern {

	/**
	 * The servers default read concern.
	 */
	DEFAULT(ReadConcern.DEFAULT),

	/**
	 * The local read concern.
	 */
	LOCAL(ReadConcern.LOCAL),

	/**
	 * The majority read concern.
	 */
	MAJORITY(ReadConcern.MAJORITY),

	/**
	 * The linearizable read concern.
	 * <p>
	 * This read concern is only compatible with {@link ReadPreference#primary()}.
	 * </p>
	 */
	LINEARIZABLE(ReadConcern.LINEARIZABLE);

	private final ReadConcern readConcern;

	private MongoReadConcern(ReadConcern readConcern) {
		this.readConcern = readConcern;
	}

	/**
	 * Get the actual {@link ReadConcern} implementation.
	 * @return the read concern implementation
	 */
	public ReadConcern getReadConcern() {
		return readConcern;
	}

}
