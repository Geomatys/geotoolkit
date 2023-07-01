/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.geotoolkit.display2d.ext.DefaultBackgroundTemplate;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class LegendSizeTest extends org.geotoolkit.test.TestBase {

    private static final LegendTemplate NO_MARGIN_TEMPLATE = new DefaultLegendTemplate(
            new DefaultBackgroundTemplate(new BasicStroke(0), Color.BLACK, Color.RED, new Insets(0, 0, 0, 0), 0),
            2, null, new Font("arial", Font.PLAIN, 10), false, new Font("arial", Font.PLAIN, 10));

    private static final MutableStyleFactory SF = DefaultStyleFactory.provider();

    public LegendSizeTest() {
    }

    @Test
    public void testRasterLegend() {
        final Symbolizer dr = StyleConstants.DEFAULT_RASTER_SYMBOLIZER;
        final MapLayer layer = MapBuilder.createEmptyMapLayer();
        layer.setStyle(SF.style(dr));

        final MapLayers ctx = MapBuilder.createContext();
        ctx.getComponents().add(layer);

        Dimension dim = DefaultLegendService.legendPreferredSize(null, ctx);

        assertEquals(30,dim.width);
        assertEquals(24,dim.height);

        //test with an empty template
        dim = DefaultLegendService.legendPreferredSize(NO_MARGIN_TEMPLATE, ctx);
        assertEquals(30,dim.width);
        assertEquals(24,dim.height);
    }

    @Test
    public void testNoStyle() {

        final MapLayer layer = MapBuilder.createEmptyMapLayer();
        layer.setStyle(SF.style());

        final MapLayers ctx = MapBuilder.createContext();
        ctx.getComponents().add(layer);

        Dimension dim = DefaultLegendService.legendPreferredSize(null, ctx);

        assertEquals(1,dim.width);
        assertEquals(1,dim.height);

        //test with an empty template
        dim = DefaultLegendService.legendPreferredSize(NO_MARGIN_TEMPLATE, ctx);
        assertEquals(1,dim.width);
        assertEquals(1,dim.height);
    }

    @Test
    public void testPolygonStyle() {

        final MapLayer layer = MapBuilder.createEmptyMapLayer();
        layer.setStyle(SF.style(StyleConstants.DEFAULT_POLYGON_SYMBOLIZER));

        final MapLayers ctx = MapBuilder.createContext();
        ctx.getComponents().add(layer);

        Dimension dim = DefaultLegendService.legendPreferredSize(null, ctx);

        assertEquals(30,dim.width);
        assertEquals(24,dim.height);

        //test with an empty template
        dim = DefaultLegendService.legendPreferredSize(NO_MARGIN_TEMPLATE, ctx);
        assertEquals(30,dim.width);
        assertEquals(24,dim.height);
    }

    @Test
    public void testRecursiveStyle() {
        // Polygon layer, glyph size : w30, h24
        final MapLayer leaf1 = MapBuilder.createEmptyMapLayer();
        leaf1.setStyle(SF.style(StyleConstants.DEFAULT_POLYGON_SYMBOLIZER));

        final MapLayer leaf2 = MapBuilder.createEmptyMapLayer();
        leaf2.setStyle(SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));

        final MapLayers node1 = MapBuilder.createItem();
        node1.setIdentifier("bouh");
        node1.getComponents().add(leaf1);
        node1.getComponents().add(leaf2);

        final MapLayers node2 = MapBuilder.createItem();
        node2.getComponents().add(leaf1);

        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(node1);
        context.getComponents().add(node2);

        Dimension dim = DefaultLegendService.legendPreferredSize(NO_MARGIN_TEMPLATE, context);

        // We've got 3 glyph vertically aligned, no margin, no title and no insets between them.
        final int glyphHeight = 24;
        final int glyphWidth = 30;
        assertEquals(glyphWidth, dim.width);
        assertEquals(glyphHeight*3, dim.height);

        // A new template to test legend estimation using titles and left insets to reflect tree structure.
        final int leftInset = 10;
        final DefaultBackgroundTemplate backTemplate = new DefaultBackgroundTemplate(
                new BasicStroke(0), Color.BLACK, Color.RED, new Insets(0, leftInset, 0, 0), 0);
        final LegendTemplate titleTemplate = new DefaultLegendTemplate(
                backTemplate,
                NO_MARGIN_TEMPLATE.getGapSize(),
                NO_MARGIN_TEMPLATE.getGlyphSize(),
                NO_MARGIN_TEMPLATE.getRuleFont(),
                true,
                NO_MARGIN_TEMPLATE.getLayerFont(),
                true);

        // Same test than above, but here we've got titles and left insets.
        dim = DefaultLegendService.legendPreferredSize(titleTemplate, context);
        // Get text pixel size.
        final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = img.createGraphics();
        final FontMetrics font = g2d.getFontMetrics(titleTemplate.getLayerFont());
        final int fontHeight = font.getHeight();
        final int fontWidth = font.stringWidth(node1.getIdentifier());
        final int gap = (int) titleTemplate.getGapSize();

        final int legendHeight =
                // node1
                fontHeight + gap // title
                + glyphHeight + gap // leaf1
                + glyphHeight + gap // leaf2
                // node2
                + glyphHeight; // leaf1

        /* Inset multiply 3 times because :
         * <-Inset->MapContext
         *          <-Inset->node1
         *                   <-Inset->leaf1
         * ...
         */
        final int legendWidth  = leftInset*3 + Math.max(fontWidth, glyphWidth);
        assertEquals("Legend width", legendWidth, dim.width);
        assertEquals("Legend height", legendHeight, dim.height);
    }
}
