/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.referencing.dggs.internal.shared;

import org.apache.sis.referencing.datum.DatumOrEnsemble;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.Ellipsoid;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractDiscreteGlobalGridSystem implements DiscreteGlobalGridSystem {

    protected final CoordinateReferenceSystem crs;
    protected final double surfaceArea;

    public AbstractDiscreteGlobalGridSystem(CoordinateReferenceSystem crs) {
        this.crs = crs;

        final GeographicCRS gcrs = (GeographicCRS) crs;
        final Ellipsoid ellipsoid = DatumOrEnsemble.asDatum(gcrs).getEllipsoid();
        final double semiMajorAxis = ellipsoid.getSemiMajorAxis();
        final double semiMinorAxis = ellipsoid.getSemiMinorAxis();
        final double r = (semiMajorAxis + semiMinorAxis) / 2;
        surfaceArea = 4.0 * Math.PI * r * r;
    }

    @Override
    public final CoordinateReferenceSystem getCrs() {
        return crs;
    }

    @Override
    public final double getCelestialBodySurface() {
        return surfaceArea;
    }

}
