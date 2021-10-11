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

import java.awt.*;

import org.geotoolkit.display2d.ext.BackgroundTemplate;
import org.apache.sis.util.ArgumentChecks;

/**
 * Default legend template, immutable.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultLegendTemplate implements LegendTemplate {

    private final float gap;
    private final Dimension glyphSize;
    private final Font rulefont;
    private final boolean layerVisible;
    private final Font layerFont;
    private Color layerFontColor;
    private Float layerFontOpacity;
    private boolean onlyVisibleLayers = false;
    private final BackgroundTemplate background;

    public DefaultLegendTemplate(final BackgroundTemplate background,
            final float gap, final Dimension glyphSize, final Font rulefont, final boolean layerVisible, final Font layerFont) {
        this.background = background;
        this.gap = gap;
        this.glyphSize = glyphSize;
        this.rulefont = rulefont;
        this.layerVisible = layerVisible;
        this.layerFont = layerFont;
    }

    public DefaultLegendTemplate(final BackgroundTemplate background,
            final float gap, final Dimension glyphSize, final Font rulefont, final boolean layerVisible, final Font layerFont, boolean displayOnlyTheVisibles) {
        this(background, gap, glyphSize, rulefont, layerVisible, layerFont);
        onlyVisibleLayers = displayOnlyTheVisibles;
    }

    public DefaultLegendTemplate(final BackgroundTemplate background, final float gap, final Dimension glyphSize,
                                 final Font rulefont, final boolean layerVisible, final Font layerFont,
                                 final Color layerFontColor, final Float layerFontOpacity, boolean displayOnlyTheVisibles) {
        this(background, gap, glyphSize, rulefont, layerVisible, layerFont, displayOnlyTheVisibles);
        this.layerFontColor = layerFontColor;
        this.layerFontOpacity = layerFontOpacity;
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

    public boolean displayOnlyVisibleLayers() {
        return onlyVisibleLayers;
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

    @Override
    public Color getLayerFontColor() {
        return layerFontColor;
    }

    @Override
    public Float getLayerFontOpacity() {
        return layerFontOpacity;
    }

    @Override
    public void setDisplayOnlyVisibleLayers(boolean displayOption) {
        ArgumentChecks.ensureNonNull("display option", displayOption);
        onlyVisibleLayers = displayOption;
    }
}
