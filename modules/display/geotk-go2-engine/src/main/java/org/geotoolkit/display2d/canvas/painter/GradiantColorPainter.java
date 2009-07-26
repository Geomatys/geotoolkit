/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
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
package org.geotoolkit.display2d.canvas.painter;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import org.geotoolkit.display2d.canvas.RenderingContext2D;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class GradiantColorPainter implements BackgroundPainter{

    private static final Color COLOR_1 = Color.BLACK;
    private static final Color COLOR_2 = Color.GRAY;

    @Override
    public void paint(RenderingContext2D context) {
        Graphics2D g = context.getGraphics();

        Rectangle rect = context.getCanvasDisplayBounds();

        Paint p1 = new GradientPaint(0,rect.y,COLOR_1,0,rect.y +(rect.height/2),COLOR_2);
        Paint p2 = new GradientPaint(0,rect.y +(rect.height/2),COLOR_2, 0,rect.y +rect.height,COLOR_1);

        g.setPaint(p1);
        g.fillRect(rect.x, rect.y, rect.width, rect.height/2);
        g.setPaint(p2);
        g.fillRect(rect.x, rect.y + rect.height/2, rect.width, rect.height/2);


    }

}
