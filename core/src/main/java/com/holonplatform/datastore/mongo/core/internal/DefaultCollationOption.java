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
import com.holonplatform.datastore.mongo.core.CollationOption;
import com.mongodb.client.model.Collation;

/**
 * Default {@link CollationOption} implementation.
 *
 * @since 5.2.0
 */
public class DefaultCollationOption implements CollationOption {

	private final Collation collation;

	public DefaultCollationOption(Collation collation) {
		super();
		ObjectUtils.argumentNotNull(collation, "Collation must be not null");
		this.collation = collation;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.CollationOption#getCollation()
	 */
	@Override
	public Collation getCollation() {
		return collation;
	}

}
