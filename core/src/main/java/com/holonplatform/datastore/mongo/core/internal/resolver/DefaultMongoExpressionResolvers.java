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
		expressionResolvers.add(LiteralValueFieldResolver.INSTANCE);
		expressionResolvers.add(NullExpressionResolver.INSTANCE);
		expressionResolvers.add(CollectionExpressionResolver.INSTANCE);
		expressionResolvers.add(ConstantExpressionResolver.INSTANCE);
		expressionResolvers.add(FieldValuePathResolver.INSTANCE);
		expressionResolvers.add(PathValueFieldResolver.INSTANCE);
		expressionResolvers.add(DocumentPropertyBoxResolver.INSTANCE);
		expressionResolvers.add(PropertyBoxDocumentResolver.INSTANCE);
		expressionResolvers.add(VisitableQueryFilterResolver.INSTANCE);
		expressionResolvers.add(VisitableQuerySortResolver.INSTANCE);
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
