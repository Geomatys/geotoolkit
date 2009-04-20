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
package org.geotoolkit.display2d.ext.legend;

import java.awt.Font;


/**
 * Template holding informations about the design of the legend to paint.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface LegendTemplate {

    /**
     * @return the gap between 2 legend elements
     */
    float getGapSize();

    /**
     * @return height of the glyph
     */
    float getGlyphHeight();

    /**
     * @return width of the glyph
     */
    float getGlyphWidth();

    /**
     * @return Font to use
     */
    Font getFont();

}
