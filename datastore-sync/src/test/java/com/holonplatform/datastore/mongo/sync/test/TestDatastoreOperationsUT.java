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
package com.holonplatform.datastore.mongo.sync.test;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.holonplatform.datastore.mongo.sync.MongoDatastore;
import com.holonplatform.datastore.mongo.sync.test.suite.AggregationFunctionsTest;
import com.holonplatform.datastore.mongo.sync.test.suite.BulkDeleteTest;
import com.holonplatform.datastore.mongo.sync.test.suite.BulkInsertTest;
import com.holonplatform.datastore.mongo.sync.test.suite.BulkUpdateTest;
import com.holonplatform.datastore.mongo.sync.test.suite.CustomExpressionsTest;
import com.holonplatform.datastore.mongo.sync.test.suite.DeleteTest;
import com.holonplatform.datastore.mongo.sync.test.suite.DistinctTest;
import com.holonplatform.datastore.mongo.sync.test.suite.InsertTest;
import com.holonplatform.datastore.mongo.sync.test.suite.QueryAggregationTest;
import com.holonplatform.datastore.mongo.sync.test.suite.QueryFilterTest;
import com.holonplatform.datastore.mongo.sync.test.suite.QueryProjectionTest;
import com.holonplatform.datastore.mongo.sync.test.suite.QueryRestrictionTest;
import com.holonplatform.datastore.mongo.sync.test.suite.QuerySortTest;
import com.holonplatform.datastore.mongo.sync.test.suite.RefreshTest;
import com.holonplatform.datastore.mongo.sync.test.suite.SaveTest;
import com.holonplatform.datastore.mongo.sync.test.suite.StringFunctionsTest;
import com.holonplatform.datastore.mongo.sync.test.suite.TemporalFunctionsTest;
import com.holonplatform.datastore.mongo.sync.test.suite.UpdateTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ InsertTest.class, UpdateTest.class, SaveTest.class, RefreshTest.class, DeleteTest.class,
		BulkInsertTest.class, BulkDeleteTest.class, BulkUpdateTest.class, QueryProjectionTest.class,
		QueryAggregationTest.class, QueryRestrictionTest.class, QuerySortTest.class, QueryFilterTest.class,
		StringFunctionsTest.class, TemporalFunctionsTest.class, AggregationFunctionsTest.class, DistinctTest.class,
		CustomExpressionsTest.class })
public class TestDatastoreOperationsUT extends AbstractMongoDBTest {

	public static final String TEST_OPS_DATABASE_NAME = "testops";

	@BeforeClass
	public static void initDatastore() {

		datastore = MongoDatastore.builder() //
				.client(getMongo()) //
				.database(TEST_OPS_DATABASE_NAME) //
				.traceEnabled(true) //
				.build();

	}

}
