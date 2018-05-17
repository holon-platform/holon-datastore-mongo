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

import com.holonplatform.core.config.ConfigProperty;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;

/**
 * A set of MongoDB Datastore read operations configuration properties.
 *
 * @since 5.2.0
 */
public interface ReadOperationConfiguration {

	/**
	 * {@link ReadConcern} operation configuration
	 */
	public static final ConfigProperty<ReadConcern> READ_CONCERN = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".readConcern", ReadConcern.class);

	/**
	 * {@link ReadPreference} operation configuration
	 */
	public static final ConfigProperty<ReadPreference> READ_PREFERENCE = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".readPreference", ReadPreference.class);

}
