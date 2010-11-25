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
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.display.primitive.Graphic;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class PositionedGraphic2D extends AbstractGraphicJ2D{

    private int position = SwingConstants.SOUTH_EAST;
    private int[] offset = new int[]{0,0};
    private Dimension minimumCanvasSize = new Dimension(0,0);

    public PositionedGraphic2D(J2DCanvas canvas) {
        super(canvas,DefaultGeographicCRS.WGS84);
    }

    /**
     * Position the graphic, NORTH, EAST, etc...
     * 
     * @param position, one value in SwingConstants.*
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Move the graphic inside the canvas, this makes
     * the graphic go away from the image borders.
     *
     * @param offsetX
     * @param offsetY
     */
    public void setOffset(int offsetX, int offsetY) {
        this.offset[0] = offsetX;
        this.offset[1] = offsetY;
    }

    @Override
    public void paint(RenderingContext2D context) {
        Rectangle rect = context.getCanvasDisplayBounds();

        //dont paint the graphic if the canvas is to small
        if(rect.width > minimumCanvasSize.width && rect.height > minimumCanvasSize.height){
            paint(context,position,offset);
        }
    }

    protected abstract void paint(RenderingContext2D context, int position, int[] offset);

    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        return graphics;
    }

}
