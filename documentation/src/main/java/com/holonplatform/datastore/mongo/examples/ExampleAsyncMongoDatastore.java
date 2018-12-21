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

import com.holonplatform.async.datastore.AsyncDatastore;
import com.holonplatform.datastore.mongo.async.AsyncMongoDatastore;

@SuppressWarnings("unused")
public class ExampleAsyncMongoDatastore {

	public void builder1() {
		// tag::builder1[]
		AsyncMongoDatastore datastore = AsyncMongoDatastore.builder() // <1>
				.client(getMongoClient()) // <2>
				.build();
		// end::builder1[]
	}

	public void builder2() {
		// tag::builder2[]
		AsyncDatastore datastore = AsyncMongoDatastore.builder() // <1>
				.client(getMongoClient()) // <2>
				.build();
		// end::builder2[]
	}

	private static com.mongodb.reactivestreams.client.MongoClient getMongoClient() {
		return null;
	}

}
