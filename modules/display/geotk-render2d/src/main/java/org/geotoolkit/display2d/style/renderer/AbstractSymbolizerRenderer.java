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
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.measure.Unit;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.apache.sis.portrayal.MapLayer;
import org.geotoolkit.renderer.ExceptionPresentation;
import org.geotoolkit.renderer.Presentation;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;

/**
 * Abstract symbolizer renderer, stores graphics2d and often used object in
 * final fields.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractSymbolizerRenderer<C extends CachedSymbolizer<? extends Symbolizer>> implements SymbolizerRenderer{

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.display2d.style.renderer");

    protected final SymbolizerRendererService service;
    protected final RenderingContext2D renderingContext;
    protected final RenderingHints hints;
    protected final Graphics2D g2d;
    protected final CanvasMonitor monitor;
    protected final C symbol;

    protected final Unit symbolUnit;
    protected final float coeff;
    protected final boolean dispGeom;
    protected final Expression geomPropertyName;

    public AbstractSymbolizerRenderer(final SymbolizerRendererService service, final C symbol, final RenderingContext2D context){
        this.service = service;
        this.symbol = symbol;
        this.renderingContext = context;
        this.g2d = renderingContext.getGraphics();
        this.monitor = renderingContext.getMonitor();
        this.hints = renderingContext.getRenderingHints();

        this.symbolUnit = symbol.getSource().getUnitOfMeasure();
        this.coeff = renderingContext.getUnitCoefficient(symbolUnit);
        this.dispGeom = (Units.POINT == symbolUnit);
        final Symbolizer symbolizer = symbol.getSource();
        this.geomPropertyName = symbolizer.getGeometry();
    }

    @Override
    public SymbolizerRendererService getService() {
        return service;
    }

    @Override
    public RenderingContext2D getRenderingContext() {
        return renderingContext;
    }

    /**
     * Obtain the presentation for given resource.Default implementation loops on each feature if resource is a FeatureSet.
     * If resource is an Aggregate, loops on each components and concatenate streams.
     *
     * @param layer
     * @param resource
     * @return Stream never null, can be empty
     * @throws BackingStoreException in stream iteration
     */
    @Override
    public Stream<Presentation> presentations(MapLayer layer, Resource resource) {
        if (resource instanceof FeatureSet) {
            /*
            Optimise case using envelopes filter and limited propery names.
            */
            final FeatureSet fs = (FeatureSet) resource;

            Set<String> names;
            if (GO2Utilities.mustPreserveAllProperties(getRenderingContext())) {
                names= null;
            } else {
                //extract the used names
                final org.geotoolkit.style.visitor.ListingPropertyVisitor visitor = new org.geotoolkit.style.visitor.ListingPropertyVisitor();
                names = new HashSet<>();
                symbol.getSource().accept(visitor, names);
                if (names.contains("*")) {
                    //we need all properties
                    names = null;
                }
            }

            //calculate max symbol size, to expand search envelope.
            double symbolsMargin = symbol.getMargin(null, renderingContext);

            if (Double.isNaN(symbolsMargin) || Double.isInfinite(symbolsMargin)) {
                //symbol margin can not be pre calculated, expect a max of 300pixels
                symbolsMargin = 300f;
            }
            if (symbolsMargin > 0) {
                final double scale = AffineTransforms2D.getScale(renderingContext.getDisplayToObjective());
                symbolsMargin = scale * symbolsMargin;
            }

            //optimize
            final Rule rule = GO2Utilities.STYLE_FACTORY.rule(symbol.getSource());
            final Query query;
            try {
                query = RenderingRoutines.prepareQuery(getRenderingContext(), fs, layer, names, Arrays.asList(rule), symbolsMargin);
            } catch (PortrayalException ex) {
                return Stream.of(new ExceptionPresentation(layer, resource, null, ex));
            }

            try {
                return fs.subset(query).features(false).flatMap(new Function<Feature, Stream<Presentation>>() {
                    @Override
                    public Stream<Presentation> apply(Feature t) {
                        return presentations(layer, t);
                    }
                });
            } catch (DataStoreException ex) {
                return Stream.of(new ExceptionPresentation(layer, resource, null, ex));
            }

        }
        return SymbolizerRenderer.super.presentations(layer, resource);
    }

    ////////////////////////////////////////////////////////////////////////////
    // usefull methods /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Check that the resolution asked match at least the span of the envelope.
     * @param resolution : resolution to check
     * @param bounds : reference envelope
     * @return resolution changed if necessary
     */
    protected static double[] checkResolution(final double[] resolution, final Envelope bounds) {
        final int minOrdi = CRSUtilities.firstHorizontalAxis(bounds.getCoordinateReferenceSystem());
        double span0 = bounds.getSpan(minOrdi);
        double span1 = bounds.getSpan(minOrdi + 1);

        if(resolution[0] > span0) resolution[0] = span0;
        if(resolution[1] > span1) resolution[1] = span1;

        return resolution;
    }

}
