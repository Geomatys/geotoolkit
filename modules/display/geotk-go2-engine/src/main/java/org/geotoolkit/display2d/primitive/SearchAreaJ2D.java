/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.primitive;

import java.awt.Shape;

import org.geotoolkit.display.primitive.SearchArea;

/**
 * Extended search area for java2d use.
 * Convinient methods to obtain JTS and Java2D equivalent of the ISO search area.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface SearchAreaJ2D extends SearchArea {

    /**
     * Objective crs search area in JTS geometry.
     * @return JTS Geometry
     */
    com.vividsolutions.jts.geom.Geometry getObjectiveGeometryJTS();

    /**
     * Display crs search area in JTS geometry.
     * @return JTS Geometry
     */
    com.vividsolutions.jts.geom.Geometry getDisplayGeometryJTS();

    /**
     * Objective crs search area in Java2D shape.
     * @return Java2D shape.
     */
    Shape getObjectiveShape();

    /**
     * Display crs search area in Java2D shape.
     * @return Java2D shape.
     */
    Shape getDisplayShape();

}
