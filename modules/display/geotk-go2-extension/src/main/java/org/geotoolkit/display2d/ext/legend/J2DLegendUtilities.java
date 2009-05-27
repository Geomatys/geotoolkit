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
package org.geotoolkit.display2d.ext.legend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;

import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;


/**
 * Utility class to render legend using a provided template.
 *
 * @author Johann Sorel (Geomatys)
 */
public class J2DLegendUtilities {

    private static J2DLegendUtilities INSTANCE = null;

    private J2DLegendUtilities(){
    }

    /**
     * Paint a legend using Java2D.
     *
     * @param context : map context, from wich to extract the style informations
     * @param g2d : Graphics2D used to render
     * @param bounds : Rectangle where the legend must be painted
     * @param template  : north arrow template
     * @throws org.geotools.display.exception.PortrayalException
     */
    public void paintLegend(final MapContext context,
                              final Graphics2D g2d,
                              final Rectangle bounds,
                              final LegendTemplate template) throws PortrayalException{

        paintLegend(extractRules(context), g2d, bounds, template);
    }

    /**
     * Paint a legend using Java2D.
     *
     * @param context : map context, from wich to extract the style informations
     * @param g2d : Graphics2D used to render
     * @param bounds : Rectangle where the legend must be painted
     * @param template  : north arrow template
     * @throws org.geotools.display.exception.PortrayalException
     */
    public void paintLegend(final List<Rule> rules,
                              Graphics2D g2d,
                              final Rectangle bounds,
                              final LegendTemplate template) throws PortrayalException{

        g2d = (Graphics2D) g2d.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setClip(bounds);
        g2d.setStroke(new BasicStroke(1));

        final float gapSize = template.getGapSize();
        final float glyphHeight = template.getGlyphHeight();
        final float glyphWidth = template.getGlyphWidth();
        final Rectangle2D rectangle = new Rectangle2D.Float();
        float X = bounds.x;
        float Y = bounds.y;

        for(final Rule rule : rules){
            final String title = rule.getDescription().getTitle().toString();
            rectangle.setRect(X, Y, glyphWidth, glyphHeight);
            DefaultGlyphService.render(rule, rectangle, g2d);
            g2d.setFont(template.getFont());
            g2d.setColor(Color.BLACK);
            g2d.drawString(title, X+glyphWidth+gapSize, Y+glyphHeight);

            Y += glyphHeight + gapSize;
        }
        
    }

    private List<Rule> extractRules(final MapContext context){
        final List<Rule> rules = new ArrayList<Rule>();

        if(context == null) return rules;

        for(final MapLayer layer : context.layers()){
            for(final FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                rules.addAll(fts.rules());
            }
        }

        return rules;
    }


    /**
     * Get the default instance, singleton.
     */
    public static final J2DLegendUtilities getInstance(){
        if(INSTANCE == null){
            INSTANCE = new J2DLegendUtilities();
        }
        return INSTANCE;
    }

}
