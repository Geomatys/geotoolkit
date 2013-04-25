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
package org.geotoolkit.db.postgres;

import java.lang.reflect.Array;
import java.util.List;
import org.geotoolkit.db.FilterToSQL;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.util.Converters;
import org.opengis.feature.type.Name;
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
 * Convert filters and expressions in SQL.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class PostgresFilterToSQL implements FilterToSQL {

    private static StringBuilder toStringBuilder(Object candidate){
        if(candidate instanceof StringBuilder){
            return (StringBuilder) candidate;
        }else{
            throw new RuntimeException("Expected a StringBuilder argument");
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // EXPRESSION EXPRESSION ///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public StringBuilder visit(NilExpression candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Add candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append('(');
        candidate.getExpression1().accept(this, o);
        sb.append(" + ");
        candidate.getExpression2().accept(this, o);
        sb.append(')');
        return sb;
    }

    @Override
    public StringBuilder visit(Divide candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append('(');
        candidate.getExpression1().accept(this, o);
        sb.append(" / ");
        candidate.getExpression2().accept(this, o);
        sb.append(')');
        return sb;
    }

    @Override
    public StringBuilder visit(Function candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Literal candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        final Object value = candidate.getValue();
        writeValue(sb, value);
        return sb;
    }
    
    private void writeValue(final StringBuilder sb, Object candidate){
        if(candidate == null){
          sb.append("NULL");
          
        }else if(candidate instanceof Number 
              || candidate instanceof Boolean){
            sb.append(String.valueOf(candidate));
            
        }else if(candidate.getClass().isArray()){
            final int size = Array.getLength(candidate);
            sb.append("'{");
            for(int i=0;i<size;i++){
                if(i>0){
                    sb.append(',');
                }
                final Object o = Array.get(candidate, i);
                if(!(o instanceof Number || o instanceof Boolean) && o != null){
                    // we don't know what this is, let's convert back to a string
                    String encoding = Converters.convert(o, String.class);
                    if (encoding == null) {
                        // could not convert back to string, use original value
                        encoding = o.toString();
                    }

                    // single quotes must be escaped to have a valid sql string
                    final String escaped = encoding.replaceAll("'", "''");
                    sb.append(escaped);
                }else{
                    writeValue(sb,o);
                }
            }
            sb.append("}'");
        }else {
            // we don't know what this is, let's convert back to a string
            String encoded = Converters.convert(candidate, String.class);
            if (encoded == null) {
                // could not convert back to string, use original value
                encoded = candidate.toString();
            }

            // single quotes must be escaped to have a valid sql string
            final String escaped = encoded.replaceAll("'", "''");
            sb.append('\'');
            sb.append(escaped);
            sb.append('\'');
        }
    }

    @Override
    public StringBuilder visit(Multiply candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append('(');
        candidate.getExpression1().accept(this, o);
        sb.append(" * ");
        candidate.getExpression2().accept(this, o);
        sb.append(')');
        return sb;
    }

    @Override
    public StringBuilder visit(PropertyName candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);        
        final Name name = DefaultName.valueOf(candidate.getPropertyName());
        sb.append('"');
        sb.append(name.getLocalPart());
        sb.append('"');        
        return sb;
    }

    @Override
    public StringBuilder visit(Subtract candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append('(');
        candidate.getExpression1().accept(this, o);
        sb.append(" - ");
        candidate.getExpression2().accept(this, o);
        sb.append(')');
        return sb;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // FILTER EXPRESSION ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public StringBuilder visitNullFilter(Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(ExcludeFilter candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("1=0");
        return sb;
    }

    @Override
    public StringBuilder visit(IncludeFilter candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("1=1");
        return sb;
    }

    @Override
    public StringBuilder visit(And candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        final List<Filter> subs = candidate.getChildren();
        sb.append('(');
        for(int i=0,n=subs.size();i<n;i++){
            if(i>0){
                sb.append(" AND ");
            }
            subs.get(i).accept(this, o);
        }
        sb.append(')');
        return sb;
    }

    @Override
    public StringBuilder visit(Id candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Not candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        sb.append("NOT(");
        candidate.getFilter().accept(this, o);
        sb.append(')');
        return sb;
    }

    @Override
    public StringBuilder visit(Or candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        final List<Filter> subs = candidate.getChildren();
        sb.append('(');
        for(int i=0,n=subs.size();i<n;i++){
            if(i>0){
                sb.append(" OR ");
            }
            subs.get(i).accept(this, o);
        }
        sb.append(')');
        return sb;
    }

    @Override
    public StringBuilder visit(PropertyIsBetween candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        final Expression exp = candidate.getExpression();
        final Expression lower = candidate.getLowerBoundary();
        final Expression upper = candidate.getUpperBoundary();

        exp.accept(this, o);
        sb.append(" BETWEEN ");
        lower.accept(this, o);
        sb.append(" AND ");
        upper.accept(this, o);
        
        return sb;
    }

    @Override
    public StringBuilder visit(PropertyIsEqualTo candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        candidate.getExpression1().accept(this, o);
        sb.append(" = ");
        candidate.getExpression2().accept(this, o);
        return sb;
    }

    @Override
    public StringBuilder visit(PropertyIsNotEqualTo candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        candidate.getExpression1().accept(this, o);
        sb.append(" <> ");
        candidate.getExpression2().accept(this, o);
        return sb;
    }

    @Override
    public StringBuilder visit(PropertyIsGreaterThan candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        candidate.getExpression1().accept(this, o);
        sb.append(" > ");
        candidate.getExpression2().accept(this, o);
        return sb;
    }

    @Override
    public StringBuilder visit(PropertyIsGreaterThanOrEqualTo candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        candidate.getExpression1().accept(this, o);
        sb.append(" >= ");
        candidate.getExpression2().accept(this, o);
        return sb;
    }

    @Override
    public StringBuilder visit(PropertyIsLessThan candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        candidate.getExpression1().accept(this, o);
        sb.append(" < ");
        candidate.getExpression2().accept(this, o);
        return sb;
    }

    @Override
    public StringBuilder visit(PropertyIsLessThanOrEqualTo candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        candidate.getExpression1().accept(this, o);
        sb.append(" <= ");
        candidate.getExpression2().accept(this, o);
        return sb;
    }

    @Override
    public StringBuilder visit(PropertyIsLike candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(PropertyIsNull candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        candidate.getExpression().accept(this, o);
        sb.append(" IS NULL");
        return sb;
    }

    @Override
    public StringBuilder visit(PropertyIsNil candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        candidate.getExpression().accept(this, o);
        sb.append(" IS NULL");
        return sb;
    }

    @Override
    public StringBuilder visit(BBOX candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Beyond candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Contains candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Crosses candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Disjoint candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(DWithin candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Equals candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Intersects candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Overlaps candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Touches candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Within candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(After candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(AnyInteracts candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Before candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Begins candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(BegunBy candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(During candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(EndedBy candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Ends candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(Meets candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(MetBy candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(OverlappedBy candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(TContains candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(TEquals candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

    @Override
    public StringBuilder visit(TOverlaps candidate, Object o) {
        final StringBuilder sb = toStringBuilder(o);
        if(true) throw new UnsupportedOperationException("Not supported yet.");
        return sb;
    }

}
