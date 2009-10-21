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

package org.geotoolkit.display2d.ext.legend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.logging.Level;

import java.util.logging.Logger;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.ext.PositionedGraphic2D;
import org.geotoolkit.map.MapContext;

import static javax.swing.SwingConstants.*;
import org.opengis.referencing.operation.TransformException;

/**
 * Graphic decoration to paint a legend if the canvas container hold a 
 * MapContext object.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GraphicLegendJ2D extends PositionedGraphic2D{

    private static final int ROUND_SIZE = 12;
    
    private final LegendTemplate template;

    public GraphicLegendJ2D(final ReferencedCanvas2D canvas, final LegendTemplate template){
        super(canvas);
        this.template = template;
    }

    @Override
    protected void paint(final RenderingContext2D context, final int position, final int[] offset) {

        final AbstractContainer2D container = getCanvas().getContainer();
        if(!(container instanceof ContextContainer2D)) return;

        final ContextContainer2D cc = (ContextContainer2D) container;
        final MapContext mapContext = cc.getContext();

        final Graphics2D g = context.getGraphics();
        context.switchToDisplayCRS();

        final Rectangle bounds = context.getCanvasDisplayBounds();
        Dimension maxSize = J2DLegendUtilities.estimate(g, mapContext, template, true);
        final int imgHeight = maxSize.height;
        final int imgWidth  = maxSize.width;
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
            //paint all labels, so that we avoid conflicts
            context.getLabelRenderer(true).portrayLabels();
        } catch (TransformException ex) {
            context.getMonitor().exceptionOccured(ex, Level.WARNING);
        }

        final Rectangle area = new Rectangle(x, y, imgWidth, imgHeight);

        try {
            J2DLegendUtilities.paintLegend(mapContext, g, area, template);
        } catch (PortrayalException ex) {
            context.getMonitor().exceptionOccured(ex, Level.WARNING);
        }
    }

}
