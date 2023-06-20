/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.filter.visitor;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.opengis.util.CodeList;
import org.apache.sis.internal.feature.Resources;
import org.apache.sis.internal.filter.FunctionNames;

// Branch-dependent imports
import org.opengis.filter.Filter;
import org.opengis.filter.ResourceId;
import org.opengis.filter.Expression;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.DistanceOperatorName;
import org.opengis.filter.TemporalOperatorName;
import org.opengis.filter.ComparisonOperatorName;


/**
 * An executor of different actions depending on filter or expression type.
 *
 * <div class="note"><b>Relationship with the visitor pattern</b><br>
 * This class provides similar functionalities than the "visitor pattern".
 * The actions are defined by lambda functions in a {@link HashMap} instead than by overriding methods,
 * but the results are similar.</div>
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 1.1
 *
 * @param  <R>  the type of resources (e.g. {@link org.opengis.feature.Feature}) used as inputs.
 * @param  <V>  the type of values computed by the actions.
 *
 * @since 1.1
 * @module
 */
public abstract class AbstractVisitor<R,V> {
    /**
     * All filters known to this visitor.
     * May contain an entry associated to the {@code null} key.
     *
     * @see #setFilterHandler(CodeList, Function)
     */
    private final Map<CodeList<?>, Function<Filter<R>,V>> filters;

    /**
     * All expressions known to this visitor.
     * May contain an entry associated to the {@code null} key.
     *
     * @see #setExpressionHandler(String, Function)
     */
    private final Map<String, Function<Expression<R,?>,V>> expressions;

    /**
     * Creates a new visitor.
     */
    protected AbstractVisitor() {
        filters     = new HashMap<>();
        expressions = new HashMap<>();
    }

    /**
     * Creates a new visitor which will accept only the specified type of objects.
     *
     * @param  hasFilters      whether this filter will accepts filters.
     * @param  hasExpressions  whether this filter will accepts expressions.
     */
    protected AbstractVisitor(final boolean hasFilters, final boolean hasExpressions) {
        filters     = hasFilters     ? new HashMap<>() : Collections.emptyMap();
        expressions = hasExpressions ? new HashMap<>() : Collections.emptyMap();
    }

    /**
     * Returns the action to execute for the given type of filter.
     * The {@code null} type is legal and identifies the action to execute
     * when the {@link Filter} instance is null or has a null type.
     *
     * @param  type  identification of the filter type (can be {@code null}).
     * @return the action to execute when the identified filter is found, or {@code null} if none.
     */
    protected final Function<Filter<R>,V> getFilterHandler(final CodeList<?> type) {
        return filters.get(type);
    }

    /**
     * Returns the action to execute for the given type of expression.
     * The {@code null} type is legal and identifies the action to execute
     * when the {@link Expression} instance is null.
     *
     * @param  type  identification of the expression type (can be {@code null}).
     * @return the action to execute when the identified expression is found, or {@code null} if none.
     */
    protected final Function<Expression<R,?>,V> getExpressionHandler(final String type) {
        return expressions.get(type);
    }

    /**
     * Sets the action to execute for the given type of filter.
     * The {@code null} type is legal and identifies the action to execute
     * when the {@link Filter} instance is null or has a null type.
     *
     * @param  type    identification of the filter type (can be {@code null}).
     * @param  action  the action to execute when the identified filter is found.
     */
    protected final void setFilterHandler(final CodeList<?> type, final Function<Filter<R>,V> action) {
        filters.put(type, action);
    }

    /**
     * Sets the same action for all member of the same family of filters.
     * The action is set in enumeration declaration order up to the last type inclusive.
     *
     * @param  lastType  identification of the last filter type (inclusive).
     * @param  action    the action to execute when an identified filter is found.
     */
    private void setFamilyHandlers(final CodeList<?> lastType, final Function<Filter<R>,V> action) {
        for (final CodeList<?> type : lastType.family()) {
            filters.put(type, action);
            if (type == lastType) break;
        }
    }

    /**
     * Sets the action to execute for the given type of expression.
     * The {@code null} type is legal and identifies the action to execute
     * when the {@link Expression} instance is null.
     *
     * @param  type    identification of the expression type (can be {@code null}).
     * @param  action  the action to execute when the identified expression is found.
     */
    protected final void setExpressionHandler(final String type, final Function<Expression<R,?>,V> action) {
        expressions.put(type, action);
    }

    /**
     * Sets the same action to execute for the given types of expression.
     *
     * @param  types   identification of the expression types.
     * @param  action  the action to execute when the identified expression is found.
     */
    private void setExpressionHandlers(final Function<Expression<R,?>,V> action, final String... types) {
        for (final String type : types) {
            expressions.put(type, action);
        }
    }

    /**
     * Name of {@link ResourceId} operator.
     * For use with {@link #setFilterHandler(CodeList, Function)}.
     */
    public static final CodeList<?> RESOURCEID_NAME = new ResourceId<Object>() {
            @Override  public Class<Object> getResourceClass() {return null;}
            @Override  public String getIdentifier()  {return null;}
            @Override  public List   getExpressions() {return null;}
            @Override  public boolean test(Object o)  {return false;}
        }.getOperatorType();

    /**
     * Sets the same action to execute for the include and exclude filters.
     *
     * @param  action  the action to execute when the include or exclude filter is found.
     */
    protected final void setIncludeExcludeHandlers(final Function<Filter<R>,V> action) {
        setFilterHandler(Filter.include().getOperatorType(), action);
        setFilterHandler(Filter.exclude().getOperatorType(), action);
    }

