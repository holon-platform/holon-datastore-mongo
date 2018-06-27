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
package com.holonplatform.datastore.mongo.core.internal.resolver;

import java.util.ArrayList;
import java.util.List;

import com.holonplatform.datastore.mongo.core.internal.resolver.projection.BeanProjectionResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.projection.ConstantExpressionProjectionResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.projection.CountAllProjectionResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.projection.PropertySetProjectionResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.projection.QueryProjectionResolver;
import com.holonplatform.datastore.mongo.core.internal.resolver.projection.TypedExpressionProjectionResolver;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * Default resolvers.
 * 
 * @since 5.2.0
 */
public final class DefaultMongoExpressionResolvers {

	@SuppressWarnings("rawtypes")
	private static final List<MongoExpressionResolver> expressionResolvers = new ArrayList<>();

	static {
		expressionResolvers.add(DataTargetCollectionNameResolver.INSTANCE);
		expressionResolvers.add(FieldNamePathResolver.INSTANCE);
		expressionResolvers.add(PathFieldNameResolver.INSTANCE);
		expressionResolvers.add(QueryFunctionNameResolver.INSTANCE);
		expressionResolvers.add(NullExpressionResolver.INSTANCE);
		expressionResolvers.add(CollectionExpressionResolver.INSTANCE);
		expressionResolvers.add(ConstantExpressionResolver.INSTANCE);
		expressionResolvers.add(FieldValueResolver.INSTANCE);
		expressionResolvers.add(ValueResolver.INSTANCE);
		expressionResolvers.add(DocumentPropertyBoxResolver.INSTANCE);
		expressionResolvers.add(PropertyBoxDocumentResolver.INSTANCE);
		expressionResolvers.add(BsonQueryFilterResolver.INSTANCE);
		expressionResolvers.add(QueryFilterResolver.INSTANCE);
		expressionResolvers.add(QuerySortResolver.INSTANCE);
		expressionResolvers.add(VisitableQueryFilterResolver.INSTANCE);
		expressionResolvers.add(VisitableQuerySortResolver.INSTANCE);
		expressionResolvers.add(BsonFilterResolver.INSTANCE);
		expressionResolvers.add(QueryProjectionResolver.INSTANCE);
		expressionResolvers.add(ConstantExpressionProjectionResolver.INSTANCE);
		expressionResolvers.add(TypedExpressionProjectionResolver.INSTANCE);
		expressionResolvers.add(CountAllProjectionResolver.INSTANCE);
		expressionResolvers.add(PropertySetProjectionResolver.INSTANCE);
		expressionResolvers.add(BeanProjectionResolver.INSTANCE);
		expressionResolvers.add(QueryConfigurationResolver.INSTANCE);
		expressionResolvers.add(QueryOperationResolver.INSTANCE);
		expressionResolvers.add(UpdateFunctionResolver.INSTANCE);
		expressionResolvers.add(QueryFunctionResolver.INSTANCE);
	}

	/**
	 * Get the available expression resolvers.
	 * @return the expression resolvers
	 */
	@SuppressWarnings("rawtypes")
	public static List<MongoExpressionResolver> getExpressionresolvers() {
		return expressionResolvers;
	}

}
