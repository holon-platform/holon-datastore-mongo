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
package com.holonplatform.datastore.mongo.core.tx;

import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.core.datastore.transaction.TransactionStatus.TransactionException;
import com.mongodb.session.ClientSession;

/**
 * Factory to create and configure new {@link MongoTransaction} implementation using a {@link ClientSession} and a
 * {@link TransactionConfiguration} definition.
 * 
 * @param <S> Concrete ClientSession type
 * @param <TX> Concrete transaction type
 *
 * @since 5.2.0
 */
public interface MongoTransactionFactory<S extends ClientSession, TX extends MongoTransaction<S>> {

	/**
	 * Create a new {@link MongoTransaction}.
	 * @param session The client sesion to use
	 * @param configuration The transaction configuration
	 * @return A new {@link MongoTransaction} instance
	 * @throws TransactionException If an error occurred
	 */
	TX createTransaction(S session, TransactionConfiguration configuration)
			throws TransactionException;

}
