/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
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
package org.geotoolkit.filter.binaryspatial;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.referencing.CRS;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BBOX;
import org.opengis.geometry.Envelope;


/**
 * Implements a Bounding Box expression.
 * <p>
 * Please note this is exactly the same as doing:
 * <code>
 * filterFactory.literal( JTS.toGeometry( bounds ) );
 * </code>
 * 
 * @author Ian Turton, CCG
 * @source $URL$
 * @version $Id$
 */
public class DefaultBBox extends AbstractBinarySpatialOperator<PropertyName,DefaultLiteral<Envelope>> implements BBOX {

    public DefaultBBox(PropertyName property, DefaultLiteral<Envelope> bbox) {
        super(property,bbox);
    }

    @Override
    public String getPropertyName() {
        return left.getPropertyName();
    }

    @Override
    public String getSRS() {
        return CRS.toSRS(right.getValue().getCoordinateReferenceSystem());
    }

    @Override
    public double getMinX() {
        return right.getValue().getMinimum(0);
    }

    @Override
    public double getMinY() {
        return right.getValue().getMinimum(1);
    }

    @Override
    public double getMaxX() {
        return right.getValue().getMaximum(0);
    }

    @Override
    public double getMaxY() {
        return right.getValue().getMaximum(1);
    }

    @Override
    public boolean evaluate(Object object) {
        final Geometry candidate = left.evaluate(object, Geometry.class);

        if(candidate == null){
            return false;
        }

        return candidate.intersects(toGeometry(right.getValue()));
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }
    
}
