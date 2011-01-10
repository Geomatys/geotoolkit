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
package org.geotoolkit.report.graphic.northarrow;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRenderable;

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.ext.northarrow.DefaultNorthArrowTemplate;
import org.geotoolkit.display2d.ext.northarrow.J2DNorthArrowUtilities;
import org.geotoolkit.display2d.ext.northarrow.NorthArrowTemplate;

/**
 * Jasper Report renderer used to render north arrow graphic.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class NorthArrowRenderer implements JRRenderable{

    private final String id = System.currentTimeMillis() + "-" + Math.random();
    private double rotation = 0;

    public NorthArrowRenderer(){
    }

    public void setRotation(final double rotation){
        this.rotation = rotation;
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

        final NorthArrowTemplate template = new DefaultNorthArrowTemplate(null,
                NorthArrowRenderer.class.getResource("/org/geotoolkit/report/boussole.svg"),
                        new Dimension((int)rect.getWidth(),(int)rect.getHeight()));

        try {
            J2DNorthArrowUtilities.paint((float)rotation, g2d, area.x, area.y, true,template);
        } catch (PortrayalException ex) {
            throw new JRException(ex);
        }
    }


}
