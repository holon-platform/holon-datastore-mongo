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
package com.holonplatform.datastore.mongo.sync.internal.operations;

import java.util.stream.Stream;

import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.query.QueryAdapterQuery;
import com.holonplatform.core.internal.query.QueryDefinition;
import com.holonplatform.core.query.Query;
import com.holonplatform.core.query.QueryAdapter;
import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.core.query.QueryOperation;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.sync.config.SyncMongoDatastoreCommodityContext;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDB {@link QueryAdapter}.
 *
 * @since 5.2.0
 */
public class MongoQuery implements QueryAdapter<QueryConfiguration> {

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Query> FACTORY = new DatastoreCommodityFactory<SyncMongoDatastoreCommodityContext, Query>() {

		@Override
		public Class<? extends Query> getCommodityType() {
			return Query.class;
		}

		@Override
		public Query createCommodity(SyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new QueryAdapterQuery<>(new MongoQuery(context), QueryDefinition.create());
		}
	};

	private final MongoOperationContext<MongoDatabase> operationContext;

	public MongoQuery(MongoOperationContext<MongoDatabase> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryAdapter#stream(com.holonplatform.core.query.QueryOperation)
	 */
	@Override
	public <R> Stream<R> stream(QueryOperation<QueryConfiguration, R> queryOperation) throws DataAccessException {
		// TODO
		return null;
	}

}
