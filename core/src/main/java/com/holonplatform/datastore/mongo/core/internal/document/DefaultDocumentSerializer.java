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

import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.IterableCodecProvider;
import org.bson.codecs.MapCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.Jsr310CodecProvider;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;
import com.mongodb.DBObjectCodecProvider;
import com.mongodb.DBRefCodecProvider;
import com.mongodb.DocumentToDBRefTransformer;
import com.mongodb.client.gridfs.codecs.GridFSFileCodecProvider;
import com.mongodb.client.model.geojson.codecs.GeoJsonCodecProvider;

/**
 * Default {@link DocumentSerializer} implementation.
 *
 * @since 5.2.0
 */
public enum DefaultDocumentSerializer implements DocumentSerializer {

	INSTANCE;

	private static final Logger LOGGER = MongoDatastoreLogger.create();

	private static final CodecRegistry DEFAULT_CODEC_REGISTRY = fromProviders(
			asList(new ValueCodecProvider(), new BsonValueCodecProvider(), new DBRefCodecProvider(),
					new DBObjectCodecProvider(), new DocumentCodecProvider(new DocumentToDBRefTransformer()),
					new IterableCodecProvider(new DocumentToDBRefTransformer()),
					new MapCodecProvider(new DocumentToDBRefTransformer()), new GeoJsonCodecProvider(),
					new GridFSFileCodecProvider(), new Jsr310CodecProvider()));

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer#toJson(org.bson.codecs.configuration.
	 * CodecRegistry, org.bson.Document)
	 */
	@Override
	public String toJson(CodecRegistry codecRegistry, Document document) {
		if (document != null) {
			ObjectUtils.argumentNotNull(codecRegistry, "CodecRegistry must be not null");
			try {
				return document.toJson(JsonWriterSettings.builder().indent(true).build(),
						new DocumentCodec(codecRegistry));
			} catch (Exception e) {
				LOGGER.warn("Failed to serialize document to JSON", e);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer#toJson(org.bson.Document)
	 */
	@Override
	public String toJson(Document document) {
		return toJson(DEFAULT_CODEC_REGISTRY, document);
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer#toJson(org.bson.codecs.configuration.CodecRegistry, java.util.List)
	 */
	@Override
	public String toJson(CodecRegistry codecRegistry, List<Document> documents) {
		final StringBuilder sb = new StringBuilder();
		documents.forEach(doc -> {
			sb.append(toJson(codecRegistry, doc));
			sb.append("\n");
		});
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer#toJson(java.util.List)
	 */
	@Override
	public String toJson(List<Document> documents) {
		return toJson(DEFAULT_CODEC_REGISTRY, documents);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer#toJson(org.bson.codecs.configuration.
	 * CodecRegistry, org.bson.conversions.Bson)
	 */
	@Override
	public String toJson(CodecRegistry codecRegistry, Bson bson) {
		if (bson != null) {
			ObjectUtils.argumentNotNull(codecRegistry, "CodecRegistry must be not null");
			try {
				BsonDocument bdoc = bson.toBsonDocument(Document.class, codecRegistry);
				if (bdoc != null) {
					return bdoc.toJson(JsonWriterSettings.builder().indent(true).build());
				}
			} catch (Exception e) {
				LOGGER.warn("Failed to serialize document to JSON", e);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer#toJson(org.bson.conversions.Bson)
	 */
	@Override
	public String toJson(Bson bson) {
		return toJson(DEFAULT_CODEC_REGISTRY, bson);
	}

}
