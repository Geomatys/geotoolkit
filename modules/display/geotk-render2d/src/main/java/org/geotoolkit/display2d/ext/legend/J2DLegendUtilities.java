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
import java.awt.geom.AffineTransform;
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
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.ext.BackgroundTemplate;
import org.geotoolkit.display2d.ext.BackgroundUtilities;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.apache.sis.util.logging.Logging;
import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.style.Description;
import org.opengis.style.Rule;
import org.opengis.util.InternationalString;

/**
 * Utility class to render legend using a provided template.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class J2DLegendUtilities {
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.display2d.ext.legend");

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
     */
    public static void paintLegend(MapItem item,
            Graphics2D g2d,
            final Rectangle bounds,
            final LegendTemplate template) {

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
            final Map<GenericName, BufferedImage> legendResults = new HashMap<>();

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

    /**
     * Draw legend using given {@link LegendTemplate} as rendering hint. At the
     * end of the drawning, input {@link Graphics2D} origin is reset at the origin
     * it was when given as parameter.
     * @param item The map item to draw legend for.
     * @param g2d The graphic object to draw legend in.
     * @param bounds Drawing authorized rectangle.
     * @param template Rendering hints.
     * @param legendResults useless. Store drawn images for each coverage layer
     * @return The number of lines which have been drawn on y axis
     */
    private static int paintWithTemplate(
            final MapItem item,
            final Graphics2D g2d,
            final Rectangle bounds,
            final LegendTemplate template,
            final Map<GenericName, BufferedImage> legendResults) {

        final AffineTransform origin =  g2d.getTransform();

        final FontMetrics layerFontMetric = g2d.getFontMetrics(template.getLayerFont());
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
            if (template.displayOnlyVisibleLayers() && !isVisible(currentItem)) {
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
                    Color layerFontColor = template.getLayerFontColor();
                    if (layerFontColor != null) {
                        if (template.getLayerFontOpacity() != null) {
                            layerFontColor = new Color(layerFontColor.getRed(), layerFontColor.getGreen(),
                                    layerFontColor.getBlue(), template.getLayerFontOpacity());
                        }
                    } else {
                        layerFontColor = Color.BLACK;
                    }
                    g2d.setColor(layerFontColor);
                    g2d.drawString(title, 0, moveY);
                    moveY += layerFontMetric.getDescent();
                    moveY += gapSize;
                }
            }

            // If we're not on a leaf, try to display this node children.
            if (!(currentItem instanceof MapLayer)) {
                // Using doubles allows current position relative translation.
                final double nodeInset = template.getBackground().getBackgroundInsets().left;
                g2d.translate(nodeInset,  moveY);
                final int itemDim = paintWithTemplate(currentItem, g2d, bounds, template, legendResults);
                g2d.translate(-nodeInset, -moveY);
                // Previous function reset graphic position at the top of drawn map item. We Add its size to vertical offset, so next item knows how much pixels it should jump.
                moveY += itemDim;
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
                final BufferedImage image = legendResults.get(covLayer.getCoverageReference().getName().tip().toString());
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
                    final Dimension preferred = DefaultGlyphService.glyphPreferredSize(rule, null, layer);
                    if (glyphSize == null) {
                        //find the best size
                        glyphHeight = preferred.height;
                        glyphWidth = preferred.width;
                    } else {
                        // Use the biggest size between preferred one and default one.
                        glyphHeight = Math.max(glyphSize.height, preferred.height);
                        glyphWidth = Math.max(glyphSize.width, preferred.width);
                    }

                    if (glyphHeight > ruleFontHeight) {
                        stepRuleTitle = ruleFontMetric.getLeading() + ruleFontMetric.getAscent()
                                + (glyphHeight - ruleFontHeight) / 2;
                    } else {
                        stepRuleTitle = ruleFontMetric.getLeading() + ruleFontMetric.getAscent();
                    }

                    rectangle.setRect(0, moveY, glyphWidth, glyphHeight);
                    DefaultGlyphService.render(rule, rectangle, g2d, layer);

                    String title = "";
                    final Description description = rule.getDescription();
                    if (description != null) {
                        final InternationalString titleTmp = description.getTitle();
                        if (titleTmp != null) {
                            title = titleTmp.toString();
                        }
                    }

                    if (title.isEmpty()) {
                        moveY += glyphHeight;
                    } else {
                        g2d.setFont(template.getRuleFont());
                        g2d.setColor(Color.BLACK);

                        g2d.drawString(title, glyphWidth + GLYPH_SPACE, moveY + stepRuleTitle);
                        moveY += (glyphHeight > ruleFontHeight) ? glyphHeight : ruleFontHeight;
                    }
                    numElement++;
                }
            }
        }

        g2d.setTransform(origin);
        return (int) moveY;
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
            final Map<GenericName,BufferedImage> images, final boolean considerBackground)
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
                final Dimension preferred = new Dimension(0, 0);
                DefaultGlyphService.glyphPreferredSize(ml.getStyle(), preferred, ml);
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
            final Map<GenericName,BufferedImage> images,
            final boolean considerBackground) {

        final FontMetrics layerFontMetric = g2d.getFontMetrics(template.getLayerFont());
        final FontMetrics ruleFontMetric = g2d.getFontMetrics(template.getRuleFont());
        final int ruleFontHeight = ruleFontMetric.getHeight();
        final Dimension glyphSize = template.getGlyphSize();

        final List<MapItem> childItems = source.items();
        for (int l = 0, n = childItems.size(); l < n; l++) {

            /* If legend template asks for visible items only, we have to proceed
             * in two steps, because we cannot just check item visibility.
             * If we are on a container (not a layer), it must be considered as
             * invisible if none of its children is visible.
             */
            final MapItem currentItem = childItems.get(l);
            if (template.displayOnlyVisibleLayers() && !isVisible(currentItem)) {
                continue;
            }

            if (template.isLayerVisible()) {
                if (l != 0) {
                    toSet.height += template.getGapSize();
                }

                // Determines space to reserve for title.
                final Dimension titleDim = estimateTitle(currentItem, layerFontMetric);
                toSet.height += titleDim.height;
                if (toSet.width < titleDim.width) {
                    toSet.width = titleDim.width;
                }
                if (titleDim.height > 0) {
                    toSet.height += template.getGapSize();
                }
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

                if (image != null) {
                    toSet.height += image.getHeight();
                    if (toSet.width < image.getWidth()) {
                        toSet.width = image.getWidth();
                    }

                    if(images != null){
                        images.put(covLayer.getCoverageReference().getName(), image);
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

                    final Dimension preferred = DefaultGlyphService.glyphPreferredSize(rule, null, layer);
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

    /**
     * Ensure that the current map item is visible. For {@link MapLayer}, it means
     * that it is visible itself. For MapItems, we check recursively all of its
     * children until we find a visible {@link MapLayer}. An item is considered
     * invisible if itself or all of its children are marked as not visible.
     * @param toCheck The map item to analyse. Cannot be null.
     * @return True if input item or at least one of its {@link MapLayer} child
     * is visible. False otherwise.
     */
    public static boolean isVisible(final MapItem toCheck) {
        ArgumentChecks.ensureNonNull("Map item to check visibility on", toCheck);
        // If it's a visible container, we must check its children.
        if (toCheck.isVisible() && !(toCheck instanceof MapLayer)) {
            for (final MapItem child : toCheck.items()) {
                if (isVisible(child)) {
                    return true;
                }
            }
            return false;
        } else {
            return toCheck.isVisible();
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

    private static Dimension estimateTitle(final MapItem source, final FontMetrics fontRules) {
        final Dimension dim = new Dimension(0, 0);
        String title = "";
        final Description description = source.getDescription();
        if (description != null) {
            final InternationalString titleTmp = description.getTitle();
            if (titleTmp != null) {
                title = titleTmp.toString().replace("{}", "");
            }
            if (title.isEmpty() && source.getName() != null) {
                title = source.getName();
            }
        }

        if (!title.isEmpty()) {
            dim.width = fontRules.stringWidth(title);
            dim.height = fontRules.getHeight();
        }

        return dim;
    }
}
