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
package org.geotoolkit.display2d.service;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.map.MapLayer;
import static org.apache.sis.util.ArgumentChecks.*;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Fill;
import org.opengis.style.Rule;
import org.opengis.style.Stroke;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;
import org.apache.sis.measure.Units;

/**
 * Factory to create small glyph used in map or application legends.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class DefaultGlyphService {

    private static final int DEFAULT_GLYPH_WIDTH = 30;
    private static final int DEFAULT_GLYPH_HEIGHT = 24;

    private DefaultGlyphService(){}

    public static BufferedImage create(final Style style, final Dimension dim, final MapLayer layer) {
        ensureNonNull("dimension", dim);
        ensureNonNull("style", style);
        if (dim.height <= 0 || dim.width <= 0) {
            throw new IllegalArgumentException("Invalid dimension, height and width must be superior to 0");
        }

        final BufferedImage buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = buffer.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, dim.width, dim.height);
        render(style, new Rectangle(dim), (Graphics2D) g2.create(), layer);
        g2.drawRect(0, 0, dim.width - 1, dim.height - 1);
        g2.dispose();
        return buffer;
    }

    public static BufferedImage create(final FeatureTypeStyle style, final Dimension dim, final MapLayer layer) {
        ensureNonNull("dimension", dim);
        ensureNonNull("style", style);
        if (dim.height <= 0 || dim.width <= 0) {
            throw new IllegalArgumentException("Invalid dimension, height and width must be superior to 0");
        }

        final BufferedImage buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = buffer.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, dim.width, dim.height);
        render(style, new Rectangle(dim), (Graphics2D) g2.create(), layer);
        g2.drawRect(0, 0, dim.width - 1, dim.height - 1);
        g2.dispose();
        return buffer;
    }

    public static BufferedImage create(final Rule style, Dimension dim, final MapLayer layer) {
        ensureNonNull("style", style);
        if (dim != null && (dim.height <= 0 || dim.width <= 0)) {
            throw new IllegalArgumentException("Invalid dimension, height and width must be superior to 0");
        }

        if(dim == null){
            //search for the best glyph size
            dim = new Dimension(DEFAULT_GLYPH_WIDTH,DEFAULT_GLYPH_HEIGHT);
            for(Symbolizer symbol : style.symbolizers()){
                final SymbolizerRendererService renderer = GO2Utilities.findRenderer(symbol.getClass());

                if(renderer != null){
                    final CachedSymbolizer cache = GO2Utilities.getCached(symbol,null);
                    final Rectangle2D preferred = renderer.glyphPreferredSize(cache,layer);
                    if(preferred!= null){
                        if(preferred.getWidth() > dim.getWidth()) dim.width = (int) preferred.getWidth();
                        if(preferred.getHeight() > dim.getHeight()) dim.height = (int) preferred.getHeight();
                    }
                }
            }
        }

        final BufferedImage buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = buffer.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, dim.width, dim.height);
        render(style, new Rectangle(dim), (Graphics2D) g2.create(), layer);
        g2.drawRect(0, 0, dim.width - 1, dim.height - 1);
        g2.dispose();
        return buffer;
    }

    public static BufferedImage create(final Symbolizer style, Dimension dim, final MapLayer layer) {
        ensureNonNull("style", style);
        if (dim != null && (dim.height <= 0 || dim.width <= 0)) {
            throw new IllegalArgumentException("Invalid dimension, height and width must be superior to 0");
        }

        if(dim == null){
            //search for the best glyph size
            dim = glyphPreferredSize(style, dim, layer);
        }

        final BufferedImage buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = buffer.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, dim.width, dim.height);
        render(style, new Rectangle(dim), (Graphics2D) g2.create(),layer);
        g2.drawRect(0, 0, dim.width - 1, dim.height - 1);
        g2.dispose();
        return buffer;
    }

    public static BufferedImage create(final Fill fill, Dimension dim, final MapLayer layer) {
        ensureNonNull("fill", fill);
        if (dim != null && (dim.height <= 0 || dim.width <= 0)) {
            throw new IllegalArgumentException("Invalid dimension, height and width must be superior to 0");
        }

        if(dim == null){
            //search for the best glyph size
            dim = glyphPreferredSize(fill, dim, layer);
        }

        final BufferedImage buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = buffer.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, dim.width, dim.height);
        render(fill, new Rectangle(dim), (Graphics2D) g2.create(),layer);
        g2.drawRect(0, 0, dim.width - 1, dim.height - 1);
        g2.dispose();
        return buffer;
    }

    public static BufferedImage create(final Stroke stroke, Dimension dim, final MapLayer layer) {
        ensureNonNull("stroke", stroke);
        if (dim != null && (dim.height <= 0 || dim.width <= 0)) {
            throw new IllegalArgumentException("Invalid dimension, height and width must be superior to 0");
        }

        if(dim == null){
            //search for the best glyph size
            dim = glyphPreferredSize(stroke, dim, layer);
        }

        final BufferedImage buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = buffer.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, dim.width, dim.height);
        render(stroke, new Rectangle(dim), (Graphics2D) g2.create(),layer);
        g2.drawRect(0, 0, dim.width - 1, dim.height - 1);
        g2.dispose();
        return buffer;
    }

    public static void render(final Style style, final Rectangle2D rectangle, final Graphics2D target, final MapLayer layer) {
        for(final FeatureTypeStyle fts : style.featureTypeStyles()){
            render(fts,rectangle,target,layer);
        }
    }

    public static void render(final FeatureTypeStyle fts, final Rectangle2D rectangle, final Graphics2D target, final MapLayer layer) {
        for(final Rule rule : fts.rules()){
            render(rule,rectangle,target,layer);
        }
    }

    public static void render(final Rule rule, final Rectangle2D rectangle, final Graphics2D target, final MapLayer layer) {
        for(final Symbolizer symbol : rule.symbolizers()){
            render(symbol,rectangle,target,layer);
        }
    }

    public static void render(final Symbolizer symbol, final Rectangle2D rectangle, Graphics2D target, final MapLayer layer) {
        target = (Graphics2D) target.create();
        target.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final SymbolizerRendererService renderer = GO2Utilities.findRenderer(symbol.getClass());

        if(renderer != null){
            CachedSymbolizer cache = GO2Utilities.getCached(symbol,null);
            renderer.glyph(target, rectangle, cache,layer);
        }
    }

    public static void render(final Fill fill, final Rectangle2D rectangle, Graphics2D target, final MapLayer layer) {
        target = (Graphics2D) target.create();
        target.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GO2Utilities.renderFill(rectangle, fill, target);
    }

    public static void render(final Stroke stroke, final Rectangle2D rectangle, Graphics2D target, final MapLayer layer) {
        target = (Graphics2D) target.create();
        target.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final Shape line = new Line2D.Double(rectangle.getMinX(), rectangle.getCenterY(), rectangle.getMaxX(), rectangle.getCenterY());
        GO2Utilities.renderStroke(line, stroke, Units.POINT, target);
    }

    public static Dimension glyphPreferredSize(final Style style, Dimension dim, final MapLayer layer){

        for(FeatureTypeStyle symbol : style.featureTypeStyles()){
            dim = glyphPreferredSize(symbol, dim, layer);
        }

        if(dim == null) dim = new Dimension(DEFAULT_GLYPH_WIDTH,DEFAULT_GLYPH_HEIGHT);
        return dim;
    }

    public static Dimension glyphPreferredSize(final FeatureTypeStyle style, Dimension dim, final MapLayer layer){

        for(Rule symbol : style.rules()){
            dim = glyphPreferredSize(symbol, dim, layer);
        }

        if(dim == null) dim = new Dimension(DEFAULT_GLYPH_WIDTH,DEFAULT_GLYPH_HEIGHT);
        return dim;
    }

    public static Dimension glyphPreferredSize(final Rule style, Dimension dim, final MapLayer layer){

        for(Symbolizer symbol : style.symbolizers()){
            dim = glyphPreferredSize(symbol, dim, layer);
        }

        if(dim == null) dim = new Dimension(DEFAULT_GLYPH_WIDTH,DEFAULT_GLYPH_HEIGHT);
        return dim;
    }

    public static Dimension glyphPreferredSize(final Symbolizer style, Dimension dim, final MapLayer layer){
        final SymbolizerRendererService renderer = GO2Utilities.findRenderer(style.getClass());

        if(renderer != null){
            final CachedSymbolizer cache = GO2Utilities.getCached(style,null);
            final Rectangle2D preferred = renderer.glyphPreferredSize(cache,layer);
            if(preferred!= null){
                if(dim == null){
                    dim = new Dimension((int)preferred.getWidth(), (int)preferred.getHeight());
                }else{
                    if(preferred.getWidth() > dim.getWidth()) dim.width = (int) preferred.getWidth();
                    if(preferred.getHeight() > dim.getHeight()) dim.height = (int) preferred.getHeight();
                }
            }
        }

        //default glyph size
        if(dim == null) dim = new Dimension(DEFAULT_GLYPH_WIDTH,DEFAULT_GLYPH_HEIGHT);
        return dim;
    }

    public static Dimension glyphPreferredSize(final Fill fill, Dimension dim, final MapLayer layer){
        //default glyph size
        if(dim == null){
            dim = new Dimension();
        }
        dim.setSize(DEFAULT_GLYPH_WIDTH, DEFAULT_GLYPH_HEIGHT);
        return dim;
    }

    public static Dimension glyphPreferredSize(final Stroke stroke, Dimension dim, final MapLayer layer){
        //default glyph size
        if(dim == null){
            dim = new Dimension();
        }
        dim.setSize(DEFAULT_GLYPH_WIDTH, DEFAULT_GLYPH_HEIGHT);
        return dim;
    }

}
