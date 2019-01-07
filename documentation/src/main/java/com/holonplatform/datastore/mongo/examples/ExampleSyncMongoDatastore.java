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

import static com.holonplatform.datastore.mongo.examples.ExampleModel.ID;
import static com.holonplatform.datastore.mongo.examples.ExampleModel.NAME;
import static com.holonplatform.datastore.mongo.examples.ExampleModel.SUBJECT;
import static com.holonplatform.datastore.mongo.examples.ExampleModel.TARGET;

import java.util.Optional;
import java.util.stream.Stream;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.sync.MongoDatastore;

@SuppressWarnings("unused")
public class ExampleSyncMongoDatastore {

	public void builder1() {
		// tag::builder1[]
		MongoDatastore datastore = MongoDatastore.builder() // <1>
				.client(getMongoClient()) // <2>
				.build();
		// end::builder1[]
	}

	public void builder2() {
		// tag::builder2[]
		Datastore datastore = MongoDatastore.builder() // <1>
				.client(getMongoClient()) // <2>
				.build();
		// end::builder2[]
	}

	public void ops() {
		// tag::ops[]
		Datastore datastore = MongoDatastore.builder().client(getMongoClient()).database("test").build(); // <1>

		PropertyBox value = PropertyBox.builder(SUBJECT).set(NAME, "My name").build();

		OperationResult result = datastore.save(TARGET, value); // <2>
		Optional<String> documentId = result.getInsertedKey(ID); // <3>

		result = datastore.insert(TARGET, value, DefaultWriteOption.BRING_BACK_GENERATED_IDS); // <4>
		documentId = value.getValueIfPresent(ID); // <5>

		value.setValue(NAME, "Updated name");
		datastore.update(TARGET, value); // <6>

		datastore.delete(TARGET, value); // <7>

		long count = datastore.query(TARGET).filter(NAME.contains("fragment").or(NAME.startsWith("prefix"))).count(); // <8>

		Stream<PropertyBox> results = datastore.query(TARGET).filter(NAME.isNotNull()).sort(ID.desc()).stream(SUBJECT); // <9>

		Optional<String> id = datastore.query(TARGET).filter(NAME.eq("My name")).findOne(ID); // <10>

		result = datastore.bulkUpdate(TARGET).set(NAME, "Updated").filter(NAME.isNull()).execute(); // <11>
		long affected = result.getAffectedCount();

		result = datastore.bulkDelete(TARGET).filter(NAME.endsWith("suffix")).execute(); // <12>
		affected = result.getAffectedCount();
		// end::ops[]
	}

	private static com.mongodb.client.MongoClient getMongoClient() {
		return null;
	}

}
