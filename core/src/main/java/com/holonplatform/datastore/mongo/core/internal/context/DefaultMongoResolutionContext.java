/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.datastore.mongo.core.internal.context;

import java.util.Optional;

import com.holonplatform.core.Expression;
import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.ExpressionResolver.ResolutionContext;
import com.holonplatform.core.ExpressionResolverRegistry;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.context.MongoContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentIdResolver;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;

/**
 * Default {@link MongoResolutionContext} implementation.
 *
 * @since 5.2.0
 */
public class DefaultMongoResolutionContext implements MongoResolutionContext {

	protected final static Logger LOGGER = MongoDatastoreLogger.create();

	/**
	 * Expression resolvers
	 */
	private final ExpressionResolverRegistry expressionResolverRegistry = ExpressionResolverRegistry.create();

	/**
	 * Mongo context
	 */
	private final MongoContext context;

	/**
	 * Optional parent context
	 */
	private final MongoResolutionContext parent;

	/**
	 * Default constructor.
	 * @param context Mongo context (not null)
	 */
	public DefaultMongoResolutionContext(MongoContext context) {
		super();
		ObjectUtils.argumentNotNull(context, "Mongo context must be not null");
		this.context = context;
		this.parent = null;
		// inherit resolvers
		addExpressionResolvers(context.getExpressionResolvers());
	}

	/**
	 * Constructor with parent composition context.
	 * @param parent Parent context (not null)
	 */
	public DefaultMongoResolutionContext(MongoResolutionContext parent) {
		super();
		ObjectUtils.argumentNotNull(parent, "Parent context must be not null");
		this.context = parent;
		this.parent = parent;
		// inherit resolvers
		addExpressionResolvers(parent.getExpressionResolvers());
	}

	/**
	 * Get the Mongo context.
	 * @return the Mongo context
	 */
	protected MongoContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoResolutionContext#getParent()
	 */
	@Override
	public Optional<MongoResolutionContext> getParent() {
		return Optional.ofNullable(parent);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoResolutionContext#childContext()
	 */
	@Override
	public MongoResolutionContext childContext() {
		return new DefaultMongoResolutionContext(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.context.MongoResolutionContext#documentContext(com.holonplatform.core.
	 * property.PropertySet, boolean)
	 */
	@Override
	public MongoDocumentContext documentContext(PropertySet<?> propertySet, boolean resolveDocumentId) {
		return new DefaultMongoDocumentContext(this, propertySet, resolveDocumentId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDocumentIdPropertyResolver()
	 */
	@Override
	public DocumentIdResolver getDocumentIdResolver() {
		return getContext().getDocumentIdResolver();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDefaultEnumCodecStrategy()
	 */
	@Override
	public EnumCodecStrategy getDefaultEnumCodecStrategy() {
		return getContext().getDefaultEnumCodecStrategy();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#trace(java.lang.String)
	 */
	@Override
	public void trace(String title, String json) {
		getContext().trace(title, json);
	}

	// Expression resolvers

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver.ExpressionResolverHandler#getExpressionResolvers()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<ExpressionResolver> getExpressionResolvers() {
		return expressionResolverRegistry.getExpressionResolvers();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.ExpressionResolver.ExpressionResolverHandler#resolve(com.holonplatform.core.Expression,
	 * java.lang.Class, com.holonplatform.core.ExpressionResolver.ResolutionContext)
	 */
	@Override
	public <E extends Expression, R extends Expression> Optional<R> resolve(E expression, Class<R> resolutionType,
			ResolutionContext context) throws InvalidExpressionException {
		return expressionResolverRegistry.resolve(expression, resolutionType, context);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.ExpressionResolver.ExpressionResolverSupport#addExpressionResolver(com.holonplatform.core.
	 * ExpressionResolver)
	 */
	@Override
	public <E extends Expression, R extends Expression> void addExpressionResolver(
			ExpressionResolver<E, R> expressionResolver) {
		expressionResolverRegistry.addExpressionResolver(expressionResolver);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.ExpressionResolver.ExpressionResolverSupport#removeExpressionResolver(com.holonplatform.
	 * core.ExpressionResolver)
	 */
	@Override
	public <E extends Expression, R extends Expression> void removeExpressionResolver(
			ExpressionResolver<E, R> expressionResolver) {
		expressionResolverRegistry.removeExpressionResolver(expressionResolver);
	}

}
