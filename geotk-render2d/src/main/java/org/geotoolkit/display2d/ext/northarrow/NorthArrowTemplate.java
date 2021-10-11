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
package org.geotoolkit.display2d.ext.northarrow;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import org.geotoolkit.display2d.ext.BackgroundTemplate;

/**
 * Template holding informations about the design of the north arrow to paint.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface NorthArrowTemplate {

    BackgroundTemplate getBackground();

    /**
     * Get an image representation of the north arrow.
     *
     * @return Image or null if the image could not be generated
     */
    Image getImage();

    /**
     * Render in SVG quality if possible.
     */
    void renderImage(Graphics2D g, Point2D center);

    Dimension getSize();

}
