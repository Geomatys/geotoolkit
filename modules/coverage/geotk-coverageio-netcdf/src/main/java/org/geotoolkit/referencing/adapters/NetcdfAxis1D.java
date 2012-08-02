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
import java.util.List;
import java.util.NoSuchElementException;

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
     * The values returned by {@link #getOrdinateRangeAt(int)}, cached when first computed.
     */
    private transient Range<?>[] ranges;

    /**
     * Creates a new {@code NetcdfAxis} object wrapping the given NetCDF coordinate axis.
     *
     * @param axis The NetCDF coordinate axis to wrap.
     * @param domain Dimensions of the coordinate system for which we are wrapping an axis, in NetCDF order.
     *        This is typically {@link ucar.nc2.dataset.CoordinateSystem#getDomain()}.
     */
    NetcdfAxis1D(final CoordinateAxis1D axis, final List<Dimension> domain) {
        super(axis);
        final int r = domain.size() - 1;
        iDim = r - domain.indexOf(axis.getDimension(0));
        if (iDim > r) {
            throw new NoSuchElementException(); // Should never happen.
        }
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
     * Returns the number of ordinates in the NetCDF axis. This method delegates to the
     * {@link CoordinateAxis1D#getShape(int)} method.
     *
     * @return The number or ordinates in the NetCDF axis.
     *
     * @since 3.15
     */
    @Override
    public int length() {
        return axis.getShape(0);
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
                    ranges[i] = new NumberRange<Double>(Double.class, (Double) c1, true, (Double) c2, maxInclusive);
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
        final double source = gridPts[srcOff + iDim];
        try {
            final int lower = (int) source;
            final CoordinateAxis1D axis = (CoordinateAxis1D) this.axis;
            double value = axis.getCoordValue(lower);
            final double delta = source - lower;
            if (delta != 0) {
                value += delta * (axis.getCoordValue(lower + 1) - value);
            }
            return value;
        } catch (IndexOutOfBoundsException e) {
            throw new TransformException(Errors.format(Errors.Keys.ILLEGAL_COORDINATE_$1, source), e);
        }
    }
}
