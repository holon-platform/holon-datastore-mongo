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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Priority;

import org.bson.BsonType;
import org.bson.Document;
import org.bson.conversions.Bson;

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
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.datastore.mongo.core.context.MongoResolutionContext;
import com.holonplatform.datastore.mongo.core.document.QueryOperationType;
import com.holonplatform.datastore.mongo.core.expression.BsonExpression;
import com.holonplatform.datastore.mongo.core.expression.BsonFilterExpression;
import com.holonplatform.datastore.mongo.core.expression.FieldName;
import com.holonplatform.datastore.mongo.core.expression.FieldValue;
import com.holonplatform.datastore.mongo.core.internal.support.DocumentIdHelper;
import com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

/**
 * MongoDB {@link VisitableQueryFilter} to {@link BsonFilterExpression} expression resolver.
 *
 * @since 5.2.0
 */
@Priority(Integer.MAX_VALUE - 10)
public enum VisitableQueryFilterResolver implements MongoExpressionResolver<VisitableQueryFilter, BsonFilterExpression>,
		QueryFilterVisitor<BsonFilterExpression, MongoResolutionContext<?>> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.mongo.core.resolver.MongoExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.mongo.core.context.MongoResolutionContext)
	 */
	@Override
	public Optional<BsonFilterExpression> resolve(VisitableQueryFilter expression, MongoResolutionContext<?> context)
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
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends BsonFilterExpression> getResolvedType() {
		return BsonFilterExpression.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NullFilter, java.lang.Object)
	 */
	@Override
	public BsonFilterExpression visit(NullFilter filter, MongoResolutionContext<?> context) {
		// not exists or it is not null
		return resolveFieldName(filter.getLeftOperand(), context)
				.map(fn -> Filters.or(Filters.not(Filters.exists(fn)), Filters.type(fn, BsonType.NULL)))
				.map(bson -> BsonFilterExpression.create(bson)).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotNullFilter, java.lang.Object)
	 */
	@Override
	public BsonFilterExpression visit(NotNullFilter filter, MongoResolutionContext<?> context) {
		// exists and it is not null
		return resolveFieldName(filter.getLeftOperand(), context)
				.map(fn -> Filters.and(Filters.exists(fn), Filters.not(Filters.type(fn, BsonType.NULL))))
				.map(bson -> BsonFilterExpression.create(bson)).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * EqualFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonFilterExpression visit(EqualFilter<T> filter, MongoResolutionContext<?> context) {
		return resolveOperationQueryFilter(context, filter,
				(c, fn) -> Filters.eq(fn, resolveRightOperand(filter, context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotEqualFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonFilterExpression visit(NotEqualFilter<T> filter, MongoResolutionContext<?> context) {
		return resolveOperationQueryFilter(context, filter,
				(c, fn) -> Filters.ne(fn, resolveRightOperand(filter, context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * GreaterFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonFilterExpression visit(GreaterFilter<T> filter, MongoResolutionContext<?> context) {
		return resolveOperationQueryFilter(context, filter,
				(c, fn) -> filter.isIncludeEquals() ? Filters.gte(fn, resolveRightOperand(filter, context))
						: Filters.gt(fn, resolveRightOperand(filter, context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * LessFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonFilterExpression visit(LessFilter<T> filter, MongoResolutionContext<?> context) {
		return resolveOperationQueryFilter(context, filter,
				(c, fn) -> filter.isIncludeEquals() ? Filters.lte(fn, resolveRightOperand(filter, context))
						: Filters.lt(fn, resolveRightOperand(filter, context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * InFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonFilterExpression visit(InFilter<T> filter, MongoResolutionContext<?> context) {
		return resolveOperationQueryFilter(context, filter,
				(c, fn) -> Filters.in(fn, resolveRightOperandAsIterable(filter, context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotInFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonFilterExpression visit(NotInFilter<T> filter, MongoResolutionContext<?> context) {
		return resolveOperationQueryFilter(context, filter,
				(c, fn) -> Filters.nin(fn, resolveRightOperandAsIterable(filter, context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * BetweenFilter, java.lang.Object)
	 */
	@Override
	public <T> BsonFilterExpression visit(BetweenFilter<T> filter, MongoResolutionContext<?> context) {
		return resolveOperationQueryFilter(context, filter,
				(c, fn) -> Filters.and(Filters.gte(fn, filter.getFromValue()), Filters.lte(fn, filter.getToValue())));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * StringMatchFilter, java.lang.Object)
	 */
	@Override
	public BsonFilterExpression visit(StringMatchFilter filter, MongoResolutionContext<?> context) {
		return resolveOperationQueryFilter(context, filter, (c, fn) -> {
			final String value = resolveRightOperandAsNotNullString(filter, context);
			final String options = filter.isIgnoreCase() ? "i" : null;
			String regex = FormatUtils.escapeRegexCharacters(value);
			switch (filter.getMatchMode()) {
			case CONTAINS:
				regex = ".*" + regex + ".*";
				break;
			case ENDS_WITH:
				regex = regex + "$";
				break;
			case STARTS_WITH:
				regex = "^" + regex;
				break;
			default:
				break;
			}
			return Filters.regex(fn, regex, options);
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * AndFilter, java.lang.Object)
	 */
	@Override
	public BsonFilterExpression visit(AndFilter filter, MongoResolutionContext<?> context) {
		return combine(context, filter.getComposition(), expressions -> Filters.and(expressions));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * OrFilter, java.lang.Object)
	 */
	@Override
	public BsonFilterExpression visit(OrFilter filter, MongoResolutionContext<?> context) {
		return combine(context, filter.getComposition(), expressions -> Filters.or(expressions));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotFilter, java.lang.Object)
	 */
	@Override
	public BsonFilterExpression visit(NotFilter filter, MongoResolutionContext<?> context) {
		final BsonFilterExpression toNegate = context.resolveOrFail(filter.getComposition().get(0),
				BsonFilterExpression.class);
		return toNegate.getPipeline()
				.map(pipeline -> BsonFilterExpression.create(Filters.not(pipeline.getMatch()),
						pipeline.getProjection().orElse(null)))
				.orElse(BsonFilterExpression.create(Filters.not(toNegate.getExpression())));
	}

	/**
	 * Combine given filters using the provided combiner function.
	 * @param context Resolution context
	 * @param filters Filters to combine
	 * @param combiner Combiner function
	 * @return Result filter
	 */
	private static BsonFilterExpression combine(MongoResolutionContext<?> context, List<QueryFilter> filters,
			Function<List<Bson>, Bson> combiner) {
		final List<Bson> expressions = new ArrayList<>();
		final List<Bson> matches = new ArrayList<>();
		final List<Bson> projections = new ArrayList<>();

		filters.stream().map(f -> context.resolveOrFail(f, BsonFilterExpression.class)).forEach(bf -> {
			expressions.add(bf.getExpression());
			bf.getPipeline().ifPresent(p -> {
				matches.add(p.getMatch());
				p.getProjection().ifPresent(pj -> {
					projections.add(pj);
				});
			});
		});

		// check filter type
		if (!matches.isEmpty()) {
			return BsonFilterExpression.create(combiner.apply(matches),
					projections.isEmpty() ? null : Projections.fields(projections));
		}
		return BsonFilterExpression.create(combiner.apply(expressions));
	}

	/**
	 * Resolve a {@link OperationQueryFilter}.
	 * @param context Resolution context
	 * @param filter Filter to resolve
	 * @param filterProvider {@link Bson} filter provider
	 * @return Resolved {@link BsonFilterExpression}
	 */
	private static BsonFilterExpression resolveOperationQueryFilter(MongoResolutionContext<?> context,
			OperationQueryFilter<?> filter, BiFunction<MongoResolutionContext<?>, String, Bson> filterProvider) {
		return context.resolve(filter.getLeftOperand(), BsonExpression.class).map(e -> e.getValue()).map(expression -> {
			// set AGGREGATE type
			context.setQueryOperationType(QueryOperationType.AGGREGATE);

			// check alias
			Optional<String> alias = context.getAlias(filter.getLeftOperand());
			if (alias.isPresent()) {
				return BsonFilterExpression.create(filterProvider.apply(context, alias.get()), null);
			}

			final String filterAlias = context.getNextProjectionFieldName();
			return BsonFilterExpression.create(filterProvider.apply(context, filterAlias),
					new Document(filterAlias, expression));

		}).orElse(resolveFieldName(filter.getLeftOperand(), context).map(fn -> filterProvider.apply(context, fn))
				.map(bson -> BsonFilterExpression.create(bson)).orElse(null));
	}

	/**
	 * Resolve a field name from given expression.
	 * @param expression Expression to resolve
	 * @param context Resolution context
	 * @return The resolved field name, of an empty Optional is no suitable resolver is available
	 */
	private static Optional<String> resolveFieldName(TypedExpression<?> expression, MongoResolutionContext<?> context) {
		// check alias
		Optional<String> alias = context.getAlias(expression);
		if (alias.isPresent()) {
			return alias;
		}
		// resolve field name
		return context.resolve(expression, FieldName.class).map(fn -> fn.getFieldName());
	}

	/**
	 * Resolve an {@link OperationQueryFilter} right operand into a field value.
	 * @param filter Filter
	 * @param context Resolution context
	 * @return The resolved right operand field value
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static Object resolveRightOperand(OperationQueryFilter<?> filter, MongoResolutionContext<?> context)
			throws InvalidExpressionException {
		TypedExpression<?> operand = filter.getRightOperand()
				.orElseThrow(() -> new InvalidExpressionException("Missing right operand in filter [" + filter + "]"));
		final Object value = context.resolveOrFail(operand, FieldValue.class).getValue();
		// check document id
		if (DocumentIdHelper.isDefaultDocumentIdProperty(context, filter.getLeftOperand())) {
			return context.getDocumentIdResolver().encode(value);
		}
		if (DocumentIdHelper.isDocumentIdPropertyPath(filter.getLeftOperand())) {
			return context.getDocumentIdResolver().encode(value);
		}
		return value;
	}

	/**
	 * Resolve an {@link OperationQueryFilter} right operand into a String value.
	 * @param filter Filter
	 * @param context Resolution context
	 * @return The resolved right operand String value (not null)
	 * @throws InvalidExpressionException If an error occurred
	 */
	private static String resolveRightOperandAsNotNullString(OperationQueryFilter<?> filter,
			MongoResolutionContext<?> context) throws InvalidExpressionException {
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
			MongoResolutionContext<?> context) throws InvalidExpressionException {
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
