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
package org.geotoolkit.display.primitive;

import org.opengis.geometry.Geometry;

/**
 * A search area describe the geometric element that sould contain or intersect
 * the graphic features.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface SearchArea {

    /**
     * @return searching geometry in display CRS.
     */
    public Geometry getDisplayGeometry();

    /**
     * @return searching geometry in objective CRS.
     */
    public Geometry getObjectiveGeometry();

}
