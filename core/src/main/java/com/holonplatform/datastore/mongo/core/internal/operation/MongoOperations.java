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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.bson.BsonType;
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
import com.holonplatform.core.internal.utils.ObjectUtils;
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
import com.holonplatform.datastore.mongo.core.expression.BsonFilterExpression;
import com.holonplatform.datastore.mongo.core.expression.BsonFilterExpression.FilterAggregationPipeline;
import com.holonplatform.datastore.mongo.core.expression.BsonProjection;
import com.holonplatform.datastore.mongo.core.expression.BsonQuery;
import com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.expression.Value;
import com.holonplatform.datastore.mongo.core.internal.support.IdUpdateDocument;
import com.holonplatform.datastore.mongo.core.internal.support.ResolvedDocument;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.Projections;
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
		return updateResult.getModifiedCount();
	}

	/**
	 * Build the update expression for given {@link BulkUpdateOperationConfiguration}.
	 * @param context Resolution context
	 * @param configuration Operation configuration
	 * @return Update expression
	 */
	public static Bson getUpdateExpression(MongoResolutionContext<?> context,
			BulkUpdateOperationConfiguration configuration) {
		// resolve values
		final Map<Path<?>, TypedExpression<?>> values = configuration.getValues();
		List<Bson> updates = new ArrayList<>(values.size());
		for (Entry<Path<?>, TypedExpression<?>> value : values.entrySet()) {
			// child update context
			final MongoResolutionContext<?> subContext = context.childContextForUpdate(value.getKey());

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
	 * Resolve given {@link PropertyBox} values as Documents.
	 * @param context Resolution context
	 * @param values Values to resolve
	 * @return Resolved {@link PropertyBox} ad {@link Document} pairs
	 */
	public static List<ResolvedDocument> resolveDocumentValues(MongoDocumentContext<?> context,
			List<PropertyBox> values) {
		if (values == null || values.isEmpty()) {
			return Collections.emptyList();
		}
		final List<ResolvedDocument> documentValues = new ArrayList<>(values.size());
		values.forEach(v -> {
			documentValues.add(ResolvedDocument.create(v,
					context.resolveOrFail(PropertyBoxValue.create(v), DocumentValue.class).getValue()));
		});
		return documentValues;
	}

	/**
	 * Get the document which can be used to update the id property field.
	 * @param context Document context
	 * @param insertedId Inserted document id
	 * @return Optional document which can be used to uodate the id property field
	 */
	public static Optional<IdUpdateDocument> getIdUpdateDocument(MongoDocumentContext<?> context, ObjectId insertedId) {
		return getIdUpdateDocument(context, insertedId, getPropertyDocumentIdFieldName(context).orElse(null));
	}

	/**
	 * Get the document which can be used to update the id property field.
	 * @param context Document context
	 * @param insertedId Inserted document id
	 * @param fieldName The id property field name
	 * @return Optional document which can be used to uodate the id property field
	 */
	public static Optional<IdUpdateDocument> getIdUpdateDocument(MongoDocumentContext<?> context, ObjectId insertedId,
			String fieldName) {
		if (fieldName != null) {
			Class<?> type = context.getDocumentIdProperty().map(p -> p.getType()).orElse(null);
			if (type != null) {
				Document document = new Document("$set",
						new Document(fieldName,
								context.resolveOrFail(
										Value.create(context.getDocumentIdResolver().decode(insertedId, type)),
										FieldValue.class).getValue()));
				return Optional.of(IdUpdateDocument.create(fieldName, type, document));
			}
		}
		return Optional.empty();
	}

	/**
	 * Get the field name which corresponds to the id property, only if the field name is not equal to the default
	 * document id field name.
	 * @param context Document context
	 * @return Optional id field name
	 */
	public static Optional<String> getPropertyDocumentIdFieldName(MongoDocumentContext<?> context) {
		return context.getDocumentIdPath().flatMap(path -> context.resolve(path, FieldName.class))
				.map(fn -> fn.getFieldName()).filter(name -> !MongoDocumentContext.ID_FIELD_NAME.equals(name));
	}

	/**
	 * Check generated document id after an insert type operation, setting the inserted keys using given
	 * {@link OperationResult} builder.
	 * @param builder OperationResult builder
	 * @param documentContext Document context
	 * @param configuration Operation configuration
	 * @param document Document result of the insert operation
	 * @param value Original {@link PropertyBox} value
	 * @return The document id to use to update the id property value, if applicable
	 */
	@SuppressWarnings("unchecked")
	public static Optional<ObjectId> checkInsertedKeys(OperationResult.Builder builder,
			MongoDocumentContext<?> documentContext, DatastoreOperationConfiguration configuration, Document document,
			PropertyBox value) {
		// check inserted keys
		if (document.containsKey(MongoDocumentContext.ID_FIELD_NAME)) {
			// get document id value
			final ObjectId oid = document.getObjectId(MongoDocumentContext.ID_FIELD_NAME);
			if (oid != null) {

				boolean idPropertyHasValue = documentContext.getDocumentIdProperty().map(p -> value.containsValue(p))
						.orElse(false);

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

				if (!idPropertyHasValue) {
					return Optional.of(oid);
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Check generated document id after an insert type operation.
	 * @param documentContext Document context
	 * @param configuration Operation configuration
	 * @param documentValues Inserted documents and corresponding {@link PropertyBox} values
	 * @return The document ids to use to update the id property value, if applicable
	 */
	@SuppressWarnings("unchecked")
	public static List<ObjectId> checkInsertedKeys(MongoDocumentContext<?> documentContext,
			DatastoreOperationConfiguration configuration, List<ResolvedDocument> documentValues) {
		final List<ObjectId> ids = new LinkedList<>();

		for (ResolvedDocument rd : documentValues) {
			final Document document = rd.getDocument();
			if (document.containsKey(MongoDocumentContext.ID_FIELD_NAME)) {
				// get document id value
				final ObjectId oid = document.getObjectId(MongoDocumentContext.ID_FIELD_NAME);
				if (oid != null) {
					ids.add(oid);
				}
			}
		}

		// check inserted keys
		if (configuration.hasWriteOption(DefaultWriteOption.BRING_BACK_GENERATED_IDS)) {
			documentContext.getDocumentIdProperty().ifPresent(idProperty -> {
				for (ResolvedDocument rd : documentValues) {
					final ObjectId oid = rd.getDocument().getObjectId(MongoDocumentContext.ID_FIELD_NAME);
					if (oid != null) {
						final Object idPropertyValue = documentContext.getDocumentIdResolver().decode(oid,
								idProperty.getType());
						if (rd.getValue().contains(idProperty)) {
							rd.getValue().setValue((Property<Object>) idProperty, idPropertyValue);
						}
					}
				}
			});
		}
		return ids;
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
	public static void checkUpsertedKey(OperationResult.Builder builder, MongoDocumentContext<?> documentContext,
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
	 * Configure a <em>find</em> query using given configurator.
	 * @param query Query definition (not null)
	 * @param configurator Operation configurator (not null)
	 * @return The configured projection, if any
	 */
	public static Optional<Bson> configure(BsonQuery query, FindOperationConfigurator configurator) {
		ObjectUtils.argumentNotNull(query, "Query must be not null");
		ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");

		// definition
		final BsonQueryDefinition definition = query.getDefinition();
		// filter
		definition.getFilter().map(f -> f.getExpression()).ifPresent(f -> configurator.filter(f));
		// sort
		definition.getSort().ifPresent(s -> configurator.sort(s));
		// limit-offset
		definition.getLimit().ifPresent(l -> configurator.limit(l));
		definition.getOffset().ifPresent(o -> configurator.skip(o));
		// timeout
		definition.getTimeout().ifPresent(t -> configurator.maxTime(t, definition.getTimeoutUnit()));
		// cursor
		definition.getCursorType().ifPresent(c -> configurator.cursorType(c));
		// batch size
		definition.getBatchSize().ifPresent(b -> configurator.batchSize(b));
		// collation
		definition.getCollation().ifPresent(c -> configurator.collation(c));
		// comment
		definition.getComment().ifPresent(c -> configurator.comment(c));
		// hint
		definition.getHint().ifPresent(h -> configurator.hint(h));
		// max-min
		definition.getMax().ifPresent(m -> configurator.max(m));
		definition.getMin().ifPresent(m -> configurator.min(m));
		// partial
		if (definition.isPartial()) {
			configurator.partial(true);
		}
		// return key
		if (definition.isReturnKey()) {
			configurator.returnKey(true);
		}
		// record id
		if (definition.isShowRecordId()) {
			configurator.showRecordId(true);
		}

		// projection
		Optional<Bson> projection = query.getProjection().filter(p -> !p.isEmpty())
				.map(p -> Projections.include(p.getFieldNames()));
		projection.ifPresent(p -> configurator.projection(p));

		return projection;
	}

	/**
	 * Configure a <em>distinct</em> query using given configurator.
	 * @param query Query definition (not null)
	 * @param configurator Operation configurator (not null)
	 */
	public static void configure(BsonQuery query, DistinctOperationConfigurator configurator) {
		ObjectUtils.argumentNotNull(query, "Query must be not null");
		ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");

		// definition
		final BsonQueryDefinition definition = query.getDefinition();
		// filter
		definition.getFilter().map(f -> f.getExpression()).ifPresent(f -> configurator.filter(f));
		// timeout
		definition.getTimeout().ifPresent(t -> configurator.maxTime(t, definition.getTimeoutUnit()));
		// batch size
		definition.getBatchSize().ifPresent(b -> configurator.batchSize(b));
		// collation
		definition.getCollation().ifPresent(c -> configurator.collation(c));
	}

	/**
	 * Configure an <em>aggregate</em> query using given configurator.
	 * @param query Query definition (not null)
	 * @param configurator Operation configurator (not null)
	 */
	public static void configure(BsonQuery query, AggregateOperationConfigurator configurator) {
		ObjectUtils.argumentNotNull(query, "Query must be not null");
		ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");

		final BsonQueryDefinition definition = query.getDefinition();

		// timeout
		definition.getTimeout().ifPresent(t -> configurator.maxTime(t, definition.getTimeoutUnit()));
		// batch size
		definition.getBatchSize().ifPresent(b -> configurator.batchSize(b));
		// collation
		definition.getCollation().ifPresent(c -> configurator.collation(c));
		// comment
		definition.getComment().ifPresent(c -> configurator.comment(c));
		// hint
		definition.getHint().ifPresent(h -> configurator.hint(h));
	}

	/**
	 * Build an <em>aggregate</em> query pipeline.
	 * @param query Query definition (not null)
	 * @return Aggregation pipeline
	 */
	public static List<Bson> buildAggregationPipeline(BsonQuery query) {
		ObjectUtils.argumentNotNull(query, "Query must be not null");

		// aggregation pipeline
		List<Bson> pipeline = new LinkedList<>();

		// definition
		final BsonQueryDefinition definition = query.getDefinition();

		// group
		boolean hasGroupStage = definition.getGroup().isPresent();

		// ------ final projection stage

		// check count all
		final boolean countAllProjection = query.getProjection().map(p -> p.isCountAllProjection()).orElse(false);

		if (!hasGroupStage) {
			// check count by field projection
			String countByField = query.getProjection().flatMap(p -> p.isCountFieldProjection()).orElse(null);
			if (countByField != null) {
				// exclude nulls
				pipeline.add(Aggregates.match(Filters.and(Filters.exists(countByField),
						Filters.not(Filters.type(countByField, BsonType.NULL)))));
				if (!query.getDefinition().isDistinct()) {
					// all
					pipeline.add(Aggregates.group(1, new BsonField("count", new Document("$sum", 1))));
				} else {
					// distinct
					pipeline.add(Aggregates.group("$" + countByField));
					pipeline.add(Aggregates.group(1, new BsonField("count", new Document("$sum", 1))));
				}
				pipeline.add(Aggregates.project(Projections.include("count")));
				hasGroupStage = true;
			}
			// check aggregation projection
			else if (query.getProjection().map(p -> p.hasAggregationFunctions()).orElse(false)) {
				// add a group stage
				List<BsonField> accumulators = getGroupAccumulators(query.getProjection().orElse(null));
				if (!accumulators.isEmpty()) {
					pipeline.add(Aggregates.group(null, accumulators));
					hasGroupStage = true;
				}
			}

		}

		final Optional<Bson> projection;
		if (!hasGroupStage) {
			// projection with expressions
			projection = query.getProjection().filter(f -> !f.isEmpty())
					.map(p -> Projections.fields(p.getFieldProjections()));
		} else {
			// projection using field names
			projection = query.getProjection().map(p -> p.getFieldNames()).filter(f -> !f.isEmpty())
					.map(fieldNames -> Projections.include(fieldNames));
		}

		final Optional<Bson> filterProjection = countAllProjection ? Optional.empty() : projection;

		// ------ match
		definition.getFilter().ifPresent(f -> pipeline.addAll(buildAggregationFilter(f, filterProjection)));

		// ------ group
		definition.getGroup().ifPresent(g -> {
			List<BsonField> accumulators = getGroupAccumulators(query.getProjection().orElse(null));
			if (!accumulators.isEmpty()) {
				pipeline.add(Aggregates.group(g, accumulators));
			} else {
				pipeline.add(Aggregates.group(g));
			}
			// group filter
			definition.getGroupFilter().ifPresent(f -> pipeline.addAll(buildAggregationFilter(f, filterProjection)));
		});

		// ------ sort
		definition.getSort().ifPresent(s -> pipeline.add(Aggregates.sort(s)));

		// ------ limit
		definition.getLimit().ifPresent(l -> pipeline.add(Aggregates.limit(l)));

		// ------ skip
		definition.getOffset().ifPresent(o -> pipeline.add(Aggregates.skip(o)));

		// ------ project
		if (countAllProjection) {
			pipeline.add(Aggregates.count());
		} else {
			projection.ifPresent(p -> pipeline.add(Aggregates.project(p)));
		}

		return pipeline;
	}

	/**
	 * Get the group stage accumulator fields and expressions.
	 * @param projection Projection
	 * @return Accumulator fields and expression
	 */
	private static List<BsonField> getGroupAccumulators(BsonProjection<?> projection) {
		final List<BsonField> fieldAccumulators = new LinkedList<>();
		if (projection != null) {
			for (Entry<String, Bson> field : projection.getFields().entrySet()) {
				if (field.getValue() != null) {
					fieldAccumulators.add(new BsonField(field.getKey(), field.getValue()));
				} else {
					fieldAccumulators.add(new BsonField(field.getKey(), new Document("$first", "$" + field.getKey())));
				}
			}
		}
		return fieldAccumulators;
	}

	/**
	 * Process given filter and returns the aggregation pipeline stages to add.
	 * @param filter Filter
	 * @param finalProjection Optional final projection
	 * @return Aggregation pipeline stage
	 */
	private static List<Bson> buildAggregationFilter(BsonFilterExpression filter, Optional<Bson> finalProjection) {
		final List<Bson> pipeline = new LinkedList<>();
		if (filter.getPipeline().isPresent()) {
			final FilterAggregationPipeline fap = filter.getPipeline().get();
			fap.getProjection().ifPresent(pj -> {
				pipeline.add(Aggregates.project(finalProjection.map(fp -> Projections.fields(fp, pj)).orElse(pj)));
			});
			pipeline.add(Aggregates.match(fap.getMatch()));

		} else {
			pipeline.add(Aggregates.match(filter.getExpression()));
		}
		return pipeline;
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
	public static String traceQuery(MongoContext<?> context, BsonQuery query, Bson projection) {
		final StringBuilder sb = new StringBuilder();

		sb.append("Collection name: ");
		sb.append(query.getDefinition().getCollectionName());

		query.getDefinition().getFilter().map(f -> f.getExpression()).ifPresent(f -> {
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
	public static String traceAggregationPipeline(MongoContext<?> context, List<Bson> pipeline) {
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
	public static String traceUpdate(MongoContext<?> context, Optional<Bson> filter, Bson update) {
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
