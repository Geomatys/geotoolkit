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
import java.util.List;
import org.geotoolkit.filter.DefaultPropertyIsLike;
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
 *
 * @author Johann Sorel (Geomatys)
 */
public class FilterToCQLVisitor implements FilterVisitor, ExpressionVisitor {

    public static final FilterToCQLVisitor INSTANCE = new FilterToCQLVisitor();

    private FilterToCQLVisitor() {
    }
    
    private static StringBuilder toStringBuilder(final Object o){
        if(o instanceof StringBuilder){
            return (StringBuilder) o;
        }
        return new StringBuilder();
    }

    @Override
    public Object visitNullFilter(final Object o) {
        throw new UnsupportedOperationException("Null filter not supported in CQL.");
    }

    @Override
    public Object visit(final ExcludeFilter filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final IncludeFilter filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final And filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final Id filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final Not filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final Or filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final PropertyIsBetween filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final PropertyIsEqualTo filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" = ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(final PropertyIsNotEqualTo filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" <> ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(final PropertyIsGreaterThan filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" > ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(final PropertyIsGreaterThanOrEqualTo filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" >= ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(final PropertyIsLessThan filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" < ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(final PropertyIsLessThanOrEqualTo filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" <= ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(final PropertyIsLike filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        final char escape = filter.getEscape().charAt(0);
        final char wildCard = filter.getWildCard().charAt(0);
        final char singleChar = filter.getSingleChar().charAt(0);
        final boolean matchingCase = filter.isMatchingCase();
        final String literal = filter.getLiteral();
        final String pattern = DefaultPropertyIsLike.convertToSQL92(escape, wildCard, singleChar, literal);

        if(matchingCase){
            filter.getExpression().accept(this,sb);
        }else{
            sb.append("upper(");
            filter.getExpression().accept(this,sb);
            sb.append(')');
        }
        
        sb.append(" LIKE ");
        sb.append('\'');
        sb.append(pattern);
        sb.append('\'');
        return sb;
    }

    @Override
    public Object visit(final PropertyIsNull filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression().accept(this,sb);
        sb.append(" IS NULL");
        return sb;
    }

    @Override
    public Object visit(final BBOX filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final Beyond filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final Contains filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final Crosses filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final Disjoint filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final DWithin filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final Equals filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final Intersects filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final Overlaps filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final Touches filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object visit(final Within filter, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // EXPRESSIONS /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public Object visit(final Literal exp, final Object o) {
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
    public Object visit(final PropertyName exp, final Object o) {
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
    public Object visit(final Add exp, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        exp.getExpression1().accept(this,sb);
        sb.append(" + ");
        exp.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(final Divide exp, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        exp.getExpression1().accept(this,sb);
        sb.append(" / ");
        exp.getExpression2().accept(this,sb);
        return sb;
    }
    
    @Override
    public Object visit(final Multiply exp, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        exp.getExpression1().accept(this,sb);
        sb.append(" * ");
        exp.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(final Subtract exp, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        exp.getExpression1().accept(this,sb);
        sb.append(" - ");
        exp.getExpression2().accept(this,sb);
        return sb;
    }
    
    @Override
    public Object visit(final Function exp, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append(exp.getName());
        sb.append('(');
        final List<Expression> exps = exp.getParameters();
        if(exps != null){
            final int size = exps.size();
            if(size==1){
                exps.get(0).accept(this,sb);
            }else if(size>1){
                for(int i=0,n=size-1;i<n;i++){
                    exps.get(i).accept(this,sb);
                    sb.append(" , ");
                }
                exps.get(size-1).accept(this,sb);
            }            
        }
        sb.append(')');
        return sb;
    }
    
    @Override
    public Object visit(final NilExpression exp, final Object o) {
        throw new UnsupportedOperationException("NilExpression not supported in CQL.");
    }
    
}
