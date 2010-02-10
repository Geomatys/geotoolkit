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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.ext.BackgroundTemplate;
import org.geotoolkit.display2d.ext.BackgroundUtilities;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;

import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;


/**
 * Utility class to render legend using a provided template.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class J2DLegendUtilities {

    private static final int GLYPH_SPACE = 5;

    private J2DLegendUtilities(){
    }

    /**
     * Paint a legend using Java2D.
     *
     * @param context : map context, from wich to extract the style informations
     * @param g2d : Graphics2D used to render
     * @param bounds : Rectangle where the legend must be painted
     * @param template  : north arrow template
     * @throws org.geotoolkit.display.exception.PortrayalException
     */
    public static void paintLegend(final MapContext context,
                              Graphics2D g2d,
                              final Rectangle bounds,
                              final LegendTemplate template) throws PortrayalException{

        g2d = (Graphics2D) g2d.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setClip(bounds);
        g2d.setStroke(new BasicStroke(1));

        float X = bounds.x;
        float Y = bounds.y;

        final Dimension estimation = estimate(g2d, context, template, false);
        
        final BackgroundTemplate background = template.getBackground();
        if(background != null){
            final Rectangle area = new Rectangle(estimation);
            area.x = bounds.x;
            area.y = bounds.y;

            Insets insets = background.getBackgroundInsets();
            area.width += insets.left + insets.right;
            area.height += insets.top + insets.bottom;
            X += insets.left;
            Y += insets.top;

            BackgroundUtilities.paint(g2d, area, background);
        }


        final FontMetrics layerFontMetric = g2d.getFontMetrics(template.getLayerFont());
        final int layerFontHeight = layerFontMetric.getHeight();
        final FontMetrics ruleFontMetric = g2d.getFontMetrics(template.getRuleFont());
        final int ruleFontHeight = ruleFontMetric.getHeight();
        final float gapSize = template.getGapSize();
        final float glyphHeight = template.getGlyphSize().height;
        final float glyphWidth = template.getGlyphSize().width;
        final Rectangle2D rectangle = new Rectangle2D.Float();
        float moveY = 0;


        final float stepRuleTitle;
        if(glyphHeight > ruleFontHeight){
            stepRuleTitle = ruleFontMetric.getLeading() + ruleFontMetric.getAscent()
                    + (glyphHeight-ruleFontHeight)/2 ;
        }else{
            stepRuleTitle = ruleFontMetric.getLeading() + ruleFontMetric.getAscent();
        }


        g2d.translate(X, Y);

        for(final MapLayer layer : context.layers()){
            final MutableStyle style = layer.getStyle();

            if(style == null) continue;

            if(template.isLayerVisible()){
                final String title = layer.getDescription().getTitle().toString();

                moveY += layerFontMetric.getLeading() + layerFontMetric.getAscent();
                g2d.setFont(template.getLayerFont());
                g2d.setColor(Color.BLACK);
                g2d.drawString(title,0,moveY);
                moveY += layerFontMetric.getDescent();

                moveY += gapSize;
            }

            for(final MutableFeatureTypeStyle fts :style.featureTypeStyles()){
                for(final MutableRule rule : fts.rules()){

                    final String title = rule.getDescription().getTitle().toString();
                    rectangle.setRect(0, moveY, glyphWidth, glyphHeight);

                    DefaultGlyphService.render(rule, rectangle, g2d,layer);
                    g2d.setFont(template.getRuleFont());
                    g2d.setColor(Color.BLACK);

//                    final float baseline;
//                    if(glyphHeight > ruleFontHeight){
//                        baseline = moveY + ruleFontMetric.getLeading() + ruleFontMetric.getAscent()
//                                + (glyphHeight-ruleFontHeight)/2 ;
//                    }else{
//                        baseline = moveY + ruleFontMetric.getLeading() + ruleFontMetric.getAscent();
//                    }

                    g2d.drawString(title, glyphWidth+GLYPH_SPACE, moveY + stepRuleTitle);


                    moveY += (glyphHeight > ruleFontHeight) ? glyphHeight : ruleFontHeight;
                    moveY += gapSize;
                }
            }
        }

        g2d.translate(0, -moveY);
        g2d.translate(-X, -Y);
    }

    /**
     * Paint a legend using Java2D.
     *
     * @param context : map context, from wich to extract the style informations
     * @param g2d : Graphics2D used to render
     * @param bounds : Rectangle where the legend must be painted
     * @param template  : north arrow template
     * @throws org.geotoolkit.display.exception.PortrayalException
     */
    public static void paintLegend(final List<Rule> rules,
                              Graphics2D g2d,
                              final Rectangle bounds,
                              final LegendTemplate template) throws PortrayalException{

        g2d = (Graphics2D) g2d.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setClip(bounds);
        g2d.setStroke(new BasicStroke(1));

        final float gapSize = template.getGapSize();
        final float glyphHeight = template.getGlyphSize().height;
        final float glyphWidth = template.getGlyphSize().width;
        final Rectangle2D rectangle = new Rectangle2D.Float();
        float X = bounds.x;
        float Y = bounds.y;

        for(final Rule rule : rules){
            final String title = rule.getDescription().getTitle().toString();
            rectangle.setRect(X, Y, glyphWidth, glyphHeight);
            DefaultGlyphService.render(rule, rectangle, g2d, null);
            g2d.setFont(template.getRuleFont());
            g2d.setColor(Color.BLACK);
            g2d.drawString(title, X+glyphWidth+gapSize, Y+glyphHeight);

            Y += glyphHeight + gapSize;
        }
        
    }


    public static Dimension estimate(Graphics2D g, MapContext context, LegendTemplate template, boolean considerBackground){
        final Dimension dim = new Dimension(0, 0);
        if(context == null) return dim;

        final FontMetrics layerFontMetric = g.getFontMetrics(template.getLayerFont());
        final int layerFontHeight = layerFontMetric.getHeight();
        final FontMetrics ruleFontMetric = g.getFontMetrics(template.getRuleFont());
        final int ruleFontHeight = ruleFontMetric.getHeight();
        final int glyphHeight = template.getGlyphSize().height;
        final int glyphWidth = template.getGlyphSize().width;

        for(final MapLayer layer : context.layers()){
            final MutableStyle style = layer.getStyle();

            if(style == null) continue;

            if(template.isLayerVisible()){
                final String title = layer.getDescription().getTitle().toString();
                final int width = layerFontMetric.stringWidth(title);

                dim.height += layerFontHeight;
                if(dim.width < width) dim.width = width;
                dim.height += template.getGapSize();
            }

            for(final MutableFeatureTypeStyle fts :style.featureTypeStyles()){
                for(final MutableRule rule : fts.rules()){
                    final String title = rule.getDescription().getTitle().toString();
                    final int width = glyphWidth + GLYPH_SPACE + ruleFontMetric.stringWidth(title);
                    dim.height += (glyphHeight > ruleFontHeight) ? glyphHeight : ruleFontHeight;
                    if(dim.width < width) dim.width = width;
                    dim.height += template.getGapSize();
                }
            }

        }

        if(considerBackground && template.getBackground() != null){
            final Insets insets = template.getBackground().getBackgroundInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.bottom + insets.top;
        }

        return dim;
    }

    private static List<Rule> extractRules(final MapContext context){
        final List<Rule> rules = new ArrayList<Rule>();

        if(context == null) return rules;

        for(final MapLayer layer : context.layers()){
            for(final FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                rules.addAll(fts.rules());
            }
        }

        return rules;
    }

}
