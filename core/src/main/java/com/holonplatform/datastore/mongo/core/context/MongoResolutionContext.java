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
package com.holonplatform.datastore.mongo.core.context;

import java.util.Optional;

import com.holonplatform.core.Expression;
import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.ExpressionResolver.ExpressionResolverSupport;
import com.holonplatform.core.ExpressionResolver.ResolutionContext;
import com.holonplatform.core.Path;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.internal.context.DefaultMongoResolutionContext;

/**
 * MongoDB Datastore expresion resolution context.
 * 
 * @since 5.2.0
 */
public interface MongoResolutionContext extends MongoContext, ResolutionContext, ExpressionResolverSupport {

	/**
	 * Get the parent context, if available.
	 * @return Optional parent context
	 */
	Optional<MongoResolutionContext> getParent();

	/**
	 * Get whether this context is intended for an update type operation.
	 * @return <code>true</code> if this context is intended for an update type operation, <code>false</code> otherwise
	 */
	boolean isForUpdate();

	/**
	 * If this context is intended for an update type operation, get the optional single {@link Path} to which the
	 * update operation refers.
	 * @return Optional {@link Path} to which the update operation refers
	 * @see #isForUpdate()
	 */
	Optional<Path<?>> getUpdatePath();

	/**
	 * Try to resolve given <code>expression</code> using current context resolvers to obtain a
	 * <code>resolutionType</code> type expression.
	 * <p>
	 * The resolved expression is validate using {@link Expression#validate()} before returning it to caller.
	 * </p>
	 * @param <E> Expression type
	 * @param <R> Resolution type
	 * @param expression Expression to resolve
	 * @param resolutionType Expression type to obtain
	 * @return Resolved expression
	 */
	default <E extends Expression, R extends Expression> Optional<R> resolve(E expression, Class<R> resolutionType)
			throws InvalidExpressionException {
		// resolve
		return resolve(expression, resolutionType, this).map(e -> {
			// validate
			e.validate();
			return e;
		});
	}

	/**
	 * Resolve given <code>expression</code> using current context resolvers to obtain a <code>resolutionType</code>
	 * type expression. If no {@link ExpressionResolver} is available to resolve given expression, an
	 * {@link InvalidExpressionException} is thrown.
	 * <p>
	 * The resolved expression is validate using {@link Expression#validate()} before returning it to caller.
	 * </p>
	 * @param <E> Expression type
	 * @param <R> Resolution type
	 * @param expression Expression to resolve
	 * @param resolutionType Expression type to obtain
	 * @return Resolved expression
	 * @throws InvalidExpressionException If an error occurred during resolution, or if no {@link ExpressionResolver} is
	 *         available to resolve given expression or if expression validation failed
	 */
	default <E extends Expression, R extends Expression> R resolveOrFail(E expression, Class<R> resolutionType) {
		return resolve(expression, resolutionType)
				.orElseThrow(() -> new InvalidExpressionException("Failed to resolve expression [" + expression + "]"));
	}

	// ------- Builders

	/**
	 * Create a new {@link MongoResolutionContext} as child of this context. This context will be setted as parent of
	 * the new context.
	 * @return A new {@link MongoResolutionContext} with this context as parent
	 */
	MongoResolutionContext childContext();

	/**
	 * Create a new {@link MongoResolutionContext} as child of this context for update type operations. This context
	 * will be setted as parent of the new context.
	 * @return A new {@link MongoResolutionContext} with this context as parent
	 */
	MongoResolutionContext childContextForUpdate();

	/**
	 * Create a new {@link MongoResolutionContext} as child of this context for update type operations. This context
	 * will be setted as parent of the new context.
	 * @param updatePath The path to which the update operation refers
	 * @return A new {@link MongoResolutionContext} with this context as parent
	 */
	MongoResolutionContext childContextForUpdate(Path<?> updatePath);

	/**
	 * Create a {@link MongoDocumentContext} as child of this context.
	 * @param propertySet The {@link PropertySet} to which the document is bound (not null)
	 * @return A new {@link MongoDocumentContext} instance
	 */
	default MongoDocumentContext documentContext(PropertySet<?> propertySet) {
		return documentContext(propertySet, true);
	}

	/**
	 * Create a {@link MongoDocumentContext} as child of this context.
	 * @param propertySet The {@link PropertySet} to which the document is bound (not null)
	 * @param resolveDocumentId Whether to resolve the document id property
	 * @return A new {@link MongoDocumentContext} instance
	 */
	MongoDocumentContext documentContext(PropertySet<?> propertySet, boolean resolveDocumentId);

	/**
	 * Create a new default {@link MongoResolutionContext}.
	 * @param context MongoContext to use (not null)
	 * @return A new {@link MongoResolutionContext}
	 */
	static MongoResolutionContext create(MongoContext context) {
		return new DefaultMongoResolutionContext(context);
	}

	/**
	 * Create a new default {@link MongoResolutionContext} for an update type operation.
	 * @param context MongoContext to use (not null)
	 * @return A new {@link MongoResolutionContext}
	 */
	static MongoResolutionContext createForUpdate(MongoContext context) {
		return new DefaultMongoResolutionContext(context, true);
	}

	/**
	 * Create a new default {@link MongoResolutionContext} for an update type operation, specifying the {@link Path} to
	 * which the update operation refers.
	 * @param context MongoContext to use (not null)
	 * @param updatePath Update operation {@link Path}
	 * @return A new {@link MongoResolutionContext}
	 */
	static MongoResolutionContext createForUpdate(MongoContext context, Path<?> updatePath) {
		return new DefaultMongoResolutionContext(context, updatePath);
	}

	/**
	 * Checks if given {@link ResolutionContext} is a {@link MongoResolutionContext}.
	 * @param context The context to check
	 * @return if given context is a {@link MongoResolutionContext}, it is returned as a {@link MongoResolutionContext}
	 *         type. Otherwise, an empty Optional is returned.
	 */
	static Optional<MongoResolutionContext> isMongoResolutionContext(ResolutionContext context) {
		if (context instanceof MongoResolutionContext) {
			return Optional.of((MongoResolutionContext) context);
		}
		return Optional.empty();
	}

}
