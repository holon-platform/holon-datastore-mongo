/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.datastore.mongo.core.context;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.holonplatform.core.ExpressionResolver.ExpressionResolverProvider;
import com.holonplatform.datastore.mongo.core.document.DocumentIdResolver;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.session.ClientSession;

/**
 * MongoDB Datastore base context.
 * 
 * @param <S> Concrete ClientSession type
 *
 * @since 5.2.0
 */
public interface MongoContext<S extends ClientSession> extends ExpressionResolverProvider {

	/**
	 * Get the current client session, if available.
	 * @return Optional client session
	 */
	Optional<S> getClientSession();

	/**
	 * Get the {@link DocumentIdResolver} of this context.
	 * @return the {@link DocumentIdResolver}
	 */
	DocumentIdResolver getDocumentIdResolver();

	/**
	 * Get the {@link CodecRegistry} bound to the current database.
	 * @return Database {@link CodecRegistry}
	 */
	CodecRegistry getDatabaseCodecRegistry();

	/**
	 * Get the default {@link EnumCodecStrategy}.
	 * @return the default {@link EnumCodecStrategy}s
	 */
	EnumCodecStrategy getDefaultEnumCodecStrategy();

	/**
	 * Get the default {@link ReadPreference} for query or data read operations.
	 * @return Optional default {@link ReadPreference}
	 */
	Optional<ReadPreference> getDefaultReadPreference();

	/**
	 * Get the default {@link ReadConcern} to declare the read operations isolation level.
	 * @return Optional the default {@link ReadConcern}
	 */
	Optional<ReadConcern> getDefaultReadConcern();

	/**
	 * Get the default {@link WriteConcern} to use with write operations.
	 * @return Optional default {@link WriteConcern}
	 */
	Optional<WriteConcern> getDefaultWriteConcern();

	/**
	 * Serialize given document to JSON using the database codec registry.
	 * @param document The document to serialize
	 * @return Serialized document
	 */
	default String toJson(Document document) {
		return DocumentSerializer.getDefault().toJson(getDatabaseCodecRegistry(), document);
	}

	/**
	 * Serialize given documents to JSON using the database codec registry.
	 * @param documents The documents to serialize
	 * @return Serialized documents
	 */
	default String toJson(List<Document> documents) {
		return DocumentSerializer.getDefault().toJson(getDatabaseCodecRegistry(), documents);
	}

	/**
	 * Serialize given {@link Bson} value to JSON using the database codec registry.
	 * @param bson The Bson value to serialize
	 * @return Serialized Bson document
	 */
	default String toJson(Bson bson) {
		return DocumentSerializer.getDefault().toJson(getDatabaseCodecRegistry(), bson);
	}

	/**
	 * Trace given JSON expression.
	 * <p>
	 * If tracing is enabled, the JSON expression is logged using the <code>INFO</code> level, otherwise it is logged
	 * using the <code>DEBUG</code> level.
	 * </p>
	 * @param title Optional title
	 * @param json JSON to trace
	 */
	void trace(String title, Supplier<String> json);

	/**
	 * Trace given JSON expression.
	 * <p>
	 * If tracing is enabled, the JSON expression is logged using the <code>INFO</code> level, otherwise it is logged
	 * using the <code>DEBUG</code> level.
	 * </p>
	 * @param title Optional title
	 * @param json JSON to trace
	 */
	default void trace(String title, String json) {
		trace(title, () -> json);
	}

	/**
	 * Trace given JSON Document.
	 * <p>
	 * If tracing is enabled, the JSON expression is logged using the <code>INFO</code> level, otherwise it is logged
	 * using the <code>DEBUG</code> level.
	 * </p>
	 * @param title Optional title
	 * @param document Document to trace
	 */
	default void trace(String title, Document document) {
		trace(title, () -> toJson(document));
	}

	/**
	 * Trace given JSON Documents.
	 * <p>
	 * If tracing is enabled, the JSON expression is logged using the <code>INFO</code> level, otherwise it is logged
	 * using the <code>DEBUG</code> level.
	 * </p>
	 * @param title Optional title
	 * @param documents Documents to trace
	 */
	default void trace(String title, List<Document> documents) {
		trace(title, () -> toJson(documents));
	}

}
