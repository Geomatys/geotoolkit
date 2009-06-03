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

import org.opengis.geometry.Geometry;
import org.opengis.referencing.operation.TransformException;

/**
 * Convinient interface to manipulate geometry in the go2 engine.
 * The geometry may be asked in different format depending of the needs.
 * </br>
 * For exemple it is interesting to use the java2d shape for painting and the
 * ISO/JTS geometries for intersections tests.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface ProjectedGeometry  {

    com.vividsolutions.jts.geom.Geometry getObjectiveGeometry() throws TransformException;

    com.vividsolutions.jts.geom.Geometry getDisplayGeometry() throws TransformException;

    Shape getObjectiveShape() throws TransformException;

    Shape getDisplayShape() throws TransformException;

    Shape getObjectiveBounds() throws TransformException;

    Shape getDisplayBounds() throws TransformException;

    Geometry getObjectiveGeometryISO() throws TransformException;

    Geometry getDisplayGeometryISO() throws TransformException;

}
