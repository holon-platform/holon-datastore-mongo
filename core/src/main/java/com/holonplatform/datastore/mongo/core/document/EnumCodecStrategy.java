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
package com.holonplatform.datastore.mongo.core.document;

import com.holonplatform.core.config.ConfigProperty;
import com.holonplatform.core.property.Property;

/**
 * Enumeration of Enum types encoding and decoding.
 *
 * @since 5.2.0
 */
public enum EnumCodecStrategy {

	/**
	 * Use the enum constant name for encoding and decoding.
	 */
	NAME,

	/**
	 * Use the enum constant ordinal for encoding and decoding.
	 */
	ORDINAL;

	/**
	 * A configuration property which can be used to declare the enum codec strategy.
	 * <p>
	 * It can be used, for example, to declare the enum codec strategy for a {@link Property}, using the property
	 * configuration.
	 * </p>
	 */
	public static final ConfigProperty<EnumCodecStrategy> CONFIG_PROPERTY = ConfigProperty
			.create(EnumCodecStrategy.class.getName(), EnumCodecStrategy.class);

	/**
	 * Return the default {@link EnumCodecStrategy}.
	 * @return {@link EnumCodecStrategy#NAME}
	 */
	public static EnumCodecStrategy getDefault() {
		return EnumCodecStrategy.NAME;
	}

}
