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
package com.holonplatform.datastore.mongo.spring.boot.test.config;

import java.util.Optional;

import com.holonplatform.datastore.mongo.core.config.MongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.mongodb.ReadPreference;

public class ConfigCheckCommodityImpl implements ConfigCheckCommodity {

	private static final long serialVersionUID = 1L;

	private final MongoDatastoreCommodityContext<?, ?> context;

	public ConfigCheckCommodityImpl(MongoDatastoreCommodityContext<?, ?> context) {
		super();
		this.context = context;
	}

	@Override
	public String getDatabaseName() {
		return context.getDatabaseName();
	}

	@Override
	public Optional<ReadPreference> getDefaultReadPreference() {
		return context.getDefaultReadPreference();
	}

	@Override
	public EnumCodecStrategy getEnumCodecStrategy() {
		return context.getDefaultEnumCodecStrategy();
	}

}
