/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.primitive.jts;

import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Simple and efficient path iterator for JTS GeometryCollection.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @since 2.9
 */
public final class DecimateJTSGeomCollectionIterator extends JTSGeomCollectionIterator {

    private final double[] resolution;

    public DecimateJTSGeomCollectionIterator(GeometryCollection gc, AffineTransform trs,double[] resolution) {
        super(gc,trs);
        this.resolution = resolution;
        reset();
    }

    /**
     * Returns the specific iterator for the geometry passed.
     *
     * @param candidate The geometry whole iterator is requested
     *
     * @return the specific iterator for the geometry passed.
     */
    @Override
    protected JTSGeometryIterator getIterator(Geometry candidate) {
        JTSGeometryIterator iterator = null;

        if (candidate.isEmpty()) {
            iterator = new JTSEmptyIterator();
        }else if (candidate instanceof Point) {
            iterator = new JTSPointIterator((Point)candidate, transform);
        } else if (candidate instanceof Polygon) {
            iterator = new JTSPolygonIterator((Polygon)candidate, transform);
        } else if (candidate instanceof LineString) {
            iterator = new DecimateJTSLineIterator((LineString)candidate, transform,resolution);
        } else if (candidate instanceof GeometryCollection) {
            iterator = new DecimateJTSGeomCollectionIterator((GeometryCollection)candidate,transform,resolution);
        }

        return iterator;
    }
    
}
