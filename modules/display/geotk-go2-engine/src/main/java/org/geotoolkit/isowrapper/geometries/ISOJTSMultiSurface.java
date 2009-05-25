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
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ISOJTSMultiSurface extends AbstractISOJTSGeometry<com.vividsolutions.jts.geom.MultiPolygon> implements MultiSurface{

    private Set<OrientableSurface> surfaces;

    public ISOJTSMultiSurface(com.vividsolutions.jts.geom.MultiPolygon pl, CoordinateReferenceSystem crs) {
        super(pl,crs);
    }

    @Override
    public Set<OrientableSurface> getElements() {
        if(surfaces == null){
            surfaces = new HashSet<OrientableSurface>();
        
            int num = jtsGeometry.getNumGeometries();

            for(int i=0; i<num;i++){
                com.vividsolutions.jts.geom.Polygon poly = (com.vividsolutions.jts.geom.Polygon) jtsGeometry.getGeometryN(i);
                surfaces.add(new ISOJTSSurface(poly,crs));
            }
        }

        return surfaces;
    }

    @Override
    public double getArea() {
        return jtsGeometry.getArea();
    }

}
