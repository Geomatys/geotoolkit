/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display2d.style.renderer;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;

/**
 * Immutable default linear label descriptor.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class DefaultLinearLabelDescriptor implements LinearLabelDescriptor{

    private final String text;
    private final Font textFont;
    private final Paint textPaint;
    private final float haloWidth;
    private final Paint haloPaint;
    private final float gap;
    private final float initial;
    private final float offset;
    private final boolean repeated;
    private final boolean aligned;
    private final boolean generalize;
    private final Shape path;
    
    public DefaultLinearLabelDescriptor(String text, Font textFont, Paint textPaint,
            float haloWidth, Paint haloPaint, 
            float gap, float initial, float offset,
            boolean repeated, boolean aligned, boolean generalize,
            Shape path){
        this.text = text;
        this.textFont = textFont;
        this.textPaint = textPaint;
        this.haloWidth = haloWidth;
        this.haloPaint = haloPaint;
        this.gap = gap;
        this.initial = initial;
        this.offset = offset;
        this.repeated = repeated;
        this.aligned = aligned;
        this.generalize = generalize;
        this.path = path;
        
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Font getTextFont() {
        return textFont;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Paint getTextPaint() {
        return textPaint;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getHaloWidth() {
        return haloWidth;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Paint getHaloPaint() {
        return haloPaint;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Shape getLineplacement() {
        return path;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getGap() {
        return gap;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getInitialGap() {
        return initial;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getOffSet() {
        return offset;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isRepeated() {
        return repeated;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isAligned() {
        return aligned;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isGeneralized() {
        return generalize;
    }

}
