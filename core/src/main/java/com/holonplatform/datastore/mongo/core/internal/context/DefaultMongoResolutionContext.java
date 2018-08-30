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
import java.util.function.Supplier;

import org.bson.codecs.configuration.CodecRegistry;

import com.holonplatform.core.Expression;
import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.ExpressionResolver.ResolutionContext;
import com.holonplatform.core.ExpressionResolverRegistry;
import com.holonplatform.core.Path;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.context.MongoContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentIdResolver;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.session.ClientSession;

/**
 * Default {@link MongoResolutionContext} implementation.
 * 
 * @param <S> Concrete ClientSession type
 *
 * @since 5.2.0
 */
public class DefaultMongoResolutionContext<S extends ClientSession> implements MongoResolutionContext<S> {

	protected final static Logger LOGGER = MongoDatastoreLogger.create();

	/**
	 * Expression resolvers
	 */
	private final ExpressionResolverRegistry expressionResolverRegistry = ExpressionResolverRegistry.create();

	/**
	 * Mongo context
	 */
	private final MongoContext<S> context;

	/**
	 * Optional parent context
	 */
	private final MongoResolutionContext<S> parent;

	/**
	 * Alias provder
	 */
	private final ExpressionAliasProvider expressionAliasProvider;

	/**
	 * Update mode
	 */
	private final boolean forUpdate;

	/**
	 * Update path
	 */
	private final Path<?> updatePath;

	/**
	 * Query operation type
	 */
	private QueryOperationType queryOperationType;

	/**
	 * Default constructor.
	 * @param context Mongo context (not null)
	 */
	public DefaultMongoResolutionContext(MongoContext<S> context) {
		this(context, null, false, null);
	}

	/**
	 * Constructor for update type context.
	 * @param context Mongo context (not null)
	 * @param forUpdate Whether this context is intended for an update type operation
	 */
	public DefaultMongoResolutionContext(MongoContext<S> context, boolean forUpdate) {
		this(context, null, forUpdate, null);
	}

	/**
	 * Constructor for update type context.
	 * @param context Mongo context (not null)
	 * @param updatePath Optional update path
	 */
	public DefaultMongoResolutionContext(MongoContext<S> context, Path<?> updatePath) {
		this(context, null, true, updatePath);
	}

	/**
	 * Constructor with parent composition context.
	 * @param parent Parent context (not null)
	 */
	public DefaultMongoResolutionContext(MongoResolutionContext<S> parent) {
		this(parent, parent, false, null);
	}

	/**
	 * Constructor with parent composition context.
	 * @param parent Parent context (not null)
	 * @param forUpdate Whether this context is intended for an update type operation
	 */
	public DefaultMongoResolutionContext(MongoResolutionContext<S> parent, boolean forUpdate) {
		this(parent, parent, forUpdate, null);
	}

	/**
	 * Constructor with parent composition context.
	 * @param parent Parent context (not null)
	 * @param updatePath Optional update path
	 * @param forUpdate Whether this context is intended for an update type operation
	 */
	public DefaultMongoResolutionContext(MongoResolutionContext<S> parent, Path<?> updatePath) {
		this(parent, parent, true, updatePath);
	}

	/**
	 * Internal constructor.
	 * @param context Mongo context (not null)
	 * @param parent Optional parent context
	 * @param forUpdate Whether this context is intended for an update type operation
	 * @param updatePath Optional update {@link Path}
	 */
	protected DefaultMongoResolutionContext(MongoContext<S> context, MongoResolutionContext<S> parent,
			boolean forUpdate, Path<?> updatePath) {
		super();
		ObjectUtils.argumentNotNull(context, "Mongo context must be not null");
		this.context = context;
		this.parent = parent;
		this.forUpdate = forUpdate;
		this.updatePath = updatePath;
		// inherit resolvers
		addExpressionResolvers(context.getExpressionResolvers());
		// parent config
		if (parent != null) {
			expressionAliasProvider = parent.getExpressionAliasProvider();
		} else {
			expressionAliasProvider = ExpressionAliasProvider.create();
		}
	}

	/**
	 * Get the Mongo context.
	 * @return the Mongo context
	 */
	protected MongoContext<S> getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoResolutionContext#getParent()
	 */
	@Override
	public Optional<MongoResolutionContext<S>> getParent() {
		return Optional.ofNullable(parent);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoResolutionContext#getExpressionAliasProvider()
	 */
	@Override
	public ExpressionAliasProvider getExpressionAliasProvider() {
		return expressionAliasProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoResolutionContext#isForUpdate()
	 */
	@Override
	public boolean isForUpdate() {
		return forUpdate;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoResolutionContext#getUpdatePath()
	 */
	@Override
	public Optional<Path<?>> getUpdatePath() {
		return Optional.ofNullable(updatePath);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoQueryContext#getQueryOperationType()
	 */
	@Override
	public Optional<QueryOperationType> getQueryOperationType() {
		if (parent != null) {
			return parent.getQueryOperationType();
		}
		return Optional.ofNullable(queryOperationType);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoQueryContext#setQueryOperationType(com.holonplatform.
	 * datastore.mongo.core.document.QueryOperationType)
	 */
	@Override
	public void setQueryOperationType(QueryOperationType queryOperationType) {
		if (parent != null) {
			parent.setQueryOperationType(queryOperationType);
		}
		this.queryOperationType = queryOperationType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoResolutionContext#childContext()
	 */
	@Override
	public MongoResolutionContext<S> childContext() {
		return new DefaultMongoResolutionContext<>(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoResolutionContext#childContextForUpdate()
	 */
	@Override
	public MongoResolutionContext<S> childContextForUpdate() {
		return new DefaultMongoResolutionContext<>(this, true);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.context.MongoResolutionContext#childContextForUpdate(com.holonplatform.
	 * core.Path)
	 */
	@Override
	public MongoResolutionContext<S> childContextForUpdate(Path<?> updatePath) {
		return new DefaultMongoResolutionContext<>(this, updatePath);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.context.MongoResolutionContext#documentContext(com.holonplatform.core.
	 * property.PropertySet, boolean)
	 */
	@Override
	public MongoDocumentContext<S> documentContext(PropertySet<?> propertySet, boolean resolveDocumentId) {
		return new DefaultMongoDocumentContext<>(this, propertySet, resolveDocumentId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getClientSession()
	 */
	@Override
	public Optional<S> getClientSession() {
		return getContext().getClientSession();
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
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDatabaseCodecRegistry()
	 */
	@Override
	public CodecRegistry getDatabaseCodecRegistry() {
		return getContext().getDatabaseCodecRegistry();
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
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDefaultReadPreference()
	 */
	@Override
	public Optional<ReadPreference> getDefaultReadPreference() {
		return getContext().getDefaultReadPreference();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDefaultReadConcern()
	 */
	@Override
	public Optional<ReadConcern> getDefaultReadConcern() {
		return getContext().getDefaultReadConcern();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDefaultWriteConcern()
	 */
	@Override
	public Optional<WriteConcern> getDefaultWriteConcern() {
		return getContext().getDefaultWriteConcern();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#trace(java.lang.String)
	 */
	@Override
	public void trace(String title, Supplier<String> json) {
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
