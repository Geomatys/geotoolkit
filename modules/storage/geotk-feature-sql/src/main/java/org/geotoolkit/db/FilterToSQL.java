/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.db;

import org.opengis.filter.And;
import org.opengis.filter.ExcludeFilter;
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
import org.opengis.filter.PropertyIsNil;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
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
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.AnyInteracts;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.Meets;
import org.opengis.filter.temporal.MetBy;
import org.opengis.filter.temporal.OverlappedBy;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.opengis.filter.temporal.TOverlaps;

/**
 * Convert filters and expressions in SQL.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface FilterToSQL extends FilterVisitor, ExpressionVisitor{

    ////////////////////////////////////////////////////////////////////////////
    // EXPRESSION EXPRESSION ///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    StringBuilder visit(NilExpression candidate, Object o);

    @Override
    StringBuilder visit(Add candidate, Object o);

    @Override
    StringBuilder visit(Divide candidate, Object o);

    @Override
    StringBuilder visit(Function candidate, Object o);

    @Override
    StringBuilder visit(Literal candidate, Object o);

    @Override
    StringBuilder visit(Multiply candidate, Object o);

    @Override
    StringBuilder visit(PropertyName candidate, Object o);

    @Override
    StringBuilder visit(Subtract candidate, Object o);

    ////////////////////////////////////////////////////////////////////////////
    // FILTER EXPRESSION ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    StringBuilder visitNullFilter(Object o);

    @Override
    StringBuilder visit(ExcludeFilter candidate, Object o);

    @Override
    StringBuilder visit(IncludeFilter candidate, Object o);

    @Override
    StringBuilder visit(And candidate, Object o);

    @Override
    StringBuilder visit(Id candidate, Object o);

    @Override
    StringBuilder visit(Not candidate, Object o);

    @Override
    StringBuilder visit(Or candidate, Object o);

    @Override
    StringBuilder visit(PropertyIsBetween candidate, Object o);

    @Override
    StringBuilder visit(PropertyIsEqualTo candidate, Object o);

    @Override
    StringBuilder visit(PropertyIsNotEqualTo candidate, Object o);

    @Override
    StringBuilder visit(PropertyIsGreaterThan candidate, Object o);

    @Override
    StringBuilder visit(PropertyIsGreaterThanOrEqualTo candidate, Object o);

    @Override
    StringBuilder visit(PropertyIsLessThan candidate, Object o);

    @Override
    StringBuilder visit(PropertyIsLessThanOrEqualTo candidate, Object o);

    @Override
    StringBuilder visit(PropertyIsLike candidate, Object o);

    @Override
    StringBuilder visit(PropertyIsNull candidate, Object o);

    @Override
    StringBuilder visit(PropertyIsNil candidate, Object o);

    @Override
    StringBuilder visit(BBOX candidate, Object o);

    @Override
    StringBuilder visit(Beyond candidate, Object o);

    @Override
    StringBuilder visit(Contains candidate, Object o);

    @Override
    StringBuilder visit(Crosses candidate, Object o);

    @Override
    StringBuilder visit(Disjoint candidate, Object o);

    @Override
    StringBuilder visit(DWithin candidate, Object o);

    @Override
    StringBuilder visit(Equals candidate, Object o);

    @Override
    StringBuilder visit(Intersects candidate, Object o);

    @Override
    StringBuilder visit(Overlaps candidate, Object o);

    @Override
    StringBuilder visit(Touches candidate, Object o);

    @Override
    StringBuilder visit(Within candidate, Object o);

    @Override
    StringBuilder visit(After candidate, Object o);

    @Override
    StringBuilder visit(AnyInteracts candidate, Object o);

    @Override
    StringBuilder visit(Before candidate, Object o);

    @Override
    StringBuilder visit(Begins candidate, Object o);

    @Override
    StringBuilder visit(BegunBy candidate, Object o);

    @Override
    StringBuilder visit(During candidate, Object o);

    @Override
    StringBuilder visit(EndedBy candidate, Object o);

    @Override
    StringBuilder visit(Ends candidate, Object o);

    @Override
    StringBuilder visit(Meets candidate, Object o);

    @Override
    StringBuilder visit(MetBy candidate, Object o);

    @Override
    StringBuilder visit(OverlappedBy candidate, Object o);

    @Override
    StringBuilder visit(TContains candidate, Object o);

    @Override
    StringBuilder visit(TEquals candidate, Object o);

    @Override
    StringBuilder visit(TOverlaps candidate, Object o);

}
