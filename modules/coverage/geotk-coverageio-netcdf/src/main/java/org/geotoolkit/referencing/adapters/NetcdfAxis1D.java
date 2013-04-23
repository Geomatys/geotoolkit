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

import java.util.Date;
import java.util.Map;
import java.util.Collections;
import javax.imageio.IIOException;

import ucar.nc2.Dimension;
import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dataset.CoordinateAxis1DTime;

import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.util.Range;
import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.geotoolkit.lang.Workaround;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.math.MathFunctions.xorSign;


/**
 * Wraps a NetCDF {@link CoordinateAxis1D} as an implementation of GeoAPI interfaces.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 3.08)
 * @module
 */
final class NetcdfAxis1D extends NetcdfAxis implements DiscreteCoordinateSystemAxis { // Parameterized type is impractical here.
    /**
     * The index of the ordinate values to fetch in a source coordinate.
     */
    final int iDim;

    /**
     * Number of ordinate values in this axis.
     */
    private final int length;

    /**
     * The values returned by {@link #getOrdinateRangeAt(int)}, cached when first computed.
     */
    private transient Range<?>[] ranges;

    /**
     * Creates a copy of the given axis with only a different {@link #iDim} value.
     */
    private NetcdfAxis1D(final NetcdfAxis1D axis, final int iDim) {
        super(axis);
        this.iDim   = iDim;
        this.length = axis.length;
        synchronized (axis) {
            this.ranges = axis.ranges;
        }
    }

    /**
     * Creates a new {@code NetcdfAxis} object wrapping the given NetCDF coordinate axis.
     *
     * @param axis The NetCDF coordinate axis to wrap.
     * @param domain Dimensions of the variable for which we are wrapping an axis, in natural order
     *        (reverse of NetCDF order). They are often, but not necessarily, the coordinate system
     *        dimensions.
     * @throws IIOException If the axis domain is not contained in the given list of dimensions.
     */
    NetcdfAxis1D(final CoordinateAxis1D axis, final Dimension[] domain) throws IIOException {
        super(axis);
        iDim = indexOfDimension(axis, 0, domain);
        length = axis.getShape(0);
    }

    /**
     * Returns the type of ordinate values.
     *
     * @since 3.20
     */
    @Override
    public Class<?> getElementType() {
        if (axis instanceof CoordinateAxis1DTime) {
            return Date.class;
        } else if (axis.isNumeric()) {
            return Double.class;
        } else {
            return String.class;
        }
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
        final int dim = indexOfDimension(axis, 0, domain);
        return (dim == iDim) ? this : new NetcdfAxis1D(this, dim);
    }

    /**
     * Returns the source dimension of this axis, associated to the index in source coordinates.
     */
    @Override
    final Map<Integer,Dimension> getDomain() {
        return Collections.singletonMap(iDim, axis.getDimension(0));
    }

    /**
     * Returns the number of ordinates in the NetCDF axis. This method delegates to the
     * {@link CoordinateAxis1D#getShape(int)} method.
     *
     * @return The number or ordinates in the NetCDF axis.
     *
     * @since 3.15
     */
    @Override
    public int length() {
        return length;
    }

    /**
     * Returns the number of source ordinate values along the given <em>source</em> dimension,
     * or -1 if this axis is not for the given dimension.
     */
    @Override
    final int length(final int sourceDimension) {
        if (sourceDimension == iDim) return length;
        return super.length(sourceDimension);
    }

    /**
     * Returns the ordinate value at the given index. This method delegates to the first
     * suitable NetCDF method in the following list:
     * <p>
     * <ul>
     *   <li>{@link CoordinateAxis1DTime#getTimeDate(int)} if the wrapped
     *       axis is an instance of {@code CoordinateAxis1DTime}.</li>
     *   <li>{@link CoordinateAxis1D#getCoordValue(int)} if the axis
     *       {@linkplain CoordinateAxis1D#isNumeric() is numeric}.</li>
     *   <li>{@link CoordinateAxis1D#getCoordName(int)} otherwise.</li>
     * </ul>
     *
     * @since 3.15
     */
    @Override
    public Comparable<?> getOrdinateAt(final int index) throws IndexOutOfBoundsException {
        final CoordinateAxis1D axis = (CoordinateAxis1D) this.axis;
        if (axis instanceof CoordinateAxis1DTime) {
            return ((CoordinateAxis1DTime) axis).getCalendarDate(index).toDate();
        } else if (axis.isNumeric()) {
            return axis.getCoordValue(index);
        } else {
            return axis.getCoordName(index);
        }
    }

