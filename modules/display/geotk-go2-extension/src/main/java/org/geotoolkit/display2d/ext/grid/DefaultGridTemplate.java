/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.display2d.ext.grid;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultGridTemplate implements GridTemplate{

    private final CoordinateReferenceSystem crs;

    private final Stroke mainLineStroke;
    private final Paint mainLinePaint;

    private final Stroke lineStroke;
    private final Paint linePaint;

    private final Font mainLabelFont;
    private final Paint mainLabelPaint;
    private final float mainHaloWidth;
    private final Paint mainHaloPaint;

    private final Font labelFont;
    private final Paint labelPaint;
    private final float haloWidth;
    private final Paint haloPaint;

    public DefaultGridTemplate(final CoordinateReferenceSystem crs, final Stroke mainLineStroke,
            final Paint mainLinePaint, final Stroke lineStroke, final Paint linePaint, final Font mainLabelFont,
            final Paint mainLabelPaint, final float mainHaloWidth, final Paint mainHaloPaint, final Font labelFont,
            final Paint labelPaint, final float haloWidth, final Paint haloPaint) {
        this.crs = crs;
        this.mainLineStroke = mainLineStroke;
        this.mainLinePaint = mainLinePaint;
        this.lineStroke = lineStroke;
        this.linePaint = linePaint;
        this.mainLabelFont = mainLabelFont;
        this.mainLabelPaint = mainLabelPaint;
        this.mainHaloWidth = mainHaloWidth;
        this.mainHaloPaint = mainHaloPaint;
        this.labelFont = labelFont;
        this.labelPaint = labelPaint;
        this.haloWidth = haloWidth;
        this.haloPaint = haloPaint;
    }

    @Override
    public CoordinateReferenceSystem getCRS() {
        return crs;
    }

    @Override
    public Stroke getLineStroke() {
        return lineStroke;
    }

    @Override
    public Paint getLinePaint() {
        return linePaint;
    }

    @Override
    public Font getLabelFont() {
        return labelFont;
    }

    @Override
    public Paint getLabelPaint() {
        return labelPaint;
    }

    @Override
    public float getHaloWidth() {
        return haloWidth;
    }

    @Override
    public Paint getHaloPaint() {
        return haloPaint;
    }

    @Override
    public Stroke getMainLineStroke() {
        return mainLineStroke;
    }

    @Override
    public Paint getMainLinePaint() {
        return mainLinePaint;
    }

    @Override
    public Font getMainLabelFont() {
        return mainLabelFont;
    }

    @Override
    public Paint getMainLabelPaint() {
        return mainLabelPaint;
    }

    @Override
    public float getMainHaloWidth() {
        return mainHaloWidth;
    }

    @Override
    public Paint getMainHaloPaint() {
        return mainHaloPaint;
    }

}
