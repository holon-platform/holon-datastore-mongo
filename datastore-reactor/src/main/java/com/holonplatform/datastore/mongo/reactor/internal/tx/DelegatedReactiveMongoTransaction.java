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
package com.holonplatform.datastore.mongo.reactor.internal.tx;

import com.holonplatform.datastore.mongo.core.internal.tx.AbstractDelegatedMongoTransaction;
import com.holonplatform.datastore.mongo.reactor.tx.ReactiveMongoTransaction;
import com.mongodb.reactivestreams.client.ClientSession;

import reactor.core.publisher.Mono;

/**
 * An {@link ReactiveMongoTransaction} which delegates its operations to another transaction.
 *
 * @since 5.2.0
 */
public class DelegatedReactiveMongoTransaction extends
		AbstractDelegatedMongoTransaction<ClientSession, ReactiveMongoTransaction> implements ReactiveMongoTransaction {

	public DelegatedReactiveMongoTransaction(ReactiveMongoTransaction delegate) {
		super(delegate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.datastore.transaction.ReactiveTransaction#commit()
	 */
	@Override
	public Mono<Boolean> commit() {
		return getDelegate().commit();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.reactor.datastore.transaction.ReactiveTransaction#rollback()
	 */
	@Override
	public Mono<Void> rollback() {
		return getDelegate().rollback();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.tx.MongoTransaction#start()
	 */
	@Override
	public void start() throws TransactionException {
		throw new IllegalTransactionStatusException("A delegated transaction should not be started");
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.reactor.tx.ReactiveMongoTransaction#end()
	 */
	@Override
	public Mono<Void> end() throws TransactionException {
		throw new IllegalTransactionStatusException("A delegated transaction should not be ended");
	}

}
