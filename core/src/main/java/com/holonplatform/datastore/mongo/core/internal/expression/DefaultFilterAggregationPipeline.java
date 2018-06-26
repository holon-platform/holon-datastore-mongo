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
package com.holonplatform.datastore.mongo.core.internal.expression;

import java.util.Optional;

import org.bson.conversions.Bson;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.expression.BsonFilter.FilterAggregationPipeline;

/**
 * Default {@link FilterAggregationPipeline} implementation.
 *
 * @since 5.2.0
 */
public class DefaultFilterAggregationPipeline implements FilterAggregationPipeline {

	private final Bson match;
	private final Bson projection;

	public DefaultFilterAggregationPipeline(Bson match, Bson projection) {
		super();
		ObjectUtils.argumentNotNull(match, "Match stage must be not null");
		this.match = match;
		this.projection = projection;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonFilter.FilterAggregationPipeline#getMatch()
	 */
	@Override
	public Bson getMatch() {
		return match;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonFilter.FilterAggregationPipeline#getProjection()
	 */
	@Override
	public Optional<Bson> getProjection() {
		return Optional.ofNullable(projection);
	}

}
