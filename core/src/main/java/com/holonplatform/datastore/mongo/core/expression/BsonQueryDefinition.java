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
package com.holonplatform.datastore.mongo.core.expression;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.bson.conversions.Bson;

import com.holonplatform.core.Expression;
import com.holonplatform.datastore.mongo.core.internal.expression.DefaultBsonQueryDefinition;
import com.mongodb.CursorType;
import com.mongodb.client.model.Collation;

/**
 * MongoDB query definition expression.
 *
 * @since 5.2.0
 */
public interface BsonQueryDefinition extends Expression {

	/**
	 * Get the collection name to query.
	 * @return Query collection name
	 */
	String getCollectionName();

	/**
	 * Get the query filters.
	 * @return Optional filter
	 */
	Optional<Bson> getFilter();

	/**
	 * Get the query sorts.
	 * @return Optional sort
	 */
	Optional<Bson> getSort();

	/**
	 * Get the query results limit.
	 * @return Optional query results limit
	 */
	Optional<Integer> getLimit();

	/**
	 * Get the number of query results to skip.
	 * @return Optional query offset
	 */
	Optional<Integer> getOffset();

	/**
	 * Get the query execution timeout, using the time unit returned by {@link #getTimeoutUnit()}.
	 * @return Optional query execution timeout
	 */
	Optional<Long> getTimeout();

	/**
	 * Get the query execution timeout unit.
	 * @return The query execution timeout unit
	 */
	TimeUnit getTimeoutUnit();

	/**
	 * Get the cursor type to use.
	 * @return Optional cursor type
	 */
	Optional<CursorType> getCursorType();

	/**
	 * Whether to get partial results from a sharded cluster if one or more shards are unreachable (instead of throwing
	 * an error).
	 * @return <code>true</code> to get partial results from a sharded cluster if one or more shards are unreachable
	 */
	boolean isPartial();

	/**
	 * Get the number of documents to return per batch.
	 * @return Optional batch size
	 */
	Optional<Integer> getBatchSize();

	/**
	 * Get the {@link Collation} to use.
	 * @return Optional collation
	 */
	Optional<Collation> getCollation();

	/**
	 * Get the query comment.
	 * @return Optional query comment
	 */
	Optional<String> getComment();

	/**
	 * Get the hint for which index to use.
	 * @return Optional index hint
	 */
	Optional<Bson> getHint();

	/**
	 * Get the exclusive upper bound for a specific index.
	 * @return Optional max index bound
	 */
	Optional<Bson> getMax();

	/**
	 * Get the minimum inclusive lower bound for a specific index.
	 * @return Optional min index bound
	 */
	Optional<Bson> getMin();

	/**
	 * Get the maximum number of documents or index keys to scan when executing the query.
	 * @return Optional maximum number of documents or index keys to scan
	 */
	Optional<Long> getMaxScan();

	/**
	 * Get whether the find operation will return only the index keys in the resulting documents.
	 * @return Whether to return only the index keys
	 */
	boolean isReturnKey();

	/**
	 * Whether to add a <code>$recordId</code> field to the returned documents.
	 * @return <code>true</code> to add a <code> $recordId</code> field to the returned documents
	 */
	boolean isShowRecordId();

	/**
	 * Whether to perform the query using a snapshot.
	 * <p>
	 * If true it prevents the cursor from returning a document more than once because of an intervening write
	 * operation.
	 * </p>
	 * @return whether to perform the query using a snapshot
	 */
	boolean isSnapshot();

	/**
	 * Get a {@link BsonQueryDefinition} builder.
	 * @return A new {@link BsonQueryDefinition} builder
	 */
	static Builder builder() {
		return new DefaultBsonQueryDefinition.DefaultBuilder();
	}

	/**
	 * Builder
	 */
	public interface Builder {

		/**
		 * Set the query collection name.
		 * @param collectionName The collection name
		 * @return this
		 */
		Builder collectionName(String collectionName);

		/**
		 * Set the query filter.
		 * @param filter Filter to set
		 * @return this
		 */
		Builder filter(Bson filter);

		/**
		 * Set the query sort.
		 * @param sort Sort to set
		 * @return this
		 */
		Builder sort(Bson sort);

		/**
		 * Set the query results limit.
		 * @param limit Limit to set
		 * @return this
		 */
		Builder limit(int limit);

		/**
		 * Set the query results offset.
		 * @param offset Offset to set
		 * @return this
		 */
		Builder offset(int offset);

		/**
		 * Set the query execution timeout.
		 * @param timeout Timeout value
		 * @param timeoutUnit Timeout unit
		 * @return this
		 */
		Builder timeout(long timeout, TimeUnit timeoutUnit);

		/**
		 * Set the cursor type.
		 * @param cursorType the cursor type to set
		 * @return this
		 */
		Builder cursorType(CursorType cursorType);

		/**
		 * Set whether to get partial results from a sharded cluster if one or more shards are unreachable (instead of
		 * throwing an error).
		 * @param partial <code>true</code> to get partial results from a sharded cluster if one or more shards are
		 *        unreachable
		 * @return this
		 */
		Builder partial(boolean partial);

		/**
		 * Set the query batch size.
		 * @param batchSize the batch size to set
		 * @return this
		 */
		Builder batchSize(int batchSize);

		/**
		 * Set the {@link Collation} to use.
		 * @param collation The collation to set
		 * @return this
		 */
		Builder collation(Collation collation);

		/**
		 * Set the query comment.
		 * @param comment The comment to set
		 * @return this
		 */
		Builder comment(String comment);

		/**
		 * Set the hint for which index to use.
		 * @param hint The index hint to set
		 * @return this
		 */
		Builder hint(Bson hint);

		/**
		 * Set the exclusive upper bound for a specific index.
		 * @param max The index upped bound
		 * @return this
		 */
		Builder max(Bson max);

		/**
		 * Set the inclusive lower bound for a specific index.
		 * @param min The index lower bound
		 * @return this
		 */
		Builder min(Bson min);

		/**
		 * Set the maximum number of documents or index keys to scan when executing the query.
		 * @param maxScan the maximum number of documents or index keys to scan when executing the query
		 * @return this
		 */
		Builder maxScan(long maxScan);

		/**
		 * Set whether the find operation will return only the index keys in the resulting documents.
		 * @param returnKey whether the find operation will return only the index keys in the resulting documents
		 * @return this
		 */
		Builder returnKey(boolean returnKey);

		/**
		 * Set whether to add a <code> $recordId</code> field to the returned documents.
		 * @param showRecordId whether to add a <code> $recordId</code> field to the returned documents
		 * @return this
		 */
		Builder showRecordId(boolean showRecordId);

		/**
		 * Set whether to perform the query using a snapshot.
		 * <p>
		 * If true it prevents the cursor from returning a document more than once because of an intervening write
		 * operation.
		 * </p>
		 * @param snapshot Whether to perform the query using a snapshot
		 * @return this
		 */
		Builder snapshot(boolean snapshot);

		/**
		 * Build the query definition.
		 * @return the query definition instance
		 */
		BsonQueryDefinition build();

	}

}
