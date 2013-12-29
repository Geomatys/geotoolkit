/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.cs;

import java.util.Map;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.ConversionException;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlTransient;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.geometry.MismatchedDimensionException;
import org.apache.sis.measure.Units;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.internal.referencing.AxisDirections;
import org.apache.sis.util.ComparisonMode;

import static java.util.Collections.singletonMap;
import static org.geotoolkit.referencing.cs.AbstractCS.name;


/**
 * A two- or three-dimensional coordinate system in which position is specified by geodetic
 * latitude, geodetic longitude, and (in the three-dimensional case) ellipsoidal height. An
 * {@code EllipsoidalCS} shall have two or three {@linkplain #getAxis axis}.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CRS type(s)</TH></TR>
 * <TR><TD>
 *   {@link org.geotoolkit.referencing.crs.DefaultGeographicCRS  Geographic},
 *   {@link org.geotoolkit.referencing.crs.DefaultEngineeringCRS Engineering}
 * </TD></TR></TABLE>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@XmlTransient
public class DefaultEllipsoidalCS extends org.apache.sis.referencing.cs.DefaultEllipsoidalCS {
    /**
     * A two-dimensional ellipsoidal CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEODETIC_LONGITUDE geodetic longitude}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEODETIC_LATITUDE geodetic latitude}</var>
     * axis in decimal degrees.
     */
    public static final DefaultEllipsoidalCS GEODETIC_2D = new DefaultEllipsoidalCS(
                    name(Vocabulary.Keys.GEODETIC_2D),
                    DefaultCoordinateSystemAxis.GEODETIC_LONGITUDE,
                    DefaultCoordinateSystemAxis.GEODETIC_LATITUDE);

    /**
     * A three-dimensional ellipsoidal CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEODETIC_LONGITUDE geodetic longitude}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEODETIC_LATITUDE geodetic latitude}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#ELLIPSOIDAL_HEIGHT ellipsoidal height}</var>
     * axis.
     */
    public static final DefaultEllipsoidalCS GEODETIC_3D = new DefaultEllipsoidalCS(
                    name(Vocabulary.Keys.GEODETIC_3D),
                    DefaultCoordinateSystemAxis.GEODETIC_LONGITUDE,
                    DefaultCoordinateSystemAxis.GEODETIC_LATITUDE,
                    DefaultCoordinateSystemAxis.ELLIPSOIDAL_HEIGHT);

    /**
     * The axis number for longitude, latitude and height.
     * Will be constructed only when first needed.
     */
    private transient int longitudeAxis, latitudeAxis, heightAxis;

    /**
     * The unit converters for longitude, latitude and height.
     * Will be constructed only when first needed.
     */
    private transient UnitConverter longitudeConverter, latitudeConverter, heightConverter;

    /**
     * A coordinate system equivalent to this one, except for a shift in the range of longitude
     * values. This field is computed by {@link #shiftAxisRange(AxisRangeType)} when first
     * needed.
     *
     * @since 3.20
     */
    private transient DefaultEllipsoidalCS shifted;

    /**
     * Constructs a new coordinate system with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param cs The coordinate system to copy.
     *
     * @since 2.2
     */
    public DefaultEllipsoidalCS(final EllipsoidalCS cs) {
        super(cs);
    }

    /**
     * Constructs a two-dimensional coordinate system from a name.
     *
     * @param name  The coordinate system name.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     */
    public DefaultEllipsoidalCS(final String               name,
                                final CoordinateSystemAxis axis0,
                                final CoordinateSystemAxis axis1)
    {
        super(singletonMap(NAME_KEY, name), axis0, axis1);
    }

    /**
     * Constructs a three-dimensional coordinate system from a name.
     *
     * @param name  The coordinate system name.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     * @param axis2 The third axis.
     */
    public DefaultEllipsoidalCS(final String               name,
                                final CoordinateSystemAxis axis0,
                                final CoordinateSystemAxis axis1,
                                final CoordinateSystemAxis axis2)
    {
        super(singletonMap(NAME_KEY, name), axis0, axis1, axis2);
    }

    /**
     * Constructs a two-dimensional coordinate system from a set of properties.
     * The properties map is given unchanged to the
     * {@linkplain AbstractCS#AbstractCS(Map,CoordinateSystemAxis[]) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     */
    public DefaultEllipsoidalCS(final Map<String,?>   properties,
                                final CoordinateSystemAxis axis0,
                                final CoordinateSystemAxis axis1)
    {
        super(properties, axis0, axis1);
    }

    /**
     * Constructs a three-dimensional coordinate system from a set of properties.
     * The properties map is given unchanged to the
     * {@linkplain AbstractCS#AbstractCS(Map,CoordinateSystemAxis[]) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     * @param axis2 The third axis.
     */
    public DefaultEllipsoidalCS(final Map<String,?>   properties,
                                final CoordinateSystemAxis axis0,
                                final CoordinateSystemAxis axis1,
                                final CoordinateSystemAxis axis2)
    {
        super(properties, axis0, axis1, axis2);
    }

    /**
     * For {@link #shiftAxisRange(AxisRangeType)} and {@link #usingUnit(Unit)} usage only.
     */
    static DefaultEllipsoidalCS create(final Map<String,?> properties, final CoordinateSystemAxis[] axis) {
        switch (axis.length) {
            case 2: return new DefaultEllipsoidalCS(properties, axis[0], axis[1]);
            case 3: return new DefaultEllipsoidalCS(properties, axis[0], axis[1], axis[2]);
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Returns a Geotk coordinate system implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultEllipsoidalCS castOrCopy(final EllipsoidalCS object) {
        return (object == null) || (object instanceof DefaultEllipsoidalCS)
                ? (DefaultEllipsoidalCS) object : new DefaultEllipsoidalCS(object);
    }

    /**
     * Update the converters.
     */
    private void updateConverters() {
        for (int i=getDimension(); --i>=0;) {
            final CoordinateSystemAxis axis = getAxis(i);
            final AxisDirection   direction = AxisDirections.absolute(axis.getDirection());
            final Unit<?>              unit = axis.getUnit();
            if (AxisDirection.EAST.equals(direction)) {
                longitudeAxis      = i;
                longitudeConverter = unit.asType(Angle.class).getConverterTo(NonSI.DEGREE_ANGLE);
                continue;
            }
            if (AxisDirection.NORTH.equals(direction)) {
                latitudeAxis      = i;
                latitudeConverter = unit.asType(Angle.class).getConverterTo(NonSI.DEGREE_ANGLE);
                continue;
            }
            if (AxisDirection.UP.equals(direction)) {
                heightAxis      = i;
                heightConverter = unit.asType(Length.class).getConverterTo(SI.METRE);
                continue;
            }
            // Should not happen, since 'isCompatibleDirection'
            // has already checked axis directions.
            throw new AssertionError(direction);
        }
    }

    /*
     * Note: the following getLongitude, getLatitude and getHeight methods will not be ported to SIS.
     *       Instead, we will try to provide methods for computing the MathTransform between two CS.
     */

    /**
     * Returns the longitude found in the specified coordinate point,
     * always in {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * @param  coordinates The coordinate point expressed in this coordinate system.
     * @return The longitude in the specified array, in {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     * @throws MismatchedDimensionException is the coordinate point doesn't have the expected dimension.
     */
    public double getLongitude(final double[] coordinates) throws MismatchedDimensionException {
        AbstractCS.ensureDimensionMatch(this, "coordinates", coordinates);
        UnitConverter converter;
        synchronized (this) {
            converter = longitudeConverter;
            if (converter == null) {
                updateConverters();
                converter = longitudeConverter;
            }
        }
        return converter.convert(coordinates[longitudeAxis]);
    }

    /**
     * Returns the latitude found in the specified coordinate point,
     * always in {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * @param  coordinates The coordinate point expressed in this coordinate system.
     * @return The latitude in the specified array, in {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     * @throws MismatchedDimensionException is the coordinate point doesn't have the expected dimension.
     */
    public double getLatitude(final double[] coordinates) throws MismatchedDimensionException {
        AbstractCS.ensureDimensionMatch(this, "coordinates", coordinates);
        UnitConverter converter;
        synchronized (this) {
            converter = latitudeConverter;
            if (converter == null) {
                updateConverters();
                converter = latitudeConverter;
            }
        }
        return converter.convert(coordinates[latitudeAxis]);
    }

    /**
     * Returns the height found in the specified coordinate point,
     * always in {@linkplain SI#METRE metres}.
     *
     * @param  coordinates The coordinate point expressed in this coordinate system.
     * @return The height in the specified array, in {@linkplain SI#METRE metres}.
     * @throws MismatchedDimensionException is the coordinate point doesn't have the expected
     *         dimension.
     */
    public double getHeight(final double[] coordinates) throws MismatchedDimensionException {
        AbstractCS.ensureDimensionMatch(this, "coordinates", coordinates);
        UnitConverter converter;
        synchronized (this) {
            converter = heightConverter;
            if (converter == null) {
                updateConverters();
                converter = heightConverter;
                if (converter == null) {
                    throw new IllegalStateException(Errors.format(Errors.Keys.NOT_THREE_DIMENSIONAL_CS));
                }
            }
        }
        return converter.convert(coordinates[heightAxis]);
    }

    /**
     * Returns a coordinate system with the same axes than this CS, except that the longitude
     * axis is shifted to a positive or negative range. This method can be used in order to
     * shift between the [-180 … +180]° and [0 … 360]° ranges.
     * <p>
     * This method shifts the axis {@linkplain CoordinateSystemAxis#getMinimumValue() minimum}
     * and {@linkplain CoordinateSystemAxis#getMaximumValue() maximum} values by a multiple of
     * 180°, converted to the units of the axis.
     * <p>
     * This method does not change the meaning of ordinate values. For example a longitude of
     * -60° still locate the same longitude in the old and the new coordinate system. But the
     * preferred way to locate that longitude may become the 300° value if the range has been
     * shifted to positive values.
     *
     * @param  range {@link AxisRangeType#POSITIVE_LONGITUDE POSITIVE_LONGITUDE} for a range
     *         of positive longitude values, or {@link AxisRangeType#SPANNING_ZERO_LONGITUDE
     *         SPANNING_ZERO_LONGITUDE} for a range of positive and negative longitude values.
     * @return A coordinate system using the given kind of longitude range (may be {@code this}).
     *
     * @see org.geotoolkit.referencing.crs.DefaultGeographicCRS#shiftAxisRange(AxisRangeType)
     *
     * @since 3.20
     */
    public synchronized DefaultEllipsoidalCS shiftAxisRange(final AxisRangeType range) {
        final boolean positive;
        switch (range) {
            case SPANNING_ZERO_LONGITUDE: positive = false; break;
            case POSITIVE_LONGITUDE:      positive = true;  break;
            default: return this;
        }
        if (longitudeConverter == null) {
            updateConverters(); // Compute 'longitudeAxis' too.
        }
        final CoordinateSystemAxis axis = getAxis(longitudeAxis);
        final double minimum = axis.getMinimumValue();
        if (positive ? (minimum >= 0) : (minimum < 0)) {
            return this;
        }
        if (shifted == null) {
            // Compute the offset needed for making the range positive, then
            // shift to a negative range if this is what the user asked for.
            final double cycle = longitudeConverter.inverse().convert(180);
            double offset = Math.floor(minimum/cycle + 1E-10);
            if (!positive) offset--;
            offset *= cycle;
            final Unit<?> unit = axis.getUnit();
            if (Double.isNaN(offset) || Double.isInfinite(offset)) {
                // Should never happen unless the axis has some strange unit.
                throw new IllegalStateException(Errors.format(Errors.Keys.UNKNOWN_UNIT_1, unit));
            }
            final DefaultCoordinateSystemAxis newAxis = new DefaultCoordinateSystemAxis(
                    IdentifiedObjects.getProperties(axis), axis.getAbbreviation(), axis.getDirection(),
                    unit, minimum - offset, axis.getMaximumValue() - offset, axis.getRangeMeaning());
            if (newAxis.equals(axis, ComparisonMode.BY_CONTRACT)) {
                // This should not happen, except in some strange cases like NaN or infinite bounds
                // (which should not occur for longitude axis).
                shifted = this;
            } else {
                final CoordinateSystemAxis[] axes = new CoordinateSystemAxis[getDimension()];
                for (int i=axes.length; --i>=0;) {
                    axes[i] = (i != longitudeAxis) ? getAxis(i) : newAxis;
                }
                shifted = create(IdentifiedObjects.getProperties(this, null), axes);
                shifted.shifted = this;
            }
        }
        return shifted;
    }

    /**
     * Returns a coordinate system with the same properties than the current one except for
     * axis units. If this coordinate system already uses the given unit, then this method
     * returns {@code this}.
     *
     * @param  unit The unit for the new axis.
     * @return A coordinate system with axis using the specified units, or {@code this}.
     * @throws IllegalArgumentException If the specified unit is incompatible with the expected one.
     *
     * @since 2.2
     */
    public DefaultEllipsoidalCS usingUnit(final Unit<?> unit) throws IllegalArgumentException {
        final CoordinateSystemAxis[] axes;
        try {
            axes = AbstractCS.axisUsingUnit(this, unit, Units.isLinear(unit) ? SI.RADIAN : SI.METRE);
        } catch (ConversionException e) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_1, unit), e);
        }
        if (axes == null) {
            return this;
        }
        return create(IdentifiedObjects.getProperties(this, null), axes);
    }
}
