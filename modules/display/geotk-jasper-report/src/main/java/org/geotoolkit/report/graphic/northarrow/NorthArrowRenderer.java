/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.report.graphic.northarrow;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRenderable;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.exception.PortrayalException;

import org.geotoolkit.display2d.ext.northarrow.DefaultNorthArrowTemplate;
import org.geotoolkit.display2d.ext.northarrow.J2DNorthArrowUtilities;
import org.geotoolkit.display2d.ext.northarrow.NorthArrowTemplate;
import org.opengis.display.canvas.Canvas;

/**
 * Jasper Report renderer used to render north arrow graphic.
 *
 * @author Johann Sorel (Geomatys)
 */
public class NorthArrowRenderer implements JRRenderable{

    private NorthArrowTemplate template = null;

    private final String id = System.currentTimeMillis() + "-" + Math.random();
    private Canvas canvas = null;

    public NorthArrowRenderer(){

        template = new DefaultNorthArrowTemplate(NorthArrowRenderer.class.getResource("/org/geotoolkit/report/boussole.svg"));
        

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

        if(canvas instanceof ReferencedCanvas2D){
           ReferencedCanvas2D c2d = (ReferencedCanvas2D) canvas;

            try {
                J2DNorthArrowUtilities.getInstance().paintNorthArrow((float)c2d.getController().getRotation(), g2d, area, template);
            } catch (PortrayalException ex) {
                ex.printStackTrace();
            }

        }
    }


}
