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

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;

import com.holonplatform.core.datastore.DatastoreConfigProperties;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.sync.MongoDatastore;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

@SuppressWarnings("unused")
public class ExampleMongoDatastore {

	public void builder1() {
		// tag::dbname[]
		MongoDatastore datastore = MongoDatastore.builder() // <1>
				.client(getMongoClient()) // <2>
				.database("my_db") // <3>
				.build();
		// end::dbname[]
	}

	public void config1() {
		// tag::config1[]
		MongoDatastore datastore = MongoDatastore.builder().client(getMongoClient()).database("my_db")
				.readPreference(ReadPreference.primary()) // <1>
				.readConcern(ReadConcern.MAJORITY) // <2>
				.writeConcern(WriteConcern.UNACKNOWLEDGED) // <3>
				.withCodec(getCustomCodec()) // <4>
				.withCodecProvider(getCustomCodecProvider()) // <5>
				.build();
		// end::config1[]
	}

	public void config2() {
		// tag::config2[]
		MongoDatastore datastore = MongoDatastore.builder().client(getMongoClient()).database("my_db")
				.enumCodecStrategy(EnumCodecStrategy.ORDINAL) // <1>
				.build();
		// end::config2[]
	}

	public void config3() {
		// tag::config3[]
		MongoDatastore datastore = MongoDatastore.builder().client(getMongoClient()).database("my_db")
				.dataContextId("mydataContextId") // <1>
				.traceEnabled(true) // <2>
				.build();
		// end::config3[]
	}

	public void config4() {
		// tag::config4[]
		MongoDatastore datastore = MongoDatastore.builder().client(getMongoClient()).database("my_db")
				.configuration(DatastoreConfigProperties.builder().withPropertySource("datastore.properties").build()) // <1>
				.build();
		// end::config4[]
	}

	private static com.mongodb.client.MongoClient getMongoClient() {
		return null;
	}

	private static Codec<?> getCustomCodec() {
		return null;
	}

	private static CodecProvider getCustomCodecProvider() {
		return null;
	}

}
