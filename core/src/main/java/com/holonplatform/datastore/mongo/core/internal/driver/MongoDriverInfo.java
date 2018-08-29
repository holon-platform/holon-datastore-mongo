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

import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;

/**
 * Mongo driver version informations.
 *
 * @since 5.2.0
 */
public final class MongoDriverInfo {

	private final static Logger LOGGER = MongoDatastoreLogger.create();

	private static final Map<ClassLoader, MongoVersion> MONGO_VERSIONS = new WeakHashMap<>();

	private MongoDriverInfo() {
	}

	/**
	 * Get the Mongo driver version informations using default ClassLoader.
	 * @return Mongo driver version informations
	 */
	public static MongoVersion getMongoVersion() {
		return getMongoVersion(ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Get the Mongo driver version informations.
	 * @param classLoader ClassLoader to use (not null)
	 * @return Mongo driver version informations
	 */
	public static MongoVersion getMongoVersion(ClassLoader classLoader) {
		ObjectUtils.argumentNotNull(classLoader, "ClassLoader must be not null");
		return MONGO_VERSIONS.computeIfAbsent(classLoader, cl -> {
			try {
				final Class<?> cls = ClassUtils.forName("com.mongodb.internal.build.MongoDriverVersion", cl);
				final Field fld = cls.getDeclaredField("VERSION");
				Object value = fld.get(null);
				if (value != null && value instanceof String) {
					return new DefaultMongoVersion(true, (String) value);
				}
			} catch (Exception e) {
				LOGGER.warn("Failed to detect Mongo driver version");
				LOGGER.debug(() -> "Failed to detect Mongo driver version using MongoDriverVersion class", e);
			}
			return new DefaultMongoVersion(false, null);
		});
	}

}
