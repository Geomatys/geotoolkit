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
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import javax.imageio.IIOException;
import javax.measure.unit.Unit;

import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.constants.CF;
import ucar.nc2.constants.CDM;
import ucar.nc2.constants.AxisType;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dataset.CoordinateAxis2D;

import org.opengis.util.GenericName;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.cs.RangeMeaning;
import org.opengis.referencing.operation.TransformException;

import org.apache.sis.util.CharSequences;
import org.apache.sis.measure.Units;

import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.resources.Errors;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Wraps a NetCDF {@link CoordinateAxis} as an implementation of GeoAPI interfaces.
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
public class NetcdfAxis extends NetcdfIdentifiedObject implements CoordinateSystemAxis {
    /**
     * The NetCDF coordinate axis wrapped by this {@code NetcdfAxis} instance.
     */
    final CoordinateAxis axis;

    /**
     * The unit, computed when first needed.
     */
    volatile Unit<?> unit;

    /**
     * Creates a copy of the given axis. Copy are normally not necessary since {@code NetcdfAxis}
     * is immutable. This constructor is provided only for subclasses that need to create almost
     * identical copies.
     */
    NetcdfAxis(final NetcdfAxis axis) {
        this.axis = axis.axis;
        this.unit = axis.unit;
    }

    /**
     * Creates a new {@code NetcdfAxis} object wrapping the given NetCDF coordinate axis.
     *
     * @param axis The NetCDF coordinate axis to wrap.
     */
    public NetcdfAxis(final CoordinateAxis axis) {
        ensureNonNull("axis", axis);
        this.axis = axis;
    }

    /**
     * Creates a new {@code NetcdfAxis} object wrapping the given NetCDF coordinate axis.
     *
     * @param axis The NetCDF coordinate axis to wrap.
     * @param domain Dimensions of the variable for which we are wrapping an axis, in natural order
     *        (reverse of NetCDF order). They are often, but not necessarily, the coordinate system
     *        dimensions.
     * @return The {@code NetcdfAxis} object wrapping the given axis.
     * @throws IIOException If the axis domain is not contained in the given list of dimensions.
     */
    static NetcdfAxis wrap(final CoordinateAxis axis, final Dimension[] domain) throws IIOException {
        if (axis instanceof CoordinateAxis1D) {
            return new NetcdfAxis1D((CoordinateAxis1D) axis, domain);
        }
        if (axis instanceof CoordinateAxis2D) {
            return new NetcdfAxis2D((CoordinateAxis2D) axis, domain);
        }
        return new NetcdfAxis(axis);
    }

    /**
     * Returns the index in the given {@code domain} array of the dimension equals to the given
     * axis dimension. This is a convenience method for subclasses.
     *
     * @param  axis   The axis for which to search the dimension in the {@code domain} array.
     * @param  index  The index of the axis dimension to search.
     * @param  domain The array in which to search for the axis dimension.
     * @return Index of the requested dimension in the {@code domain} array.
     * @throws IIOException If the dimension has not been found in the given array.
     */
    static int indexOfDimension(final CoordinateAxis axis, final int index, final Dimension[] domain)
            throws IIOException
    {
        final Dimension toSearch = axis.getDimension(index);
        for (int i=0; i<domain.length; i++) {
            if (toSearch.equals(domain[i])) {
                return i;
            }
        }
        final StringBuilder buffer = new StringBuilder(40);
        for (final Dimension dimension : domain) {
            if (buffer.length() != 0) {
                buffer.append(", ");
            }
            buffer.append(dimension.getName());
        }
        throw new IIOException(Errors.format(Errors.Keys.UNEXPECTED_AXIS_DOMAIN_2, axis.getShortName(), buffer));
    }

