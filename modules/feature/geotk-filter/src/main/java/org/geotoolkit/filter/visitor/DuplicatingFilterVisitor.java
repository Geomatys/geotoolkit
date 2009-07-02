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
import java.util.Iterator;
import java.util.List;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.opengis.filter.And;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;


/**
 * Used to duplication Filters and/or Expressions - returned object is a copy.
 * <p>
 * Extra data can be used to provide a {@link FilterFactory2} but this is NOT required.
 * This class is thread safe.
 * </ul>
 * @author Jesse
 *
 */
public class DuplicatingFilterVisitor implements FilterVisitor, ExpressionVisitor {

    protected final FilterFactory2 ff;

    public DuplicatingFilterVisitor() {
        this((FilterFactory2) FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY, FilterFactory2.class)));
    }

    public DuplicatingFilterVisitor(final FilterFactory2 factory) {
        this.ff = factory;
    }

    protected FilterFactory2 getFactory(final Object extraData) {
        if (extraData instanceof FilterFactory2) {
            return (FilterFactory2) extraData;
        }
        return ff;
    }

    @Override
    public Object visit(final ExcludeFilter filter, final Object extraData) {
        return filter;
    }

    @Override
    public Object visit(final IncludeFilter filter, final Object extraData) {
        return filter;
    }

    /**
     * Null safe expression cloning
     * @param expression
     * @param extraData
     * @return
     */
    Expression visit(final Expression expression, final Object extraData) {
        if (expression == null) {
            return null;
        }
        return (Expression) expression.accept(this, extraData);
    }

    @Override
    public Object visit(final And filter, final Object extraData) {
        final List<Filter> children = filter.getChildren();
        final List<Filter> newChildren = new ArrayList<Filter>();
        for (final Filter child : children) {
            if (child != null) {
                newChildren.add((Filter)child.accept(this, extraData));
            }
        }
        return getFactory(extraData).and(newChildren);
    }

    @Override
    public Object visit(final Id filter, final Object extraData) {
        return getFactory(extraData).id(filter.getIdentifiers());
    }

    @Override
    public Object visit(final Not filter, final Object extraData) {
        return getFactory(extraData).not((Filter) filter.getFilter().accept(this, extraData));
    }

    @Override
    public Object visit(final Or filter, final Object extraData) {
        final List<Filter> children = filter.getChildren();
        final List<Filter> newChildren = new ArrayList<Filter>();
        for (Filter child : children) {
            if (child != null) {
                newChildren.add((Filter)child.accept(this, extraData));
            }
        }
        return getFactory(extraData).or(newChildren);
    }

    @Override
    public Object visit(final PropertyIsBetween filter, final Object extraData) {
        final Expression expr = visit(filter.getExpression(), extraData);
        final Expression lower = visit(filter.getLowerBoundary(), extraData);
        final Expression upper = visit(filter.getUpperBoundary(), extraData);
        return getFactory(extraData).between(expr, lower, upper);
    }

    @Override
    public Object visit(final PropertyIsEqualTo filter, final Object extraData) {
        final Expression expr1 = visit(filter.getExpression1(), extraData);
        final Expression expr2 = visit(filter.getExpression2(), extraData);
        boolean matchCase = filter.isMatchingCase();
        return getFactory(extraData).equal(expr1, expr2, matchCase);
    }

    @Override
    public Object visit(final PropertyIsNotEqualTo filter, final Object extraData) {
        final Expression expr1 = visit(filter.getExpression1(), extraData);
        final Expression expr2 = visit(filter.getExpression2(), extraData);
        boolean matchCase = filter.isMatchingCase();
        return getFactory(extraData).notEqual(expr1, expr2, matchCase);
    }

    @Override
    public Object visit(final PropertyIsGreaterThan filter, final Object extraData) {
        final Expression expr1 = visit(filter.getExpression1(), extraData);
        final Expression expr2 = visit(filter.getExpression2(), extraData);
        return getFactory(extraData).greater(expr1, expr2);
    }

    @Override
    public Object visit(final PropertyIsGreaterThanOrEqualTo filter, final Object extraData) {
        final Expression expr1 = visit(filter.getExpression1(), extraData);
        final Expression expr2 = visit(filter.getExpression2(), extraData);
        return getFactory(extraData).greaterOrEqual(expr1, expr2);
    }

    @Override
    public Object visit(final PropertyIsLessThan filter, final Object extraData) {
        final Expression expr1 = visit(filter.getExpression1(), extraData);
        final Expression expr2 = visit(filter.getExpression2(), extraData);
        return getFactory(extraData).less(expr1, expr2);
    }

    @Override
    public Object visit(final PropertyIsLessThanOrEqualTo filter, final Object extraData) {
        final Expression expr1 = visit(filter.getExpression1(), extraData);
        final Expression expr2 = visit(filter.getExpression2(), extraData);
        return getFactory(extraData).lessOrEqual(expr1, expr2);
    }

    @Override
    public Object visit(final PropertyIsLike filter, final Object extraData) {
        final Expression expr = visit(filter.getExpression(), extraData);
        final String pattern = filter.getLiteral();
        final String wildcard = filter.getWildCard();
        final String singleChar = filter.getSingleChar();
        final String escape = filter.getEscape();
        final boolean matchCase = filter.isMatchingCase();
        return getFactory(extraData).like(expr, pattern, wildcard, singleChar, escape, matchCase);
    }

    @Override
    public Object visit(final PropertyIsNull filter, final Object extraData) {
        final Expression expr = visit(filter.getExpression(), extraData);
        return getFactory(extraData).isNull(expr);
    }

    @Override
    public Object visit(final BBOX filter, final Object extraData) {
        final String propertyName = filter.getPropertyName();
        final double minx = filter.getMinX();
        final double miny = filter.getMinY();
        final double maxx = filter.getMaxX();
        final double maxy = filter.getMaxY();
        final String srs = filter.getSRS();
        return getFactory(extraData).bbox(propertyName, minx, miny, maxx, maxy, srs);
    }

    @Override
    public Object visit(final Beyond filter, final Object extraData) {
        final Expression geometry1 = visit(filter.getExpression1(), extraData);
        final Expression geometry2 = visit(filter.getExpression2(), extraData);
        final double distance = filter.getDistance();
        final String units = filter.getDistanceUnits();
        return getFactory(extraData).beyond(geometry1, geometry2, distance, units);
    }

    @Override
    public Object visit(final Contains filter, final Object extraData) {
        final Expression geometry1 = visit(filter.getExpression1(), extraData);
        final Expression geometry2 = visit(filter.getExpression2(), extraData);
        return getFactory(extraData).contains(geometry1, geometry2);
    }

    @Override
    public Object visit(final Crosses filter, final Object extraData) {
        final Expression geometry1 = visit(filter.getExpression1(), extraData);
        final Expression geometry2 = visit(filter.getExpression2(), extraData);
        return getFactory(extraData).crosses(geometry1, geometry2);
    }

    @Override
    public Object visit(final Disjoint filter, final Object extraData) {
        final Expression geometry1 = visit(filter.getExpression1(), extraData);
        final Expression geometry2 = visit(filter.getExpression2(), extraData);
        return getFactory(extraData).disjoint(geometry1, geometry2);
    }

    @Override
    public Object visit(final DWithin filter, final Object extraData) {
        final Expression geometry1 = visit(filter.getExpression1(), extraData);
        final Expression geometry2 = visit(filter.getExpression2(), extraData);
        final double distance = filter.getDistance();
        final String units = filter.getDistanceUnits();
        return getFactory(extraData).dwithin(geometry1, geometry2, distance, units);
    }

    @Override
    public Object visit(final Equals filter, final Object extraData) {
        final Expression geometry1 = visit(filter.getExpression1(), extraData);
        final Expression geometry2 = visit(filter.getExpression2(), extraData);
        return getFactory(extraData).equal(geometry1, geometry2);
    }

    @Override
    public Object visit(final Intersects filter, final Object extraData) {
        final Expression geometry1 = visit(filter.getExpression1(), extraData);
        final Expression geometry2 = visit(filter.getExpression2(), extraData);
        return getFactory(extraData).intersects(geometry1, geometry2);
    }

    @Override
    public Object visit(final Overlaps filter, final Object extraData) {
        final Expression geometry1 = visit(filter.getExpression1(), extraData);
        final Expression geometry2 = visit(filter.getExpression2(), extraData);
        return getFactory(extraData).overlaps(geometry1, geometry2);
    }

    @Override
    public Object visit(final Touches filter, final Object extraData) {
        final Expression geometry1 = visit(filter.getExpression1(), extraData);
        final Expression geometry2 = visit(filter.getExpression2(), extraData);
        return getFactory(extraData).touches(geometry1, geometry2);
    }

    @Override
    public Object visit(final Within filter, final Object extraData) {
        final Expression geometry1 = visit(filter.getExpression1(), extraData);
        final Expression geometry2 = visit(filter.getExpression2(), extraData);
        return getFactory(extraData).within(geometry1, geometry2);
    }

    @Override
    public Object visitNullFilter(final Object extraData) {
        return null;
    }

    @Override
    public Object visit(final NilExpression expression, final Object extraData) {
        return expression;
    }

    @Override
    public Object visit(final Add expression, final Object extraData) {
        final Expression expr1 = visit(expression.getExpression1(), extraData);
        final Expression expr2 = visit(expression.getExpression2(), extraData);
        return getFactory(extraData).add(expr1, expr2);
    }

    @Override
    public Object visit(final Divide expression, final Object extraData) {
        final Expression expr1 = visit(expression.getExpression1(), extraData);
        final Expression expr2 = visit(expression.getExpression2(), extraData);
        return getFactory(extraData).divide(expr1, expr2);
    }

    @Override
    public Object visit(final Function expression, final Object extraData) {
        final List<Expression> old = expression.getParameters();
        final Expression[] args = new Expression[old.size()];
        int i = 0;
        for (Iterator<Expression> iter = old.iterator(); iter.hasNext(); i++) {
            Expression exp = iter.next();
            args[i] = visit(exp, extraData);
        }
        return getFactory(extraData).function(expression.getName(), args);
    }

    @Override
    public Object visit(final Literal expression, final Object extraData) {
        return getFactory(extraData).literal(expression.getValue());
    }

    @Override
    public Object visit(final Multiply expression, final Object extraData) {
        final Expression expr1 = visit(expression.getExpression1(), extraData);
        final Expression expr2 = visit(expression.getExpression2(), extraData);
        return getFactory(extraData).multiply(expr1, expr2);
    }

    @Override
    public Object visit(final PropertyName expression, final Object extraData) {
        return getFactory(extraData).property(expression.getPropertyName());
    }

    @Override
    public Object visit(final Subtract expression, final Object extraData) {
        final Expression expr1 = visit(expression.getExpression1(), extraData);
        final Expression expr2 = visit(expression.getExpression2(), extraData);
        return getFactory(extraData).subtract(expr1, expr2);
    }
}
