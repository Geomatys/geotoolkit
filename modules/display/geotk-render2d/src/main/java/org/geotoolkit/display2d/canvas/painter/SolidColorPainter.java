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
import static org.apache.sis.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
public class SolidColorPainter implements BackgroundPainter{

    private final Color color;

    public SolidColorPainter(final Color color){
        ensureNonNull("color", color);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void paint(final RenderingContext2D context) {
        if(color.getAlpha() == 0) return;
        final Graphics2D g = context.getGraphics();
        final Rectangle rect = context.getCanvasDisplayBounds();
        g.setPaint(color);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    @Override
    public boolean isOpaque() {
        return color.getAlpha() == 255;
    }

}
