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
package com.holonplatform.datastore.mongo.core.config;

import java.util.ServiceLoader;

import com.holonplatform.core.Expression;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;

/**
 * MongoDB Datastore {@link ExpressionResolver} extension type to allow automatic registration using Java
 * {@link ServiceLoader} extension, through a
 * <code>com.holonplatform.datastore.mongo.core.config.MongoDatastoreExpressionResolver</code> file under the
 * <code>META-INF/services</code> folder.
 * 
 * @param <E> Expression type
 * @param <R> Resolved expression type
 *
 * @since 5.0.0
 */
public interface MongoDatastoreExpressionResolver<E extends Expression, R extends Expression>
		extends MongoExpressionResolver<E, R> {

}