    /**
     * Returns the wrapped NetCDF axis.
     */
    @Override
    public CoordinateAxis delegate() {
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
     * Returns NetCDF axis standard name and long name, if available.
     */
    @Override
    public Collection<GenericName> getAlias() {
        String standardName = null;
        final List<GenericName> names = new ArrayList<>(2);
        Attribute attribute = axis.findAttributeIgnoreCase(CF.STANDARD_NAME);
        if (attribute != null) {
            standardName = attribute.getStringValue();
            if (standardName != null) {
                names.add(new NamedIdentifier(Citations.NETCDF_CF, standardName));
            }
        }
        attribute = axis.findAttributeIgnoreCase(CDM.LONG_NAME);
        if (attribute != null) {
            final String name = attribute.getStringValue();
            if (name != null && !name.equals(standardName)) {
                names.add(new NamedIdentifier(Citations.NETCDF, name));
            }
        }
        return names;
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
        String abbreviation = CharSequences.camelCaseToAcronym(name).toString().toLowerCase();
        if (abbreviation.startsWith("l")) {
            // Heuristic disambiguity.
            final int length = Math.min(9, name.length()); // 9 is the length of "longitude".
            int s = 0;
            while (++s != length && Character.isLetter(name.charAt(s)));
            final char prefix;
            if (name.regionMatches(true, 0, "longitude", 0, s)) {
                prefix = '\u03BB';
            } else if (name.regionMatches(true, 0, "latitude", 0, Math.min(8, s))) {
                prefix = '\u03C6';
            } else {
                return abbreviation;
            }
            final StringBuilder buffer = new StringBuilder(abbreviation);
            buffer.setCharAt(0, prefix);
            abbreviation = buffer.toString();
        }
        return abbreviation;
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
     * to {@link CoordinateAxis#getMinValue()}.
     *
     * @see CoordinateAxis#getMinValue()
     */
    @Override
    public double getMinimumValue() {
        return axis.getMinValue();
    }

    /**
     * Returns the axis maximal value. The default implementation delegates
     * to {@link CoordinateAxis#getMaxValue()}.
     *
     * @see CoordinateAxis#getMaxValue()
     */
    @Override
    public double getMaximumValue() {
        return axis.getMaxValue();
    }

    /**
     * Returns {@code null} since the range meaning is unspecified.
     */
    @Override
    public RangeMeaning getRangeMeaning() {
        return null;
    }

    /**
     * Returns {@code true} if the NetCDF axis is an instance of {@link CoordinateAxis1D} and
     * {@linkplain CoordinateAxis1D#isRegular() is regular}.
     *
     * {@note We do not allow overriding of this method, because callers assume that a
     *        value of <code>true</code> implies that the NetCDF axis is an instance of
     *        <code>CoordinateAxis1D</code>.}
     *
     * @return {@code true} if the NetCDF axis is regular.
     *
     * @see CoordinateAxis1D#isRegular()
     *
     * @since 3.20
     */
    final boolean isRegular() {
        return (axis instanceof CoordinateAxis1D) && ((CoordinateAxis1D) axis).isRegular();
    }

    /**
     * Returns the source dimension of this axis, or {@code null} if unknown. The dimensions are
     * stored in the values of the returned map. The keys are the indices at which the dimensions
     * are expected to be found in a source coordinates.
     * <p>
     * Note that the source indices are <strong>not</strong> the dimension in the coordinate system
     * (while "source" and "target" dimensions are often the same, they could also be different).
     * This method is not public in order to avoid confusion.
     *
     * @return The source dimensions associated to their indices as expected by a math transform
     *         from pixel indices to geodetic coordinates, or {@code null} if unknown.
     */
    Map<Integer,Dimension> getDomain() {
        return null;
    }

    /**
     * Returns a NetCDF axis which is part of the given domain.
     * This method does not modify this axis. Instead, it will create a new one if necessary.
     *
     * @param  domain The new domain in <em>natural</em> order (<strong>not</strong> the NetCDF order).
     * @return A NetCDF axis which is part of the given domain.
     * @throws IIOException If the given domain does not contains this axis domain.
     */
    NetcdfAxis forDomain(final Dimension[] domain) throws IIOException {
        return this;
    }

    /**
     * Returns the number of source ordinate values along the given <em>source</em> dimension,
     * or -1 if none. Note that the given argument is <strong>not</strong> the dimension in the
     * coordinate system (while "source" and "target" dimensions are often the same, they could
     * also be different). This method is not public in order to avoid confusion.
     *
     * {@note It would almost be possible to infer the length from the <code>Dimension</code> object
     * returned by <code>getDomain()</code>. However it would not work for "unlimited" dimensions,
     * so we still need this method.}
     *
     * @param  sourceDimension The source dimension in a math transform from pixel indices to
     *         geodetic coordinates.
     * @return Number of ordinate values in the given dimension, or -1 if unknown.
     */
    int length(final int sourceDimension) {
        return -1;
    }

    /**
     * Interpolates the ordinate value for the given grid coordinate. The {@code gridPts} array
     * shall contains a complete grid coordinate - not only the grid index value for this axis -
     * starting at index {@code srcOff}.
     * <p>
     * The interpolated ordinate value shall maps the
     * {@linkplain org.opengis.referencing.datum.PixelInCell#CELL_CENTER cell center}.
     * <p>
     * The default implementation throws an exception in all cases.
     * The actual implementation needs to be provided by subclasses.
     *
     * @param  gridPts An array containing grid coordinates.
     * @param  srcOff  Index of the first ordinate value in the {@code gridPts}.
     * @return The ordinate value of cell center interpolated from the given grid coordinate.
     * @throws TransformException If the ordinate value can not be computed.
     *
     * @since 3.20
     */
    public double getOrdinateValue(final double[] gridPts, final int srcOff) throws TransformException {
        throw new TransformException(Errors.format(Errors.Keys.UNSPECIFIED_TRANSFORM));
    }

    /**
     * The reverse of {@link #getOrdinateValue(double[], int)}, finding the index of a given
     * ordinate value.
     *
     * @todo This method is currently implemented only for the 1D-case. Generalization to the
     *       2D case would probably require a change in the method signature.
     *
     * @param ordinate The ordinate value to convert.
     * @param gridPts  The array where to store the grid index.
     * @param dstOff   Offset of the first ordinate to write in {@code gridPts}.
     *
     * @since 3.21
     */
    void getOrdinateIndex(final double ordinate, final double[] gridPts, final int dstOff) throws TransformException {
        throw new TransformException(Errors.format(Errors.Keys.NONINVERTIBLE_TRANSFORM));
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
}
