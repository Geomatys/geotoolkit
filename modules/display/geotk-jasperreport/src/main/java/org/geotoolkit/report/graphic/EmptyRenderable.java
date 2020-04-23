/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.report.graphic;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.renderers.Graphics2DRenderable;
import net.sf.jasperreports.renderers.Renderable;

/**
 * A JRRenderable which paint nothing.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class EmptyRenderable implements Graphics2DRenderable, Renderable {

    public static final EmptyRenderable INSTANCE = new EmptyRenderable();

    private EmptyRenderable(){}

    @Override
    public String getId() {
        return "EmptyRenderable";
    }

    @Override
    public void render(JasperReportsContext jrc, Graphics2D gd, Rectangle2D rd) throws JRException {
        //do nothing
    }

}
