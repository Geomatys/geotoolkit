/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.report.graphic.scalebar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import javax.measure.unit.SI;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRenderable;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.ext.scalebar.DefaultScaleBarTemplate;
import org.geotoolkit.display2d.ext.scalebar.J2DScaleBarUtilities;
import org.geotoolkit.display2d.ext.scalebar.ScaleBarTemplate;

import org.opengis.display.canvas.Canvas;

/**
 * Jasper Report renderer used to render scale bar graphic.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ScaleBarRenderer implements JRRenderable{

    private final ScaleBarTemplate template = new DefaultScaleBarTemplate(6,
                            false, 5, NumberFormat.getNumberInstance(),
                            Color.BLACK, Color.BLACK, Color.WHITE,
                            3,true,false, new Font("Serial", Font.PLAIN, 8),true,SI.METER);

    private final String id = System.currentTimeMillis() + "-" + Math.random();
    private Canvas canvas = null;

    public void setCanvas(final Canvas canvas){
        this.canvas = canvas;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public byte getType() {
        return TYPE_SVG;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public byte getImageType() {
        return IMAGE_TYPE_PNG;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Dimension2D getDimension() throws JRException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public byte[] getImageData() throws JRException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void render(final Graphics2D g, final Rectangle2D rect) throws JRException {
        final Graphics2D g2d = (Graphics2D) g.create();
        final Rectangle area = rect.getBounds();

        if(canvas instanceof ReferencedCanvas2D){
            ReferencedCanvas2D c2d = (ReferencedCanvas2D) canvas;

            final double[] center = c2d.getController().getCenter().getCoordinate();
            final Point2D centerPoint = new Point2D.Double(center[0], center[1]);

            try {
                J2DScaleBarUtilities.getInstance().paintScaleBar(c2d.getObjectiveCRS(), c2d.getDisplayCRS(), centerPoint, g2d, area, template);
            } catch (PortrayalException ex) {
                ex.printStackTrace();
            }

        }
    }

}
