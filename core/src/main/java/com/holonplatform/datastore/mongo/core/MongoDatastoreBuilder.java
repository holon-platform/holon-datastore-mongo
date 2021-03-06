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
package com.holonplatform.datastore.mongo.core;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;

import com.holonplatform.core.datastore.DatastoreOperations;
import com.holonplatform.datastore.mongo.core.document.EnumCodecStrategy;
import com.holonplatform.datastore.mongo.core.tx.MongoTransaction;
import com.holonplatform.datastore.mongo.core.tx.MongoTransactionFactory;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.session.ClientSession;

/**
 * Base MongoDB Datastore builder.
 * 
 * @param <D> {@link DatastoreOperations} type
 * @param <S> Concrete client session type
 * @param <TX> Concrete transaction type
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
public interface MongoDatastoreBuilder<D extends DatastoreOperations, S extends ClientSession, TX extends MongoTransaction<S>, B extends MongoDatastoreBuilder<D, S, TX, B>>
		extends DatastoreOperations.Builder<D, B> {

	/**
	 * Set the database name to use.
	 * @param database The database name to set (not null)
	 * @return this
	 */
	B database(String database);

	/**
	 * Add a {@link Codec} to be registered in the default Mongo Datastore codec registry.
	 * <p>
	 * The {@link Codec} will be available to encode/decode values for the database managed by the Mongo Datastore
	 * instance.
	 * </p>
	 * @param codec The {@link Codec} to add (not null)
	 * @return this
	 */
	B withCodec(Codec<?> codec);

	/**
	 * Add a {@link CodecProvider} to be registered in the default Mongo Datastore codec registry.
	 * <p>
	 * The {@link CodecProvider} will be available to encode/decode values for the database managed by the Mongo
	 * Datastore instance.
	 * </p>
	 * @param codecProvider The {@link CodecProvider} to add (not null)
	 * @return this
	 */
	B withCodecProvider(CodecProvider codecProvider);

	/**
	 * Set the default {@link ReadPreference} for query or data read operations.
	 * @param readPreference Default {@link ReadPreference} to use (not null)
	 * @return this
	 */
	B readPreference(ReadPreference readPreference);

	/**
	 * Set the default {@link ReadConcern} to declare the read operations isolation level.
	 * @param readConcern Default {@link ReadConcern} to use (not null)
	 * @return this
	 */
	B readConcern(ReadConcern readConcern);

	/**
	 * Set the default acknowledgment of write operations.
	 * @param writeConcern Default {@link WriteConcern} to use (not null)
	 * @return this
	 */
	B writeConcern(WriteConcern writeConcern);

	/**
	 * Set the default {@link EnumCodecStrategy} to use when encoding and decoding {@link Enum} type values.
	 * @param enumCodecStrategy The default EnumCodecStrategy to set
	 * @return this
	 */
	B enumCodecStrategy(EnumCodecStrategy enumCodecStrategy);

	/**
	 * Set the {@link MongoTransactionFactory} to use to provide {@link MongoTransaction} implementations.
	 * @param transactionFactory The transaction factory to set (not null)
	 * @return this
	 */
	B transactionFactory(MongoTransactionFactory<S, TX> transactionFactory);

}
