/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2008, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display2d.ext.northarrow;

import java.awt.Dimension;
import java.awt.Image;

/**
 * Template holding informations about the design of the north arrow to paint.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface NorthArrowTemplate {

    /**
     * Get an image representation of the north arrow.
     *
     * @param size, wanted image size
     * @return Image or null if the image could not be generated
     */
    public Image getImage(Dimension size);

}
