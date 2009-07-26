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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import org.geotoolkit.display2d.canvas.RenderingContext2D;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class SolidColorPainter implements BackgroundPainter{

    private final Color color;

    public SolidColorPainter(Color color){
        this.color = color;
    }

    @Override
    public void paint(RenderingContext2D context) {
        if(color != null){
            Graphics2D g = context.getGraphics();
            Rectangle rect = context.getCanvasDisplayBounds();
            g.setPaint(color);
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
    }

}
