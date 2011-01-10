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

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * A thin wrapper that adapts a JTS geometry to the Shape interface so that the geometry can be used
 * by java2d without coordinate cloning.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @version 2.9
 * @module pending
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

        if(iterator == null){
            if (this.geometry.isEmpty()) {
                iterator = JTSEmptyIterator.INSTANCE;
            }else if (this.geometry instanceof Point) {
                iterator = new JTSPointIterator((Point) geometry, at);
            } else if (this.geometry instanceof Polygon) {
                iterator = new JTSPolygonIterator((Polygon) geometry, at);
            } else if (this.geometry instanceof LineString) {
                iterator = new DecimateJTSLineIterator((LineString)geometry, at,resolution);
            } else if (this.geometry instanceof GeometryCollection) {
                iterator = new DecimateJTSGeomCollectionIterator((GeometryCollection)geometry,at,resolution);
            }
        }else{
            iterator.setTransform(at);
        }

        return iterator;
    }


}
