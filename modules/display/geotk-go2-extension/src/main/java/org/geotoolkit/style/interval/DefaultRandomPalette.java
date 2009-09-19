/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009 Geomatys
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

package org.geotoolkit.style.interval;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultRandomPalette implements RandomPalette{

    @Override
    public Color next() {
        return new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1f);
    }

    @Override
    public void render(Graphics2D g, Rectangle rectangle) {

        final float[] fractions = new float[3];
        final Color[] colors = new Color[3];
        final MultipleGradientPaint.CycleMethod cycleMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
        final MultipleGradientPaint.ColorSpaceType colorSpace = MultipleGradientPaint.ColorSpaceType.SRGB;

        fractions[0] = 0.0f;
        fractions[1] = 0.5f;
        fractions[2] = 1f;

        colors[0] = Color.RED;
        colors[1] = Color.GREEN;
        colors[2] = Color.BLUE;

        final LinearGradientPaint paint = new LinearGradientPaint(
            new Point2D.Double(rectangle.getMinX(),rectangle.getMinY()),
            new Point2D.Double(rectangle.getMaxX(),rectangle.getMinY()),
            fractions,
            colors,
            cycleMethod
        );

        g.setPaint(paint);
        g.fill(rectangle);

    }

}
