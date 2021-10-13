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
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.opengis.referencing.operation.MathTransform;

/**
 * A thin wrapper that adapts a JTS geometry to the Shape interface so that the geometry can be used
 * by java2d without coordinate cloning.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @version 2.9
 * @module
 */
public class DecimateJTSGeometryJ2D extends JTSGeometryJ2D {

    private final double[] resolution;

    /**
     * Creates a new GeometryJ2D object.
     *
     * @param geom - the wrapped geometry
     */
    public DecimateJTSGeometryJ2D(final Geometry geom, final double[] resolution) {
        super(geom);
        this.resolution = resolution;
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform at) {

        MathTransform t = (at==null) ? null : new AffineTransform2D(at);

        if(iterator == null){
            if (this.geometry.isEmpty()) {
                iterator = JTSEmptyIterator.INSTANCE;
            }else if (this.geometry instanceof Point) {
                iterator = new JTSPointIterator((Point) geometry, t);
            } else if (this.geometry instanceof Polygon) {
                iterator = new JTSPolygonIterator((Polygon) geometry, t);
            } else if (this.geometry instanceof LineString) {
                iterator = new DecimateJTSLineIterator((LineString)geometry, t,resolution);
            } else if (this.geometry instanceof GeometryCollection) {
                iterator = new DecimateJTSGeomCollectionIterator((GeometryCollection)geometry,t,resolution);
            }
        }else{
            iterator.setTransform(t);
        }

        return iterator;
    }


}
