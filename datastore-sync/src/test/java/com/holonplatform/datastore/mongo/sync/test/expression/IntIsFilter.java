/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.datastore.mongo.sync.test.expression;

import java.util.Optional;

import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.datastore.mongo.sync.test.data.ModelTest;

public class IntIsFilter implements QueryFilter {

	private static final long serialVersionUID = 1L;

	public static final QueryFilterResolver<IntIsFilter> RESOLVER = new Resolver();

	private final int value;

	public IntIsFilter(int value) {
		super();
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public void validate() throws InvalidExpressionException {
	}

	@SuppressWarnings("serial")
	public static final class Resolver implements QueryFilterResolver<IntIsFilter> {

		@Override
		public Class<? extends IntIsFilter> getExpressionType() {
			return IntIsFilter.class;
		}

		@Override
		public Optional<QueryFilter> resolve(IntIsFilter expression, ResolutionContext context)
				throws InvalidExpressionException {
			return Optional.of(ModelTest.INT.eq(expression.getValue()));
		}

	}

}
