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
package org.geotoolkit.display2d.service;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;

import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;

/**
 * Factory to create small glyphs used in map or application legends.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class DefaultGlyphService {

    private DefaultGlyphService(){}

    public static BufferedImage create(final Style style, final Dimension dim) {
        if (dim == null || style == null) {
            throw new NullPointerException("Style and dimension can not be null");
        }
        if (dim.height <= 0 || dim.width <= 0) {
            throw new IllegalArgumentException("Invalid dimension, height and width must be superior to 0");
        }

        final BufferedImage buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = buffer.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, dim.width, dim.height);
        render(style, new Rectangle(dim), (Graphics2D) g2.create());
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, dim.width - 1, dim.height - 1);
        g2.dispose();
        return buffer;
    }

    public static BufferedImage create(final FeatureTypeStyle style, final Dimension dim) {
        if (dim == null || style == null) {
            throw new NullPointerException("Style and dimension can not be null");
        }
        if (dim.height <= 0 || dim.width <= 0) {
            throw new IllegalArgumentException("Invalid dimension, height and width must be superior to 0");
        }

        final BufferedImage buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = buffer.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, dim.width, dim.height);
        render(style, new Rectangle(dim), (Graphics2D) g2.create());
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, dim.width - 1, dim.height - 1);
        g2.dispose();
        return buffer;
    }

    public static BufferedImage create(final Rule style, final Dimension dim) {
        if (dim == null || style == null) {
            throw new NullPointerException("Style and dimension can not be null");
        }
        if (dim.height <= 0 || dim.width <= 0) {
            throw new IllegalArgumentException("Invalid dimension, height and width must be superior to 0");
        }

        final BufferedImage buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = buffer.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, dim.width, dim.height);
        render(style, new Rectangle(dim), (Graphics2D) g2.create());
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, dim.width - 1, dim.height - 1);
        g2.dispose();
        return buffer;
    }

    public static BufferedImage create(final Symbolizer style, final Dimension dim) {
        if (dim == null || style == null) {
            throw new NullPointerException("Style and dimension can not be null");
        }
        if (dim.height <= 0 || dim.width <= 0) {
            throw new IllegalArgumentException("Invalid dimension, height and width must be superior to 0");
        }

        final BufferedImage buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = buffer.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, dim.width, dim.height);
        render(style, new Rectangle(dim), (Graphics2D) g2.create());
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, dim.width - 1, dim.height - 1);
        g2.dispose();
        return buffer;
    }

    public static void render(final Style style, final Rectangle2D rectangle, final Graphics2D target) {
        for(final FeatureTypeStyle fts : style.featureTypeStyles()){
            render(fts,rectangle,target);
        }
    }

    public static void render(final FeatureTypeStyle fts, final Rectangle2D rectangle, final Graphics2D target) {
        for(final Rule rule : fts.rules()){
            render(rule,rectangle,target);
        }
    }

    public static void render(final Rule rule, final Rectangle2D rectangle, final Graphics2D target) {
        for(final Symbolizer symbol : rule.symbolizers()){
            render(symbol,rectangle,target);
        }
    }

    public static void render(final Symbolizer symbol, final Rectangle2D rectangle, Graphics2D target) {
        target = (Graphics2D) target.create();
        target.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final SymbolizerRenderer renderer = GO2Utilities.findRenderer(symbol.getClass());

        if(renderer != null){
            CachedSymbolizer cache = GO2Utilities.getCached(symbol);
            renderer.glyph(target, rectangle, cache);
        }
    }
        
}
