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
import java.util.concurrent.TimeUnit;

import org.bson.conversions.Bson;

import com.holonplatform.datastore.mongo.core.expression.BsonFilterExpression;
import com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition;
import com.mongodb.CursorType;
import com.mongodb.client.model.Collation;

/**
 * Default {@link BsonQueryDefinition} implementation.
 *
 * @since 5.2.0
 */
public class DefaultBsonQueryDefinition implements BsonQueryDefinition {

	private String collectionName;
	private boolean distinct = false;
	private BsonFilterExpression filter;
	private Bson sort;
	private Bson group;
	private BsonFilterExpression groupFilter;
	private Integer limit;
	private Integer offset;
	private Long timeout;
	private TimeUnit timeoutUnit = TimeUnit.MILLISECONDS;
	private CursorType cursorType;
	private boolean partial = false;
	private Integer batchSize;
	private Collation collation;
	private String comment;
	private Bson hint;
	private Bson max;
	private Bson min;
	private boolean returnKey = false;
	private boolean showRecordId = false;

	public DefaultBsonQueryDefinition() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition#getCollectionName()
	 */
	@Override
	public String getCollectionName() {
		return collectionName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition#isDistinct()
	 */
	@Override
	public boolean isDistinct() {
		return distinct;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getFilter()
	 */
	@Override
	public Optional<BsonFilterExpression> getFilter() {
		return Optional.ofNullable(filter);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getSort()
	 */
	@Override
	public Optional<Bson> getSort() {
		return Optional.ofNullable(sort);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition#getGroup()
	 */
	@Override
	public Optional<Bson> getGroup() {
		return Optional.ofNullable(group);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition#getGroupFilter()
	 */
	@Override
	public Optional<BsonFilterExpression> getGroupFilter() {
		return Optional.ofNullable(groupFilter);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getLimit()
	 */
	@Override
	public Optional<Integer> getLimit() {
		return Optional.ofNullable(limit);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getOffset()
	 */
	@Override
	public Optional<Integer> getOffset() {
		return Optional.ofNullable(offset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getTimeout()
	 */
	@Override
	public Optional<Long> getTimeout() {
		return Optional.ofNullable(timeout);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getTimeoutUnit()
	 */
	@Override
	public TimeUnit getTimeoutUnit() {
		return (timeoutUnit == null) ? TimeUnit.MILLISECONDS : timeoutUnit;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getCursorType()
	 */
	@Override
	public Optional<CursorType> getCursorType() {
		return Optional.ofNullable(cursorType);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#isPartial()
	 */
	@Override
	public boolean isPartial() {
		return partial;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getBatchSize()
	 */
	@Override
	public Optional<Integer> getBatchSize() {
		return Optional.ofNullable(batchSize);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getCollation()
	 */
	@Override
	public Optional<Collation> getCollation() {
		return Optional.ofNullable(collation);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getComment()
	 */
	@Override
	public Optional<String> getComment() {
		return Optional.ofNullable(comment);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getHint()
	 */
	@Override
	public Optional<Bson> getHint() {
		return Optional.ofNullable(hint);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getMax()
	 */
	@Override
	public Optional<Bson> getMax() {
		return Optional.ofNullable(max);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#getMin()
	 */
	@Override
	public Optional<Bson> getMin() {
		return Optional.ofNullable(min);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#isReturnKey()
	 */
	@Override
	public boolean isReturnKey() {
		return returnKey;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition#isShowRecordId()
	 */
	@Override
	public boolean isShowRecordId() {
		return showRecordId;
	}

	// ------- setters

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public void setFilter(BsonFilterExpression filter) {
		this.filter = filter;
	}

	public void setSort(Bson sort) {
		this.sort = sort;
	}

	public void setGroup(Bson group) {
		this.group = group;
	}

	public void setGroupFilter(BsonFilterExpression groupFilter) {
		this.groupFilter = groupFilter;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public void setTimeoutUnit(TimeUnit timeoutUnit) {
		this.timeoutUnit = timeoutUnit;
	}

	public void setCursorType(CursorType cursorType) {
		this.cursorType = cursorType;
	}

	public void setPartial(boolean partial) {
		this.partial = partial;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public void setCollation(Collation collation) {
		this.collation = collation;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setHint(Bson hint) {
		this.hint = hint;
	}

	public void setMax(Bson max) {
		this.max = max;
	}

	public void setMin(Bson min) {
		this.min = min;
	}

	public void setReturnKey(boolean returnKey) {
		this.returnKey = returnKey;
	}

	public void setShowRecordId(boolean showRecordId) {
		this.showRecordId = showRecordId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getCollectionName() == null) {
			throw new InvalidExpressionException("Null query collection name");
		}
	}

	public static class DefaultBuilder implements Builder {

		private final DefaultBsonQueryDefinition instance = new DefaultBsonQueryDefinition();

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition.Builder#collectionName(java.lang.
		 * String)
		 */
		@Override
		public Builder collectionName(String collectionName) {
			instance.setCollectionName(collectionName);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition.Builder#distinct(boolean)
		 */
		@Override
		public Builder distinct(boolean distinct) {
			instance.setDistinct(distinct);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#filter(org.bson.conversions.
		 * Bson)
		 */
		@Override
		public Builder filter(BsonFilterExpression filter) {
			instance.setFilter(filter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#sort(org.bson.conversions.
		 * Bson)
		 */
		@Override
		public Builder sort(Bson sort) {
			instance.setSort(sort);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition.Builder#group(org.bson.conversions.
		 * Bson)
		 */
		@Override
		public Builder group(Bson group) {
			instance.setGroup(group);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.BsonQueryDefinition.Builder#groupFilter(org.bson.
		 * conversions.Bson)
		 */
		@Override
		public Builder groupFilter(BsonFilterExpression groupFilter) {
			instance.setGroupFilter(groupFilter);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#limit(int)
		 */
		@Override
		public Builder limit(int limit) {
			instance.setLimit(limit);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#offset(int)
		 */
		@Override
		public Builder offset(int offset) {
			instance.setOffset(offset);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#timeout(long,
		 * java.util.concurrent.TimeUnit)
		 */
		@Override
		public Builder timeout(long timeout, TimeUnit timeoutUnit) {
			instance.setTimeout(timeout);
			instance.setTimeoutUnit(timeoutUnit);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#cursorType(com.mongodb.
		 * CursorType)
		 */
		@Override
		public Builder cursorType(CursorType cursorType) {
			instance.setCursorType(cursorType);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#partial(boolean)
		 */
		@Override
		public Builder partial(boolean partial) {
			instance.setPartial(partial);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#batchSize(int)
		 */
		@Override
		public Builder batchSize(int batchSize) {
			instance.setBatchSize(batchSize);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#collation(com.mongodb.client.
		 * model.Collation)
		 */
		@Override
		public Builder collation(Collation collation) {
			instance.setCollation(collation);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#comment(java.lang.String)
		 */
		@Override
		public Builder comment(String comment) {
			instance.setComment(comment);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#hint(org.bson.conversions.
		 * Bson)
		 */
		@Override
		public Builder hint(Bson hint) {
			instance.setHint(hint);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#max(org.bson.conversions.Bson)
		 */
		@Override
		public Builder max(Bson max) {
			instance.setMax(max);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#min(org.bson.conversions.Bson)
		 */
		@Override
		public Builder min(Bson min) {
			instance.setMin(min);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#returnKey(boolean)
		 */
		@Override
		public Builder returnKey(boolean returnKey) {
			instance.setReturnKey(returnKey);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#showRecordId(boolean)
		 */
		@Override
		public Builder showRecordId(boolean showRecordId) {
			instance.setShowRecordId(showRecordId);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.datastore.mongo.core.expression.MongoQueryDefinition.Builder#build()
		 */
		@Override
		public BsonQueryDefinition build() {
			return instance;
		}

	}

}
