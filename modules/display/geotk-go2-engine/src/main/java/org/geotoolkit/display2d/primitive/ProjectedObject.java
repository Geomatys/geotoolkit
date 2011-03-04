/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.map.MapLayer;

import org.opengis.display.primitive.Graphic;

/**
 * Convenient representation of a custom object for rendering.
 * We expect the sub classes to cache information for more efficient rendering.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface ProjectedObject<T> extends Graphic {

    /**
     * Get the original MapLayer from where the object is from.
     *
     * @return MapLayer
     */
    MapLayer getLayer();

    /**
     * Get the object itself.
     *
     * @return Object
     */
    T getCandidate();

    /**
     * Get a Projected geometry for rendering purpose.
     *
     * @param name of the wanted geometry.
     * @return ProjectedGeometry or null if the named geometry attribute doesn't exist
     */
    ProjectedGeometry getGeometry(String name);

    /**
     * @return original canvas of this graphic
     */
    ReferencedCanvas2D getCanvas();

}
