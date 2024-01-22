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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.renderers.Graphics2DRenderable;
import net.sf.jasperreports.renderers.Renderable;
import org.apache.sis.map.MapLayers;
import org.geotoolkit.display2d.ext.BackgroundTemplate;
import org.geotoolkit.display2d.ext.DefaultBackgroundTemplate;
import org.geotoolkit.display2d.ext.legend.DefaultLegendTemplate;
import org.geotoolkit.display2d.ext.legend.J2DLegendUtilities;
import org.geotoolkit.display2d.ext.legend.LegendTemplate;

/**
 * Jasper Report renderer used to render legend graphic.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class LegendRenderer implements Graphics2DRenderable, Renderable {

    private BackgroundTemplate back = new DefaultBackgroundTemplate(
            new BasicStroke(0),
            new Color(0f, 0f, 0f, 0f),
            Color.WHITE,
            new Insets(5, 5, 5, 5),
            0);

    private LegendTemplate template = new DefaultLegendTemplate(
            back,
            2,
            new Dimension(25, 20),
            new Font("Serial", Font.ITALIC, 11),
            true,
            new Font("Serial", Font.BOLD, 11));

    private final String id = System.currentTimeMillis() + "-" + Math.random();
    private MapLayers context;

    public LegendRenderer(){
    }

    @Override
    public String getId() {
        return id;
    }

    public void setContext(final MapLayers context){
        this.context = context;
    }

    @Override
    public void render(JasperReportsContext jrc, Graphics2D g2d, Rectangle2D rect) throws JRException {
        if(context == null) return;
        final Rectangle area = rect.getBounds();
        J2DLegendUtilities.paintLegend(context, g2d, area, template);
    }
}
