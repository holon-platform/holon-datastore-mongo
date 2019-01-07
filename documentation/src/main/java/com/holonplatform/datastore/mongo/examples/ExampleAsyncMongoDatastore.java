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
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import com.holonplatform.async.datastore.AsyncDatastore;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.core.property.PropertyBox;
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

	public void ops() {
		// tag::ops[]
		AsyncDatastore datastore = AsyncMongoDatastore.builder().client(getMongoClient()).database("test").build(); // <1>

		PropertyBox value = PropertyBox.builder(SUBJECT).set(NAME, "My name").build();

		datastore.save(TARGET, value) // <2>
				.thenApply(result -> result.getInsertedKey(ID)).thenAccept(id -> {
					Optional<String> documentId = id; // <3>
				});

		datastore.insert(TARGET, value, DefaultWriteOption.BRING_BACK_GENERATED_IDS) // <4>
				.thenApply(result -> value.getValueIfPresent(ID)).thenAccept(id -> {
					Optional<String> documentId = id; // <5>
				});

		value.setValue(NAME, "Updated name");
		CompletionStage<OperationResult> result = datastore.update(TARGET, value); // <6>

		result = datastore.delete(TARGET, value); // <7>

		CompletionStage<Long> count = datastore.query(TARGET)
				.filter(NAME.contains("fragment").or(NAME.startsWith("prefix"))).count(); // <8>

		CompletionStage<Stream<PropertyBox>> results = datastore.query(TARGET).filter(NAME.isNotNull()).sort(ID.desc())
				.stream(SUBJECT); // <9>

		CompletionStage<Optional<String>> id = datastore.query(TARGET).filter(NAME.eq("My name")).findOne(ID); // <10>

		datastore.bulkUpdate(TARGET).set(NAME, "Updated").filter(NAME.isNull()).execute().thenAccept(r -> { // <11>
			long affected = r.getAffectedCount();
		});

		datastore.bulkDelete(TARGET).filter(NAME.endsWith("suffix")).execute().thenAccept(r -> { // <12>
			long affected = r.getAffectedCount();
		});
		// end::ops[]
	}

	public void transactional() {
		PropertyBox value = null;

		// tag::transactional[]
		final AsyncDatastore datastore = getMongoDatastore(); // build or obtain a MongoDB Datastore

		CompletionStage<Boolean> committed = datastore.requireTransactional().withTransaction(tx -> { // <1>
			datastore.save(TARGET, value);
			return tx.commit(); // <2>
		});

		CompletionStage<OperationResult> result = datastore.requireTransactional().withTransaction(tx -> { // <3>
			return datastore.save(TARGET, value);
		}, TransactionConfiguration.withAutoCommit()); // <4>
		// end::transactional[]
	}

	private static com.mongodb.reactivestreams.client.MongoClient getMongoClient() {
		return null;
	}

	private static AsyncDatastore getMongoDatastore() {
		return null;
	}

}
