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
package com.holonplatform.datastore.mongo.core.internal.resolver;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Priority;

import org.bson.BsonType;

import com.holonplatform.core.Expression;
import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.internal.query.QueryFilterVisitor;
import com.holonplatform.core.internal.query.QueryFilterVisitor.VisitableQueryFilter;
import com.holonplatform.core.internal.query.filter.AndFilter;
import com.holonplatform.core.internal.query.filter.BetweenFilter;
import com.holonplatform.core.internal.query.filter.EqualFilter;
import com.holonplatform.core.internal.query.filter.GreaterFilter;
import com.holonplatform.core.internal.query.filter.InFilter;
import com.holonplatform.core.internal.query.filter.LessFilter;
import com.holonplatform.core.internal.query.filter.NotEqualFilter;
import com.holonplatform.core.internal.query.filter.NotFilter;
import com.holonplatform.core.internal.query.filter.NotInFilter;
import com.holonplatform.core.internal.query.filter.NotNullFilter;
import com.holonplatform.core.internal.query.filter.NullFilter;
import com.holonplatform.core.internal.query.filter.OperationQueryFilter;
import com.holonplatform.core.internal.query.filter.OrFilter;
import com.holonplatform.core.internal.query.filter.StringMatchFilter;
import com.holonplatform.core.internal.utils.FormatUtils;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.resolver.BsonExpressionResolver;
import com.mongodb.client.model.Filters;

/**
 * MongoDB {@link VisitableQueryFilter} expression resolver.
 *
 * @since 5.2.0
 */
