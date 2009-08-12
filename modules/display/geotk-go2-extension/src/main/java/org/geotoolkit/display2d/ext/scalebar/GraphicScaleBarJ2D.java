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
package org.geotoolkit.display2d.ext.scalebar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.logging.Level;
import javax.measure.unit.SI;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.ext.PositionedGraphic2D;

import static javax.swing.SwingConstants.*;

/**
 * Java2D graphic object displaying a scalebar.
 *
 * @author Johann sorel (Geomatys)
 */
public class GraphicScaleBarJ2D extends PositionedGraphic2D{

    private static final int ROUND_SIZE = 12;
    private static final int INTER_MARGIN = 10;
    
    private ScaleBarTemplate template = new DefaultScaleBarTemplate(10,
                            false, 5, NumberFormat.getNumberInstance(),
                            Color.BLACK, Color.BLACK, Color.WHITE,
                            3,true,false, new Font("Serial", Font.PLAIN, 12),true,SI.METER);

    private Dimension size;

    public GraphicScaleBarJ2D(ReferencedCanvas2D canvas){
        this(canvas, null);
    }

    public GraphicScaleBarJ2D(ReferencedCanvas2D canvas, Dimension size){
        super(canvas);

        setPosition(SOUTH_WEST);

        if(size != null){
            this.size = size;
        }else{
            this.size = new Dimension(500, 40);
        }
    }

    public void setSize(Dimension size) {
        if(size == null) throw new NullPointerException("Size can't be null");
        this.size = size;
    }

    public void setTemplate(ScaleBarTemplate template) {
        if(template == null) throw new NullPointerException("Template can't be null");
        this.template = template;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void paint(RenderingContext2D context, int position, int[] offset) {

        final double[] center = context.getCanvas().getController().getCenter().getCoordinate();
        final Point2D centerPoint = new Point2D.Double(center[0], center[1]);

        final Rectangle bounds = context.getCanvasDisplayBounds();

        final int height = size.height;
        final int width  = size.width;
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


        final Rectangle area = new Rectangle(x,y, size.width, size.height);

        context.switchToDisplayCRS();

        final Graphics2D g2d = context.getGraphics();

        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(new Color(1f, 1f, 1f, 0.85f));
        g2d.fillRoundRect(area.x, area.y, area.width, area.height, ROUND_SIZE, ROUND_SIZE);

        g2d.setColor(Color.GRAY);
        g2d.drawRoundRect(area.x, area.y, area.width, area.height, ROUND_SIZE, ROUND_SIZE);

        area.x += INTER_MARGIN;
        area.y += INTER_MARGIN;
        area.width -= 2*INTER_MARGIN;
        area.height -= 2*INTER_MARGIN;

        try {
            J2DScaleBarUtilities.getInstance().paintScaleBar(context.getObjectiveCRS(), context.getDisplayCRS(), centerPoint, g2d, area, template);
        } catch (PortrayalException ex) {
            context.getMonitor().exceptionOccured(ex, Level.SEVERE);
        }

    }

}
