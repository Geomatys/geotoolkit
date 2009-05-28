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
package org.geotoolkit.report.graphic.legend;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRenderable;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.ext.legend.DefaultLegendTemplate;
import org.geotoolkit.display2d.ext.legend.J2DLegendUtilities;
import org.geotoolkit.display2d.ext.legend.LegendTemplate;
import org.geotoolkit.map.MapContext;

import org.opengis.display.canvas.Canvas;

/**
 * Jasper Report renderer used to render legend graphic.
 *
 * @author Johann Sorel (Geomatys)
 */
public class LegendRenderer implements JRRenderable{

    private LegendTemplate template = new DefaultLegendTemplate(3, 20, 25, new Font("Serial", Font.PLAIN, 8));

    private final String id = System.currentTimeMillis() + "-" + Math.random();
    private Canvas canvas = null;

    public LegendRenderer(){        

    }

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

        if(!(canvas instanceof ReferencedCanvas2D)) return;
        final ReferencedCanvas2D c2d = (ReferencedCanvas2D) canvas;

        if(!(c2d.getContainer() instanceof ContextContainer2D)) return;
        final ContextContainer2D renderer = (ContextContainer2D) c2d.getContainer();
        final MapContext context = renderer.getContext();

        try {
            J2DLegendUtilities.getInstance().paintLegend(context, g2d, area, template);
        } catch (PortrayalException ex) {
            ex.printStackTrace();
        }

    }


}
