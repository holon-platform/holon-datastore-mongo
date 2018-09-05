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
package com.holonplatform.datastore.mongo.core.internal.tx;

import java.util.Optional;

import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.datastore.transaction.AbstractTransaction;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.internal.logger.MongoDatastoreLogger;
import com.holonplatform.datastore.mongo.core.tx.MongoTransaction;
import com.holonplatform.datastore.mongo.core.tx.MongoTransactionOptions;
import com.mongodb.TransactionOptions;
import com.mongodb.session.ClientSession;

/**
 * Abstract {@link MongoTransaction} implementation.
 * 
 * @param <S> Concrete ClientSession type
 *
 * @since 5.2.0
 */
public abstract class AbstractMongoTransaction<S extends ClientSession> extends AbstractTransaction
		implements MongoTransaction<S> {

	protected static final Logger LOGGER = MongoDatastoreLogger.create();

	private final S session;

	private final TransactionConfiguration configuration;

	/**
	 * Constructor
	 * @param session Client session (not null)
	 * @param configuration Transaction configuration (not null)
	 */
	public AbstractMongoTransaction(S session, TransactionConfiguration configuration) {
		super(true);
		ObjectUtils.argumentNotNull(session, "Session must be not null");
		ObjectUtils.argumentNotNull(configuration, "TransactionConfiguration must be not null");
		this.session = session;
		this.configuration = configuration;
	}

	/**
	 * Get the MongoDB {@link TransactionOptions} using current transaction configuration.
	 * @return The transaction options
	 */
	protected TransactionOptions getTransactionOptions() {
		final Optional<MongoTransactionOptions> txOptions = getConfiguration().getTransactionOptions()
				.filter(o -> o instanceof MongoTransactionOptions).map(o -> (MongoTransactionOptions) o);

		final TransactionOptions.Builder transactionOptions = TransactionOptions.builder();
		txOptions.flatMap(o -> o.getReadConcern()).ifPresent(rc -> transactionOptions.readConcern(rc.getReadConcern()));
		txOptions.flatMap(o -> o.getWriteConcern()).flatMap(wc -> wc.getWriteConcern())
				.ifPresent(wc -> transactionOptions.writeConcern(wc));
		txOptions.flatMap(o -> o.getReadPreference()).flatMap(rp -> rp.getReadPreference())
				.ifPresent(rp -> transactionOptions.readPreference(rp));

		return transactionOptions.build();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.tx.MongoTransaction#getSession()
	 */
	@Override
	public S getSession() {
		return session;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.tx.MongoTransaction#getConfiguration()
	 */
	@Override
	public TransactionConfiguration getConfiguration() {
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MongoTransaction [" + hashCode() + "] - [session=" + session + ", configuration=" + configuration + "]";
	}

}
