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

import java.util.logging.Logger;

import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.expression.Literal;
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
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.util.logging.Logging;

/**
 * Extract a maximal envelope from the provided Filter.
 * <p>
 * The maximal envelope is generated from:
 * <ul>
 * <li>all the literal geometry instances involved if spatial operations - using
 * geom.getEnvelopeInternal().
 * <li>Filter.EXCLUDES will result in <code>null</code>
 * <li>Filter.INCLUDES will result in a "world" envelope with range Double.NEGATIVE_INFINITY to
 * Double.POSITIVE_INFINITY for each axis.
 * </ul>
 * Since geometry literals do not contains CRS information we can only produce a ReferencedEnvelope
 * without CRS information. You can call this function with an existing ReferencedEnvelope 
 * or with your data CRS to correct for this limitation.
 * ReferencedEnvelope example:<pre><code>
 * ReferencedEnvelope bbox = (ReferencedEnvelope)
 *     filter.accepts(new ExtractBoundsFilterVisitor(), dataCRS );
 * </code></pre>
 * You can also call this function with an existing Envelope; if you are building up bounds based on
 * several filters.
 * <p>
 * This is a replacement for FilterConsumer.
 * 
 * @author Jody Garnett
 * @module pending
 */
public class ExtractBoundsFilterVisitor extends NullFilterVisitor {
    static public NullFilterVisitor BOUNDS_VISITOR = new ExtractBoundsFilterVisitor();
    
    private static final Logger LOGGER = Logging.getLogger(ExtractBoundsFilterVisitor.class);

    /**
     * This FilterVisitor is stateless - use ExtractBoundsFilterVisitor.BOUNDS_VISITOR.
     * <p>
     * You may also subclass in order to reuse this functionality in your own
     * FilterVisitor implementation.
     */
    protected ExtractBoundsFilterVisitor(){        
    }
    
    /**
     * Produce an ReferencedEnvelope from the provided data parameter.
     * 
     * @param data
     * @return ReferencedEnvelope
     */
    private JTSEnvelope2D bbox( Object data ) {
        if( data == null ){
            return null;
        }
        else if (data instanceof JTSEnvelope2D) {
            return (JTSEnvelope2D) data;
        }
        else if (data instanceof Envelope){
            return new JTSEnvelope2D( (Envelope) data, null );
        }
        else if (data instanceof CoordinateReferenceSystem){
            return new JTSEnvelope2D( (CoordinateReferenceSystem) data );
        }
        throw new ClassCastException("Could not cast data to ReferencedEnvelope");        
    }

    @Override
    public Object visit( ExcludeFilter filter, Object data ) {
        return null;
    }

    @Override
    public Object visit( IncludeFilter filter, Object data ) {
        if( data == null ) return null;
        JTSEnvelope2D bbox = bbox( data );
        
        // also consider making use of CRS extent?
        Envelope world = new Envelope(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        bbox.expandToInclude( world );
        return bbox;
    }

    @Override
    public Object visit( BBOX filter, Object data ) {
        if( data == null ) return null;
        JTSEnvelope2D bbox = bbox( data );
                
        // consider doing reprojection here into data CRS?
        Envelope bounds = new Envelope(filter.getMinX(), filter.getMaxX(), filter.getMinY(), filter
                .getMaxY());
        bbox.expandToInclude(bounds);
        return bbox;
    }
    /**
     * Please note we are only visiting literals involved in spatial operations.
     * @param expression , hopefully a Geometry or Envelope
     * @param data Incoming BoundingBox (or Envelope or CRS)
     * 
     * @return ReferencedEnvelope updated to reflect literal
     */
    @Override
    public Object visit( Literal expression, Object data ) {        
        if( data == null ) return null;
        JTSEnvelope2D bbox = bbox( data );

        Object value = expression.getValue();
        if (value instanceof Geometry) {
                        
            Geometry geometry = (Geometry) value;
            Envelope bounds = geometry.getEnvelopeInternal();
            
            bbox.expandToInclude(bounds);
        } else {
            LOGGER.finer("LiteralExpression ignored!");
        }
        return bbox;
    }

    @Override
    public Object visit( Beyond filter, Object data ) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit( Contains filter, Object data ) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit( Crosses filter, Object data ) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit( Disjoint filter, Object data ) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit( DWithin filter, Object data ) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit( Equals filter, Object data ) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        return data;
    }

    @Override
    public Object visit( Intersects filter, Object data ) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit( Overlaps filter, Object data ) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit( Touches filter, Object data ) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);

        return data;
    }

    @Override
    public Object visit( Within filter, Object data ) {
        data = filter.getExpression1().accept(this, data);
        data = filter.getExpression2().accept(this, data);
        
        return data;
    }
    
}