@Priority(Integer.MAX_VALUE - 10)
public enum VisitableQueryFilterResolver implements BsonExpressionResolver<VisitableQueryFilter>,
		QueryFilterVisitor<BsonExpression, MongoResolutionContext> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<BsonExpression> resolve(VisitableQueryFilter expression, MongoResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// resolve using visitor
		return Optional.ofNullable(expression.accept(this, context));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends VisitableQueryFilter> getExpressionType() {
		return VisitableQueryFilter.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NullFilter, java.lang.Object)
	 */
	@Override
	public BsonExpression visit(NullFilter filter, MongoResolutionContext context) {
		// not exists or it is not null
		return resolveFieldName(filter.getLeftOperand(), context)
				.map(fn -> Filters.or(Filters.not(Filters.exists(fn)), Filters.type(fn, BsonType.NULL)))
				.map(bson -> BsonExpression.create(bson)).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotNullFilter, java.lang.Object)
	 */
	@Override
	public BsonExpression visit(NotNullFilter filter, MongoResolutionContext context) {
		// exists and it is not null
		return resolveFieldName(filter.getLeftOperand(), context)
				.map(fn -> Filters.and(Filters.exists(fn), Filters.not(Filters.type(fn, BsonType.NULL))))
				.map(bson -> BsonExpression.create(bson)).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * EqualFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonExpression visit(EqualFilter<T> filter, MongoResolutionContext context) {
		return resolveFieldName(filter.getLeftOperand(), context)
				.map(fn -> Filters.eq(fn, resolveRightOperand(filter, context)))
				.map(bson -> BsonExpression.create(bson)).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotEqualFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonExpression visit(NotEqualFilter<T> filter, MongoResolutionContext context) {
		return resolveFieldName(filter.getLeftOperand(), context)
				.map(fn -> Filters.ne(fn, resolveRightOperand(filter, context)))
				.map(bson -> BsonExpression.create(bson)).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * GreaterFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonExpression visit(GreaterFilter<T> filter, MongoResolutionContext context) {
		return resolveFieldName(filter.getLeftOperand(), context)
				.map(fn -> filter.isIncludeEquals() ? Filters.gte(fn, resolveRightOperand(filter, context))
						: Filters.gt(fn, resolveRightOperand(filter, context)))
				.map(bson -> BsonExpression.create(bson)).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * LessFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonExpression visit(LessFilter<T> filter, MongoResolutionContext context) {
		return resolveFieldName(filter.getLeftOperand(), context)
				.map(fn -> filter.isIncludeEquals() ? Filters.lte(fn, resolveRightOperand(filter, context))
						: Filters.lt(fn, resolveRightOperand(filter, context)))
				.map(bson -> BsonExpression.create(bson)).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * InFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonExpression visit(InFilter<T> filter, MongoResolutionContext context) {
		return resolveFieldName(filter.getLeftOperand(), context)
				.map(fn -> Filters.in(fn, resolveRightOperandAsIterable(filter, context)))
				.map(bson -> BsonExpression.create(bson)).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotInFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonExpression visit(NotInFilter<T> filter, MongoResolutionContext context) {
		return resolveFieldName(filter.getLeftOperand(), context)
				.map(fn -> Filters.nin(fn, resolveRightOperandAsIterable(filter, context)))
				.map(bson -> BsonExpression.create(bson)).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * BetweenFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonExpression visit(BetweenFilter<T> filter, MongoResolutionContext context) {
		return resolveFieldName(filter.getLeftOperand(), context)
				.map(fn -> Filters.and(Filters.gte(fn, filter.getFromValue()), Filters.lte(fn, filter.getToValue())))
				.map(bson -> BsonExpression.create(bson)).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * StringMatchFilter, java.lang.Object)
	 */
	@Override
	public BsonExpression visit(StringMatchFilter filter, MongoResolutionContext context) {
		return resolveFieldName(filter.getLeftOperand(), context).map(fn -> {
			final String value = resolveRightOperandAsNotNullString(filter, context);
			final String options = filter.isIgnoreCase() ? "i" : null;
			String regex = FormatUtils.escapeRegexCharacters(value);
			switch (filter.getMatchMode()) {
			case CONTAINS:
				regex = ".*" + regex + ".*";
				break;
			case ENDS_WITH:
				regex = ".*" + regex;
				break;
			case STARTS_WITH:
				regex = regex + ".*";
				break;
			default:
				break;
			}
			return BsonExpression.create(Filters.regex(fn, regex, options));
		}).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * AndFilter, java.lang.Object)
	 */
	@Override
	public BsonExpression visit(AndFilter filter, MongoResolutionContext context) {
		return BsonExpression.create(
				Filters.and(filter.getComposition().stream().map(f -> context.resolveOrFail(f, BsonExpression.class))
						.map(e -> e.getValue()).collect(Collectors.toList())));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * OrFilter, java.lang.Object)
	 */
	@Override
	public BsonExpression visit(OrFilter filter, MongoResolutionContext context) {
		return BsonExpression.create(
				Filters.or(filter.getComposition().stream().map(f -> context.resolveOrFail(f, BsonExpression.class))
						.map(e -> e.getValue()).collect(Collectors.toList())));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotFilter, java.lang.Object)
	 */
	@Override
	public BsonExpression visit(NotFilter filter, MongoResolutionContext context) {
		return BsonExpression.create(
				Filters.not(context.resolveOrFail(filter.getComposition().get(0), BsonExpression.class).getValue()));
	}

	/**
	 * Resolve a field name from given expression.
	 * @param expression Expression to resolve
	 * @param context Resolution context
	 * @return The resolved field name, of an empty Optional is no suitable resolver is available
	 */
	private static Optional<String> resolveFieldName(Expression expression, MongoResolutionContext context) {
		return context.resolve(expression, FieldName.class).map(fn -> fn.getFieldName());
	}

	/**
	 * Resolve an {@link OperationQueryFilter} right operand into a field value.
	 * @param filter Filter
	 * @param context Resolution context
	 * @return The resolved right operand field value
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static Object resolveRightOperand(OperationQueryFilter<?> filter, MongoResolutionContext context)
			throws InvalidExpressionException {
		TypedExpression<?> operand = filter.getRightOperand()
				.orElseThrow(() -> new InvalidExpressionException("Missing right operand in filter [" + filter + "]"));
		return context.resolveOrFail(operand, FieldValue.class).getValue();
	}

	/**
	 * Resolve an {@link OperationQueryFilter} right operand into a String value.
	 * @param filter Filter
	 * @param context Resolution context
	 * @return The resolved right operand String value (not null)
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static String resolveRightOperandAsNotNullString(OperationQueryFilter<?> filter,
			MongoResolutionContext context) throws InvalidExpressionException {
		Object value = resolveRightOperand(filter, context);
		if (value == null) {
			throw new InvalidExpressionException("Null right operand value for filter [" + filter + "]");
		}
		return value.toString();
	}

	/**
	 * Resolve an {@link OperationQueryFilter} right operand into an {@link Iterable} field value.
	 * @param filter Filter
	 * @param context Resolution context
	 * @return The resolved right operand field value
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static Iterable<?> resolveRightOperandAsIterable(OperationQueryFilter<?> filter,
			MongoResolutionContext context) throws InvalidExpressionException {
		Object value = resolveRightOperand(filter, context);
		if (value == null) {
			return Collections.emptyList();
		}
		// check iterable
		if (Iterable.class.isAssignableFrom(value.getClass())) {
			return (Iterable<?>) value;
		}
		return Collections.singletonList(value);
	}

}
