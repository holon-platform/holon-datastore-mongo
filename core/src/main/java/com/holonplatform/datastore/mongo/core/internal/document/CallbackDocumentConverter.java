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
package com.holonplatform.datastore.mongo.core.internal.document;

import java.util.function.BiFunction;

import org.bson.Document;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.DocumentConverter;

/**
 * {@link DocumentConverter} implementation using a callback {@link BiFunction}.
 *
 * @param <R> Conversion type
 * 
 * @since 5.2.0
 */
public class CallbackDocumentConverter<R> implements DocumentConverter<R> {

	private final Class<? extends R> conversionType;
	private final BiFunction<MongoResolutionContext, Document, R> converter;

	public CallbackDocumentConverter(Class<? extends R> conversionType,
			BiFunction<MongoResolutionContext, Document, R> converter) {
		super();
		ObjectUtils.argumentNotNull(conversionType, "Conversion type must be not null");
		ObjectUtils.argumentNotNull(converter, "Converter function must be not null");
		this.conversionType = conversionType;
		this.converter = converter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.document.DocumentConverter#getConversionType()
	 */
	@Override
	public Class<? extends R> getConversionType() {
		return conversionType;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.document.DocumentConverter#convert(com.holonplatform.datastore.mongo.core.
	 * context.MongoResolutionContext, org.bson.Document)
	 */
	@Override
	public R convert(MongoResolutionContext context, Document document) {
		return converter.apply(context, document);
	}

}
