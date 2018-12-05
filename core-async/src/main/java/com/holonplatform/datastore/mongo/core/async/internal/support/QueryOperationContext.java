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

import java.util.function.Supplier;

import org.bson.Document;

import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoCollection;

/**
 * Query operation context.
 * 
 * @param <R> Query result type
 *
 * @since 5.2.0
 */
public interface QueryOperationContext<R> {

	/**
	 * Get the resolution context.
	 * @return Resolution context
	 */
	MongoResolutionContext<ClientSession> getResolutionContext();

	/**
	 * Get the mongo collection reference.
	 * @return Mongo collection
	 */
	MongoCollection<Document> getCollection();

	/**
	 * Get the query expression.
	 * @return Query expression
	 */
	BsonQuery getQuery();

	/**
	 * Get the query result type.
	 * @return Query result type
	 */
	Class<? extends R> getResultType();

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
		getResolutionContext().trace(title, json);
	}

	/**
	 * Trace given JSON expression.
	 * @param title Optional title
	 * @param json JSON to trace
	 */
	default void trace(String title, String json) {
		getResolutionContext().trace(title, json);
	}

	static <R> QueryOperationContext<R> create(MongoResolutionContext<ClientSession> resolutionContext,
			MongoCollection<Document> collection, BsonQuery query, Class<? extends R> resultType) {
		return new DefaultQueryOperationContext<>(resolutionContext, collection, query, resultType);
	}

}
