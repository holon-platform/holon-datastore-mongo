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
package com.holonplatform.datastore.mongo.core.internal.operation;

import org.bson.conversions.Bson;

import com.mongodb.CursorType;

/**
 * Generic interface to configure a <em>find</em> operation.
 *
 * @since 5.2.0
 */
public interface FindOperationConfigurator extends QueryOperationConfigurator<FindOperationConfigurator> {

	/**
	 * Set the operation filter.
	 * @param filter Filter to set
	 * @return this
	 */
	FindOperationConfigurator filter(Bson filter);

	/**
	 * Set the operation sort.
	 * @param sort Sort to set
	 * @return this
	 */
	FindOperationConfigurator sort(Bson sort);

	/**
	 * Sets a document describing the fields to return for all matching documents.
	 * @param projection the project document, which may be null.
	 * @return this
	 */
	FindOperationConfigurator projection(Bson projection);

	/**
	 * Sets the limit to apply.
	 * @param limit the limit, which may be 0
	 * @return this
	 */
	FindOperationConfigurator limit(int limit);

	/**
	 * Sets the number of documents to skip.
	 * @param skip the number of documents to skip
	 * @return this
	 */
	FindOperationConfigurator skip(int skip);

	/**
	 * Get partial results from a sharded cluster if one or more shards are unreachable (instead of throwing an error).
	 * @param partial if partial results for sharded clusters is enabled
	 * @return this
	 */
	FindOperationConfigurator partial(boolean partial);

	/**
	 * Sets the cursor type.
	 * @param cursorType the cursor type
	 * @return this
	 */
	FindOperationConfigurator cursorType(CursorType cursorType);

	/**
	 * Sets the comment to the query. A null value means no comment is set.
	 * @param comment the comment
	 * @return this
	 */
	FindOperationConfigurator comment(String comment);

	/**
	 * Sets the hint for which index to use. A null value means no hint is set.
	 * @param hint the hint
	 * @return this
	 */
	FindOperationConfigurator hint(Bson hint);

	/**
	 * Sets the exclusive upper bound for a specific index. A null value means no max is set.
	 * @param max the max
	 * @return this
	 */
	FindOperationConfigurator max(Bson max);

	/**
	 * Sets the minimum inclusive lower bound for a specific index. A null value means no max is set.
	 * @param min the min
	 * @return this
	 */
	FindOperationConfigurator min(Bson min);

	/**
	 * Sets the returnKey. If true the find operation will return only the index keys in the resulting documents.
	 * @param returnKey the returnKey
	 * @return this
	 */
	FindOperationConfigurator returnKey(boolean returnKey);

	/**
	 * Sets the showRecordId. Set to true to add a field {@code $recordId} to the returned documents.
	 * @param showRecordId the showRecordId
	 * @return this
	 */
	FindOperationConfigurator showRecordId(boolean showRecordId);

}
