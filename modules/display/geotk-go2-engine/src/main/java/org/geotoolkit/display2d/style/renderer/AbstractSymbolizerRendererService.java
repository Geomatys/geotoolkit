/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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


import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.map.MapLayer;

import org.opengis.style.Symbolizer;

/**
 * Abstract symbolizer renderer service, will redirect most call on using
 * createRenderer and use it.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractSymbolizerRendererService<S extends Symbolizer, C extends CachedSymbolizer<S>> implements SymbolizerRendererService<S, C>{

    /**
     * Returns the standard glyph size : 30x24
     */
    @Override
    public Rectangle2D glyphPreferredSize(C symbol, MapLayer layer) {
        return new Rectangle2D.Double(0, 0, 30, 24);
    }

    @Override
    public void portray(ProjectedFeature graphic, C symbol, RenderingContext2D context) throws PortrayalException {
        final SymbolizerRenderer renderer = createRenderer(symbol, context);
        renderer.portray(graphic);
    }

    @Override
    public void portray(Iterator<ProjectedFeature> graphics, C symbol, RenderingContext2D context) throws PortrayalException {
        final SymbolizerRenderer renderer = createRenderer(symbol, context);
        renderer.portray(graphics);
    }

    @Override
    public void portray(ProjectedCoverage graphic, C symbol, RenderingContext2D context) throws PortrayalException {
        final SymbolizerRenderer renderer = createRenderer(symbol, context);
        renderer.portray(graphic);
    }

    @Override
    public boolean hit(ProjectedFeature graphic, C symbol, RenderingContext2D context, SearchAreaJ2D mask, VisitFilter filter) {
        final SymbolizerRenderer renderer = createRenderer(symbol, context);
        return renderer.hit(graphic, mask, filter);
    }

    @Override
    public boolean hit(ProjectedCoverage graphic, C symbol, RenderingContext2D context, SearchAreaJ2D mask, VisitFilter filter) {
        final SymbolizerRenderer renderer = createRenderer(symbol, context);
        return renderer.hit(graphic, mask, filter);
    }

}