    /**
     * Returns the range of ordinate values at the given index.
     *
     * @since 3.15
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    @Workaround(library="NetCDF", version="4.1")
    public synchronized Range<?> getOrdinateRangeAt(final int index)
            throws IndexOutOfBoundsException, UnsupportedOperationException
    {
        Range<?>[] ranges = this.ranges;
        if (ranges == null) {
            final CoordinateAxis1D axis = (CoordinateAxis1D) this.axis;
            final double[] bound1 = axis.getBound1();
            final double[] bound2 = axis.getBound2();
            final int length = Math.min(bound1.length, bound2.length);
            /*
             * Workaround for what is apparently a NetCDF 4.1 bug.
             */
            if (length == 1 && bound1[0] == 0) {
                bound1[0] = bound2[0] = axis.getCoordValue(0);
            }
            /*
             * Computes the conversion factor from numerical values to milliseconds.
             */
            double toMillis = 0;
            final CoordinateAxis1DTime timeAxis;
            if (axis instanceof CoordinateAxis1DTime) {
                timeAxis = (CoordinateAxis1DTime) axis;
                toMillis = timeAxis.getCalendarDateRange().getDurationInSecs();
                if (toMillis > 0) {
                    toMillis = toMillis * 1000 / (axis.getMaxValue() - axis.getMinValue());
                }
                ranges = new DateRange[length];
            } else {
                timeAxis = null;
                ranges = new NumberRange<?>[length];
            }
            /*
             * Creates the ranges.
             */
            Comparable<?> previous = null;
            for (int i=0; i<length; i++) {
                final double b1 = bound1[i];
                final double b2 = bound2[i];
                Comparable<?> c1, c2;
                if (timeAxis != null) {
                    final long time = timeAxis.getCalendarDate(i).toDate().getTime();
                    long t1 = time; // Usually the minimum value, but not necessarily.
                    long t2 = time; // Usually the maximum value, but not necessarily.
                    if (toMillis > 0) {
                        final double ordinate = axis.getCoordValue(i);
                        t1 -= Math.round((ordinate - b1) * toMillis);
                        t2 += Math.round((b2 - ordinate) * toMillis);
                    }
                    c1 = new Date(t1);
                    c2 = new Date(t2);
                } else {
                    c1 = b1;
                    c2 = b2;
                }
                // Reuse the instance of the previous iteration.
                if (c1.equals(previous)) {
                    c1 = previous;
                }
                previous = c2;
                // Ensure that (c1,c2) are sorted.
                if (((Comparable) c2).compareTo(c1) < 0) {
                    final Comparable<?> tmp = c1;
                    c1 = c2;
                    c2 = tmp;
                }
                // Store the result.
                final boolean maxInclusive = c1.equals(c2);
                if (timeAxis != null) {
                    ranges[i] = new DateRange((Date) c1, true, (Date) c2, maxInclusive);
                } else {
                    ranges[i] = new NumberRange<>(Double.class, (Double) c1, true, (Double) c2, maxInclusive);
                }
            }
            this.ranges = ranges; // Only on success.
        }
        return ranges[index];
    }

    /**
     * Interpolates the ordinate values at cell center from the given grid coordinate.
     */
    @Override
    public double getOrdinateValue(final double[] gridPts, final int srcOff) throws TransformException {
        final double x = gridPts[srcOff + iDim];
        try {
            /*
             * Casting to (int) round all values between -1 and 1 toward 0, which is exactly what we
             * need in this particular case. We want -0.5 to be rounded toward zero because envelope
             * transformations will often apply a 0.5 shift on the pixel coordinates, thus resulting
             * in some -0.5 values. For such cases, a small extrapolation will be applied.
             */
            final int i = (int) x;
            final CoordinateAxis1D axis = (CoordinateAxis1D) this.axis;
            double value = axis.getCoordValue(i);
            double delta = x - i;
            if (delta != 0 && length != 1) {
                int i1 = i + 1;
                if (i1 == length) {
                    i1 -= 2;
                    delta = -delta;
                }
                value += delta * (axis.getCoordValue(i1) - value);
            }
            return value;
        } catch (IndexOutOfBoundsException e) {
            throw new TransformException(Errors.format(Errors.Keys.ILLEGAL_COORDINATE_1, x), e);
        }
    }

    /**
     * The reverse of {@link #getOrdinateValue(double[], int)}, finding the index of a given
     * ordinate value. The returned values are bounded to the range of valid grid indices.
     *
     * @since 3.21
     */
    @Override
    void getOrdinateIndex(final double ordinate, final double[] gridPts, final int dstOff) {
        if (Double.isNaN(ordinate)) {
            gridPts[dstOff + iDim] = ordinate;
            return;
        }
        final CoordinateAxis1D axis = (CoordinateAxis1D) this.axis;
        final int i = axis.findCoordElementBounded(ordinate);
        double gridOrdinate = i;
        if (length > 1 && (axis.isRegular() || axis.isContiguous())) {
            final double atGridPoint = axis.getCoordValue(i);
            final double delta = ordinate - atGridPoint;
            if (delta != 0) {
                double other;
                double sign;
                if (i != length-1) {
                    other = axis.getCoordValue(i+1);
                    sign  = 1;
                    if (xorSign(ordinate - other, delta) >= 0) {
                        /*
                         * If we enter in this block, then the 'other' value is on the same side
                         * than 'atGridPoint' relative to the given 'ordinate' value. This means
                         * than using this 'other' value would be an extrapolation. Since we want
                         * interpolations, use the value on the opposite side instead. This is not
                         * necessarily the nearest value.
                         */
                        sign = 0; // This will prevent extrapolation if there is no opposite side.
                        if (i != 0) {
                            other = axis.getCoordValue(i-1);
                            sign  = -1;
                        }
                    }
                } else {
                    other = axis.getCoordValue(i-1);
                    sign  = (xorSign(ordinate - other, delta) < 0) ? -1 : 0;
                    // The above check is for preventing extrapolations.
                }
                gridOrdinate += sign * delta / (other - atGridPoint);
            }
        }
        gridPts[dstOff + iDim] = gridOrdinate;
    }
}
