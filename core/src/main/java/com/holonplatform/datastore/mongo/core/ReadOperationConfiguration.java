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

import java.util.concurrent.TimeUnit;

import org.bson.conversions.Bson;

import com.holonplatform.core.config.ConfigProperty;
import com.mongodb.CursorType;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.client.model.Collation;

/**
 * A set of MongoDB Datastore read operations configuration properties.
 *
 * @since 5.2.0
 */
public interface ReadOperationConfiguration {

	/**
	 * {@link ReadConcern} operation configuration
	 */
	public static final ConfigProperty<ReadConcern> READ_CONCERN = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".readConcern", ReadConcern.class);

	/**
	 * {@link ReadPreference} operation configuration
	 */
	public static final ConfigProperty<ReadPreference> READ_PREFERENCE = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".readPreference", ReadPreference.class);

	// find and aggregate read operations

	/**
	 * Query operation timeout (max execution time), expressed in {@link #QUERY_TIMEOUT_UNIT}.
	 */
	public static final ConfigProperty<Long> QUERY_TIMEOUT = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryTimeout", Long.class);

	/**
	 * Query operation timeout unit. Milliseconds by default.
	 */
	public static final ConfigProperty<TimeUnit> QUERY_TIMEOUT_UNIT = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryTimeoutUnit", TimeUnit.class);

	/**
	 * Query operation cursor type.
	 */
	public static final ConfigProperty<CursorType> QUERY_CURSOR_TYPE = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryCursorType", CursorType.class);

	/**
	 * Query operation batch size.
	 */
	public static final ConfigProperty<Integer> QUERY_BATCH_SIZE = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryBatchSize", Integer.class);

	/**
	 * Query operation collation.
	 */
	public static final ConfigProperty<Collation> QUERY_COLLATION = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryCollation", Collation.class);

	/**
	 * Query operation comment.
	 */
	public static final ConfigProperty<String> QUERY_COMMENT = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryComment", String.class);

	/**
	 * Query operation hint for which index to use.
	 */
	public static final ConfigProperty<Bson> QUERY_HINT = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryHint", Bson.class);

	/**
	 * Query operation exclusive upper bound for a specific index.
	 */
	public static final ConfigProperty<Bson> QUERY_MAX = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryMax", Bson.class);

	/**
	 * Query operation minimum inclusive lower bound for a specific index.
	 */
	public static final ConfigProperty<Bson> QUERY_MIN = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryMin", Bson.class);

	/**
	 * The maximum number of documents or index keys to scan when executing a query operation.
	 */
	public static final ConfigProperty<Long> QUERY_MAX_SCAN = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryMaxScan", Long.class);

	/**
	 * Whether to get partial results from a sharded cluster if one or more shards are unreachable (instead of throwing
	 * an error) for a query operation.
	 */
	public static final ConfigProperty<Boolean> QUERY_PARTIAL = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryPartial", Boolean.class);

	/**
	 * Whether the query operation will return only the index keys in the resulting documents.
	 */
	public static final ConfigProperty<Boolean> QUERY_RETURN_KEY = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryReturnKey", Boolean.class);

	/**
	 * Whether to add a <code>$recordId</code> field to the returned documents of a query operation.
	 */
	public static final ConfigProperty<Boolean> QUERY_SHOW_RECORD_ID = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".queryShowRecordId", Boolean.class);

	/**
	 * Whether to perform a query operation using a snapshot.
	 * <p>
	 * If true it prevents the cursor from returning a document more than once because of an intervening write
	 * operation.
	 * </p>
	 */
	public static final ConfigProperty<Boolean> QUERY_SNAPSHOT = ConfigProperty
			.create(ReadOperationConfiguration.class.getName() + ".querySnapshot", Boolean.class);

}
