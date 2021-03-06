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
package com.holonplatform.datastore.mongo.core.test.context;

import java.util.Optional;
import java.util.function.Supplier;

import org.bson.codecs.configuration.CodecRegistry;

import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.ExpressionResolverRegistry;
import com.holonplatform.core.internal.DefaultExpressionResolverRegistry;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.datastore.mongo.core.context.MongoContext;
import com.holonplatform.datastore.mongo.core.document.DocumentIdResolver;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.session.ClientSession;

public class MongoTestContext implements MongoContext<ClientSession> {

	private static final Logger LOGGER = MongoDatastoreLogger.create();

	private final ExpressionResolverRegistry registry = new DefaultExpressionResolverRegistry();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver.ExpressionResolverProvider#getExpressionResolvers()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<ExpressionResolver> getExpressionResolvers() {
		return registry.getExpressionResolvers();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDocumentIdResolver()
	 */
	@Override
	public DocumentIdResolver getDocumentIdResolver() {
		return DocumentIdResolver.getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDefaultEnumCodecStrategy()
	 */
	@Override
	public EnumCodecStrategy getDefaultEnumCodecStrategy() {
		return EnumCodecStrategy.getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDefaultReadPreference()
	 */
	@Override
	public Optional<ReadPreference> getDefaultReadPreference() {
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDefaultReadConcern()
	 */
	@Override
	public Optional<ReadConcern> getDefaultReadConcern() {
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDefaultWriteConcern()
	 */
	@Override
	public Optional<WriteConcern> getDefaultWriteConcern() {
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getDatabaseCodecRegistry()
	 */
	@Override
	public CodecRegistry getDatabaseCodecRegistry() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#trace(java.lang.String)
	 */
	@Override
	public void trace(String title, Supplier<String> json) {
		LOGGER.info(title + ": " + json.get());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoContext#getClientSession()
	 */
	@Override
	public Optional<ClientSession> getClientSession() {
		return Optional.empty();
	}

}
