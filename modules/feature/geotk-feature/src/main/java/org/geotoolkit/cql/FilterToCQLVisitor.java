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
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.geotoolkit.filter.DefaultPropertyIsLike;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.opengis.filter.And;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
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
 * Visitor to convert a Filter in CQL.<br>
 * Returned object is a StringBuilder containing the CQL text.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FilterToCQLVisitor implements FilterVisitor, ExpressionVisitor {

    public static final FilterToCQLVisitor INSTANCE = new FilterToCQLVisitor();
    private static final TimeZone TZ = new SimpleTimeZone(0, "Out Timezone");

    private FilterToCQLVisitor() {
    }
    
    private static StringBuilder toStringBuilder(final Object o){
        if(o instanceof StringBuilder){
            return (StringBuilder) o;
        }
        return new StringBuilder();
    }

    
    ////////////////////////////////////////////////////////////////////////////
    // FILTER //////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public Object visitNullFilter(final Object o) {
        throw new UnsupportedOperationException("Null filter not supported in CQL.");
    }

    @Override
    public Object visit(final ExcludeFilter filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("1=0");
        return sb;
    }

    @Override
    public Object visit(final IncludeFilter filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("1=1");
        return sb;
    }

    @Override
    public Object visit(final And filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);        
        final List<Filter> filters = filter.getChildren();
        if(filters != null && !filters.isEmpty()){
            final int size = filters.size();
            sb.append('(');            
            for(int i=0,n=size-1;i<n;i++){
                filters.get(i).accept(this,sb);
                sb.append(" AND ");
            }
            filters.get(size-1).accept(this,sb);            
            sb.append(')');
        }        
        return sb;
    }
    
    @Override
    public Object visit(final Or filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);        
        final List<Filter> filters = filter.getChildren();
        if(filters != null && !filters.isEmpty()){
            final int size = filters.size();
            sb.append('(');            
            for(int i=0,n=size-1;i<n;i++){
                filters.get(i).accept(this,sb);
                sb.append(" OR ");
            }
            filters.get(size-1).accept(this,sb);            
            sb.append(')');
        }        
        return sb;
    }
    
    @Override
    public Object visit(final Id filter, final Object o) {
        throw new UnsupportedOperationException("ID filter not supported in CQL.");
    }

    @Override
    public Object visit(final Not filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("NOT ");
        filter.getFilter().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(final PropertyIsBetween filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression().accept(this,sb);
        sb.append(" BETWEEN ");
        filter.getLowerBoundary().accept(this,sb);
        sb.append(" AND ");
        filter.getUpperBoundary().accept(this,sb);
        return sb;
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
            sb.append("strToUpperCase(");
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
    public Object visit(PropertyIsNil filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression().accept(this,sb);
        sb.append(" IS NIL");
        return sb;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // GEOMETRY FILTER /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public Object visit(final BBOX filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        
        if(filter.getExpression1() instanceof PropertyName 
           && filter.getExpression2() instanceof Literal){
            //use writing : BBOX(att,v1,v2,v3,v4)
            sb.append("BBOX(");
            sb.append(filter.getPropertyName());
            sb.append(',');
            sb.append(filter.getMinX());
            sb.append(',');
            sb.append(filter.getMaxX());
            sb.append(',');
            sb.append(filter.getMinY());
            sb.append(',');
            sb.append(filter.getMaxY());
            sb.append(')');
            
        }else{
            //use writing BBOX(exp1,exp2)
            sb.append("BBOX(");
            filter.getExpression1().accept(this,sb);
            sb.append(',');
            filter.getExpression2().accept(this,sb);
            sb.append(')');            
        }
        
        return sb;
    }

    @Override
    public Object visit(final Beyond filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("BEYOND(");
        filter.getExpression1().accept(this,sb);
        sb.append(',');
        filter.getExpression2().accept(this,sb);
        sb.append(')');        
        return sb;
    }

    @Override
    public Object visit(final Contains filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("CONTAINS(");
        filter.getExpression1().accept(this,sb);
        sb.append(',');
        filter.getExpression2().accept(this,sb);
        sb.append(')');        
        return sb;
    }

    @Override
    public Object visit(final Crosses filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("CROSSES(");
        filter.getExpression1().accept(this,sb);
        sb.append(',');
        filter.getExpression2().accept(this,sb);
        sb.append(')');        
        return sb;
    }

    @Override
    public Object visit(final Disjoint filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("DISJOINT(");
        filter.getExpression1().accept(this,sb);
        sb.append(',');
        filter.getExpression2().accept(this,sb);
        sb.append(')');        
        return sb;
    }

    @Override
    public Object visit(final DWithin filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("DWITHIN(");
        filter.getExpression1().accept(this,sb);
        sb.append(',');
        filter.getExpression2().accept(this,sb);
        sb.append(')');        
        return sb;
    }

    @Override
    public Object visit(final Equals filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("EQUALS(");
        filter.getExpression1().accept(this,sb);
        sb.append(',');
        filter.getExpression2().accept(this,sb);
        sb.append(')');        
        return sb;
    }

    @Override
    public Object visit(final Intersects filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("INTERSECTS(");
        filter.getExpression1().accept(this,sb);
        sb.append(',');
        filter.getExpression2().accept(this,sb);
        sb.append(')');        
        return sb;
    }

    @Override
    public Object visit(final Overlaps filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("OVERLAPS(");
        filter.getExpression1().accept(this,sb);
        sb.append(',');
        filter.getExpression2().accept(this,sb);
        sb.append(')');        
        return sb;
    }

    @Override
    public Object visit(final Touches filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("TOUCHES(");
        filter.getExpression1().accept(this,sb);
        sb.append(',');
        filter.getExpression2().accept(this,sb);
        sb.append(')');        
        return sb;
    }

    @Override
    public Object visit(final Within filter, final Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("WITHIN(");
        filter.getExpression1().accept(this,sb);
        sb.append(',');
        filter.getExpression2().accept(this,sb);
        sb.append(')');        
        return sb;
    }

    ////////////////////////////////////////////////////////////////////////////
    // TEMPORAL FILTER /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public Object visit(After filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" AFTER ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(AnyInteracts filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" ANYINTERACTS ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(Before filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" BEFORE ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(Begins filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" BEGINS ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(BegunBy filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" BEGUNBY ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(During filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" DURING ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(EndedBy filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" ENDEDBY ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(Ends filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" ENDS ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(Meets filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" MEETS ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(MetBy filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" METBY ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(OverlappedBy filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" OVERLAPPEDBY ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(TContains filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" TCONTAINS ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(TEquals filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" TEQUALS ");
        filter.getExpression2().accept(this,sb);
        return sb;
    }

    @Override
    public Object visit(TOverlaps filter, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        filter.getExpression1().accept(this,sb);
        sb.append(" TOVERLAPS ");
        filter.getExpression2().accept(this,sb);
        return sb;
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
            sb.append(TemporalUtilities.toISO8601Z(date,TZ));
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
