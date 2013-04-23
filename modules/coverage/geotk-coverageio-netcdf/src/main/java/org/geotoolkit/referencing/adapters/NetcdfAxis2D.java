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

import java.util.Map;
import java.util.LinkedHashMap;
import javax.imageio.IIOException;

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
     * Lengths along the two first axes.
     */
    private final int iNum, jNum;

    /**
     * Creates a new {@code NetcdfAxis} object wrapping the given NetCDF coordinate axis.
     *
     * @param axis The NetCDF coordinate axis to wrap.
     * @param domain Dimensions of the variable for which we are wrapping an axis, in natural order
     *        (reverse of NetCDF order). They are often, but not necessarily, the coordinate system
     *        dimensions.
     * @throws IIOException If the axis domain is not contained in the given list of dimensions.
     */
    NetcdfAxis2D(final CoordinateAxis2D axis, final Dimension[] domain) throws IIOException {
        super(axis);
        iDim = indexOfDimension(axis, 0, domain);
        jDim = indexOfDimension(axis, 1, domain);
        iNum = axis.getShape(0);
        jNum = axis.getShape(1);
    }

    /**
     * Creates a copy of the given axis with only different {@link #iDim} and {@link #jDim} values.
     */
    private NetcdfAxis2D(final NetcdfAxis2D axis, final int iDim, final int jDim) {
        super(axis);
        this.iDim = iDim;
        this.jDim = jDim;
        this.iNum = axis.iNum;
        this.jNum = axis.jNum;
    }

    /**
     * Returns a NetCDF axis which is part of the given domain.
     * This method does not modify this axis. Instead, it will create a new one if necessary.
     *
     * @param domain The new domain in <em>natural</em> order (<strong>not</strong> the NetCDF order).
     * @throws IIOException If the given domain does not contains this axis domain.
     */
    @Override
    final NetcdfAxis forDomain(final Dimension[] domain) throws IIOException {
        final int i0 = indexOfDimension(axis, 0, domain);
        final int i1 = indexOfDimension(axis, 1, domain);
        return (i0 == iDim && i1 == jDim) ? this : new NetcdfAxis2D(this, i0, i1);
    }

    /**
     * Returns the source dimensions of this axis, associated to the indices in source coordinates.
     */
    @Override
    final Map<Integer,Dimension> getDomain() {
        final Map<Integer,Dimension> domain = new LinkedHashMap<Integer,Dimension>(4);
        domain.put(iDim, axis.getDimension(0));
        domain.put(jDim, axis.getDimension(1));
        return domain;
    }

    /**
     * Returns the number of source ordinate values along the given <em>source</em> dimension,
     * or -1 if this axis is not for the given dimension.
     */
    @Override
    final int length(final int sourceDimension) {
        if (sourceDimension == iDim) return iNum;
        if (sourceDimension == jDim) return jNum;
        return super.length(sourceDimension);
    }

    /**
     * Interpolates the ordinate values at cell center from the given grid coordinate.
     */
    @Override
    public double getOrdinateValue(final double[] gridPts, final int srcOff) throws TransformException {
        final double x = gridPts[srcOff + iDim];
        final double y = gridPts[srcOff + jDim];
        try {
            /*
             * Casting to (int) round all values between -1 and 1 toward 0, which is exactly what we
             * need in this particular case. We want -0.5 to be rounded toward zero because envelope
             * transformations will often apply a 0.5 shift on the pixel coordinates, thus resulting
             * in some -0.5 values. For such cases, a small extrapolation will be applied.
             */
            final int i = (int) x;
            final int j = (int) y;
            final CoordinateAxis2D axis = (CoordinateAxis2D) this.axis;
            double value = axis.getCoordValue(i, j);
            double dx = x - i;
            double dy = y - j;
            if (dx != 0 || dy != 0) {
                int i1 = i+1; if (i1 == iNum) {i1 -= 2; dx = -dx;}
                int j1 = j+1; if (j1 == jNum) {j1 -= 2; dy = -dy;}
                double v2   =  axis.getCoordValue(i,  j1);
                v2    += dx * (axis.getCoordValue(i1, j1) - v2);
                value += dx * (axis.getCoordValue(i1, j ) - value);
                value += dy * (v2 - value);
            }
            return value;
        } catch (IndexOutOfBoundsException e) {
            throw new TransformException(Errors.format(Errors.Keys.ILLEGAL_COORDINATE_1,
                    "(" + x + ", " + y + ')'), e);
        }
    }
}
