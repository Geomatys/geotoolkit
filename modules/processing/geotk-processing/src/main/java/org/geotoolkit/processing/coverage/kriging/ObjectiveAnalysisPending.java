/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *    (C) OSGEO
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
package org.geotoolkit.processing.coverage.kriging;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.opengis.metadata.spatial.PixelOrientation;

/**
 * Computes values at the location of grid cells from a set of values at random locations.
 * This class is typically used for computing values on a regular grid (the output) from a
 * set of values at random locations (the input). However the class can also be used for
 * creating non-regular grids. For creating a non-regular grid, user should subclass
 * {@code ObjectiveAnalysis} and override the {@link #getOutputLocation getOutputLocation(...)}
 * method.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Howard Freeland (MPO, for algorithmic inspiration)
 * @author Remi Marechal (Geomatys, for pending adaptation).
 * @version 3.09
 *
 * @since 3.09 (derived from 1.0)
 * @module
 */
public class ObjectiveAnalysisPending extends org.geotoolkit.math.ObjectiveAnalysis {

    /**
     * An arbitrary scale factor applied in the distance computed by {@link #correlation(double)}.
     * This is a hack for allowing the code to work with different CRS. Do not on this hack,
     * it may be suppressed in future versions.
     */
    private double scaleHack = 1;

    /**
     * Creates a new instance for interpolating values in the given region.
     *
     * @param gridRegion The grid bounding box. The maximal ordinates are inclusive.
     * @param size The number of grid cells along the <var>x</var> and <var>y</var> axes.
     */
    public ObjectiveAnalysisPending(final Rectangle2D gridRegion, final Dimension size) {
        super(gridRegion, size.width, size.height, PixelOrientation.CENTER);
    }

    /**
     * Sets an arbitrary scale factor to be applied in the distance computed by {@link #correlation(double)}.
     * This is a hack for allowing the code to work with different CRS. Do not rely on this hack,
     * it may be suppressed in future versions.
     */
    public void setScaleFactor(final double scale) {
        if (!(scale > 0)) {
            throw new IllegalArgumentException();
        }
        this.scaleHack = scale;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected double correlation(final Point2D.Double P1, final Point2D.Double P2) {
        double distance = Math.hypot(P1.x - P2.x, P1.y - P2.y);
        distance = distance / scaleHack - 1./150; // Similar to the basic program DISPWX
        if (distance < 0) {
            return 1 - 15*distance;
        }
        return Math.exp(-distance * distance);
    }
}
