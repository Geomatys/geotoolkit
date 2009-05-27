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
