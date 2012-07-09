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
import javax.measure.unit.Unit;

import ucar.nc2.constants.CF;
import ucar.nc2.constants.AxisType;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dataset.CoordinateAxis1DTime;

import org.opengis.util.InternationalString;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.cs.RangeMeaning;

import org.geotoolkit.util.Range;
import org.geotoolkit.util.Strings;
import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.geotoolkit.lang.Workaround;
import org.geotoolkit.measure.Units;

import static org.geotoolkit.util.ArgumentChecks.ensureNonNull;


/**
 * Wraps a NetCDF {@link CoordinateAxis1D} as an implementation of GeoAPI interfaces.
 * <p>
 * {@code NetcdfAxis} is a <cite>view</cite>: every methods in this class delegate their work to the
 * wrapped NetCDF axis. Consequently any change in the wrapped axis is immediately reflected in this
 * {@code NetcdfAxis} instance. However users are encouraged to not change the wrapped axis after
 * construction, since GeoAPI referencing objects are expected to be immutable.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.08
 * @module
 */
public class NetcdfAxis extends NetcdfIdentifiedObject implements CoordinateSystemAxis,
        DiscreteCoordinateSystemAxis // Parameterized type is impractical here.
{
    /**
     * The NetCDF coordinate axis wrapped by this {@code NetcdfAxis} instance.
     */
    private final CoordinateAxis1D axis;

    /**
     * The unit, computed when first needed.
     */
    volatile Unit<?> unit;

    /**
     * The values returned by {@link #getOrdinateRangeAt(int)}, cached when first computed.
     */
    private transient Range<?>[] ranges;

    /**
     * Creates a new {@code NetcdfAxis} object wrapping the given NetCDF coordinate axis.
     *
     * @param axis The NetCDF coordinate axis to wrap.
     */
    public NetcdfAxis(final CoordinateAxis1D axis) {
        ensureNonNull("axis", axis);
        this.axis = axis;
    }

    /**
     * Returns the wrapped NetCDF axis.
     */
    @Override
    public CoordinateAxis1D delegate() {
        return axis;
    }

    /**
     * Returns the axis name. The default implementation delegates to
     * {@link CoordinateAxis1D#getShortName()}.
     *
     * @see CoordinateAxis1D#getShortName()
     */
    @Override
    public String getCode() {
        return axis.getShortName();
    }

    /**
     * Returns the axis abbreviation. The default implementation returns
     * an acronym of the value returned by {@link CoordinateAxis1D#getShortName()}.
     *
     * @see CoordinateAxis1D#getShortName()
     */
    @Override
    public String getAbbreviation() {
        final String name = axis.getShortName().trim();
        if (name.equalsIgnoreCase("longitude")) return "\u03BB";
        if (name.equalsIgnoreCase("latitude"))  return "\u03C6";
        return Strings.camelCaseToAcronym(name).toLowerCase();
    }

    /**
     * Returns the axis direction. The default implementation delegates to
     * {@link #getDirection(CoordinateAxis)}.
     *
     * @see CoordinateAxis1D#getAxisType()
     * @see CoordinateAxis1D#getPositive()
     */
    @Override
    public AxisDirection getDirection() {
        return getDirection(axis);
    }

    /**
     * Returns the direction of the given axis. This method infers the direction from
     * {@link CoordinateAxis#getAxisType()} and {@link CoordinateAxis#getPositive()}.
     * If the direction can not be determined, then this method returns
     * {@link AxisDirection#OTHER}.
     *
     * @param  axis The axis for which to get the direction.
     * @return The direction of the given axis.
     */
    public static AxisDirection getDirection(final CoordinateAxis axis) {
        final AxisType type = axis.getAxisType();
        final boolean down = CF.POSITIVE_DOWN.equals(axis.getPositive());
        if (type != null) {
            switch (type) {
                case Time: return down ? AxisDirection.PAST : AxisDirection.FUTURE;
                case Lon:
                case GeoX: return down ? AxisDirection.WEST : AxisDirection.EAST;
                case Lat:
                case GeoY: return down ? AxisDirection.SOUTH : AxisDirection.NORTH;
                case Pressure:
                case Height:
                case GeoZ: return down ? AxisDirection.DOWN : AxisDirection.UP;
            }
        }
        return AxisDirection.OTHER;
    }

    /**
     * Returns the axis minimal value. The default implementation delegates
     * to {@link CoordinateAxis1D#getMinValue()}.
     *
     * @see CoordinateAxis1D#getMinValue()
     */
    @Override
    public double getMinimumValue() {
        return axis.getMinValue();
    }

    /**
     * Returns the axis maximal value. The default implementation delegates
     * to {@link CoordinateAxis1D#getMaxValue()}.
     *
     * @see CoordinateAxis1D#getMaxValue()
     */
    @Override
    public double getMaximumValue() {
        return axis.getMaxValue();
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
        if (axis instanceof CoordinateAxis1DTime) {
            if (false) {
                // Replacement of the following deprecated method call. Not yet used,
                // because we wait for the ucar.nc2.time API to be published as public
                // API. Maybe their API will evolve.
                return ((CoordinateAxis1DTime) axis).getCalendarDate(index).toDate();
            }
            return ((CoordinateAxis1DTime) axis).getTimeDate(index);
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
                toMillis = timeAxis.getDateRange().getDuration().getValueInSeconds();
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
                    final long time = timeAxis.getTimeDate(i).getTime(); // See getOrdinateAt(i)
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
     * Returns {@code null} since the range meaning is unspecified.
     */
    @Override
    public RangeMeaning getRangeMeaning() {
        return null;
    }

    /**
     * Returns the units as a string. If the axis direction or the time epoch
     * was appended to the units, then this part of the string is removed.
     */
    private String getUnitsString() {
        String symbol = axis.getUnitsString();
        if (symbol != null) {
            int i = symbol.lastIndexOf('_');
            if (i > 0) {
                final String direction = getDirection().name();
                if (symbol.regionMatches(true, i+1, direction, 0, direction.length())) {
                    symbol = symbol.substring(0, i).trim();
                }
            }
            i = symbol.indexOf(" since ");
            if (i > 0) {
                symbol = symbol.substring(0, i);
            }
            symbol = symbol.trim();
        }
        return symbol;
    }

    /**
     * Returns the units, or {@code null} if unknown.
     *
     * @see CoordinateAxis1D#getUnitsString()
     * @see Units#valueOf(String)
     */
    @Override
    public Unit<?> getUnit() {
        Unit<?> unit = this.unit;
        if (unit == null) {
            final String symbol = getUnitsString();
            if (symbol != null) try {
                this.unit = unit = Units.valueOf(symbol);
            } catch (IllegalArgumentException e) {
                // TODO: use Unit library in order to parse this kind of units.
                // For now just report that the unit is unknown, which is compatible
                // with the method contract.
            }
        }
        return unit;
    }

    /**
     * Returns the NetCDF description, or {@code null} if none.
     * The default implementation delegates to {@link CoordinateAxis1D#getDescription()}.
     *
     * @see CoordinateAxis1D#getDescription()
     */
    @Override
    public InternationalString getRemarks() {
        final String description = axis.getDescription();
        if (description != null) {
            return new SimpleInternationalString(description);
        }
        return super.getRemarks();
    }
}
