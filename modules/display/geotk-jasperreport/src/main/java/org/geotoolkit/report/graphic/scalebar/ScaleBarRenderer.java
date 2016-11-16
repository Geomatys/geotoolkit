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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.logging.Level;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRenderable;

import org.geotoolkit.display.canvas.AbstractCanvas2D;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.ext.scalebar.DefaultScaleBarTemplate;
import org.geotoolkit.display2d.ext.scalebar.J2DScaleBarUtilities;
import org.geotoolkit.display2d.ext.scalebar.ScaleBarTemplate;
import org.geotoolkit.report.graphic.map.CanvasRenderer;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.measure.Units;

/**
 * Jasper Report renderer used to render scale bar graphic.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ScaleBarRenderer implements JRRenderable{

    private final ScaleBarTemplate template = new DefaultScaleBarTemplate(
                            null,new Dimension(300,30),6,
                            false, 5, NumberFormat.getNumberInstance(),
                            Color.BLACK, Color.BLACK, Color.WHITE,
                            3,true,false, new Font("Serial", Font.PLAIN, 8),true,Units.METRE);

    private final String id = System.currentTimeMillis() + "-" + Math.random();
    private CanvasRenderer canvas = null;

    public void setCanvas(final CanvasRenderer canvas){
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

        if(canvas instanceof AbstractCanvas2D){
            AbstractCanvas2D c2d = (AbstractCanvas2D) canvas;

            try {
                final double[] center = c2d.getObjectiveCenter().getCoordinate();
                final Point2D centerPoint = new Point2D.Double(center[0], center[1]);
                J2DScaleBarUtilities.paint(c2d.getObjectiveCRS(), c2d.getDisplayCRS(), centerPoint, g2d, area.x,area.y, template);
            } catch ( PortrayalException | NoninvertibleTransformException | TransformException ex) {
                Logging.getLogger("org.geotoolkit.report.graphic.scalebar").log(Level.WARNING, null, ex);
            }

        }
    }

}
