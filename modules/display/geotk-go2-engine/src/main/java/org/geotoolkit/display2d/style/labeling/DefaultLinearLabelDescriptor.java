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
package org.geotoolkit.display2d.style.labeling;

import java.awt.Font;
import java.awt.Paint;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;

/**
 * Immutable default linear label descriptor.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
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
    private final ProjectedGeometry geom;
    
    public DefaultLinearLabelDescriptor(final String text, final Font textFont, final Paint textPaint,
            final float haloWidth, final Paint haloPaint, 
            final float gap, final float initial, final float offset,
            final boolean repeated, final boolean aligned, final boolean generalize,
            final ProjectedGeometry geom){
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
        this.geom = geom;
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

    /**
     * {@inheritDoc }
     */
    @Override
    public ProjectedGeometry getGeometry() {
        return geom;
    }

}
