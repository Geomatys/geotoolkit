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

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GraphicCoverageJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;

import org.opengis.style.Symbolizer;

/**
 * @author Johann Sorel (Geomatys)
 */
public interface SymbolizerRenderer<S extends Symbolizer, C extends CachedSymbolizer<S>> {

    Class<S> getSymbolizerClass();

    Class<C> getCachedSymbolizerClass();

    C createCachedSymbolizer(S symbol);

    void portray(ProjectedFeature graphic, C symbol,
            RenderingContext2D context) throws PortrayalException;

    void portray(GraphicCoverageJ2D graphic, C symbol,
            RenderingContext2D context) throws PortrayalException;

    boolean hit(ProjectedFeature graphic, C symbol,
            RenderingContext2D context, SearchAreaJ2D mask, VisitFilter filter);

    boolean hit(GraphicCoverageJ2D graphic, C symbol,
            RenderingContext2D renderingContext, SearchAreaJ2D mask, VisitFilter filter);

    Rectangle2D estimate(ProjectedFeature graphic, C symbol,
            RenderingContext2D context, Rectangle2D rect);

    Rectangle2D glyphPreferredSize(C symbol);

    void glyph(Graphics2D g, Rectangle2D rect, C symbol);

}
