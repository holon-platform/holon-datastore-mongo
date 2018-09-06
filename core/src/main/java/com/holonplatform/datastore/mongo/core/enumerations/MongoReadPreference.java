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

import java.util.Optional;

import com.mongodb.ReadPreference;

/**
 * MongoDB {@link ReadPreference} enumeration.
 *
 * @since 5.2.0
 */
public enum MongoReadPreference {

	/**
	 * Default read preference.
	 */
	DEFAULT(null),

	/**
	 * Forces read to the primary.
	 */
	PRIMARY(ReadPreference.primary()),

	/**
	 * Forces reads to the primary if available, otherwise to a secondary.
	 */
	PRIMARY_PREFERRED(ReadPreference.primaryPreferred()),

	/**
	 * Forces reads to a secondary.
	 */
	SECONDARY(ReadPreference.secondary()),

	/**
	 * Forces reads to a secondary if one is available, otherwise to the primary.
	 */
	SECONDARY_PREFERRED(ReadPreference.secondaryPreferred()),

	/**
	 * Forces reads to a primary or a secondary.
	 */
	NEAREST(ReadPreference.nearest());

	private final ReadPreference readPreference;

	private MongoReadPreference(ReadPreference readPreference) {
		this.readPreference = readPreference;
	}

	/**
	 * Get the actual {@link ReadPreference} implementation, if available.
	 * @return the read preference implementation, or an empty Optional if {@link #DEFAULT}
	 */
	public Optional<ReadPreference> getReadPreference() {
		return Optional.ofNullable(readPreference);
	}

}
