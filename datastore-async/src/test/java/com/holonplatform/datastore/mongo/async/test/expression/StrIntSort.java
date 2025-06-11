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
package com.holonplatform.datastore.mongo.async.test.expression;

import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.INT;
import static com.holonplatform.datastore.mongo.async.test.data.ModelTest.STR1;

import java.util.Optional;

import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.query.QuerySort;

@SuppressWarnings("serial")
public class StrIntSort implements QuerySort {

	@Override
	public void validate() throws InvalidExpressionException {
	}

	public static final ExpressionResolver<QuerySort, QuerySort> RESOLVER = ExpressionResolver.create(StrIntSort.class,
			QuerySort.class, (sort, ctx) -> Optional.of(STR1.desc().and(INT.asc())));

}
