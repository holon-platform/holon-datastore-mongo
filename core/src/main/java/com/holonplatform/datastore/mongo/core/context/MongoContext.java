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

import java.util.function.Supplier;

import com.holonplatform.core.ExpressionResolver.ExpressionResolverProvider;
import com.holonplatform.datastore.mongo.core.document.DocumentIdResolver;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;

/**
 * MongoDB Datastore base context.
 *
 * @since 5.2.0
 */
public interface MongoContext extends ExpressionResolverProvider {

	/**
	 * Get the {@link DocumentIdResolver} of this context.
	 * @return the {@link DocumentIdResolver}
	 */
	DocumentIdResolver getDocumentIdResolver();

	/**
	 * Get the default {@link EnumCodecStrategy}.
	 * @return the default {@link EnumCodecStrategy}s
	 */
	EnumCodecStrategy getDefaultEnumCodecStrategy();

	/**
	 * Trace given JSON expression.
	 * <p>
	 * If tracing is enabled, the JSON expression is logged using the <code>INFO</code> level, otherwise it is logged
	 * using the <code>DEBUG</code> level.
	 * </p>
	 * @param title Optional title
	 * @param json JSON to trace
	 */
	void trace(String title, Supplier<String> json);

	/**
	 * Trace given JSON expression.
	 * <p>
	 * If tracing is enabled, the JSON expression is logged using the <code>INFO</code> level, otherwise it is logged
	 * using the <code>DEBUG</code> level.
	 * </p>
	 * @param title Optional title
	 * @param json JSON to trace
	 */
	default void trace(String title, String json) {
		trace(title, () -> json);
	}

}
