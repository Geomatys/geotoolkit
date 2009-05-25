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

package org.geotoolkit.isowrapper;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.geotoolkit.isowrapper.geometries.AbstractISOJTSGeometry;
import org.geotoolkit.isowrapper.geometries.ISOJTSCurve;
import org.geotoolkit.isowrapper.geometries.ISOJTSMultiCurve;
import org.geotoolkit.isowrapper.geometries.ISOJTSMultiPoint;
import org.geotoolkit.isowrapper.geometries.ISOJTSMultiSurface;
import org.geotoolkit.isowrapper.geometries.ISOJTSPoint;
import org.geotoolkit.isowrapper.geometries.ISOJTSSurface;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WrappingUtilities {

    private WrappingUtilities(){}

    public static AbstractISOJTSGeometry wrap(Geometry geom,CoordinateReferenceSystem crs){
        if(geom == null) return null;

        if(geom instanceof Point){
            return new ISOJTSPoint((Point)geom,crs);
        }else if(geom instanceof MultiPoint){
            return new ISOJTSMultiPoint((MultiPoint)geom,crs);
        }else if(geom instanceof LineString){
            return new ISOJTSCurve((LineString)geom,crs);
        }else if(geom instanceof MultiLineString){
            return new ISOJTSMultiCurve((MultiLineString)geom,crs);
        }else if(geom instanceof Polygon){
            return new ISOJTSSurface((Polygon)geom,crs);
        }else if(geom instanceof MultiPolygon){
            return new ISOJTSMultiSurface((MultiPolygon)geom,crs);
        }else{
            throw new IllegalArgumentException("Unexpected geometry type : " + geom.getClass());
        }
    }

}
