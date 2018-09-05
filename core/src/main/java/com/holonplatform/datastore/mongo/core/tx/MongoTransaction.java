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

import com.holonplatform.core.datastore.transaction.Transaction;
import com.holonplatform.core.datastore.transaction.TransactionConfiguration;
import com.holonplatform.core.datastore.transaction.TransactionStatus;
import com.mongodb.session.ClientSession;

/**
 * MongoDB {@link Transaction}.
 * 
 * @param <S> Concrete ClientSession type
 *
 * @since 5.2.0
 */
public interface MongoTransaction<S extends ClientSession> extends TransactionStatus {

	/**
	 * Get the {@link ClientSession} bound to this transaction.
	 * @return The transaction session
	 */
	S getSession();

	/**
	 * Get the transaction configuration.
	 * @return the transaction configuration
	 */
	TransactionConfiguration getConfiguration();

}
