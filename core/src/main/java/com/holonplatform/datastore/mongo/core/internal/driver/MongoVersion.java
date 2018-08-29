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
package com.holonplatform.datastore.mongo.core.internal.driver;

/**
 * Mongo version informations.
 *
 * @since 5.2.0
 */
public interface MongoVersion {

	/**
	 * Gets whether the driver version was detected.
	 * @return Whether the driver version was detected
	 */
	boolean wasDriverVersionDetected();

	/**
	 * Get the driver major version.
	 * @return Driver major version, <code>-1</code> if not detected
	 */
	int getDriverMajorVersion();

	/**
	 * Get the driver minor version.
	 * @return Driver minor version, <code>-1</code> if not detected
	 */
	int getDriverMinorVersion();

	/**
	 * Get the driver patch version.
	 * @return Driver patch version, <code>-1</code> if not detected
	 */
	int getDriverPatchVersion();

}
