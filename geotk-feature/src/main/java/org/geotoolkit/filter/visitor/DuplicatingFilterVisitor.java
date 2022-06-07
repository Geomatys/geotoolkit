/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.filter.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.apache.sis.internal.filter.FunctionNames;
import org.geotoolkit.filter.FilterFactory2;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.filter.BetweenComparisonOperator;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.DistanceOperator;
import org.opengis.filter.DistanceOperatorName;
import org.opengis.filter.Expression;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.LikeOperator;
import org.opengis.filter.Literal;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.MatchAction;
import org.opengis.filter.ResourceId;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.TemporalOperator;
import org.opengis.filter.TemporalOperatorName;
import org.opengis.filter.ValueReference;
import org.opengis.geometry.Envelope;


/**
 * Used to duplication Filters and/or Expressions - returned object is a copy.
 * <p>
 * Extra data can be used to provide a {@link FilterFactory2} but this is NOT required.
 * This class is thread safe.
 *
 * @author Jesse
 */
public class DuplicatingFilterVisitor extends AbstractVisitor<Object,Object> {

    protected final FilterFactory<Object,Object,Object> ff;

    public DuplicatingFilterVisitor() {
        this(FilterUtilities.FF);
    }

    public DuplicatingFilterVisitor(final FilterFactory<Object,Object,Object> factory) {
        this.ff = factory;
        setIncludeExcludeHandlers((f) -> f);
        setNullAndNilHandlers((f) -> {
            final Expression expr = (Expression) visit(f.getExpressions().get(0));
            return ff.isNull(expr);
        });
        setFilterHandler(RESOURCEID_NAME, (f) -> {
            final ResourceId<Object> filter = (ResourceId<Object>) f;
            return ff.resourceId(filter.getIdentifier());
        });
        setBinaryLogicalHandlers((f) -> {
            final LogicalOperator<Object> filter = (LogicalOperator<Object>) f;
            final List<Filter<? super Object>> children = filter.getOperands();
            final List<Filter<? super Object>> newChildren = new ArrayList<>();
            for (final Filter<? super Object> child : children) {
                newChildren.add((Filter) visit(child));
            }
            final LogicalOperatorName type = filter.getOperatorType();
            if (type == LogicalOperatorName.AND) return ff.and(newChildren);
            if (type == LogicalOperatorName.OR)  return ff.or (newChildren);
            throw new AssertionError(type);
        });
        setFilterHandler(LogicalOperatorName.NOT, (f) -> {
            final LogicalOperator<Object> filter = (LogicalOperator<Object>) f;
            return ff.not((Filter) visit(filter.getOperands().get(0)));
        });
        setBinaryComparisonHandlers((f) -> {
            final BinaryComparisonOperator<Object> filter = (BinaryComparisonOperator<Object>) f;
            final Expression expr1 = (Expression) visit(filter.getOperand1());
            final Expression expr2 = (Expression) visit(filter.getOperand2());
            final boolean matchCase = filter.isMatchingCase();
            final MatchAction matchAction = filter.getMatchAction();
            final ComparisonOperatorName type = filter.getOperatorType();
            if (type == ComparisonOperatorName.PROPERTY_IS_EQUAL_TO)                 return ff.equal         (expr1, expr2, matchCase,matchAction);
            if (type == ComparisonOperatorName.PROPERTY_IS_NOT_EQUAL_TO)             return ff.notEqual      (expr1, expr2, matchCase,matchAction);
            if (type == ComparisonOperatorName.PROPERTY_IS_GREATER_THAN)             return ff.greater       (expr1, expr2, matchCase,matchAction);
            if (type == ComparisonOperatorName.PROPERTY_IS_GREATER_THAN_OR_EQUAL_TO) return ff.greaterOrEqual(expr1, expr2, matchCase,matchAction);
            if (type == ComparisonOperatorName.PROPERTY_IS_LESS_THAN)                return ff.less          (expr1, expr2, matchCase,matchAction);
            if (type == ComparisonOperatorName.PROPERTY_IS_LESS_THAN_OR_EQUAL_TO)    return ff.lessOrEqual   (expr1, expr2, matchCase,matchAction);
            throw new AssertionError(type);
        });
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_BETWEEN), (f) -> {
            final BetweenComparisonOperator<Object> filter = (BetweenComparisonOperator<Object>) f;
            final Expression expr  = (Expression) visit(filter.getExpression());
            final Expression lower = (Expression) visit(filter.getLowerBoundary());
            final Expression upper = (Expression) visit(filter.getUpperBoundary());
            return ff.between(expr, lower, upper);
        });
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_LIKE), (f) -> {
            final LikeOperator<Object> filter = (LikeOperator<Object>) f;
            final List<Expression<? super Object, ?>> expressions = filter.getExpressions();
            final Expression expr   = (Expression) visit(expressions.get(0));
            final String pattern    = (String) ((Literal<Object,?>) expressions.get(1)).getValue();
            final char wildcard     = filter.getWildCard();
            final char singleChar   = filter.getSingleChar();
            final char escape       = filter.getEscapeChar();
            final boolean matchCase = filter.isMatchingCase();
            return ff.like(expr, pattern, wildcard, singleChar, escape, matchCase);
        });
        setBinarySpatialHandlers((f) -> {
            final BinarySpatialOperator<Object> filter = (BinarySpatialOperator<Object>) f;
            final Expression geometry1 = (Expression) visit(filter.getOperand1());
            final Expression geometry2 = (Expression) visit(filter.getOperand2());
            final SpatialOperatorName type = filter.getOperatorType();
            if (type == SpatialOperatorName.EQUALS)     return ff.equals    (geometry1, geometry2);
            if (type == SpatialOperatorName.CONTAINS)   return ff.contains  (geometry1, geometry2);
            if (type == SpatialOperatorName.CROSSES)    return ff.crosses   (geometry1, geometry2);
            if (type == SpatialOperatorName.DISJOINT)   return ff.disjoint  (geometry1, geometry2);
            if (type == SpatialOperatorName.INTERSECTS) return ff.intersects(geometry1, geometry2);
            if (type == SpatialOperatorName.OVERLAPS)   return ff.overlaps  (geometry1, geometry2);
            if (type == SpatialOperatorName.TOUCHES)    return ff.touches   (geometry1, geometry2);
            if (type == SpatialOperatorName.WITHIN)     return ff.within    (geometry1, geometry2);
            throw new AssertionError(type);
        });
        setFilterHandler(SpatialOperatorName.BBOX, (f) -> {     // Must be after `setBinarySpatialHandlers(…)`.
            final BinarySpatialOperator<Object> filter = (BinarySpatialOperator<Object>) f;
            final List<Expression<? super Object, ?>> expressions = filter.getExpressions();
            final Expression exp1 = (Expression) visit(expressions.get(0));
            final Expression exp2 = expressions.get(1);
            if (!(exp2 instanceof Literal)) {
                // This value is supposed to hold a BoundingBox.
                throw new IllegalArgumentException("Illegal BBOX filter, "
                        + "second expression should have been a literal with a boundingBox value:\n" + filter);
            }
            Literal<?, ?> l = (Literal<?, ?>) visit(exp2);
            final Envelope obj = l.toValueType(Envelope.class).apply(null);
            return ff.bbox(exp1, obj);
        });
        setDistanceSpatialHandlers((f) -> {
            final DistanceOperator<Object> filter = (DistanceOperator<Object>) f;
            final List<Expression<? super Object, ?>> expressions = filter.getExpressions();
            final Expression geometry1 = (Expression) visit(expressions.get(0));
            final Expression geometry2 = (Expression) visit(expressions.get(1));
            final Quantity<Length> distance = filter.getDistance();
            final DistanceOperatorName type = filter.getOperatorType();
            if (type == DistanceOperatorName.BEYOND) return ff.beyond(geometry1, geometry2, distance);
            if (type == DistanceOperatorName.WITHIN) return ff.within(geometry1, geometry2, distance);
            throw new AssertionError(type);
        });
        setBinaryTemporalHandlers((f) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            final List<Expression<? super Object, ?>> expressions = filter.getExpressions();
            final Expression exp1 = (Expression) visit(expressions.get(0));
            final Expression exp2 = (Expression) visit(expressions.get(1));
            final TemporalOperatorName type = filter.getOperatorType();
            if (type == TemporalOperatorName.EQUALS)        return ff.tequals     (exp1, exp2);
            if (type == TemporalOperatorName.BEFORE)        return ff.before      (exp1, exp2);
            if (type == TemporalOperatorName.AFTER)         return ff.after       (exp1, exp2);
            if (type == TemporalOperatorName.BEGINS)        return ff.begins      (exp1, exp2);
            if (type == TemporalOperatorName.BEGUN_BY)      return ff.begunBy     (exp1, exp2);
            if (type == TemporalOperatorName.CONTAINS)      return ff.tcontains   (exp1, exp2);
            if (type == TemporalOperatorName.DURING)        return ff.during      (exp1, exp2);
            if (type == TemporalOperatorName.ENDS)          return ff.ends        (exp1, exp2);
            if (type == TemporalOperatorName.ENDED_BY)      return ff.endedBy     (exp1, exp2);
            if (type == TemporalOperatorName.MEETS)         return ff.meets       (exp1, exp2);
            if (type == TemporalOperatorName.MET_BY)        return ff.metBy       (exp1, exp2);
            if (type == TemporalOperatorName.OVERLAPS)      return ff.overlaps    (exp1, exp2);
            if (type == TemporalOperatorName.OVERLAPPED_BY) return ff.overlappedBy(exp1, exp2);
            if (type == TemporalOperatorName.ANY_INTERACTS) return ff.anyInteracts(exp1, exp2);
            throw new AssertionError(type);
        });
        setExpressionHandler(FunctionNames.Literal, (e) -> {
            final Literal<Object,?> expression = (Literal<Object,?>) e;
            return ff.literal(expression.getValue());
        });
        setExpressionHandler(FunctionNames.ValueReference, (e) -> {
            final ValueReference<Object,?> expression = (ValueReference<Object,?>) e;
            return ff.property(expression.getXPath());
        });
    }

    @Override
    protected Object typeNotFound(final String type, final Expression<Object,?> expression) {
        final List<Expression<? super Object, ?>> old = expression.getParameters();
        final Expression[] args = new Expression[old.size()];
        for (int i=0; i<args.length; i++) {
            args[i] = (Expression) visit(old.get(i));
        }
        try {
            return ff.function(expression.getFunctionName().tip().toString(), args);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger("org.geotoolkit.filter").log(Level.FINE, "Could not duplicate expression {0} it will remain untouched.", expression.getFunctionName());
            return expression;
        }
    }
}
