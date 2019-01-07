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
package com.holonplatform.datastore.mongo.examples;

import java.util.Optional;

import org.bson.types.ObjectId;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.query.Query;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.sync.MongoDatastore;
import com.mongodb.client.model.Filters;

@SuppressWarnings({ "unused", "serial" })
public class ExampleMongoDatastoreExtension {

	class MyExpressionResolver implements ExpressionResolver<BsonExpression, BsonExpression> {

		@Override
		public Optional<BsonExpression> resolve(BsonExpression expression, ResolutionContext context)
				throws InvalidExpressionException {
			return Optional.of(expression);
		}

		@Override
		public Class<? extends BsonExpression> getExpressionType() {
			return BsonExpression.class;
		}

		@Override
		public Class<? extends BsonExpression> getResolvedType() {
			return BsonExpression.class;
		}

	}

	public void expressionResolverRegistration() {
		// tag::expreg1[]
		Datastore datastore = MongoDatastore.builder() //
				.withExpressionResolver(new MyExpressionResolver()) // <1>
				.build();
		// end::expreg1[]

		// tag::expreg2[]
		datastore.addExpressionResolver(new MyExpressionResolver()); // <1>
		// end::expreg2[]

		// tag::expreg3[]
		long result = datastore.query().target(DataTarget.named("test")) //
				.withExpressionResolver(new MyExpressionResolver()) // <1>
				.count();
		// end::expreg3[]
	}

	// tag::expres1[]
	class IdIs implements QueryFilter {

		private final ObjectId value;

		public IdIs(ObjectId value) {
			this.value = value;
		}

		public ObjectId getValue() {
			return value;
		}

		@Override
		public void validate() throws InvalidExpressionException {
			if (value == null) {
				throw new InvalidExpressionException("Id value must be not null");
			}
		}

	}
	// end::expres1[]

	public void expres2() {
		// tag::expres2[]
		final ExpressionResolver<IdIs, BsonExpression> keyIsResolver = ExpressionResolver.create( //
				IdIs.class, // <1>
				BsonExpression.class, // <2>
				(keyIs, ctx) -> Optional.of(BsonExpression.create(Filters.eq("_id", keyIs.getValue())))); // <3>
		// end::expres2[]

		// tag::expres3[]
		Datastore datastore = MongoDatastore.builder().withExpressionResolver(keyIsResolver) // <1>
				.build();

		Query query = datastore.query().filter(new IdIs(new ObjectId("xxxx"))); // <2>
		// end::expres3[]
	}

}
