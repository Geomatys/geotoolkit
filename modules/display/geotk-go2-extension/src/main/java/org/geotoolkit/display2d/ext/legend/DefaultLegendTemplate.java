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