    /**
     * Sets the same action to execute for the {@code AND} and {@code OR} types filter.
     *
     * @param  action  the action to execute when one of the enumerated filters is found.
     */
    protected final void setBinaryLogicalHandlers(final Function<Filter<R>,V> action) {
        setFamilyHandlers(LogicalOperatorName.OR, action);
    }

    /**
     * Sets the same action for both the {@code IsNull} and {@code IsNil} types of filter.
     *
     * @param  action  the action to execute when one of the enumerated filters is found.
     */
    protected final void setNullAndNilHandlers(final Function<Filter<R>,V> action) {
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_NULL), action);
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_NIL),  action);
    }

    /**
     * Sets the same action to execute for the &lt;, &gt;, ≤, ≥, = and ≠ types of filter.
     *
     * @param  action  the action to execute when one of the enumerated filters is found.
     */
    protected final void setBinaryComparisonHandlers(final Function<Filter<R>,V> action) {
        setFamilyHandlers(ComparisonOperatorName.PROPERTY_IS_GREATER_THAN_OR_EQUAL_TO, action);
    }

    /**
     * Sets the same action to execute for the temporal comparison operators.
     * The operators are {@code AFTER}, {@code BEFORE}, {@code BEGINS}, {@code BEGUN_BY}, {@code CONTAINS},
     * {@code DURING}, {@code EQUALS}, {@code OVERLAPS}, {@code MEETS}, {@code ENDS}, {@code OVERLAPPED_BY},
     * {@code MET_BY}, {@code ENDED_BY} and {@code ANY_INTERACTS} types filter.
     *
     * @param  action  the action to execute when one of the enumerated filters is found.
     */
    protected final void setBinaryTemporalHandlers(final Function<Filter<R>,V> action) {
        setFamilyHandlers(TemporalOperatorName.ANY_INTERACTS, action);
    }

    /**
     * Sets the same action to execute for the spatial comparison operators without distance parameter.
     * The operators are {@code BBOX}, {@code EQUALS}, {@code DISJOINT}, {@code INTERSECTS}, {@code TOUCHES},
     * {@code CROSSES}, {@code WITHIN}, {@code CONTAINS} and {@code OVERLAPS}.
     *
     * @param  action  the action to execute when one of the enumerated filters is found.
     */
    protected final void setBinarySpatialHandlers(final Function<Filter<R>,V> action) {
        setFamilyHandlers(SpatialOperatorName.OVERLAPS, action);
    }

    /**
     * Sets the same action to execute for the spatial comparison operators taking a distance parameter.
     * The operators are {@code DWITHIN} and {@code BEYOND} types filter.
     *
     * @param  action  the action to execute when one of the enumerated filters is found.
     */
    protected final void setDistanceSpatialHandlers(final Function<Filter<R>,V> action) {
        setFamilyHandlers(DistanceOperatorName.WITHIN, action);
    }

    /**
     * Sets the same action to execute for the +, −, × and ÷ expressions.
     *
     * @param  action  the action to execute when one of the enumerated expressions is found.
     */
    protected final void setMathHandlers(final Function<Expression<R,?>,V> action) {
        setExpressionHandlers(action, FunctionNames.Add, FunctionNames.Subtract, FunctionNames.Multiply, FunctionNames.Divide);
    }

    /**
     * Executes the registered action for the given filter.
     * Actions are registered by calls to {@code setFooHandler(…)} before the call to this {@code visit(…)} method.
     *
     * @param  filter  the filter for which to execute an action based on its type.
     * @return result of the action.
     * @throws UnsupportedOperationException if there is no action registered for the given filter.
     */
    public final V visit(final Filter<R> filter) {
        final CodeList<?> type = (filter != null) ? filter.getOperatorType() : null;
        final Function<Filter<R>, V> f = filters.get(type);
        if (f != null) {
            return f.apply(filter);
        }
        return typeNotFound(type, filter);
    }

    /**
     * Executes the registered action for the given expression.
     * Actions are registered by calls to {@code setFooHandler(…)} before the call to this {@code visit(…)} method.
     *
     * @param  expression  the expression for which to execute an action based on its type.
     * @return result of the action.
     * @throws UnsupportedOperationException if there is no action registered for the given expression.
     */
    public final V visit(final Expression<R,?> expression) {
        final String type = (expression != null) ? expression.getFunctionName().tip().toString() : null;
        final Function<Expression<R,?>, V> f = expressions.get(type);
        if (f != null) {
            return f.apply(expression);
        }
        return typeNotFound(type, expression);
    }

    /**
     * Returns the value to use or throws an exception when there is no action registered for a given filter type.
     * The default implementation throws {@link UnsupportedOperationException}.
     *
     * @param  type    the filter type which has not been found, or {@code null} if {@coce filter} is null.
     * @param  filter  the filter (may be {@code null}).
     * @return the value to use.
     * @throws UnsupportedOperationException if there is no default value.
     */
    protected V typeNotFound(final CodeList<?> type, final Filter<R> filter) {
        throw new UnsupportedOperationException(Resources.format(Resources.Keys.CanNotVisit_2, 0, type));
    }

    /**
     * Returns the value to use or throws an exception when there is no action registered for a given expression type.
     * The default implementation throws {@link UnsupportedOperationException}.
     *
     * @param  type        the expression type which has not been found, or {@code null} if {@coce expression} is null.
     * @param  expression  the expression (may be {@code null}).
     * @return the value to use.
     * @throws UnsupportedOperationException if there is no default value.
     */
    protected V typeNotFound(final String type, final Expression<R,?> expression) {
        throw new UnsupportedOperationException(Resources.format(Resources.Keys.CanNotVisit_2, 1, type));
    }
}
