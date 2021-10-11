/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.display2d.style.renderer;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.apache.sis.portrayal.MapLayer;
import org.opengis.style.Symbolizer;

/**
 * A symbolizer renderer service is capable to paint a given symbolizer on a java2d
 * canvas.
 *
 * Here is the normal call order to use the renderer :
 * - First see if the renderer can handle your symbolizer by testing the symbolizer class
 * with method getSymbolizerClass.
 * - Second create a cached version of the symbolizer using the createCachedSymbolizer() method.
 * A cached symbolizer is a prepare symbolizer that should optimize the rendering performance
 * when called often, an example is a symbolizer using a reference to a distant image file, the
 * cached version of this symbolizer should make a cache of it to greatly improve performances.
 * - Thread call the appropriate portray method given a graphic object, the cached symbolizer
 * and the rendering context.
 *
 * To perform some visual intersection test using the hit methods.
 *
 *
 * If higher performances are needed then you can create a symbolizerRenderer with
 * createRenderer(Renderingcontext, cachedSymbolizer).
 *
 * And you can generate glyphs using the glyph method.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface SymbolizerRendererService<S extends Symbolizer, C extends CachedSymbolizer<S>> {

    /**
     * If this symbolizer needs all elements for rendering.
     * Only the portray(Iterator<? extends ProjectedObject> graphics) on SymbolizerRenderer will be used.
     *
     * @return true if this symbolizer render groups of elements.
     */
    boolean isGroupSymbolizer();

    /**
     * @return The symbolizer class handle by this renderer.
     */
    Class<S> getSymbolizerClass();

    /**
     * @return The cached class that will produce this renderer.
     */
    Class<C> getCachedSymbolizerClass();

    /**
     * Create a cached version of the given symbolizer.
     *
     * @param symbol : symbolizer to cache
     * @return a cached symbolizer
     */
    C createCachedSymbolizer(S symbol);

    /**
     * Create a renderer fixed for a symbol and a context.
     *
     * @param symbol : cached symbolizer
     * @param context : rendering context
     * @return SymbolizerRenderer or null if symbol is never visible.
     */
    SymbolizerRenderer createRenderer(C symbol, RenderingContext2D context);

    /**
     * Find the most efficient glyph size to represent this symbol.
     *
     * @param symbol : cached symbolizer to use
     * @return the preferred size of this symbol, null if no preferred size.
     */
    Rectangle2D glyphPreferredSize(C symbol, MapLayer layer);

    /**
     * Paint the glyph of the given symbolizer.
     *
     * @param g : Graphics2D
     * @param rect : rectangle where the glyph must be painted
     * @param symbol : cached symbolizer to use
     */
    void glyph(Graphics2D g, Rectangle2D rect, C symbol, MapLayer layer);

}
