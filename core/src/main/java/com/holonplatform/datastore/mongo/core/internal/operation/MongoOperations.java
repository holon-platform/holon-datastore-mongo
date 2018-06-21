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
package com.holonplatform.datastore.mongo.core.internal.operation;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.CollationOption;
import com.holonplatform.datastore.mongo.core.DocumentWriteOption;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentSerializer;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.UpdateOptions;

/**
 * Utility class for MongoDB operation configuration.
 * 
 * @since 5.2.0
 */
public class MongoOperations {

	/**
	 * Build a {@link InsertOneOptions} instance using given operation configuration.
	 * @param configuration Operation configuration
	 * @return Options
	 */
	public static InsertOneOptions getInsertOneOptions(DatastoreOperationConfiguration configuration) {
		final InsertOneOptions options = new InsertOneOptions();
		options.bypassDocumentValidation(configuration.hasWriteOption(DocumentWriteOption.BYPASS_VALIDATION));
		return options;
	}

	/**
	 * Build a {@link InsertManyOptions} instance using given operation configuration.
	 * @param configuration Operation configuration
	 * @return Options
	 */
	public static InsertManyOptions getInsertManyOptions(DatastoreOperationConfiguration configuration) {
		final InsertManyOptions options = new InsertManyOptions();
		options.bypassDocumentValidation(configuration.hasWriteOption(DocumentWriteOption.BYPASS_VALIDATION));
		options.ordered(!configuration.hasWriteOption(DocumentWriteOption.UNORDERED));
		return options;
	}

	/**
	 * Build a {@link UpdateOptions} instance using given operation configuration.
	 * @param configuration Operation configuration
	 * @param upsert Set to true if a new document should be inserted if there are no matches to the query filter.
	 * @return Options
	 */
	public static UpdateOptions getUpdateOptions(DatastoreOperationConfiguration configuration, boolean upsert) {
		final UpdateOptions options = new UpdateOptions();
		options.bypassDocumentValidation(configuration.hasWriteOption(DocumentWriteOption.BYPASS_VALIDATION));
		configuration.getWriteOption(CollationOption.class).ifPresent(o -> options.collation(o.getCollation()));
		options.upsert(upsert);
		return options;
	}

	/**
	 * Build a {@link DeleteOptions} instance using given operation configuration.
	 * @param configuration Operation configuration
	 * @return Options
	 */
	public static DeleteOptions getDeleteOptions(DatastoreOperationConfiguration configuration) {
		final DeleteOptions options = new DeleteOptions();
		configuration.getWriteOption(CollationOption.class).ifPresent(o -> options.collation(o.getCollation()));
		return options;
	}

	/**
	 * Check generated document id after an insert type operation, setting the inserted keys using given
	 * {@link OperationResult} builder.
	 * @param builder OperationResult builder
	 * @param documentContext Document context
	 * @param configuration Operation configuration
	 * @param document Document result of the insert operation
	 * @param value Original {@link PropertyBox} value
	 */
	@SuppressWarnings("unchecked")
	public static void checkInsertedKeys(OperationResult.Builder builder, MongoDocumentContext documentContext,
			DatastoreOperationConfiguration configuration, Document document, PropertyBox value) {
		// check inserted keys
		if (document.containsKey(MongoDocumentContext.ID_FIELD_NAME)) {
			// get document id value
			final ObjectId oid = document.getObjectId(MongoDocumentContext.ID_FIELD_NAME);
			if (oid != null) {
				documentContext.getDocumentIdPath().ifPresent(idp -> {
					final Object idPropertyValue = documentContext.getDocumentIdResolver().decode(oid, idp.getType());
					builder.withInsertedKey(idp, idPropertyValue);

					// check bring back ids
					if (configuration.hasWriteOption(DefaultWriteOption.BRING_BACK_GENERATED_IDS)) {
						documentContext.getDocumentIdProperty().ifPresent(idProperty -> {
							if (value.contains(idProperty)) {
								value.setValue((Property<Object>) idProperty, idPropertyValue);
							}
						});
					}
				});
			}
		}
	}

	/**
	 * Get the {@link DocumentConverter} to use to process given query results, checking the conversion type and
	 * expected query results type consistency.
	 * @param <R> Query result type
	 * @param query Query
	 * @param resultType Query result type
	 * @return DocumentConverter
	 * @throws DataAccessException If the conversion type and expected query results type are not consistent
	 */
	public static <R> DocumentConverter<R> getAndCheckConverter(BsonQuery query, Class<? extends R> resultType)
			throws DataAccessException {
		final DocumentConverter<R> documentConverter = MongoOperations.getConverter(query);
		if (!TypeUtils.isAssignable(documentConverter.getConversionType(), resultType)) {
			throw new DataAccessException("The query results converter type [" + documentConverter.getConversionType()
					+ "] is not compatible with the query projection type [" + resultType + "]");
		}
		return documentConverter;
	}

	/**
	 * Get the {@link DocumentConverter} to use to process given query results.
	 * @param <R> Query result type
	 * @param query Query
	 * @return DocumentConverter
	 */
	@SuppressWarnings("unchecked")
	public static <R> DocumentConverter<R> getConverter(BsonQuery query) {
		DocumentConverter<?> converter = query.getProjection().flatMap(p -> p.getConverter()).orElse(null);
		if (converter != null) {
			return (DocumentConverter<R>) converter;
		}
		return (DocumentConverter<R>) DocumentConverter.identity();
	}

	/**
	 * Build the trace information for given query.
	 * @param query Query to trace
	 * @param projection Optional query projection
	 * @return Query trace information
	 */
	public static String traceQuery(BsonQuery query, Bson projection) {
		final StringBuilder sb = new StringBuilder();

		sb.append("Collection name: ");
		sb.append(query.getDefinition().getCollectionName());

		query.getDefinition().getFilter().ifPresent(f -> {
			sb.append("\nFilter: \n");
			sb.append(DocumentSerializer.getDefault().toJson(f));
		});
		query.getDefinition().getSort().ifPresent(s -> {
			sb.append("\nSort: \n");
			sb.append(DocumentSerializer.getDefault().toJson(s));
		});
		if (projection != null) {
			sb.append("\nProjection: \n");
			sb.append(DocumentSerializer.getDefault().toJson(projection));
		}

		return sb.toString();
	}

	/**
	 * Build the trace information for given aggregation pipeline.
	 * @param pipeline Aggregation pipeline to trace
	 * @return String Aggregation pipeline trace information
	 */
	public static String traceAggregationPipeline(List<Bson> pipeline) {
		final StringBuilder sb = new StringBuilder();
		pipeline.forEach(stage -> {
			sb.append(DocumentSerializer.getDefault().toJson(stage));
			sb.append("\n");
		});
		return sb.toString();
	}

}
