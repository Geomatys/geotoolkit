/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.isowrapper.geometries;

import java.util.HashSet;
import java.util.Set;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.primitive.Point;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ISOJTSMultiPoint extends AbstractISOJTSGeometry<com.vividsolutions.jts.geom.MultiPoint> implements MultiPoint{

    private Set<Point> points;

    public ISOJTSMultiPoint(com.vividsolutions.jts.geom.MultiPoint point, CoordinateReferenceSystem crs) {
        super(point,crs);
    }

    @Override
    public synchronized Set<Point> getElements() {
        if(points == null){
            points = new HashSet<Point>();
            for(int i=0,n=jtsGeometry.getNumGeometries(); i<n; i++){
                com.vividsolutions.jts.geom.Point p = (com.vividsolutions.jts.geom.Point) jtsGeometry.getGeometryN(i);
                points.add(new ISOJTSPoint(p,crs));
            }
        }

        return points;
    }

}
