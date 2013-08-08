/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.ext.BackgroundTemplate;
import org.geotoolkit.display2d.ext.BackgroundUtilities;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.apache.sis.util.logging.Logging;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
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
    private static final Logger LOGGER = Logging.getLogger(J2DLegendUtilities.class);

    private static final int GLYPH_SPACE = 5;

    private J2DLegendUtilities() {
    }

    /**
     * Paint a legend using Java2D.
     *
     * @param item : map context, from wich to extract style information
     * @param g2d : Graphics2D used for rendering
     * @param bounds : Rectangle where the legend must be painted
     * @param template : Legend rendering hints.
     * @throws org.geotoolkit.display.exception.PortrayalException
     */
    public static void paintLegend(MapItem item,
            Graphics2D g2d,
            final Rectangle bounds,
            final LegendTemplate template) throws PortrayalException {

        if (item instanceof MapLayer) {
            final MapContext context = MapBuilder.createContext();
            context.items().add(item);
            item = context;
        }

        g2d = (Graphics2D) g2d.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setClip(bounds);
        g2d.setStroke(new BasicStroke(1));

        if (template == null) {
            //no template generate a glyph
            paintNoTemplate(item, g2d, bounds);
        
        } else {
            float X = bounds.x, Y = bounds.y;

            // Will store the get legend graphic results
            final Map<Name, BufferedImage> legendResults = new HashMap<Name, BufferedImage>();

            final Dimension estimation = estimate(g2d, item, template, legendResults, false);
            final BackgroundTemplate background = template.getBackground();
            if (background != null) {
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

            g2d.translate(X, Y);            
            paintWithTemplate(item, g2d, bounds, template, legendResults);
            g2d.translate(-X, -Y);
        }
    }

    /**
     * Paint the legend of the given MapItem, without any {@link LegendTemplate}.
     * To do so, a simple glyph is generated for each layer of input item.
     * @param item The {@link MapItem} to paint legend for.
     * @param g2d The {@link Graphics2D} on which we'll paint legend.
     * @param bounds The graphic area to paint into.
     */
    private static void paintNoTemplate(final MapItem item, final Graphics2D g2d, final Rectangle bounds) {
        for (MapItem layer : item.items()) {
            if (layer instanceof MapLayer) {
                DefaultGlyphService.render(((MapLayer) layer).getStyle(), bounds, g2d, (MapLayer) layer);
            } else {
                paintNoTemplate(layer, g2d, bounds);
            }
        }
    }
    
    private static void paintWithTemplate(
            final MapItem item,
            final Graphics2D g2d,
            final Rectangle bounds,
            final LegendTemplate template,
            final Map<Name, BufferedImage> legendResults) {

        final FontMetrics layerFontMetric = g2d.getFontMetrics(template.getLayerFont());
        final int layerFontHeight = layerFontMetric.getHeight();
        final FontMetrics ruleFontMetric = g2d.getFontMetrics(template.getRuleFont());
        final int ruleFontHeight = ruleFontMetric.getHeight();
        final float gapSize = template.getGapSize();
        final Dimension glyphSize = template.getGlyphSize();
        final Rectangle2D rectangle = new Rectangle2D.Float();
        float moveY = 0;
            
        final List<MapItem> layers = item.items();
        for (int l = 0, n = layers.size(); l < n; l++) {
            final MapItem currentItem = layers.get(l);
            
            //check if the given layer is visible, and if we should display invisible layers.
            if (template.displayOnlyVisibleLayers() && !currentItem.isVisible()) {
                continue;
            }
            
            // Check for current item title.
            if (template.isLayerVisible()) {
                if (l != 0) {
                    moveY += gapSize;
                }
                String title = "";
                final Description description = currentItem.getDescription();
                if (description != null) {
                    final InternationalString titleTmp = description.getTitle();
                    if (titleTmp != null) {
                        title = titleTmp.toString().replace("{}", "");
                    }
                }
                if (title.isEmpty()) {
                    title = currentItem.getName();
                }

                if (title != null && !title.isEmpty()) {
                    moveY += layerFontMetric.getLeading() + layerFontMetric.getAscent();
                    g2d.setFont(template.getLayerFont());
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(title, 0, moveY);
                    moveY += layerFontMetric.getDescent();
                    moveY += gapSize;
                }
            }
            
            // If we're not on a leaf, try to display this node children.
            if (!(currentItem instanceof MapLayer)) {
                // Using double allows origin relative translation.
                final double nodeInset = (double) template.getBackground().getBackgroundInsets().left;
                g2d.translate(nodeInset, moveY);
                paintWithTemplate(currentItem, g2d, bounds, template, legendResults);
                g2d.translate(-nodeInset, 0.0);
                continue;
            }

            final MapLayer layer = (MapLayer) layers.get(l);

            // If we are browsing a coverage map layer, a default generic style has been defined,
            // we can use the result of a GetLegendGraphic request instead. It should presents the
            // default style defined on the WMS service for this layer
            wmscase:
            if (layer instanceof DefaultCoverageMapLayer) {
                final DefaultCoverageMapLayer covLayer = (DefaultCoverageMapLayer)layer;
                // Get the image from the ones previously stored, to not resend a get legend graphic request.
                final BufferedImage image = legendResults.get(covLayer.getCoverageName());
                if (image == null) {
                    break wmscase;
                }
                if (l != 0) {
                    moveY += gapSize;
                }
                g2d.drawImage(image, null, 0, Math.round(moveY));
                moveY += image.getHeight();
                continue;
            }

            final MutableStyle style = layer.getStyle();

            if (style == null) {
                continue;
            }

            int numElement = 0;
            for (final MutableFeatureTypeStyle fts : style.featureTypeStyles()) {
                for (final MutableRule rule : fts.rules()) {
                    if (numElement != 0) {
                        moveY += gapSize;
                    }

                    //calculate the rule text displacement with the glyph size
                    final float stepRuleTitle;
                    final float glyphHeight;
                    final float glyphWidth;
                    if (glyphSize == null) {
                        //find the best size
                        final Dimension preferred = DefaultGlyphService.glyphPreferredSize(rule, glyphSize, layer);
                        glyphHeight = preferred.height;
                        glyphWidth = preferred.width;
                    } else {
                        //use the defined size
                        glyphHeight = glyphSize.height;
                        glyphWidth = glyphSize.width;
                    }

                    if (glyphHeight > ruleFontHeight) {
                        stepRuleTitle = ruleFontMetric.getLeading() + ruleFontMetric.getAscent()
                                + (glyphHeight - ruleFontHeight) / 2;
                    } else {
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

                    DefaultGlyphService.render(rule, rectangle, g2d, layer);
                    g2d.setFont(template.getRuleFont());
                    g2d.setColor(Color.BLACK);

                    /**
                     * TODO : Remove that lines, or explain clearly why it is commented.
                     */
//                    final float baseline;
//                    if(glyphHeight > ruleFontHeight){
//                        baseline = moveY + ruleFontMetric.getLeading() + ruleFontMetric.getAscent()
//                                + (glyphHeight-ruleFontHeight)/2 ;
//                    }else{
//                        baseline = moveY + ruleFontMetric.getLeading() + ruleFontMetric.getAscent();
//                    }

                    g2d.drawString(title, glyphWidth + GLYPH_SPACE, moveY + stepRuleTitle);

                    moveY += (glyphHeight > ruleFontHeight) ? glyphHeight : ruleFontHeight;
                    numElement++;
                }
            }
        }
        
        g2d.translate(0, -moveY);
    }
    
    
    /**
     * Paint a legend using Java2D.
     *
     * @param rules : A list of rules to use for legend rendering.
     * @param g2d : Graphics2D used to render
     * @param bounds : Rectangle where the legend must be painted
     * @param template : Legend rendering hints.
     * @throws org.geotoolkit.display.exception.PortrayalException
     */
    public static void paintLegend(final List<Rule> rules,
            Graphics2D g2d,
            final Rectangle bounds,
            final LegendTemplate template) throws PortrayalException {

        g2d = (Graphics2D) g2d.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setClip(bounds);
        g2d.setStroke(new BasicStroke(1));

        final float gapSize = template.getGapSize();
        final float glyphHeight = template.getGlyphSize().height;
        final float glyphWidth = template.getGlyphSize().width;
        final Rectangle2D rectangle = new Rectangle2D.Float();
        float X = bounds.x;
        float Y = bounds.y;

        for (final Rule rule : rules) {
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
            g2d.drawString(title, X + glyphWidth + gapSize, Y + glyphHeight);

            Y += glyphHeight + gapSize;
        }
    }

    /**
     * Compute the optimum size for the given {@link MapItem}, using given configuration.
     * @param g The graphic2D in which we want to paint legend.
     * @param mapitem The mapItem we want legend for.
     * @param template The {@link LegendTemplate} defining legend paniting rules.
     * Can be null.
     * @param considerBackground A boolean to specify if we must count input 
     * {@link LegendTemplate} background margins in the result size (true) or 
     * not (false).
     * 
     * @return An estimation of the size (pixels) needed to display the complete
     * legend of the input map item.
     */
    public static Dimension estimate(final Graphics2D g, MapItem mapitem, final LegendTemplate template, final boolean considerBackground) {
        return estimate(g, mapitem, template, null, considerBackground);
    }

    /**
     * Compute the optimum size for the given {@link MapItem}, using given configuration.
     * @param g The graphic2D in which we want to paint legend.
     * @param mapitem The mapItem we want legend for.
     * @param template The {@link LegendTemplate} defining legend paniting rules.
     * Can be null.
     * @param images A map in which we'll store eventual legend we generated
     * during the process (key = layer name, value = layer legend). Can be null.
     * @param considerBackground A boolean to specify if we must count input 
     * {@link LegendTemplate} background margins in the result size (true) or 
     * not (false).
     * 
     * @return An estimation of the size (pixels) needed to display the complete
     * legend of the input map item.
     */
    public static Dimension estimate(final Graphics2D g, MapItem mapitem, final LegendTemplate template,
            final Map<Name,BufferedImage> images, final boolean considerBackground)
    {
        final Dimension dim = new Dimension(0, 0);
        if (mapitem == null) {
            return dim;
        }

        if (mapitem instanceof MapLayer) {
            final MapContext context = MapBuilder.createContext();
            context.items().add(mapitem);
            mapitem = context;
        }

        if (template == null) {
            //fallback on glyph size
            estimateNoTemplate(mapitem, dim);
            
        } else {
            estimateWithTemplate(g, mapitem, dim, template, images, considerBackground);
        }
        
        checkMinimumSize(dim);
        return dim;
    }

    /**
     * Estimate the size (pixels) needed to render the given {@link MapItem} legend
     * without using any rendering hint (see {@link LegendTemplate}).
     * @param source the map item to paint legend for.
     * @param toSet the dimension used to store estimation result.
     */
    private static void estimateNoTemplate(MapItem source, Dimension toSet) {
        for (MapItem childItem : source.items()) {
            
            if (childItem instanceof MapLayer) {
                final MapLayer ml = (MapLayer) childItem;
                final Dimension preferred = DefaultGlyphService.glyphPreferredSize(ml.getStyle(), null, ml);
                if (preferred != null) {
                    if (preferred.width > toSet.width) {
                        toSet.width = preferred.width;
                    }
                    toSet.height += preferred.height;                    
                }
                
            } else {
                estimateNoTemplate(childItem, toSet);
            }
        }
        checkMinimumSize(toSet);
    }
    
    /**
     * Estimate the size (pixels) needed to render the given {@link MapItem} legend
     * using the input rendering hint (see {@link LegendTemplate}).
     * @param g2d The {@link Graphics2D} to paint legend into.
     * @param source The source map item to render.
     * @param toSet The {@link Dimension} to store estimation result into.
     * @param template the {@link LegendTemplate} to use as rendering hint.
     * @param images A map in which we'll store eventual legend we generated
     * during the process (key = layer name, value = layer legend). Can be null.
     * @param considerBackground A boolean to specify if we must count input 
     * {@link LegendTemplate} background margins in the result size (true) or 
     * not (false).
     */
    private static void estimateWithTemplate(
            final Graphics2D g2d, 
            final MapItem source, 
            final Dimension toSet, 
            final LegendTemplate template,
            final Map<Name,BufferedImage> images, 
            final boolean considerBackground) {
        
        final FontMetrics layerFontMetric = g2d.getFontMetrics(template.getLayerFont());
        final int layerFontHeight = layerFontMetric.getHeight();
        final FontMetrics ruleFontMetric = g2d.getFontMetrics(template.getRuleFont());
        final int ruleFontHeight = ruleFontMetric.getHeight();
        final Dimension glyphSize = template.getGlyphSize();

        final List<MapItem> childItems = source.items();
        for (int l = 0, n = childItems.size(); l < n; l++) {
            
            final MapItem currentItem = childItems.get(l);            
            if (template.displayOnlyVisibleLayers() && !currentItem.isVisible()) {
                continue;
            }
            
            // Determines space to reserve for title.
            if (template.isLayerVisible()) {
                if (l != 0) {
                    toSet.height += template.getGapSize();
                }
                
                String title = "";
                final Description description = currentItem.getDescription();
                if (description != null) {
                    final InternationalString titleTmp = description.getTitle();
                    if (titleTmp != null) {
                        title = titleTmp.toString().replace("{}", "");
                    }
                    if(title.isEmpty() && currentItem.getName() != null) {
                        title = currentItem.getName();
                    } 
                }
                final int width = layerFontMetric.stringWidth(title);

                toSet.height += layerFontHeight;
                if (toSet.width < width) {
                    toSet.width = width;
                }
                toSet.height += template.getGapSize();
            }
                        
            if (!(currentItem instanceof MapLayer)) {
                estimateWithTemplate(g2d, currentItem, toSet, template, images, considerBackground);
                continue;
            }

            final MapLayer layer = (MapLayer) currentItem;
            
            // Launch a get legend request and take the dimensions from the result
            testwms:
            if (layer instanceof DefaultCoverageMapLayer) {
                final DefaultCoverageMapLayer covLayer = (DefaultCoverageMapLayer)layer;
                final CoverageReference covRef = covLayer.getCoverageReference();

                if (covRef == null) {
                    continue;
                }
                // try first to retrieve the legend directly from the coverage reference.
                BufferedImage image;
                try {
                    image = (BufferedImage) covRef.getLegend();
                } catch (DataStoreException ex) {
                    LOGGER.log(Level.FINE, ex.getLocalizedMessage(), ex);
                    continue;
                }

                // else try a WMS getLegendGraphic request
                if (image == null) {
                    final ParameterValue paramVal;
                    try {
                        paramVal = covRef.getStore().getConfiguration().parameter("url");
                    } catch (ParameterNotFoundException e) {
                        LOGGER.log(Level.FINE, e.getLocalizedMessage(), e);
                        break testwms;
                    }
                    final URL urlWms = (URL) paramVal.getValue();
                    final StringBuilder sb = new StringBuilder(urlWms.toString());
                    if (!urlWms.toString().endsWith("?")) {
                        sb.append("?");
                    }
                    sb.append("request=GetLegendGraphic&service=WMS&format=image/png&layer=")
                      .append(covLayer.getCoverageName());

                    try {
                        final URL getLegendUrl = new URL(sb.toString());
                        image = ImageIO.read(getLegendUrl);
                    } catch (IOException e) {
                        LOGGER.log(Level.INFO, e.getLocalizedMessage(), e);
                        // just skip this layer if we didn't succeed in getting the get legend result.
                        continue;
                    }
                }

                if (image != null) {
                    toSet.height += image.getHeight();
                    if (toSet.width < image.getWidth()) {
                        toSet.width = image.getWidth();
                    }

                    if(images != null){
                        images.put(covLayer.getCoverageName(), image);
                    }
                    
                    continue;
                }
            }

            final MutableStyle style = layer.getStyle();
            if (style == null) {
                continue;
            }

            int numElement = 0;
            for (final MutableFeatureTypeStyle fts : style.featureTypeStyles()) {
                for (final MutableRule rule : fts.rules()) {
                    if (numElement != 0) {
                        toSet.height += template.getGapSize();
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
                    
                    final Dimension preferred = DefaultGlyphService.glyphPreferredSize(rule, glyphSize, layer);
                    if (glyphSize == null) {
                        //find the best size
                        glyphHeight = preferred.height;
                        glyphWidth = preferred.width;
                    } else {
                        //use the defined size
                        glyphHeight = glyphSize.height > preferred.height ? glyphSize.height : preferred.height;
                        glyphWidth = glyphSize.width > preferred.width ? glyphSize.width : preferred.width;
                    }

                    final int totalWidth = glyphWidth + ((textLenght == 0) ? 0 : (GLYPH_SPACE + textLenght));
                    final int fh = (textLenght > 0) ? ruleFontHeight : 0;
                    final int totalHeight = (glyphHeight > fh) ? glyphHeight : fh;

                    toSet.height += totalHeight;
                    if (toSet.width < totalWidth) {
                        toSet.width = totalWidth;
                    }
                    numElement++;
                }
            }

        }

        if (considerBackground && template.getBackground() != null) {
            final Insets insets = template.getBackground().getBackgroundInsets();
            toSet.width += insets.left + insets.right;
            toSet.height += insets.bottom + insets.top;
        }
    }
    
    
    private static void checkMinimumSize(final Dimension dim) {
        if (dim.width == 0) {
            dim.width = 1;
        }
        if (dim.height == 0) {
            dim.height = 1;
        }
    }
}
