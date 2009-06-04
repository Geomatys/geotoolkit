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
import java.util.List;
import java.util.logging.Level;
import javax.measure.unit.SI;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;

import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;
import org.opengis.display.primitive.Graphic;

/**
 * Java2D graphic object displaying a scalebar.
 *
 * @author Johann sorel (Geomatys)
 */
public class GraphicScaleBarJ2D extends AbstractGraphicJ2D{

    private final ScaleBarTemplate template = new DefaultScaleBarTemplate(10,
                            false, 5, NumberFormat.getNumberInstance(),
                            Color.BLACK, Color.BLACK, Color.WHITE,
                            3,true,false, new Font("Serial", Font.PLAIN, 12),true,SI.METER);

    private final Dimension dim = new Dimension(500, 40);
    private final int margin = 10;
    private final int roundSize = 12;
    private final int interMargin = 10;

    public GraphicScaleBarJ2D(ReferencedCanvas2D canvas){
        super(canvas,canvas.getObjectiveCRS());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void paint(final RenderingContext2D context) {

        final double[] center = context.getCanvas().getController().getCenter().getCoordinate();
        final Point2D centerPoint = new Point2D.Double(center[0], center[1]);

        final Rectangle all = context.getCanvasDisplayBounds();
        final Rectangle area = new Rectangle(margin, all.y+all.height-margin-dim.height, dim.width, dim.height);

        context.switchToDisplayCRS();

        final Graphics2D g2d = context.getGraphics();

        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(new Color(1f, 1f, 1f, 0.85f));
        g2d.fillRoundRect(area.x, area.y, area.width, area.height, roundSize, roundSize);

        g2d.setColor(Color.GRAY);
        g2d.drawRoundRect(area.x, area.y, area.width, area.height, roundSize, roundSize);

        area.x += interMargin;
        area.y += interMargin;
        area.width -= 2*interMargin;
        area.height -= 2*interMargin;

        try {
            J2DScaleBarUtilities.getInstance().paintScaleBar(context.getObjectiveCRS(), context.getDisplayCRS(), centerPoint, g2d, area, template);
        } catch (PortrayalException ex) {
            context.getMonitor().exceptionOccured(ex, Level.SEVERE);
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        //not selectable graphic
        return graphics;
    }

}
