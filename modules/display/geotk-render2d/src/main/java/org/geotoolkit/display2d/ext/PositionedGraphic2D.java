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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.SwingConstants;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.opengis.display.primitive.Graphic;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class PositionedGraphic2D extends GraphicJ2D{

    private int position = SwingConstants.SOUTH_EAST;
    private int[] offset = new int[]{0,0};
    private Dimension minimumCanvasSize = new Dimension(0,0);

    public PositionedGraphic2D(final J2DCanvas canvas) {
        super(canvas);
    }

    /**
     * Position the graphic, NORTH, EAST, etc...
     *
     * @param position, one value in SwingConstants.*
     */
    public void setPosition(final int position) {
        this.position = position;
    }

    /**
     * Move the graphic inside the canvas, this makes
     * the graphic go away from the image borders.
     *
     * @param offsetX
     * @param offsetY
     */
    public void setOffset(final int offsetX, final int offsetY) {
        this.offset[0] = offsetX;
        this.offset[1] = offsetY;
    }

    @Override
    public boolean paint(final RenderingContext2D context) {
        Rectangle rect = context.getCanvasDisplayBounds();

        //dont paint the graphic if the canvas is to small
        if(rect.width > minimumCanvasSize.width && rect.height > minimumCanvasSize.height){
            paint(context,position,offset);
        }
        return false;
    }

    protected abstract void paint(RenderingContext2D context, int position, int[] offset);

    @Override
    public List<Graphic> getGraphicAt(final RenderingContext context, final SearchArea mask, final VisitFilter filter, final List<Graphic> graphics) {
        return graphics;
    }

}
