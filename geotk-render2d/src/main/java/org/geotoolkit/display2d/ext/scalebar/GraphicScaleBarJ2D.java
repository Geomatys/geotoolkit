/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.display2d.ext.scalebar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.logging.Level;
import static javax.swing.SwingConstants.*;
import org.apache.sis.measure.Units;
import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.ext.PositionedGraphic2D;
import org.opengis.geometry.Envelope;

/**
 * Java2D graphic object displaying a scalebar.
 *
 * @author Johann sorel (Geomatys)
 * @module
 */
public class GraphicScaleBarJ2D extends PositionedGraphic2D{

    private static final int ROUND_SIZE = 12;
    private static final int INTER_MARGIN = 10;

    private ScaleBarTemplate template = new DefaultScaleBarTemplate(null,
                            new Dimension(500, 40),10,
                            false, 5, NumberFormat.getNumberInstance(),
                            Color.BLACK, Color.BLACK, Color.WHITE,
                            3,true,false, new Font("Serial", Font.PLAIN, 12),true,Units.METRE);


    public GraphicScaleBarJ2D(final J2DCanvas canvas){
        super(canvas);
        setPosition(SOUTH_WEST);
    }

    public void setTemplate(final ScaleBarTemplate template) {
        ensureNonNull("template", template);
        this.template = template;
    }

    @Override
    public Object getUserObject() {
        return template;
    }

    @Override
    public Envelope getEnvelope() {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void paint(final RenderingContext2D context, final int position, final int[] offset) {

        final Graphics2D g2d = context.getGraphics();

        final double[] center = context.getGridGeometry2D().getExtent().getPointOfInterest();
        context.getDisplayToObjective().transform(center, 0, center, 0, 1);
        final Point2D centerPoint = new Point2D.Double(center[0], center[1]);

        final Rectangle bounds = context.getCanvasDisplayBounds();

        final Dimension estimate = J2DScaleBarUtilities.estimate(g2d,template,true);

        final int height = estimate.height;
        final int width  = estimate.width;
        int x = 0;
        int y = 0;

        switch(position){
            case NORTH :
                x = (bounds.width - width) / 2 + offset[0];
                y = offset[1];
                break;
            case NORTH_EAST :
                x = (bounds.width - width)  - offset[0];
                y = offset[1];
                break;
            case NORTH_WEST :
                x = offset[0];
                y = offset[1];
                break;
            case SOUTH :
                x = (bounds.width - width) / 2 + offset[0];
                y = (bounds.height - height) - offset[1];
                break;
            case SOUTH_EAST :
                x = (bounds.width - width) - offset[0];
                y = (bounds.height - height) - offset[1];
                break;
            case SOUTH_WEST :
                x = offset[0];
                y = (bounds.height - height) - offset[1];
                break;
            case CENTER :
                x = (bounds.width - width) / 2 + offset[0];
                y = (bounds.height - height) / 2 + offset[1];
                break;
            case EAST :
                x = (bounds.width - width) - offset[0];
                y = (bounds.height - height) / 2 + offset[1];
                break;
            case WEST :
                x = offset[0];
                y = (bounds.height - height) / 2 + offset[1];
                break;
        }

        try {
            J2DScaleBarUtilities.paint(context.getObjectiveCRS(), context.getDisplayCRS(), centerPoint, g2d, x, y, template);
        } catch (PortrayalException ex) {
            context.getMonitor().exceptionOccured(ex, Level.WARNING);
        }

    }

}
