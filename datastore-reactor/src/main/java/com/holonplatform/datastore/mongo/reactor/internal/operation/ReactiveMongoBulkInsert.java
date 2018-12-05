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
package com.holonplatform.datastore.mongo.reactor.internal.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.commons.BulkInsertOperationConfiguration;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.async.internal.config.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.core.async.internal.support.AsyncMultiPropertyBoxOperationResultContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.holonplatform.datastore.mongo.core.internal.support.ResolvedDocument;
import com.holonplatform.reactor.datastore.internal.operation.AbstractReactiveBulkInsert;
import com.holonplatform.reactor.datastore.operation.ReactiveBulkInsert;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import reactor.core.publisher.Mono;

/**
 * Mongo {@link ReactiveBulkInsert} implementation.
 * 
 * @since 5.2.0
 */
public class ReactiveMongoBulkInsert extends AbstractReactiveBulkInsert {

	private static final long serialVersionUID = -2408795803968570820L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, ReactiveBulkInsert> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, ReactiveBulkInsert>() {

		@Override
		public Class<? extends ReactiveBulkInsert> getCommodityType() {
			return ReactiveBulkInsert.class;
		}

		@Override
		public ReactiveBulkInsert createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new ReactiveMongoBulkInsert(context);
		}
	};

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public ReactiveMongoBulkInsert(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.commons.ExecutableOperation#execute()
	 */
	@Override
	public Mono<OperationResult> execute() {
		return Mono.fromCallable(() -> {
			// configuration
			final BulkInsertOperationConfiguration configuration = getConfiguration();
			// validate
			configuration.validate();

			// property set
			final PropertySet<?> propertySet = configuration.getPropertySet()
					.orElseThrow(() -> new InvalidExpressionException("Missing bulk insert operation property set"));

			// resolution context
			final MongoDocumentContext<ClientSession> context = MongoDocumentContext.create(operationContext,
					propertySet);
			context.addExpressionResolvers(configuration.getExpressionResolvers());

			// resolve collection name
			final String collectionName = context.resolveOrFail(configuration.getTarget(), CollectionName.class)
					.getName();
			// get and configure collection
			final MongoCollection<Document> collection = operationContext.withDatabase(database -> {
				return AsyncMongoCollectionConfigurator.configureWrite(database.getCollection(collectionName), context,
						configuration);
			});

			// encode documents
			final List<ResolvedDocument> documentValues = MongoOperations.resolveDocumentValues(context,
					configuration.getValues());

			// done
			return AsyncMultiPropertyBoxOperationResultContext.create(context, collection, configuration,
					documentValues.size(), OperationType.INSERT, documentValues);
		}).flatMap(context -> {
			// documents to insert
			final List<Document> documents = context.getValues().stream().map(v -> v.getDocument())
					.collect(Collectors.toList());
			// check client session available
			return context.getContext().getClientSession()
					.map(session -> Mono.from(context.getCollection().insertMany(session, documents,
							MongoOperations.getInsertManyOptions(context.getConfiguration()))).map(
									result -> context))
					.orElseGet(() -> Mono
							.from(context.getCollection().insertMany(documents,
									MongoOperations.getInsertManyOptions(context.getConfiguration())))
							.map(result -> context));
		}).flatMap(context -> {

			// check inserted keys
			List<ObjectId> insertedIds = MongoOperations.checkInsertedKeys(context.getContext(),
					context.getConfiguration(), context.getValues());

			// check if the identifier property has to be updated with the document ids values
			if (!insertedIds.isEmpty()) {
				final String fieldName = MongoOperations.getPropertyDocumentIdFieldName(context.getContext())
						.orElse(null);
				if (fieldName != null) {
					final List<Mono<UpdateResult>> updates = new ArrayList<>(insertedIds.size());
					for (ObjectId insertedId : insertedIds) {
						MongoOperations.getIdUpdateDocument(context.getContext(), insertedId, fieldName)
								.ifPresent(toUpdate -> {
									updates.add(Mono.from(context.getCollection().updateOne(Filters.eq(insertedId),
											toUpdate.getUpdateDocument())).doOnSuccess(s -> {
												context.trace("Updated identifier property value",
														toUpdate.getUpdateDocument());
											}));
								});
					}
					if (!updates.isEmpty()) {
						return Mono.zip(updates, urs -> context);
					}
				}
			}
			return Mono.just(context);
		}).map(context -> {
			// trace
			context.trace("Inserted documents",
					context.getValues().stream().map(v -> v.getDocument()).collect(Collectors.toList()));
			// result
			return OperationResult.builder().type(OperationType.INSERT).affectedCount(context.getAffectedCount())
					.build();
		});
	}

}
