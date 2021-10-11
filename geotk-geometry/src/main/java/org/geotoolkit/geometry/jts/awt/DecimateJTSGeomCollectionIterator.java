/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.geometry.jts.awt;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.referencing.operation.MathTransform;

/**
 * Simple and efficient path iterator for JTS GeometryCollection.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 * @since 2.9
 */
public final class DecimateJTSGeomCollectionIterator extends JTSGeomCollectionIterator {

    private final double[] resolution;

    public DecimateJTSGeomCollectionIterator(final GeometryCollection gc, final MathTransform trs,final double[] resolution) {
        super(gc,trs);
        this.resolution = resolution;
        reset();
    }

    /**
     * Returns the specific iterator for the geometry passed.
     *
     * @param candidate The geometry whole iterator is requested
     *
     */
    @Override
    protected void prepareIterator(final Geometry candidate) {

        if (candidate.isEmpty()) {
            currentIterator = JTSEmptyIterator.INSTANCE;
        }else if (candidate instanceof Point) {
            currentIterator = new JTSPointIterator((Point)candidate, transform);
        } else if (candidate instanceof Polygon) {
            currentIterator = new JTSPolygonIterator((Polygon)candidate, transform);
        } else if (candidate instanceof LineString) {
            currentIterator = new DecimateJTSLineIterator((LineString)candidate, transform,resolution);
        } else if (candidate instanceof GeometryCollection) {
            currentIterator = new DecimateJTSGeomCollectionIterator((GeometryCollection)candidate,transform,resolution);
        }else{
            currentIterator = JTSEmptyIterator.INSTANCE;
        }
    }

}
