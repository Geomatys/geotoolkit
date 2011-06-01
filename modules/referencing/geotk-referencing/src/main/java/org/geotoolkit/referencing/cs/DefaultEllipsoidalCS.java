/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.geometry.MismatchedDimensionException;

import org.geotoolkit.measure.Units;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.internal.referencing.AxisDirections;


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
 * @version 3.18
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultEllipsoidalCS extends AbstractCS implements EllipsoidalCS {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -1452492488902329211L;

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
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultEllipsoidalCS() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new coordinate system with the same values than the specified one.
     * This copy constructor provides a way to wrap an arbitrary implementation into a
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
        super(name, axis0, axis1);
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
        super(name, axis0, axis1, axis2);
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
     * For {@link #usingUnit} usage only.
     */
    private DefaultEllipsoidalCS(final Map<String,?> properties, final CoordinateSystemAxis[] axis) {
        super(properties, axis);
    }

    /**
     * Returns a Geotk coordinate system implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object.
     *
     * @param  object The object to wrap in a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultEllipsoidalCS wrap(final EllipsoidalCS object) {
        return (object == null) || (object instanceof DefaultEllipsoidalCS)
                ? (DefaultEllipsoidalCS) object : new DefaultEllipsoidalCS(object);
    }

    /**
     * Returns {@code true} if the specified axis direction is allowed for this coordinate
     * system. The default implementation accepts only the following directions:
     * {@link AxisDirection#NORTH NORTH}, {@link AxisDirection#SOUTH SOUTH},
     * {@link AxisDirection#EAST  EAST},  {@link AxisDirection#WEST  WEST},
     * {@link AxisDirection#UP    UP} and {@link AxisDirection#DOWN  DOWN}.
     */
    @Override
    protected boolean isCompatibleDirection(AxisDirection direction) {
        direction = AxisDirections.absolute(direction);
        return AxisDirection.NORTH.equals(direction) ||
               AxisDirection.EAST .equals(direction) ||
               AxisDirection.UP   .equals(direction);
    }

    /**
     * Returns {@code true} if the specified unit is an angular units, or a linear one in the
     * special case of height. This method is invoked at construction time for checking units
     * compatibility.
     *
     * @since 2.2
     */
    @Override
    protected boolean isCompatibleUnit(AxisDirection direction, final Unit<?> unit) {
        direction = AxisDirections.absolute(direction);
        if (AxisDirection.UP.equals(direction)) {
            return Units.isLinear(unit);
        }
        return Units.isAngular(unit);
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

    /**
     * Returns the longitude found in the specified coordinate point,
     * always in {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * @param  coordinates The coordinate point expressed in this coordinate system.
     * @return The longitude in the specified array, in {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     * @throws MismatchedDimensionException is the coordinate point doesn't have the expected dimension.
     */
    public double getLongitude(final double[] coordinates) throws MismatchedDimensionException {
        ensureDimensionMatch("coordinates", coordinates);
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
        ensureDimensionMatch("coordinates", coordinates);
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
        ensureDimensionMatch("coordinates", coordinates);
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
     * Returns a new coordinate system with the same properties than the current one except for
     * axis units.
     *
     * @param  unit The unit for the new axis.
     * @return A coordinate system with axis using the specified units.
     * @throws IllegalArgumentException If the specified unit is incompatible with the expected one.
     *
     * @todo Current implementation can't work for 3D coordinate systems.
     *
     * @since 2.2
     */
    public DefaultEllipsoidalCS usingUnit(final Unit<?> unit) throws IllegalArgumentException {
        final CoordinateSystemAxis[] axis = axisUsingUnit(unit);
        if (axis == null) {
            return this;
        }
        return new DefaultEllipsoidalCS(IdentifiedObjects.getProperties(this, null), axis);
    }
}
