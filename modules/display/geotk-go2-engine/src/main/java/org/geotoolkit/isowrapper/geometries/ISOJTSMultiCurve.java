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
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ISOJTSMultiCurve extends AbstractISOJTSGeometry<com.vividsolutions.jts.geom.MultiLineString> implements MultiCurve{

    private Set<OrientableCurve> curves;

    public ISOJTSMultiCurve(com.vividsolutions.jts.geom.MultiLineString ln, CoordinateReferenceSystem crs) {
        super(ln,crs);
    }

    @Override
    public synchronized Set<OrientableCurve> getElements() {
        if(curves == null){
            curves = new HashSet<OrientableCurve>();
            int num = jtsGeometry.getNumGeometries();

            for(int i=0; i<num;i++){
                com.vividsolutions.jts.geom.LineString line = (com.vividsolutions.jts.geom.LineString) jtsGeometry.getGeometryN(i);
                curves.add(new ISOJTSCurve(line,crs));
            }
        }

        return curves;
    }

    @Override
    public double length() {
        return jtsGeometry.getLength();
    }

}
