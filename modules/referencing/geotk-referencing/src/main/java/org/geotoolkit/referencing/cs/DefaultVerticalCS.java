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
import net.jcip.annotations.Immutable;

import org.opengis.referencing.cs.VerticalCS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;

import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.internal.referencing.AxisDirections;


/**
 * A one-dimensional coordinate system used to record the heights (or depths) of points. Such a
 * coordinate system is usually dependent on the Earth's gravity field, perhaps loosely as when
 * atmospheric pressure is the basis for the vertical coordinate system axis. An exact definition
 * is deliberately not provided as the complexities of the subject fall outside the scope of this
 * specification. A {@code VerticalCS} shall have one {@linkplain #getAxis axis}.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CRS type(s)</TH></TR>
 * <TR><TD>
 *   {@link org.geotoolkit.referencing.crs.DefaultVerticalCRS    Vertical},
 *   {@link org.geotoolkit.referencing.crs.DefaultEngineeringCRS Engineering}
 * </TD></TR></TABLE>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultVerticalCS extends AbstractCS implements VerticalCS {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 1201155778896630499L;

    /**
     * A one-dimensional vertical CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#ELLIPSOIDAL_HEIGHT
     * ellipsoidal height}</var> axis in metres.
     */
    public static final DefaultVerticalCS ELLIPSOIDAL_HEIGHT = new DefaultVerticalCS(
                    DefaultCoordinateSystemAxis.ELLIPSOIDAL_HEIGHT);

    /**
     * A one-dimensional vertical CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#GRAVITY_RELATED_HEIGHT
     * gravity-related height}</var> axis in metres.
     *
     * @since 2.5
     */
    public static final DefaultVerticalCS GRAVITY_RELATED_HEIGHT = new DefaultVerticalCS(
                    DefaultCoordinateSystemAxis.GRAVITY_RELATED_HEIGHT);

    /**
     * A one-dimensional vertical CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#DEPTH depth}</var>
     * axis in metres.
     */
    public static final DefaultVerticalCS DEPTH = new DefaultVerticalCS(
                    DefaultCoordinateSystemAxis.DEPTH);

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultVerticalCS() {
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
    public DefaultVerticalCS(final VerticalCS cs) {
        super(cs);
    }

    /**
     * Constructs a coordinate system with the same properties than the specified axis.
     * The inherited properties include the {@linkplain #getName name} and aliases.
     *
     * @param axis The axis.
     *
     * @since 2.5
     */
    public DefaultVerticalCS(final CoordinateSystemAxis axis) {
        super(IdentifiedObjects.getProperties(axis), axis);
    }

    /**
     * Constructs a coordinate system from a name.
     *
     * @param name  The coordinate system name.
     * @param axis  The axis.
     */
    public DefaultVerticalCS(final String name, final CoordinateSystemAxis axis) {
        super(name, axis);
    }

    /**
     * Constructs a coordinate system from a set of properties. The properties map is given unchanged
     * to the {@linkplain AbstractCS#AbstractCS(Map,CoordinateSystemAxis[]) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param axis       The axis.
     */
    public DefaultVerticalCS(final Map<String,?> properties, final CoordinateSystemAxis axis) {
        super(properties, axis);
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
    public static DefaultVerticalCS castOrCopy(final VerticalCS object) {
        return (object == null) || (object instanceof DefaultVerticalCS)
                ? (DefaultVerticalCS) object : new DefaultVerticalCS(object);
    }

    /**
     * Returns {@code true} if the specified axis direction is allowed for this coordinate
     * system. The default implementation accepts only vertical directions (i.e.
     * {@link AxisDirection#UP UP} and {@link AxisDirection#DOWN DOWN}).
     */
    @Override
    protected boolean isCompatibleDirection(final AxisDirection direction) {
        return AxisDirection.UP.equals(AxisDirections.absolute(direction));
    }
}
