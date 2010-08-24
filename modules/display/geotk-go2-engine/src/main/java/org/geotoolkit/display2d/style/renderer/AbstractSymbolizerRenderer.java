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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.util.logging.Logging;

import org.opengis.geometry.Envelope;
import org.opengis.style.Symbolizer;

/**
 * Abstract symbolizer renderer, stores graphics2d and often used object in
 * final fields.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractSymbolizerRenderer<C extends CachedSymbolizer<? extends Symbolizer>> implements SymbolizerRenderer{

    protected static final Logger LOGGER = Logging.getLogger(AbstractSymbolizerRenderer.class);

    protected final RenderingContext2D renderingContext;
    protected final RenderingHints hints;
    protected final Graphics2D g2d;
    protected final CanvasMonitor monitor;
    protected final C symbol;

    protected final Unit symbolUnit;
    protected final float coeff;
    protected final boolean dispGeom;
    protected final String geomPropertyName;

    public AbstractSymbolizerRenderer(C symbol, RenderingContext2D context){
        this.symbol = symbol;
        this.renderingContext = context;
        this.g2d = renderingContext.getGraphics();
        this.monitor = renderingContext.getMonitor();
        this.hints = renderingContext.getRenderingHints();

        this.symbolUnit = symbol.getSource().getUnitOfMeasure();
        this.coeff = renderingContext.getUnitCoefficient(symbolUnit);
        this.dispGeom = (NonSI.PIXEL == symbolUnit);
        this.geomPropertyName = symbol.getSource().getGeometryPropertyName();

    }

    @Override
    public void portray(Iterator<ProjectedFeature> graphics) throws PortrayalException {
        while(graphics.hasNext()){
            if(monitor.stopRequested()) return;
            portray(graphics.next());
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // usefull methods /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Chack that the resolution asked match at least the span of the envelope.
     * @param resolution : resolution to check
     * @param bounds : reference envelope
     * @return resolution changed if necessary
     */
    protected static double[] checkResolution(double[] resolution, Envelope bounds) {
        double span0 = bounds.getSpan(0);
        double span1 = bounds.getSpan(1);

        if(resolution[0] > span0) resolution[0] = span0;
        if(resolution[1] > span1) resolution[1] = span1;

        return resolution;
    }

}
