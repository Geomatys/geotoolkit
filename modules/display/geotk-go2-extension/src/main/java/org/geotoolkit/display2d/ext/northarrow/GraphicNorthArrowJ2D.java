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
package org.geotoolkit.display2d.ext.northarrow;

import org.geotoolkit.display2d.canvas.J2DCanvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.logging.Level;

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.ext.PositionedGraphic2D;

import static javax.swing.SwingConstants.*;

/**
 * Java2D graphic object displaying a north arrow.
 *
 * @author Johann sorel (Geomatys)
 * @module pending
 */
public class GraphicNorthArrowJ2D extends PositionedGraphic2D{

    private NorthArrowTemplate template;

    public GraphicNorthArrowJ2D(final J2DCanvas canvas, final NorthArrowTemplate template){
        super(canvas);
        this.template = template;
    }

    public void setTemplate(final NorthArrowTemplate template) {
        this.template = template;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void paint(final RenderingContext2D context, final int position, final int[] offset) {

        final Rectangle bounds = context.getCanvasDisplayBounds();

        context.switchToDisplayCRS();
        final Graphics2D g2d = context.getGraphics();

        final Dimension estimate = J2DNorthArrowUtilities.estimate(g2d, template, true);

        final int imgHeight = estimate.height;
        final int imgWidth  = estimate.width;
        int x = 0;
        int y = 0;

        switch(position){
            case NORTH :
                x = (bounds.width - imgWidth) / 2 + offset[0];
                y = offset[1];
                break;
            case NORTH_EAST :
                x = (bounds.width - imgWidth)  - offset[0];
                y = offset[1];
                break;
            case NORTH_WEST :
                x = offset[0];
                y = offset[1];
                break;
            case SOUTH :
                x = (bounds.width - imgWidth) / 2 + offset[0];
                y = (bounds.height - imgHeight) - offset[1];
                break;
            case SOUTH_EAST :
                x = (bounds.width - imgWidth) - offset[0];
                y = (bounds.height - imgHeight) - offset[1];
                break;
            case SOUTH_WEST :
                x = offset[0];
                y = (bounds.height - imgHeight) - offset[1];
                break;
            case CENTER :
                x = (bounds.width - imgWidth) / 2 + offset[0];
                y = (bounds.height - imgHeight) / 2 + offset[1];
                break;
            case EAST :
                x = (bounds.width - imgWidth) - offset[0];
                y = (bounds.height - imgHeight) / 2 + offset[1];
                break;
            case WEST :
                x = offset[0];
                y = (bounds.height - imgHeight) / 2 + offset[1];
                break;
        }

        try {
            J2DNorthArrowUtilities.paint((float)context.getCanvas().getController().getRotation(),g2d, x,y, template);
        } catch (PortrayalException ex) {
            context.getMonitor().exceptionOccured(ex, Level.WARNING);
        }

    }

}
