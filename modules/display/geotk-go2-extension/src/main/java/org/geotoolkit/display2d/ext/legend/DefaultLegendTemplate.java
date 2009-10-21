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
package org.geotoolkit.display2d.ext.legend;

import java.awt.Dimension;
import java.awt.Font;
import org.geotoolkit.display2d.ext.BackgroundTemplate;


/**
 * Default legend template, immutable.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultLegendTemplate implements LegendTemplate{

    private final float gap;
    private final Dimension glyphSize;
    private final Font rulefont;
    private final boolean layerVisible;
    private final Font layerFont;

    private final BackgroundTemplate background;


    public DefaultLegendTemplate(BackgroundTemplate background,
            float gap, Dimension glyphSize, Font rulefont, boolean layerVisible, Font layerFont) {
        this.background = background;
        this.gap = gap;
        this.glyphSize = glyphSize;
        this.rulefont = rulefont;
        this.layerVisible = layerVisible;
        this.layerFont = layerFont;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BackgroundTemplate getBackground() {
        return background;
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
    public Dimension getGlyphSize() {
        return glyphSize;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Font getRuleFont() {
        return rulefont;
    }

    @Override
    public boolean isLayerVisible() {
        return layerVisible;
    }

    @Override
    public Font getLayerFont() {
        return layerFont;
    }

}
