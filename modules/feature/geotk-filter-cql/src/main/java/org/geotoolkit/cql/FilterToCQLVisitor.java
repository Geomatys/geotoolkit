/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.cql;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;
import java.util.Date;
import org.geotoolkit.temporal.object.TemporalUtilities;
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

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FilterToCQLVisitor implements FilterVisitor, ExpressionVisitor {

    public static FilterToCQLVisitor INSTANCE = new FilterToCQLVisitor();

    private FilterToCQLVisitor() {
    }
    
    private static StringBuilder toStringBuilder(Object o){
        if(o instanceof StringBuilder){
            return (StringBuilder) o;
        }
        return new StringBuilder();
    }

    @Override
    public Object visitNullFilter(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(ExcludeFilter filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(IncludeFilter filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(And filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Id filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Not filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Or filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsBetween filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsEqualTo filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsNotEqualTo filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsGreaterThan filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsGreaterThanOrEqualTo filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsLessThan filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsLessThanOrEqualTo filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsLike filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(PropertyIsNull filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(BBOX filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Beyond filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Contains filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Crosses filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Disjoint filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(DWithin filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Equals filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Intersects filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Overlaps filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Touches filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Within filter, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // EXPRESSIONS /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public Object visit(Literal exp, Object o) {
        final StringBuilder sb = toStringBuilder(o);

        final Object value = exp.getValue();
        if(value instanceof Number){
            final Number num = (Number) value;
            sb.append(num.toString());
        }else if(value instanceof Date){
            final Date date = (Date) value;
            sb.append(TemporalUtilities.toISO8601(date));
        }else if(value instanceof Geometry){
            final Geometry geometry = (Geometry) value;
            final WKTWriter writer = new WKTWriter();
            final String wkt = writer.write(geometry);
            sb.append(wkt);
        }else{
            sb.append('\'').append(value.toString()).append('\'');
        }
        return sb;
    }

    @Override
    public Object visit(PropertyName exp, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        final String name = exp.getPropertyName();
        if(name.contains(" ")){
            //contain spaces, we escape it
            sb.append('"').append(name).append('"');
        }else{
            sb.append(name);
        }
        return sb;        
    }
    
    @Override
    public Object visit(Add exp, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Divide exp, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Object visit(Multiply exp, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(Subtract exp, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Object visit(Function exp, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Object visit(NilExpression exp, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
