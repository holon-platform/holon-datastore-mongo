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
package com.holonplatform.datastore.mongo.core.document;

import java.util.function.BiFunction;

import org.bson.Document;

import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.internal.document.CallbackDocumentConverter;
import com.holonplatform.datastore.mongo.core.internal.document.converter.BeanDocumentConverter;
import com.holonplatform.datastore.mongo.core.internal.document.converter.PropertyBoxDocumentConverter;
import com.holonplatform.datastore.mongo.core.internal.document.converter.TypedExpressionDocumentConverter;

/**
 * Converter to convert a {@link Document} instance into a different type.
 * 
 * @param <R> Conversion result type
 *
 * @since 5.2.0
 */
public interface DocumentConverter<R> {

	/**
	 * Get the type into which this converter is able to convert a {@link Document}.
	 * @return The conversion type
	 */
	Class<? extends R> getConversionType();

	/**
	 * Convert a {@link Document} into expected result type.
	 * @param context Resolution context
	 * @param document The Document to convert
	 * @return Converted result
	 */
	R convert(MongoResolutionContext context, Document document);

	/**
	 * Create a new {@link DocumentConverter}.
	 * @param <R> Conversion type
	 * @param type Conversion type (not null)
	 * @param converter Conversion function (not null)
	 * @return A new {@link DocumentConverter} instance
	 */
	static <R> DocumentConverter<R> create(Class<R> type, BiFunction<MongoResolutionContext, Document, R> converter) {
		return new CallbackDocumentConverter<>(type, converter);
	}

	/**
	 * Create a new {@link DocumentConverter} to convert documents into {@link PropertyBox} instances using given
	 * property set.
	 * @param <P> Property type
	 * @param properties The property set to use (not null)
	 * @return A new {@link PropertyBox} type {@link DocumentConverter}
	 */
	@SuppressWarnings("rawtypes")
	static <P extends Property> DocumentConverter<PropertyBox> propertyBox(Iterable<P> properties) {
		return new PropertyBoxDocumentConverter(PropertySet.of(properties));
	}

	/**
	 * Create a new {@link DocumentConverter} to convert documents into {@link PropertyBox} instances using given
	 * property set.
	 * @param <P> Property type
	 * @param properties The property set to use (not null)
	 * @return A new {@link PropertyBox} type {@link DocumentConverter}
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static <P extends Property> DocumentConverter<PropertyBox> propertyBox(P... properties) {
		return new PropertyBoxDocumentConverter(PropertySet.of(properties));
	}

	/**
	 * Create a new {@link DocumentConverter} to convert documents into Java Bean instances.
	 * @param <T> Bean class
	 * @param beanClass Bean class to use (not null)
	 * @return A new bean {@link DocumentConverter}
	 */
	static <T> DocumentConverter<T> bean(Class<T> beanClass) {
		return new BeanDocumentConverter<>(BeanPropertySet.create(beanClass));
	}

	/**
	 * Create a new {@link DocumentConverter} to convert documents into Java Bean instances.
	 * @param <T> Bean class
	 * @param beanPropertySet Bean property set to use (not null)
	 * @return A new bean {@link DocumentConverter}
	 */
	static <T> DocumentConverter<T> bean(BeanPropertySet<T> beanPropertySet) {
		return new BeanDocumentConverter<>(beanPropertySet);
	}

	/**
	 * Create a new {@link DocumentConverter} to convert a document field using given expression.
	 * @param <T> Expression type
	 * @param expression Field expression
	 * @param selection Field name
	 * @return A new expression {@link DocumentConverter}
	 */
	static <T> DocumentConverter<T> expression(TypedExpression<T> expression, String selection) {
		return new TypedExpressionDocumentConverter<>(expression, selection);
	}

	/**
	 * Get a {@link DocumentConverter} which simply returns the document instance, without performing any conversion
	 * operation.
	 * @return A new identity document converter
	 */
	static DocumentConverter<Document> identity() {
		return create(Document.class, (c, d) -> d);
	}

}
