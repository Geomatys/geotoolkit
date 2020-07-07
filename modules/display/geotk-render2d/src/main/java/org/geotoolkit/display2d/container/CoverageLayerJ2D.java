/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2014, Geomatys
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
package org.geotoolkit.display2d.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.DefaultSearchAreaJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.opengis.display.primitive.Graphic;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CoverageLayerJ2D extends MapLayerJ2D<MapLayer> {

    public CoverageLayerJ2D(final J2DCanvas canvas, final MapLayer layer){
        super(canvas, layer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean paintLayer(final RenderingContext2D renderingContext) {
        if (renderingContext.getMonitor().stopRequested()) return false;
        GenericName coverageName = null;
        try {
            coverageName = item.getResource().getIdentifier().orElse(null);
        } catch (DataStoreException ex) {
            //do nothing
        }
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(item.getStyle(),
                renderingContext.getSEScale(), coverageName,null);
        if (renderingContext.getMonitor().stopRequested()) return false;

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.length == 0) {
            return false;
        }

        final ProjectedCoverage projectedCoverage = new ProjectedCoverage(item);
        if (renderingContext.getMonitor().stopRequested()) return false;

        //search for a special graphic renderer
        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) item.getGraphicBuilder(GraphicJ2D.class);
        if (builder != null) {
            //this layer has a special graphic rendering, use it instead of normal rendering
            final Collection<GraphicJ2D> graphics = builder.createGraphics(item, getCanvas());
            boolean dataRendered = false;
            for (GraphicJ2D gra : graphics) {
                dataRendered |= gra.paint(renderingContext);
            }
            return dataRendered;
        }

        boolean dataRendered = false;
        for (final CachedRule rule : rules) {
            for (final CachedSymbolizer symbol : rule.symbolizers()) {
                try {
                    dataRendered |= GO2Utilities.portray(projectedCoverage, symbol, renderingContext);
                } catch (PortrayalException ex) {
                    renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
                }
            }
        }
        return dataRendered;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(final RenderingContext context, final SearchArea mask, final VisitFilter filter, List<Graphic> graphics) {

        if (!(context instanceof RenderingContext2D) ) return graphics;
        if (!item.isSelectable()) return graphics;
        if (!item.isVisible()) return graphics;

        final RenderingContext2D renderingContext = (RenderingContext2D) context;

        GenericName coverageName = null;
        try {
            coverageName = item.getResource().getIdentifier().orElse(null);
        } catch (DataStoreException ex) {
            //do nothing
        }
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(item.getStyle(),
                renderingContext.getSEScale(), coverageName,null);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.length == 0) {
            return graphics;
        }

        if (graphics == null) {
            graphics = new ArrayList<>();
        }
        final SearchAreaJ2D search = (mask instanceof SearchAreaJ2D) ? (SearchAreaJ2D) mask : new DefaultSearchAreaJ2D(mask);
        final ProjectedCoverage projectedCoverage = new ProjectedCoverage(item);

        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) item.getGraphicBuilder(GraphicJ2D.class);
        if (builder != null) {
            //this layer hasa special graphic rendering, use it instead of normal rendering
            final Collection<GraphicJ2D> gras = builder.createGraphics(item, canvas);
            for (final GraphicJ2D gra : gras) {
                graphics = gra.getGraphicAt(renderingContext, search, filter,graphics);
            }
            return graphics;
        }

        for (final CachedRule rule : rules) {
            for (final CachedSymbolizer symbol : rule.symbolizers()) {
                if (GO2Utilities.hit(projectedCoverage, symbol, renderingContext, search, filter)) {
                    graphics.add(projectedCoverage);
                    break;
                }
            }
        }

        return graphics;
    }

}
