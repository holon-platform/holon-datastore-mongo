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
package com.holonplatform.datastore.mongo.core.internal.context;

import java.util.Optional;

import com.holonplatform.core.Path;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.datastore.mongo.core.context.MongoContext;
import com.holonplatform.datastore.mongo.core.context.MongoDocumentContext;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.internal.document.DocumentPathMatcher;

/**
 * Default {@link MongoDocumentContext} implementation.
 *
 * @since 5.1.0
 */
public class DefaultMongoDocumentContext extends DefaultMongoResolutionContext implements MongoDocumentContext {

	/**
	 * Document property set
	 */
	private final PropertySet<?> propertySet;

	/**
	 * Whether to resolve the document id property/path
	 */
	private final boolean resolveDocumentId;

	/**
	 * Constructor.
	 * @param context Mongo context (not null)
	 * @param propertySet The {@link PropertySet} to which the document is bound (not null)
	 */
	public DefaultMongoDocumentContext(MongoContext context, PropertySet<?> propertySet, boolean forUpdate) {
		super(context, forUpdate);
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		this.propertySet = propertySet;
		this.resolveDocumentId = true;
	}

	/**
	 * Constructor.
	 * @param parent Parent context (not null)
	 * @param propertySet The {@link PropertySet} to which the document is bound (not null)
	 * @param resolveDocumentId Whether to resolve the document id property
	 */
	public DefaultMongoDocumentContext(MongoResolutionContext parent, PropertySet<?> propertySet,
			boolean resolveDocumentId) {
		super(parent);
		ObjectUtils.argumentNotNull(propertySet, "PropertySet must be not null");
		this.propertySet = propertySet;
		this.resolveDocumentId = resolveDocumentId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoDocumentContext#getPropertySet()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <P extends Property<?>> PropertySet<P> getPropertySet() {
		return (PropertySet<P>) propertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoDocumentContext#getDocumentIdPath()
	 */
	@Override
	public Optional<Path<?>> getDocumentIdPath() {
		return !resolveDocumentId ? Optional.empty() : getDocumentIdResolver().resolveDocumentIdPath(getPropertySet());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.context.MongoDocumentContext#getDocumentIdProperty()
	 */
	@Override
	public Optional<Property<?>> getDocumentIdProperty() {
		return !resolveDocumentId ? Optional.empty()
				: getDocumentIdResolver().resolveDocumentIdProperty(getPropertySet());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.datastore.mongo.core.context.MongoDocumentContext#isDocumentIdPath(com.holonplatform.core.Path)
	 */
	@Override
	public Optional<Path<?>> isDocumentIdPath(Path<?> path) {
		return getDocumentIdPath().filter(p -> DocumentPathMatcher.INSTANCE.match(p, path));
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DefaultMongoDocumentContext [propertySet=" + propertySet + ", resolveDocumentId=" + resolveDocumentId
				+ "]";
	}

}
