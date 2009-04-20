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
 * Default legend template, immutable.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultLegendTemplate implements LegendTemplate{

    private final float gap;
    private final float glyphHeight;
    private final float glyphWidth;
    private final Font font;

    public DefaultLegendTemplate(float gap, float glyphHeight, float glyphWidth, Font font){
        this.gap = gap;
        this.glyphHeight = glyphHeight;
        this.glyphWidth = glyphWidth;
        this.font = font;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getGapSize() {
        return gap;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getGlyphHeight() {
        return glyphHeight;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getGlyphWidth() {
        return glyphWidth;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Font getFont() {
        return font;
    }

}
