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

import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.core.internal.datastore.transaction.AbstractDelegatedTransaction;
import com.holonplatform.datastore.mongo.core.tx.MongoTransaction;
import com.mongodb.session.ClientSession;

/**
 * Base {@link MongoTransaction} implementation which delegates its operations to another transaction.
 * 
 * @param <S> Concrete ClientSession type
 * @param <S> Concrete transaction type
 *
 * @since 5.2.0
 */
public class AbstractDelegatedMongoTransaction<S extends ClientSession, T extends MongoTransaction<S>>
		extends AbstractDelegatedTransaction<T> implements MongoTransaction<S> {

	public AbstractDelegatedMongoTransaction(T delegate) {
		super(delegate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.tx.MongoTransaction#getSession()
	 */
	@Override
	public S getSession() {
		return getDelegate().getSession();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.tx.MongoTransaction#getConfiguration()
	 */
	@Override
	public TransactionConfiguration getConfiguration() {
		return getDelegate().getConfiguration();
	}

}
