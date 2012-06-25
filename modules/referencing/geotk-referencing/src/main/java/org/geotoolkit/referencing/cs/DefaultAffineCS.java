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
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.converter.ConversionException;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.cs.AffineCS;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;

import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.internal.referencing.AxisDirections;
import org.geotoolkit.resources.Errors;


/**
 * A two- or three-dimensional coordinate system with straight axes that are not necessarily
 * orthogonal. An {@code AffineCS} shall have two or three {@linkplain #getAxis axis}.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CRS type(s)</TH></TR>
 * <TR><TD>
 *   {@link org.geotoolkit.referencing.crs.DefaultEngineeringCRS Engineering},
 *   {@link org.geotoolkit.referencing.crs.DefaultImageCRS       Image}
 * </TD></TR></TABLE>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @see DefaultCartesianCS
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultAffineCS extends AbstractCS implements AffineCS {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7977674229369042440L;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultAffineCS() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

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
    public DefaultAffineCS(final AffineCS cs) {
        super(cs);
    }

    /**
     * Constructs a two-dimensional coordinate system from a name.
     *
     * @param name  The coordinate system name.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     */
    public DefaultAffineCS(final String               name,
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
    public DefaultAffineCS(final String               name,
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
    public DefaultAffineCS(final Map<String,?>   properties,
                           final CoordinateSystemAxis axis0,
                           final CoordinateSystemAxis axis1)
    {
        super(properties, axis0, axis1);
    }

    /**
     * Constructs a three-dimensional coordinate system from a set of properties.
     * The properties map is given unchanged to the superclass constructor.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     * @param axis2 The third axis.
     */
    public DefaultAffineCS(final Map<String,?>   properties,
                           final CoordinateSystemAxis axis0,
                           final CoordinateSystemAxis axis1,
                           final CoordinateSystemAxis axis2)
    {
        super(properties, axis0, axis1, axis2);
    }

    /**
     * For {@link #usingUnit(Unit)} and {@link PredefinedCS#rightHanded(AffineCS)} usage only.
     */
    DefaultAffineCS(final Map<String,?> properties, final CoordinateSystemAxis[] axis) {
        super(properties, axis);
    }

    /**
     * Returns a Geotk coordinate system implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object.
     * <p>
     * This method checks for the {@link CartesianCS} sub-interface. If that interface is
     * found, then this method delegates to the corresponding {@code castOrCopy} static method.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultAffineCS castOrCopy(final AffineCS object) {
        if (object instanceof CartesianCS) {
            return DefaultCartesianCS.castOrCopy((CartesianCS) object);
        }
        return (object == null) || (object instanceof DefaultAffineCS)
                ? (DefaultAffineCS) object : new DefaultAffineCS(object);
    }

    /**
     * Returns {@code true} if the specified axis direction is allowed for this coordinate
     * system. The default implementation accepts all directions except temporal ones (i.e.
     * {@link AxisDirection#FUTURE FUTURE} and {@link AxisDirection#PAST PAST}).
     */
    @Override
    protected boolean isCompatibleDirection(final AxisDirection direction) {
        return !AxisDirection.FUTURE.equals(AxisDirections.absolute(direction));
    }

    /**
     * Returns {@code true} if the specified unit is compatible with {@linkplain SI#METRE metres}.
     * In addition, this method also accepts {@link Unit#ONE}, which is used for coordinates in a
     * grid. This method is invoked at construction time for checking units compatibility.
     *
     * @since 2.2
     */
    @Override
    protected boolean isCompatibleUnit(final AxisDirection direction, final Unit<?> unit) {
        return SI.METRE.isCompatible(unit) || Unit.ONE.equals(unit);
        // Note: this condition is also coded in PredefinedCS.rightHanded(AffineCS).
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
     * @since 3.00
     */
    public DefaultAffineCS usingUnit(final Unit<?> unit) throws IllegalArgumentException {
        final CoordinateSystemAxis[] axes;
        try {
            axes = axisUsingUnit(unit, null);
        } catch (ConversionException e) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_$1, unit), e);
        }
        if (axes == null) {
            return this;
        }
        return new DefaultAffineCS(IdentifiedObjects.getProperties(this, null), axes);
    }
}
