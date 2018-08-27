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

import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityFactory;
import com.holonplatform.spring.DatastoreCommodityFactory;

@DatastoreCommodityFactory
public class ConfigCheckCommodityFactory implements SyncMongoDatastoreCommodityFactory<ConfigCheckCommodity> {

	private static final long serialVersionUID = 1L;

	@Override
	public ConfigCheckCommodity createCommodity(SyncMongoDatastoreCommodityContext ctx)
			throws CommodityConfigurationException {
		return new ConfigCheckCommodityImpl(ctx);
	}

	@Override
	public Class<? extends ConfigCheckCommodity> getCommodityType() {
		return ConfigCheckCommodity.class;
	}

}
