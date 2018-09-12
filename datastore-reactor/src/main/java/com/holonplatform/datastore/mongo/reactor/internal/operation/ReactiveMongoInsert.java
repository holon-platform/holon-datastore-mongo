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

import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.commons.PropertyBoxOperationConfiguration;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.async.internal.config.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.core.async.internal.support.AsyncPropertyBoxOperationContext;
import com.holonplatform.datastore.mongo.core.async.internal.support.AsyncPropertyBoxOperationResultContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.expression.DocumentValue;
import com.holonplatform.datastore.mongo.core.expression.PropertyBoxValue;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.holonplatform.datastore.mongo.core.internal.support.IdUpdateDocument;
import com.holonplatform.reactor.datastore.internal.operation.AbstractReactiveInsert;
import com.holonplatform.reactor.datastore.operation.ReactiveInsert;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Mono;

/**
 * MongoDB {@link ReactiveInsert}.
 *
 * @since 5.2.0
 */
public class ReactiveMongoInsert extends AbstractReactiveInsert {

	private static final long serialVersionUID = 5721530187597592446L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, ReactiveInsert> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, ReactiveInsert>() {

		@Override
		public Class<? extends ReactiveInsert> getCommodityType() {
			return ReactiveInsert.class;
		}

		@Override
		public ReactiveInsert createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new ReactiveMongoInsert(context);
		}
	};

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public ReactiveMongoInsert(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.operation.commons.ExecutableOperation#execute()
	 */
	@Override
	public Mono<OperationResult> execute() {
		return Mono.fromSupplier(() -> {
			// configuration
			final PropertyBoxOperationConfiguration configuration = getConfiguration();
			// validate
			configuration.validate();

			// build context
			final MongoDocumentContext<ClientSession> context = MongoDocumentContext.create(operationContext,
					configuration.getValue());
			context.addExpressionResolvers(configuration.getExpressionResolvers());

			// resolve collection name
			final String collectionName = context.resolveOrFail(configuration.getTarget(), CollectionName.class)
					.getName();
			// get and configure collection
			final MongoCollection<Document> collection = operationContext.withDatabase(database -> {
				return AsyncMongoCollectionConfigurator.configureWrite(database.getCollection(collectionName), context,
						configuration);
			});

			// document to insert
			final Document document = context
					.resolveOrFail(PropertyBoxValue.create(configuration.getValue()), DocumentValue.class).getValue();

			// done
			return AsyncPropertyBoxOperationContext.create(context, collection, configuration, configuration.getValue(),
					document);
		}).flatMap(ctx -> {
			// check client session available
			return ctx.getContext().getClientSession().map(session -> {
				return Mono.<AsyncPropertyBoxOperationResultContext>create(sink -> {
					ctx.getCollection().insertOne(session, ctx.requireDocument(),
							MongoOperations.getInsertOneOptions(ctx.getConfiguration()), (result, error) -> {
								if (error != null) {
									sink.error(error);
								} else {
									sink.success(AsyncPropertyBoxOperationResultContext.create(ctx.getContext(),
											ctx.getCollection(), ctx.getConfiguration(), 1, OperationType.INSERT,
											ctx.getValue(), ctx.requireDocument()));
								}
							});
				});
			}).orElseGet(() -> {
				return Mono.<AsyncPropertyBoxOperationResultContext>create(sink -> {
					ctx.getCollection().insertOne(ctx.requireDocument(),
							MongoOperations.getInsertOneOptions(ctx.getConfiguration()), (result, error) -> {
								if (error != null) {
									sink.error(error);
								} else {
									sink.success(AsyncPropertyBoxOperationResultContext.create(ctx.getContext(),
											ctx.getCollection(), ctx.getConfiguration(), 1, OperationType.INSERT,
											ctx.getValue(), ctx.requireDocument()));
								}
							});
				});
			});
		}).flatMap(ctx -> {

			// trace
			ctx.trace("Inserted document", ctx.requireDocument());

			// build operation result
			final OperationResult.Builder builder = OperationResult.builder().type(OperationType.INSERT)
					.affectedCount(1);

			// check inserted keys
			Optional<ObjectId> insertedId = MongoOperations.checkInsertedKeys(builder, ctx.getContext(),
					ctx.getConfiguration(), ctx.requireDocument(), ctx.getValue());

			final OperationResult result = builder.build();

			// check if the identifier property has to be updated with the document id value
			final IdUpdateDocument toUpdate = (!insertedId.isPresent()) ? null
					: MongoOperations.getIdUpdateDocument(ctx.getContext(), insertedId.get()).orElse(null);
			if (insertedId.isPresent() && toUpdate != null) {
				Mono<UpdateResult> upd = Mono.<UpdateResult>create(sink -> {
					ctx.getCollection().updateOne(Filters.eq(insertedId.get()), toUpdate.getUpdateDocument(),
							(ur, error) -> {
								if (error != null) {
									sink.error(error);
								} else {
									sink.success(ur);
									ctx.trace("Updated identifier property value", toUpdate.getUpdateDocument());
								}
							});
				});
				return upd.map(x -> result);
			}

			return Mono.just(result);
		});
	}

}
