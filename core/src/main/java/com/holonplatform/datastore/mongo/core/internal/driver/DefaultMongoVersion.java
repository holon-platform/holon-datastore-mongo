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

import com.holonplatform.core.internal.Logger;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;

/**
 * Default {@link MongoVersion} implementation.
 * 
 * @since 5.2.0
 */
public class DefaultMongoVersion implements MongoVersion {

	private final static Logger LOGGER = MongoDatastoreLogger.create();

	private final boolean detected;

	private int driverMajorVersion = -1;
	private int driverMinorVersion = -1;
	private int driverPatchVersion = -1;

	public DefaultMongoVersion(boolean detected, final String driverVersion) {
		super();
		this.detected = detected;
		if (detected && driverVersion != null) {
			try {
				int idx = driverVersion.indexOf('.');
				// major
				if (idx > 0) {
					driverMajorVersion = Integer.parseInt(driverVersion.substring(0, idx));
					// minor
					if (idx < (driverVersion.length() - 1)) {
						String str = driverVersion.substring(idx + 1);
						idx = str.indexOf('.');
						if (idx > 0) {
							driverMinorVersion = Integer.parseInt(str.substring(0, idx));
							// patch
							if (idx < (str.length() - 1)) {
								str = str.substring(idx + 1);
								if (str.length() > 0) {
									driverPatchVersion = Integer.parseInt(str);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				LOGGER.debug(() -> "Failed to parse Mongo driver version String [" + driverVersion + "]", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.driver.MongoVersion#wasDriverVersionDetected()
	 */
	@Override
	public boolean wasDriverVersionDetected() {
		return detected;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.driver.MongoVersion#getDriverMajorVersion()
	 */
	@Override
	public int getDriverMajorVersion() {
		return driverMajorVersion;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.driver.MongoVersion#getDriverMinorVersion()
	 */
	@Override
	public int getDriverMinorVersion() {
		return driverMinorVersion;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.driver.MongoVersion#getDriverPatchVersion()
	 */
	@Override
	public int getDriverPatchVersion() {
		return driverPatchVersion;
	}

}
