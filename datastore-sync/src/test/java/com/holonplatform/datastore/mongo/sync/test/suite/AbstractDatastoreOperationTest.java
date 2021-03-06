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
package com.holonplatform.datastore.mongo.sync.test.suite;

import java.util.function.Supplier;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;
import com.holonplatform.datastore.mongo.sync.test.AbstractMongoDatastoreTest;
import com.holonplatform.datastore.mongo.sync.test.TestDatastoreOperationsUT;

public class AbstractDatastoreOperationTest {

	protected final static Logger LOGGER = MongoDatastoreLogger.create();

	protected final static DataTarget<?> TARGET = DataTarget.named(TestDatastoreOperationsUT.TEST_OPS_DATABASE_NAME);

	protected Datastore getDatastore() {
		return AbstractMongoDatastoreTest.datastore;
	}

	protected void inTransaction(Runnable operation) {
		getDatastore().requireTransactional().withTransaction(tx -> {
			tx.setRollbackOnly();
			operation.run();
		});
	}

	protected <T> T inTransaction(Supplier<T> operation) {
		return getDatastore().requireTransactional().withTransaction(tx -> {
			tx.setRollbackOnly();
			return operation.get();
		});
	}

}
