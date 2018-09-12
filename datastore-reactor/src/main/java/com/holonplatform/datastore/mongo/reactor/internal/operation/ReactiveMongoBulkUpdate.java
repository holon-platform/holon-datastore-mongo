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
import org.bson.conversions.Bson;

import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.Datastore.OperationType;
import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.core.datastore.operation.commons.BulkUpdateOperationConfiguration;
import com.holonplatform.datastore.mongo.core.async.config.AsyncMongoDatastoreCommodityContext;
import com.holonplatform.datastore.mongo.core.async.internal.config.AsyncMongoCollectionConfigurator;
import com.holonplatform.datastore.mongo.core.async.internal.support.AsyncOperationContext;
import com.holonplatform.datastore.mongo.core.async.internal.support.AsyncOperationResultContext;
import com.holonplatform.datastore.mongo.core.context.MongoOperationContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.CollectionName;
import com.holonplatform.datastore.mongo.core.internal.operation.MongoOperations;
import com.holonplatform.reactor.datastore.internal.operation.AbstractReactiveBulkUpdate;
import com.holonplatform.reactor.datastore.operation.ReactiveBulkUpdate;
import com.mongodb.async.client.ClientSession;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

import reactor.core.publisher.Mono;

/**
 * Mongo {@link ReactiveBulkUpdate} implementation.
 * 
 * @since 5.2.0
 */
public class ReactiveMongoBulkUpdate extends AbstractReactiveBulkUpdate {

	private static final long serialVersionUID = -314880174513967652L;

	// Commodity factory
	@SuppressWarnings("serial")
	public static final DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, ReactiveBulkUpdate> FACTORY = new DatastoreCommodityFactory<AsyncMongoDatastoreCommodityContext, ReactiveBulkUpdate>() {

		@Override
		public Class<? extends ReactiveBulkUpdate> getCommodityType() {
			return ReactiveBulkUpdate.class;
		}

		@Override
		public ReactiveBulkUpdate createCommodity(AsyncMongoDatastoreCommodityContext context)
				throws CommodityConfigurationException {
			return new ReactiveMongoBulkUpdate(context);
		}
	};

	private final MongoOperationContext<MongoDatabase, ClientSession> operationContext;

	public ReactiveMongoBulkUpdate(MongoOperationContext<MongoDatabase, ClientSession> operationContext) {
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
			final BulkUpdateOperationConfiguration configuration = getConfiguration();
			// validate
			configuration.validate();

			// context
			final MongoResolutionContext<ClientSession> context = MongoResolutionContext.create(operationContext);
			context.addExpressionResolvers(configuration.getExpressionResolvers());

			// filter
			final Optional<Bson> filter = configuration.getFilter()
					.map(f -> context.resolveOrFail(f, BsonExpression.class).getValue());

			// resolve collection name
			final String collectionName = context.resolveOrFail(configuration.getTarget(), CollectionName.class)
					.getName();
			// get and configure collection
			final MongoCollection<Document> collection = operationContext.withDatabase(database -> {
				return AsyncMongoCollectionConfigurator.configureWrite(database.getCollection(collectionName), context,
						configuration);
			});

			// update expression
			final Bson update = MongoOperations.getUpdateExpression(context, configuration);

			// trace
			context.trace("Update documents", MongoOperations.traceUpdate(context, filter, update));

			// done
			return AsyncOperationContext.create(context, collection, configuration, filter.orElse(null));
		}).flatMap(context -> {
			// update expression
			final Bson update = MongoOperations.getUpdateExpression(context.getContext(),
					(BulkUpdateOperationConfiguration) context.getConfiguration());
			// check client session available
			return context.getContext().getClientSession().map(session -> {
				return Mono.<AsyncOperationResultContext<MongoResolutionContext<ClientSession>>>create(sink -> {
					context.getCollection().updateMany(session, context.getFilter().orElse(null), update,
							MongoOperations.getUpdateOptions(context.getConfiguration(), false), (result, error) -> {
								if (error != null) {
									sink.error(error);
								} else {
									sink.success(AsyncOperationResultContext.create(context.getContext(),
											context.getCollection(), context.getConfiguration(),
											result.getModifiedCount(), OperationType.UPDATE));
								}
							});
				});
			}).orElseGet(() -> {
				return Mono.<AsyncOperationResultContext<MongoResolutionContext<ClientSession>>>create(sink -> {
					context.getCollection().updateMany(context.getFilter().orElse(null), update,
							MongoOperations.getUpdateOptions(context.getConfiguration(), false), (result, error) -> {
								if (error != null) {
									sink.error(error);
								} else {
									sink.success(AsyncOperationResultContext.create(context.getContext(),
											context.getCollection(), context.getConfiguration(),
											result.getModifiedCount(), OperationType.UPDATE));
								}
							});
				});
			});
		}).map(context -> {
			// result
			return OperationResult.builder().type(OperationType.UPDATE).affectedCount(context.getAffectedCount())
					.build();
		});
	}

}
