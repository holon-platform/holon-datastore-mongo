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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.holonplatform.core.Path;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.DefaultWriteOption;
import com.holonplatform.core.datastore.operation.commons.BulkUpdateOperationConfiguration;
import com.holonplatform.core.datastore.operation.commons.DatastoreOperationConfiguration;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.datastore.mongo.core.CollationOption;
import com.holonplatform.datastore.mongo.core.DocumentWriteOption;
import com.holonplatform.datastore.mongo.core.context.MongoContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

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
	 * Get the elements affected by the update operation.
	 * @param updateResult Update result
	 * @return Affected elements count
	 */
	public static long getAffectedCount(UpdateResult updateResult) {
		return updateResult.isModifiedCountAvailable() ? Long.valueOf(updateResult.getModifiedCount()).intValue() : 1;
	}

	/**
	 * Build the update expression for given {@link BulkUpdateOperationConfiguration}.
	 * @param context Resolution context
	 * @param configuration Operation configuration
	 * @return Update expression
	 */
	public static Bson getUpdateExpression(MongoResolutionContext context,
			BulkUpdateOperationConfiguration configuration) {
		// resolve values
		final Map<Path<?>, TypedExpression<?>> values = configuration.getValues();
		List<Bson> updates = new ArrayList<>(values.size());
		for (Entry<Path<?>, TypedExpression<?>> value : values.entrySet()) {
			// child update context
			final MongoResolutionContext subContext = context.childContextForUpdate(value.getKey());

			if (value.getValue() == null) {
				// resolve field name
				final String fieldName = subContext.resolveOrFail(value.getKey(), FieldName.class).getFieldName();
				// $unset for null values
				updates.add(Updates.unset(fieldName));
			} else {
				// check value expression resolution
				updates.add(subContext.resolve(value.getValue(), BsonExpression.class).map(be -> be.getValue())
						.orElseGet(() -> {
							// resolve field name
							final String fieldName = subContext.resolveOrFail(value.getKey(), FieldName.class)
									.getFieldName();
							// resolve field value
							final Object fieldValue = subContext.resolveOrFail(value.getValue(), FieldValue.class)
									.getValue();
							// $set value
							return Updates.set(fieldName, fieldValue);
						}));
			}
		}

		return Updates.combine(updates);
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
	 * Check generated document id after an insert type operation.
	 * @param documentContext Document context
	 * @param configuration Operation configuration
	 * @param documentValues Inserted documents and corresponding {@link PropertyBox} values
	 */
	@SuppressWarnings("unchecked")
	public static void checkInsertedKeys(MongoDocumentContext documentContext,
			DatastoreOperationConfiguration configuration, Map<Document, PropertyBox> documentValues) {
		// check inserted keys
		if (configuration.hasWriteOption(DefaultWriteOption.BRING_BACK_GENERATED_IDS)) {
			documentContext.getDocumentIdProperty().ifPresent(idProperty -> {
				for (Entry<Document, PropertyBox> document : documentValues.entrySet()) {
					final ObjectId oid = document.getKey().getObjectId(MongoDocumentContext.ID_FIELD_NAME);
					if (oid != null) {
						final Object idPropertyValue = documentContext.getDocumentIdResolver().decode(oid,
								idProperty.getType());
						if (document.getValue().contains(idProperty)) {
							document.getValue().setValue((Property<Object>) idProperty, idPropertyValue);
						}
					}
				}
			});
		}
	}

	/**
	 * Check generated document id after an upsert type operation, setting the inserted key using given
	 * {@link OperationResult} builder.
	 * @param builder OperationResult builder
	 * @param documentContext Document context
	 * @param configuration Operation configuration
	 * @param upsertedId Optional upserted id value
	 * @param value Original {@link PropertyBox} value
	 */
	@SuppressWarnings("unchecked")
	public static void checkUpsertedKey(OperationResult.Builder builder, MongoDocumentContext documentContext,
			DatastoreOperationConfiguration configuration, BsonValue upsertedId, PropertyBox value) {
		if (upsertedId != null) {
			final ObjectId oid = upsertedId.asObjectId().getValue();
			documentContext.getDocumentIdPath().ifPresent(idp -> {
				final Object idPropertyValue = documentContext.getDocumentIdResolver().decode(oid, idp.getType());
				builder.withInsertedKey(idp, idPropertyValue);

				// check bring back ids
				if (configuration.hasWriteOption(DefaultWriteOption.BRING_BACK_GENERATED_IDS)) {
					documentContext.getDocumentIdProperty().ifPresent(idprp -> {
						if (value.contains(idprp)) {
							value.setValue((Property<Object>) idprp, idPropertyValue);
						}
					});
				}
			});
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
	 * @param context Mongo context
	 * @param query Query to trace
	 * @param projection Optional query projection
	 * @return Query trace information
	 */
	public static String traceQuery(MongoContext context, BsonQuery query, Bson projection) {
		final StringBuilder sb = new StringBuilder();

		sb.append("Collection name: ");
		sb.append(query.getDefinition().getCollectionName());

		query.getDefinition().getFilter().ifPresent(f -> {
			sb.append("\nFilter: \n");
			sb.append(context.toJson(f));
		});
		query.getDefinition().getSort().ifPresent(s -> {
			sb.append("\nSort: \n");
			sb.append(context.toJson(s));
		});
		if (projection != null) {
			sb.append("\nProjection: \n");
			sb.append(context.toJson(projection));
		}

		return sb.toString();
	}

	/**
	 * Build the trace information for given aggregation pipeline.
	 * @param context Mongo context
	 * @param pipeline Aggregation pipeline to trace
	 * @return Aggregation pipeline trace information
	 */
	public static String traceAggregationPipeline(MongoContext context, List<Bson> pipeline) {
		final StringBuilder sb = new StringBuilder();
		pipeline.forEach(stage -> {
			sb.append(context.toJson(stage));
			sb.append("\n");
		});
		return sb.toString();
	}

	/**
	 * Build the trace information for given update operation.
	 * @param context Mongo context
	 * @param filter Optional filter
	 * @param update Update expression
	 * @return Update trace information
	 */
	public static String traceUpdate(MongoContext context, Optional<Bson> filter, Bson update) {
		final StringBuilder sb = new StringBuilder();
		filter.ifPresent(f -> {
			sb.append("Filter:\n");
			sb.append(context.toJson(f));
			sb.append("\n");
		});
		sb.append("Values:\n");
		sb.append(context.toJson(update));
		return sb.toString();
	}

}
