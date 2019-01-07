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
import java.util.stream.Stream;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.types.ObjectId;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.DatastoreConfigProperties;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.datastore.mongo.core.BsonFilter;
import com.holonplatform.datastore.mongo.core.BsonSort;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.sync.MongoDatastore;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

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

	// tag::target[]
	final static DataTarget<?> TARGET = DataTarget.named("my_collection"); // <1>
	// end::target[]

	public void ids1() {
		// tag::ids1[]
		final PathProperty<ObjectId> ID = PathProperty.create("_id", ObjectId.class);

		Datastore datastore = getMongoDatastore(); // build or obtain a MongoDB Datastore
		PropertyBox value = buildPropertyBoxValue();

		OperationResult result = datastore.insert(DataTarget.named("my_collection"), value); // <1>

		Optional<ObjectId> idValue = result.getInsertedKey(ID); // <2>
		idValue = result.getFirstInsertedKey(ObjectId.class); // <3>
		// end::ids1[]
	}

	public void ids2() {
		// tag::ids2[]
		final StringProperty ID = StringProperty.create("_id"); // <1>
		final PathProperty<String> TEXT = PathProperty.create("text", String.class);

		Datastore datastore = getMongoDatastore(); // build or obtain a MongoDB Datastore

		PropertyBox value = PropertyBox.builder(ID, TEXT).set(TEXT, "test").build(); // <2>

		datastore.insert(DataTarget.named("my_collection"), value, DefaultWriteOption.BRING_BACK_GENERATED_IDS); // <3>

		String idValue = value.getValue(ID); // <4>
		// end::ids2[]
	}

	public void bsonfilter() {
		// tag::bsonfilter[]
		Datastore datastore = getMongoDatastore(); // build or obtain a MongoDB Datastore

		long count = datastore.query(DataTarget.named("my_collection"))
				.filter(BsonFilter.create(Filters.size("my_field", 3))) // <1>
				.count();
		// end::bsonfilter[]
	}

	private static final StringProperty MY_PROPERTY = StringProperty.create("myp");

	public void bsonsort() {
		// tag::bsonsort[]
		Datastore datastore = getMongoDatastore(); // build or obtain a MongoDB Datastore

		Stream<String> values = datastore.query(DataTarget.named("my_collection"))
				.sort(BsonSort.create(Sorts.descending("my_field"))) // <1>
				.stream(MY_PROPERTY);
		// end::bsonsort[]
	}

	private static PropertyBox buildPropertyBoxValue() {
		return null;
	}

	private static com.mongodb.client.MongoClient getMongoClient() {
		return null;
	}

	private static MongoDatastore getMongoDatastore() {
		return null;
	}

	private static Codec<?> getCustomCodec() {
		return null;
	}

	private static CodecProvider getCustomCodecProvider() {
		return null;
	}

}
