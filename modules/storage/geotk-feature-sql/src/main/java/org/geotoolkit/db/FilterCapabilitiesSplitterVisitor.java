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
import org.opengis.filter.capability.FilterCapabilities;
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
 * Given a filter capability, this filter will divide a filter in a
 * pre and post filter.
 * The pre-filter can be used directly while the post-filter will have to
 * be handle in java.
 *
 * TODO
 *
 * @author Johann Sorel (Geomatys)
 */
public class FilterCapabilitiesSplitterVisitor implements FilterVisitor, ExpressionVisitor {

    private final FilterCapabilities capabilities;

    public FilterCapabilitiesSplitterVisitor(FilterCapabilities capabilities) {
        this.capabilities = capabilities;


    }


    ////////////////////////////////////////////////////////////////////////////
    // EXPRESSION EXPRESSION ///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public Object visit(NilExpression candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Add candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Divide candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Function candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Literal candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Multiply candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyName candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Subtract candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // FILTER EXPRESSION ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public Object visitNullFilter(Object candidate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(ExcludeFilter candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(IncludeFilter candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(And candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Id candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Not candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Or candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsBetween candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsEqualTo candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsNotEqualTo candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsGreaterThan candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsGreaterThanOrEqualTo candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsLessThan candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsLessThanOrEqualTo candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsLike candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsNull candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsNil candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(BBOX candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Beyond candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Contains candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Crosses candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Disjoint candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(DWithin candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Equals candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Intersects candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Overlaps candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Touches candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Within candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(After candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(AnyInteracts candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Before candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Begins candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(BegunBy candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(During candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(EndedBy candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Ends candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Meets candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(MetBy candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(OverlappedBy candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(TContains candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(TEquals candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(TOverlaps candidate, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
