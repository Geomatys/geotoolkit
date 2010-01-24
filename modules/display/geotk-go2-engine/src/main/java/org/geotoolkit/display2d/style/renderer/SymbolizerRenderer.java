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
package org.geotoolkit.display2d.style.renderer;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;

import org.opengis.style.Symbolizer;

/**
 * A symbolizer renderer is capable to paint a given symbolizer on a java2d
 * canvas.
 *
 * Here is the normal call order to use the renderer :
 * - First see if the renderer can handle your symbolizr by testing the symbolizer class
 * with method getSymbolizerClass.
 * - Second create a cached version of the symbolizer using the createCachedSymbolizer() method.
 * A cached symbolizer is a prepare symbolizer that should optimize the rendering performance
 * when called often, an exemple is a symbolizer using a reference to a distant image file, the
 * cached version of this symbolizer should make a cache of it to greatly improve performances.
 * - Theard call the appropriate portray method given a graphic object, the cached symbolizer
 * and the rendering context.
 *
 * To perform some visual intersection test using the hit methods.
 *
 * And you can generate glyphs using the glyph method.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface SymbolizerRenderer<S extends Symbolizer, C extends CachedSymbolizer<S>> {

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
     * Paint the graphic object using the cached symbolizer and the rendering parameters.
     *
     * @param graphic : cached graphic representation of a feature
     * @param symbol : cached symbolizer to use
     * @param context : rendering context contains the java2d rendering parameters
     * @throws PortrayalException
     */
    void portray(ProjectedFeature graphic, C symbol,
            RenderingContext2D context) throws PortrayalException;

    /**
     * Paint in one iteration a complete set of features.
     * 
     * @param graphics : iterator over all graphics to render
     * @param symbol : cached symbolizer to use
     * @param context : rendering context contains the java2d rendering parameters
     * @throws PortrayalException
     */
    void portray(Iterator<ProjectedFeature> graphics, C symbol, 
            RenderingContext2D context) throws PortrayalException;

    /**
     * Paint the graphic object using the cached symbolizer and the rendering parameters.
     *
     * @param graphic : cached graphic representation of a coverage
     * @param symbol : cached symbolizer to use
     * @param context : rendering context contains the java2d rendering parameters
     * @throws PortrayalException
     */
    void portray(ProjectedCoverage graphic, C symbol,
            RenderingContext2D context) throws PortrayalException;

    /**
     * Test if the graphic object hit the given search area.
     *
     * @param graphic : cached graphic representation of a feature
     * @param symbol : cached symbolizer to use
     * @param context : rendering context contains the java2d rendering parameters
     * @param mask : search area, it can represent a mouse position or a particular shape
     * @param filter : the type of searching, intersect or within
     * @return true if the searcharea hit this graphic object, false otherwise.
     */
    boolean hit(ProjectedFeature graphic, C symbol,
            RenderingContext2D context, SearchAreaJ2D mask, VisitFilter filter);

    /**
     * Test if the graphic object hit the given search area.
     *
     * @param graphic : cached graphic representation of a coverage
     * @param symbol : cached symbolizer to use
     * @param renderingContext : rendering context contains the java2d rendering parameters
     * @param mask : search area, it can represent a mouse position or a particular shape
     * @param filter : the type of searching, intersect or within
     * @return true if the searcharea hit this graphic object, false otherwise.
     */
    boolean hit(ProjectedCoverage graphic, C symbol,
            RenderingContext2D renderingContext, SearchAreaJ2D mask, VisitFilter filter);

    /**
     * Find the most efficient glyph size to represent this symbol.
     *
     * @param symbol : cached symbolizer to use
     * @return the preferred size of this symbol, null if no preferred size.
     */
    Rectangle2D glyphPreferredSize(C symbol);

    /**
     * Paint the glyph of the given symbolizer.
     *
     * @param g : Graphics2D
     * @param rect : rectangle where the glyph must be painted
     * @param symbol : cached symbolizer to use
     */
    void glyph(Graphics2D g, Rectangle2D rect, C symbol);

}
