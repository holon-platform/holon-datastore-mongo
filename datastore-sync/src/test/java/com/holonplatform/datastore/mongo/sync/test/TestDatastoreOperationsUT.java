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

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.datastore.mongo.sync.MongoDatastore;
import com.holonplatform.datastore.mongo.sync.test.suite.InsertTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ InsertTest.class })
public class TestDatastoreOperationsUT extends AbstractMongoDBTest {

	public static final String TEST_OPS_DATABASE_NAME = "testops";

	public static Datastore datastore;

	@BeforeClass
	public static void initDatastore() {

		datastore = MongoDatastore.builder() //
				.client(getMongo()) //
				.database(TEST_OPS_DATABASE_NAME) //
				.traceEnabled(true) //
				.build();

	}

}
