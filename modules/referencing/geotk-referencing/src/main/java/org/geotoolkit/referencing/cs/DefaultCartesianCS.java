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
import javax.measure.unit.Unit;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.ConversionException;
import javax.xml.bind.annotation.XmlTransient;

import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.geometry.MismatchedDimensionException;

import org.apache.sis.math.MathFunctions;
import org.apache.sis.measure.Units;
import org.geotoolkit.measure.Measure;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.referencing.IdentifiedObjects;

import static java.util.Collections.singletonMap;
import static org.geotoolkit.referencing.cs.AbstractCS.name;


/**
 * A 1-, 2-, or 3-dimensional coordinate system. Gives the position of points relative to
 * orthogonal straight axes in the 2- and 3-dimensional cases. In the 1-dimensional case,
 * it contains a single straight coordinate axis. In the multi-dimensional case, all axes
 * shall have the same length unit of measure. A {@code CartesianCS} shall have one,
 * two, or three {@linkplain #getAxis axis}.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CRS type(s)</TH></TR>
 * <TR><TD>
 *   {@link org.geotoolkit.referencing.crs.DefaultGeocentricCRS  Geocentric},
 *   {@link org.geotoolkit.referencing.crs.DefaultProjectedCRS   Projected},
 *   {@link org.geotoolkit.referencing.crs.DefaultEngineeringCRS Engineering},
 *   {@link org.geotoolkit.referencing.crs.DefaultImageCRS       Image}
 * </TD></TR></TABLE>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see DefaultAffineCS
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@XmlTransient
public class DefaultCartesianCS extends org.apache.sis.referencing.cs.DefaultCartesianCS {
    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#EASTING Easting}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#NORTHING Northing}</var>
     * axis in metres.
     */
    public static final DefaultCartesianCS PROJECTED = new DefaultCartesianCS(
                    name(Vocabulary.Keys.PROJECTED),
                    DefaultCoordinateSystemAxis.EASTING,
                    DefaultCoordinateSystemAxis.NORTHING);

    /**
     * A three-dimensional Cartesian CS with geocentric
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEOCENTRIC_X x}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEOCENTRIC_Y y}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEOCENTRIC_Z z}</var>
     * axis in metres.
     *
     * @see DefaultSphericalCS#GEOCENTRIC
     */
    public static final DefaultCartesianCS GEOCENTRIC = new DefaultCartesianCS(
                    name(Vocabulary.Keys.GEOCENTRIC),
                    DefaultCoordinateSystemAxis.GEOCENTRIC_X,
                    DefaultCoordinateSystemAxis.GEOCENTRIC_Y,
                    DefaultCoordinateSystemAxis.GEOCENTRIC_Z);
    static {
        org.apache.sis.referencing.cs.DefaultCartesianCS.GEOCENTRIC = GEOCENTRIC;
    }

    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#X x}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#Y y}</var>
     * axis in metres.
     */
    public static final DefaultCartesianCS GENERIC_2D = new DefaultCartesianCS(
                    name(Vocabulary.Keys.CARTESIAN_2D),
                    DefaultCoordinateSystemAxis.X,
                    DefaultCoordinateSystemAxis.Y);

    /**
     * A three-dimensional Cartesian CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#X x}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#Y y}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#Z z}</var>
     * axis in metres.
     */
    public static final DefaultCartesianCS GENERIC_3D = new DefaultCartesianCS(
                    name(Vocabulary.Keys.CARTESIAN_3D),
                    DefaultCoordinateSystemAxis.X,
                    DefaultCoordinateSystemAxis.Y,
                    DefaultCoordinateSystemAxis.Z);

    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#COLUMN column}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#ROW row}</var>
     * axis.
     */
    public static final DefaultCartesianCS GRID = new DefaultCartesianCS(
                    name(Vocabulary.Keys.GRID),
                    DefaultCoordinateSystemAxis.COLUMN,
                    DefaultCoordinateSystemAxis.ROW);

    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#DISPLAY_X display x}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#DISPLAY_Y display y}</var>
     * axis.
     *
     * @since 2.2
     */
    public static final DefaultCartesianCS DISPLAY = new DefaultCartesianCS(
                    name(Vocabulary.Keys.DISPLAY),
                    DefaultCoordinateSystemAxis.DISPLAY_X,
                    DefaultCoordinateSystemAxis.DISPLAY_Y);

    /**
     * The unit for measuring distance in this coordinate system, or {@code null} if none.
     * Will be computed only when first needed.
     */
    private transient volatile Unit<?> distanceUnit;

    /**
     * Converters from {@linkplain CoordinateSystemAxis#getUnit axis units} to
     * {@linkplain #getDistanceUnit() distance unit}. Will be constructed only
     * when first needed.
     */
    private transient volatile UnitConverter[] converters;

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
    public DefaultCartesianCS(final CartesianCS cs) {
        super(cs);
    }

    /**
     * Constructs a two-dimensional coordinate system from a name.
     *
     * @param name  The coordinate system name.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     */
    public DefaultCartesianCS(final String               name,
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
    public DefaultCartesianCS(final String               name,
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
    public DefaultCartesianCS(final Map<String,?>   properties,
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
    public DefaultCartesianCS(final Map<String,?>   properties,
                              final CoordinateSystemAxis axis0,
                              final CoordinateSystemAxis axis1,
                              final CoordinateSystemAxis axis2)
    {
        super(properties, axis0, axis1, axis2);
    }

    /**
     * For {@link #usingUnit(Unit)} and {@link PredefinedCS#rightHanded(AffineCS)} usage only.
     */
    static DefaultCartesianCS create(final Map<String,?> properties, final CoordinateSystemAxis[] axis) {
        switch (axis.length) {
            case 2: return new DefaultCartesianCS(properties, axis[0], axis[1]);
            case 3: return new DefaultCartesianCS(properties, axis[0], axis[1], axis[2]);
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Computes the distance between two points.
     *
     * @param  coord1 Coordinates of the first point.
     * @param  coord2 Coordinates of the second point.
     * @return The distance between {@code coord1} and {@code coord2}.
     * @throws UnsupportedOperationException if this coordinate system can't compute distances.
     * @throws MismatchedDimensionException if a coordinate doesn't have the expected dimension.
     */
    public Measure distance(final double[] coord1, final double[] coord2)
            throws UnsupportedOperationException, MismatchedDimensionException
    {
        AbstractCS.ensureDimensionMatch(this, "coord1", coord1);
        AbstractCS.ensureDimensionMatch(this, "coord2", coord2);
        final Unit<?> unit = getDistanceUnit();
        UnitConverter[] converters = this.converters; // Avoid the need for synchronization.
        if (converters == null) {
            converters = new UnitConverter[getDimension()];
            for (int i=0; i<converters.length; i++) {
                final Unit<?> axisUnit = getAxis(i).getUnit();
                try {
                    converters[i] = axisUnit.getConverterToAny(unit);
                } catch (ConversionException e) {
                    throw new UnsupportedOperationException(Errors.format(
                            Errors.Keys.INCOMPATIBLE_UNIT_1, axisUnit), e);
                }
            }
            this.converters = converters;
        }
        final double[] delta = new double[converters.length];
        for (int i=0; i<converters.length; i++) {
            final UnitConverter  c = converters[i];
            delta[i] = c.convert(coord1[i]) - c.convert(coord2[i]);
        }
        return new Measure(MathFunctions.magnitude(delta), unit);
    }

    /**
     * Suggests an unit for measuring distances in this coordinate system. The default
     * implementation scans all {@linkplain CoordinateSystemAxis#getUnit() axis units},
     * then returns the "largest" one (e.g. kilometre instead of metre).
     *
     * @return Suggested distance unit.
     */
    private Unit<?> getDistanceUnit() {
        Unit<?> unit = distanceUnit;  // Avoid the need for synchronization.
        if (unit == null) {
            double maxScale = 0;
            for (int i=getDimension(); --i>=0;) {
                final Unit<?> candidate = getAxis(i).getUnit();
                if (candidate != null) {
                    final double scale = Math.abs(Units.toStandardUnit(candidate));
                    if (scale > maxScale) {
                        unit = candidate;
                        maxScale = scale;
                    }
                }
            }
            distanceUnit = unit;
        }
        return unit;
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
    public DefaultCartesianCS usingUnit(final Unit<?> unit) throws IllegalArgumentException {
        final CoordinateSystemAxis[] axes;
        try {
            axes = AbstractCS.axisUsingUnit(this, unit, null);
        } catch (ConversionException e) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_1, unit), e);
        }
        if (axes == null) {
            return this;
        }
        return create(IdentifiedObjects.getProperties(this, null), axes);
    }
}
