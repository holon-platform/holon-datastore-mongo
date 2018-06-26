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

/**
 * Generic interface to configure an <em>aggregate</em> operation.
 *
 * @since 5.2.0
 */
public interface AggregateOperationConfigurator extends QueryOperationConfigurator<AggregateOperationConfigurator> {

	/**
	 * Sets the comment to the query. A null value means no comment is set.
	 * @param comment the comment
	 * @return this
	 */
	AggregateOperationConfigurator comment(String comment);

	/**
	 * Sets the hint for which index to use. A null value means no hint is set.
	 * @param hint the hint
	 * @return this
	 */
	AggregateOperationConfigurator hint(Bson hint);

}
