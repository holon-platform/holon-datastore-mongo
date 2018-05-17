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
package com.holonplatform.datastore.mongo.core.internal;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.WriteConcernOption;
import com.mongodb.WriteConcern;

/**
 * Default {@link WriteConcernOption} implementation.
 *
 * @since 5.2.0
 */
public class DefaultWriteConcernOption implements WriteConcernOption {

	private final WriteConcern writeConcern;

	public DefaultWriteConcernOption(WriteConcern writeConcern) {
		super();
		ObjectUtils.argumentNotNull(writeConcern, "WriteConcern must be not null");
		this.writeConcern = writeConcern;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.WriteConcernOption#getWriteConcern()
	 */
	@Override
	public WriteConcern getWriteConcern() {
		return writeConcern;
	}

}
