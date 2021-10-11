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
package org.geotoolkit.display2d.style.labeling;

import java.awt.Font;
import java.awt.Paint;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default immutable implementation of Point label descriptor.
 *
 * @author johann Sorel (Geomatys)
 * @module
 */
public class DefaultPointLabelDescriptor implements PointLabelDescriptor{

    private final String text;
    private final Font textFont;
    private final Paint textPaint;
    private final float haloWidth;
    private final Paint haloPaint;
    private final float dispX;
    private final float dispY;
    private final float anchorX;
    private final float anchorY;
    private final float rotation;
    private final CoordinateReferenceSystem crs;
    private final ProjectedGeometry geom;

    public DefaultPointLabelDescriptor(final String text, final Font textFont, final Paint textPaint,
            final float haloWidth, final Paint haloPaint,
            final float anchorX, final float anchorY,
            final float dispX, final float dispY,
            final float rotation,
            final CoordinateReferenceSystem crs,
            final ProjectedGeometry geom){
        this.text = text;
        this.textFont = textFont;
        this.textPaint = textPaint;
        this.haloWidth = haloWidth;
        this.haloPaint = haloPaint;
        this.dispX = dispX;
        this.dispY = dispY;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.rotation = rotation;
        this.crs = crs;
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
    public float getAnchorX() {
        return anchorX;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getAnchorY() {
        return anchorY;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getDisplacementX() {
        return dispX;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getDisplacementY() {
        return dispY;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getRotation() {
        return rotation;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ProjectedGeometry getGeometry() {
        return geom;
    }

}
