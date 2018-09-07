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
package com.holonplatform.datastore.mongo.core.async.internal.support;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.datastore.mongo.core.context.MongoContext;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoCollection;

/**
 * Async operation context.
 * 
 * @param <C> Context type
 *
 * @since 5.2.0
 */
public interface AsyncOperationContext<C extends MongoContext<ClientSession>> {

	/**
	 * Get the related {@link MongoContext}.
	 * @return The Mongo context
	 */
	C getContext();

	/**
	 * Get the collection.
	 * @return The collection
	 */
	MongoCollection<Document> getCollection();

	/**
	 * Get the operation configuration.
	 * @return Operation configuration
	 */
	DatastoreOperationConfiguration getConfiguration();

	/**
	 * Get the optional resolved operation filter.
	 * @return Optional filter
	 */
	Optional<Bson> getFilter();

	/**
	 * Trace given JSON expression.
	 * <p>
	 * If tracing is enabled, the JSON expression is logged using the <code>INFO</code> level, otherwise it is logged
	 * using the <code>DEBUG</code> level.
	 * </p>
	 * @param title Optional title
	 * @param json JSON to trace
	 */
	default void trace(String title, Supplier<String> json) {
		getContext().trace(title, json);
	}

	/**
	 * Trace given JSON expression.
	 * @param title Optional title
	 * @param json JSON to trace
	 */
	default void trace(String title, String json) {
		getContext().trace(title, json);
	}

	/**
	 * Trace given JSON Document.
	 * @param title Optional title
	 * @param document Document to trace
	 */
	default void trace(String title, Document document) {
		getContext().trace(title, document);
	}

	/**
	 * Trace given JSON Documents.
	 * @param title Optional title
	 * @param documents Documents to trace
	 */
	default void trace(String title, List<Document> documents) {
		getContext().trace(title, documents);
	}

	static <C extends MongoContext<ClientSession>> AsyncOperationContext<C> create(C mongoContext,
			MongoCollection<Document> collection, DatastoreOperationConfiguration configuration) {
		return create(mongoContext, collection, configuration, null);
	}

	static <C extends MongoContext<ClientSession>> AsyncOperationContext<C> create(C mongoContext,
			MongoCollection<Document> collection, DatastoreOperationConfiguration configuration, Bson filter) {
		return new DefaultAsyncOperationContext<>(mongoContext, collection, configuration, filter);
	}

}
