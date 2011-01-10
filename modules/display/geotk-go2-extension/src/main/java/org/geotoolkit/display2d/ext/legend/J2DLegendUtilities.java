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
import org.opengis.style.Description;
import org.opengis.style.Rule;
import org.opengis.util.InternationalString;


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

        if(template == null){
            //no template generate a glyph
            for(MapLayer layer : context.layers()){
                DefaultGlyphService.render(layer.getStyle(), bounds, g2d, layer);
            }
            return;
        }


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
        final Dimension glyphSize = template.getGlyphSize();
        final Rectangle2D rectangle = new Rectangle2D.Float();
        float moveY = 0;

        g2d.translate(X, Y);

        final List<MapLayer> layers = context.layers();
        for(int l=0,n=layers.size();l<n;l++){
            final MapLayer layer = layers.get(l);
            final MutableStyle style = layer.getStyle();

            if(style == null) continue;

            if(template.isLayerVisible()){
                if(l!=0){
                    moveY += gapSize;
                }
                String title = "";
                final Description description = layer.getDescription();
                if (description != null) {
                    final InternationalString titleTmp = description.getTitle();
                    if (titleTmp != null) {
                        title = titleTmp.toString();
                    }
                }

                moveY += layerFontMetric.getLeading() + layerFontMetric.getAscent();
                g2d.setFont(template.getLayerFont());
                g2d.setColor(Color.BLACK);
                g2d.drawString(title,0,moveY);
                moveY += layerFontMetric.getDescent();

                moveY += gapSize;
            }

            int numElement = 0;
            for(final MutableFeatureTypeStyle fts :style.featureTypeStyles()){
                for(final MutableRule rule : fts.rules()){
                    if(numElement!=0){
                        moveY += gapSize;
                    }

                    //calculate the rule text displacement with the glyph size
                    final float stepRuleTitle;
                    final float glyphHeight;
                    final float glyphWidth;
                    if(glyphSize == null){
                        //find the best size
                        final Dimension preferred = DefaultGlyphService.glyphPreferredSize(rule, glyphSize, layer);
                        glyphHeight = preferred.height;
                        glyphWidth = preferred.width;
                    }else{
                        //use the defined size
                        glyphHeight = glyphSize.height;
                        glyphWidth = glyphSize.width;
                    }

                    if(glyphHeight > ruleFontHeight){
                        stepRuleTitle = ruleFontMetric.getLeading() + ruleFontMetric.getAscent()
                                + (glyphHeight-ruleFontHeight)/2 ;
                    }else{
                        stepRuleTitle = ruleFontMetric.getLeading() + ruleFontMetric.getAscent();
                    }


                    String title = "";
                    final Description description = rule.getDescription();
                    if (description != null) {
                        final InternationalString titleTmp = description.getTitle();
                        if (titleTmp != null) {
                            title = titleTmp.toString();
                        }
                    }
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
                    numElement++;
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
            String title = "";
            final Description description = rule.getDescription();
            if (description != null) {
                final InternationalString titleTmp = description.getTitle();
                if (titleTmp != null) {
                    title = titleTmp.toString();
                }
            }
            rectangle.setRect(X, Y, glyphWidth, glyphHeight);
            DefaultGlyphService.render(rule, rectangle, g2d, null);
            g2d.setFont(template.getRuleFont());
            g2d.setColor(Color.BLACK);
            g2d.drawString(title, X+glyphWidth+gapSize, Y+glyphHeight);

            Y += glyphHeight + gapSize;
        }

    }


    public static Dimension estimate(final Graphics2D g, final MapContext context, final LegendTemplate template, final boolean considerBackground){
        final Dimension dim = new Dimension(0, 0);
        if(context == null) return dim;

        if(template == null){
            //fallback on glyph size
            for(MapLayer layer : context.layers()){
                final Dimension preferred = DefaultGlyphService.glyphPreferredSize(layer.getStyle(), dim, layer);
                if(preferred != null){
                    if(preferred.width > dim.width) dim.width = preferred.width;
                    if(preferred.height > dim.height) dim.height = preferred.height;
                }
            }
            checkMinimumSize(dim);
            return dim;
        }

        final FontMetrics layerFontMetric = g.getFontMetrics(template.getLayerFont());
        final int layerFontHeight = layerFontMetric.getHeight();
        final FontMetrics ruleFontMetric = g.getFontMetrics(template.getRuleFont());
        final int ruleFontHeight = ruleFontMetric.getHeight();
        final Dimension glyphSize = template.getGlyphSize();

        final List<MapLayer> layers = context.layers();
        for(int l=0,n=layers.size();l<n;l++){
            final MapLayer layer = layers.get(l);
            final MutableStyle style = layer.getStyle();

            if(style == null) continue;

            if(template.isLayerVisible()){
                if(l!=0){
                    dim.height += template.getGapSize();
                }
                final String title = layer.getDescription().getTitle().toString();
                final int width = layerFontMetric.stringWidth(title);

                dim.height += layerFontHeight;
                if(dim.width < width) dim.width = width;
                dim.height += template.getGapSize();
            }

            int numElement = 0;
            for(final MutableFeatureTypeStyle fts :style.featureTypeStyles()){
                for(final MutableRule rule : fts.rules()){
                    if(numElement!=0){
                        dim.height += template.getGapSize();
                    }

                    //calculate the text lenght
                    int textLenght = 0;
                    final Description description = rule.getDescription();
                    if (description != null) {
                        final InternationalString titleTmp = description.getTitle();
                        if (titleTmp != null) {
                            final String title = titleTmp.toString();
                            textLenght = ruleFontMetric.stringWidth(title);
                        }
                    }

                    //calculate the glyph size
                    final int glyphHeight;
                    final int glyphWidth;
                    if(glyphSize == null){
                        //find the best size
                        final Dimension preferred = DefaultGlyphService.glyphPreferredSize(rule, glyphSize, layer);
                        glyphHeight = preferred.height;
                        glyphWidth = preferred.width;
                    }else{
                        //use the defined size
                        glyphHeight = glyphSize.height;
                        glyphWidth = glyphSize.width;
                    }

                    final int totalWidth = glyphWidth + ((textLenght==0)? 0 : (GLYPH_SPACE+textLenght));
                    final int totalHeight = (glyphHeight > ruleFontHeight) ? glyphHeight : ruleFontHeight;

                    dim.height += totalHeight;
                    if (dim.width < totalWidth) {
                        dim.width = totalWidth;
                    }
                    numElement++;
                }
            }

        }

        if(considerBackground && template.getBackground() != null){
            final Insets insets = template.getBackground().getBackgroundInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.bottom + insets.top;
        }

        
        checkMinimumSize(dim);
        return dim;
    }

    private static void checkMinimumSize(final Dimension dim){
        if(dim.width==0){
            dim.width = 1;
        }
        if(dim.height==0){
            dim.height = 1;
        }
    }

}
