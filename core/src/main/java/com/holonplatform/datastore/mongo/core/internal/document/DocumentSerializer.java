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
package com.holonplatform.datastore.mongo.core.internal.document;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Bson document serializer.
 *
 * @since 5.2.0
 */
public interface DocumentSerializer {

	/**
	 * Serialize given document to JSON.
	 * @param codecRegistry The codec registry to use (not null)
	 * @param document The document to serialize
	 * @return Serialized document
	 */
	String toJson(CodecRegistry codecRegistry, Document document);

	/**
	 * Serialize given document to JSON using the default codec registry. param document The document to serialize
	 * @return Serialized document
	 */
	String toJson(Document document);

	/**
	 * Get the default {@link DocumentSerializer}.
	 * @return the default {@link DocumentSerializer}
	 */
	static DocumentSerializer getDefault() {
		return DefaultDocumentSerializer.INSTANCE;
	}

}
