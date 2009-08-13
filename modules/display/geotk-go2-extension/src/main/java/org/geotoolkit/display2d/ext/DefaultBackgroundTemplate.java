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

package org.geotoolkit.display2d.ext;

import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultBackgroundTemplate implements BackgroundTemplate{

    private final Stroke stroke;
    private final Paint strokeFill;
    private final Paint fill;
    private final Insets insets;
    private final int round;

    public DefaultBackgroundTemplate(Stroke stroke, Paint strokeFill, Paint fill, Insets insets, int round) {
        this.stroke = stroke;
        this.strokeFill = strokeFill;
        this.fill = fill;
        this.insets = insets;
        this.round = round;
    }

    @Override
    public Stroke getBackgroundStroke() {
        return stroke;
    }

    @Override
    public Paint getBackgroundStrokePaint() {
        return strokeFill;
    }

    @Override
    public Paint getBackgroundPaint() {
        return fill;
    }

    @Override
    public Insets getBackgroundInsets() {
        return insets;
    }

    @Override
    public int getRoundBorder() {
        return round;
    }

}
