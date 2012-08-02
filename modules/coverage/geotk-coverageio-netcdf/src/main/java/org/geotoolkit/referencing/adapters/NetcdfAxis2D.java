/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.referencing.adapters;

import java.util.List;
import java.util.NoSuchElementException;

import ucar.nc2.Dimension;
import ucar.nc2.dataset.CoordinateAxis2D;

import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.resources.Errors;


/**
 * Wraps a NetCDF {@link CoordinateAxis2D} as an implementation of GeoAPI interfaces.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 3.08)
 * @module
 */
final class NetcdfAxis2D extends NetcdfAxis {
    /**
     * The index of the ordinate values to fetch in a source coordinate.
     */
    private final int iDim, jDim;

    /**
     * Creates a new {@code NetcdfAxis} object wrapping the given NetCDF coordinate axis.
     *
     * @param axis The NetCDF coordinate axis to wrap.
     * @param domain Dimensions of the coordinate system for which we are wrapping an axis, in NetCDF order.
     *        This is typically {@link ucar.nc2.dataset.CoordinateSystem#getDomain()}.
     */
    NetcdfAxis2D(final CoordinateAxis2D axis, final List<Dimension> domain) {
        super(axis);
        final int r = domain.size() - 1;
        iDim = r - domain.indexOf(axis.getDimension(0));
        jDim = r - domain.indexOf(axis.getDimension(1));
        if (iDim > r || jDim > r) {
            throw new NoSuchElementException(); // Should never happen.
        }
    }

    /**
     * Interpolates the ordinate values at cell center from the given grid coordinate.
     */
    @Override
    public double getOrdinateValue(final double[] gridPts, final int srcOff) throws TransformException {
        final double x = gridPts[srcOff + iDim];
        final double y = gridPts[srcOff + jDim];
        try {
            final int xlow = (int) x;
            final int ylow = (int) y;
            final CoordinateAxis2D axis = (CoordinateAxis2D) this.axis;
            double value = axis.getCoordValue(xlow, ylow);
            final double dx = x - xlow;
            final double dy = y - ylow;
            if (dx != 0 || dy != 0) {
                double v2   =  axis.getCoordValue(xlow,   ylow+1);
                v2    += dx * (axis.getCoordValue(xlow+1, ylow+1) - v2);
                value += dx * (axis.getCoordValue(xlow+1, ylow) - value);
                value += dy * (v2 - value);
            }
            return value;
        } catch (IndexOutOfBoundsException e) {
            throw new TransformException(Errors.format(Errors.Keys.ILLEGAL_COORDINATE_$1, x), e);
        }
    }
}
