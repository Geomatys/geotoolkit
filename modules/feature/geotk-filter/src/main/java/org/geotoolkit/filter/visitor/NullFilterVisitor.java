/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
 * Abstract implementation of FilterVisitor simple returns the provided data.
 * <p>
 * This class can be used as is as a placeholder that does nothing:<pre><code>
 * Integer one = (Integer) filter.accepts( NullFilterVisitor.NULL_VISITOR, 1 );
 * </code></pre>
 * 
 * The class can also be used as an alternative to DefaultFilterVisitor if
 * you want to only walk part of the data structure:
 * <pre><code>
 * FilterVisitor allFids = new NullFilterVisitor(){
 *     public Object visit( Id filter, Object data ) {
 *         if( data == null) return null;
 *         Set set = (Set) data;
 *         set.addAll(filter.getIDs());
 *         return set;
 *     }
 * };
 * Set set = (Set) myFilter.accept(allFids, new HashSet());
 * Set set2 = (Set) myFilter.accept(allFids, null ); // set2 will be null
 * </code></pre>
 * The base class provides implementations for:
 * <ul>
 * <li>walking And, Or, and Not data structures, returning null at any point will exit early
 * <li>a default implementation for every other construct that will return the provided data
 * </ul>
 * 
 * @author Jody Garnett (Refractions Research)
 * @module pending
 */
public abstract class NullFilterVisitor implements FilterVisitor, ExpressionVisitor {
    static public NullFilterVisitor NULL_VISITOR = new NullFilterVisitor(){
        @Override
        public Object visit( And filter, Object data ) {
            return data;
        }
        @Override
        public Object visit( Or filter, Object data ) {
            return data;
        }
        @Override
        public Object visit( Not filter, Object data ) {
            return data;
        }
    };
    
    public NullFilterVisitor() {        
    }

    @Override
    public Object visit( final ExcludeFilter filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final IncludeFilter filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final And filter, Object data ) {
        if( data == null ) return null;
        if (filter.getChildren() != null) {
            for( Filter child : filter.getChildren() ) {
                data = child.accept(this, data);
                if( data == null ) return null;
            }
        }
        return data;
    }

    @Override
    public Object visit( final Id filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Not filter, Object data ) {
        if( data == null ) return data;

        Filter child = filter.getFilter();
        if ( child != null) {
            data = child.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit( final Or filter, Object data ) {
        if( data == null ) return null;
        if (filter.getChildren() != null) {
            for( Filter child : filter.getChildren() ) {
                data = child.accept(this, data);
                if( data == null ) return null;
            }
        }
        return data;
    }

    @Override
    public Object visit( final PropertyIsBetween filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final PropertyIsEqualTo filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final PropertyIsNotEqualTo filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final PropertyIsGreaterThan filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final PropertyIsGreaterThanOrEqualTo filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final PropertyIsLessThan filter, final Object data ) {        
        return data;
    }

    @Override
    public Object visit( final PropertyIsLessThanOrEqualTo filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final PropertyIsLike filter, final Object data ) {        
        return data;
    }

    @Override
    public Object visit( final PropertyIsNull filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final BBOX filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Beyond filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Contains filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Crosses filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Disjoint filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final DWithin filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Equals filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Intersects filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Overlaps filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Touches filter, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Within filter, final Object data ) {
        return data;
    }

    @Override
    public Object visitNullFilter( final Object data ) {
        return data;
    }

    @Override
    public Object visit( final NilExpression expression, final Object data ) {        
        return null;
    }

    @Override
    public Object visit( final Add expression, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Divide expression, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Function expression, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Literal expression, final Object data ) {        
        return data;
    }

    @Override
    public Object visit( final Multiply expression, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final PropertyName expression, final Object data ) {
        return data;
    }

    @Override
    public Object visit( final Subtract expression, final Object data ) {
        return data;
    }
}
