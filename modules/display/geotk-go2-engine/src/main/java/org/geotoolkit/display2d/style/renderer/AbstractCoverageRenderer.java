/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import java.util.Iterator;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.opengis.geometry.Envelope;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCoverageRenderer<S extends Symbolizer, C extends CachedSymbolizer<S>> implements SymbolizerRenderer<S,C> {

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(ProjectedFeature graphic, C symbol, RenderingContext2D context) throws PortrayalException{
        //nothing to portray on features
    }

    @Override
    public void portray(Iterator<ProjectedFeature> graphics, C symbol, RenderingContext2D context) {
        //nothing to portray on features
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(ProjectedFeature feature, C symbol,
            RenderingContext2D context, SearchAreaJ2D mask, VisitFilter filter) {
        //nothing to hit on a feature with coverage symbolizer
        return false;
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
