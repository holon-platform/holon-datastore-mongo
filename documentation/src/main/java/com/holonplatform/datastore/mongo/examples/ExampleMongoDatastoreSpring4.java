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
package com.holonplatform.datastore.mongo.examples;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.datastore.ConfigurableDatastore;
import com.holonplatform.core.datastore.DatastoreCommodity;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QueryFilter.QueryFilterResolver;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityFactory;
import com.holonplatform.spring.DatastoreCommodityFactory;
import com.holonplatform.spring.DatastorePostProcessor;
import com.holonplatform.spring.DatastoreResolver;

@SuppressWarnings("serial")
public class ExampleMongoDatastoreSpring4 {

	static class MyCommodity implements DatastoreCommodity {

	}

	static class MyFilter implements QueryFilter {

		@Override
		public void validate() throws InvalidExpressionException {
		}

	}

	static class MySort implements QuerySort {

		@Override
		public void validate() throws InvalidExpressionException {
		}

	}

	// tag::config[]
	@DatastoreResolver // <1>
	class MyFilterExpressionResolver implements QueryFilterResolver<MyFilter> {

		@Override
		public Class<? extends MyFilter> getExpressionType() {
			return MyFilter.class;
		}

		@Override
		public Optional<QueryFilter> resolve(MyFilter expression, ResolutionContext context)
				throws InvalidExpressionException {
			// implement actual MyFilter expression resolution
			return Optional.empty();
		}

	}

	@Component
	class MyDatastorePostProcessor implements DatastorePostProcessor { // <2>

		@Override
		public void postProcessDatastore(ConfigurableDatastore datastore, String datastoreBeanName) {
			// configure Datastore
		}

	}

	@DatastoreCommodityFactory // <3>
	class MyCommodityFactory implements SyncMongoDatastoreCommodityFactory<MyCommodity> {

		@Override
		public Class<? extends MyCommodity> getCommodityType() {
			return MyCommodity.class;
		}

		@Override
		public MyCommodity createCommodity(SyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			// create commodity instance
			return new MyCommodity();
		}

	}
	// end::config[]

}
