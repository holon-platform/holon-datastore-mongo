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
package com.holonplatform.datastore.mongo.core.enumerations;

import java.util.Optional;

import com.mongodb.WriteConcern;

/**
 * MongoDB {@link WriteConcern} enumeration.
 *
 * @since 5.2.0
 */
public enum MongoWriteConcern {

	/**
	 * Default write concern.
	 */
	DEFAULT(null),

	/**
	 * Write operations that use this write concern will return as soon as the message is written to the socket.
	 * Exceptions are raised for network issues, but not server errors.
	 */
	UNACKNOWLEDGED(WriteConcern.UNACKNOWLEDGED),

	/**
	 * Write operations that use this write concern will wait for acknowledgement, using the default write concern
	 * configured on the server.
	 */
	ACKNOWLEDGED(WriteConcern.ACKNOWLEDGED),

	/**
	 * Write operations that use this write concern will wait for acknowledgement from a single member.
	 */
	W1(WriteConcern.W1),

	/**
	 * Write operations that use this write concern will wait for acknowledgement from two members.
	 */
	W2(WriteConcern.W2),

	/**
	 * Write operations that use this write concern will wait for acknowledgement from three members.
	 */
	W3(WriteConcern.W3),

	/**
	 * Write operations wait for the server to group commit to the journal file on disk.
	 */
	JOURNALED(WriteConcern.JOURNALED),

	/**
	 * Exceptions are raised for network issues, and server errors; waits on a majority of servers for the write
	 * operation.
	 */
	MAJORITY(WriteConcern.MAJORITY);

	private final WriteConcern writeConcern;

	private MongoWriteConcern(WriteConcern writeConcern) {
		this.writeConcern = writeConcern;
	}

	/**
	 * Get the actual {@link WriteConcern} implementation, if available.
	 * @return the write concern implementation, or an empty Optional if {@link #DEFAULT}
	 */
	public Optional<WriteConcern> getWriteConcern() {
		return Optional.ofNullable(writeConcern);
	}

}
