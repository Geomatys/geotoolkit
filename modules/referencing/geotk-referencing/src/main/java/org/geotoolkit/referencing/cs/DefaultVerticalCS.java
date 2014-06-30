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
import javax.xml.bind.annotation.XmlTransient;
import org.opengis.referencing.cs.VerticalCS;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.apache.sis.referencing.IdentifiedObjects;

import static java.util.Collections.singletonMap;


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
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@XmlTransient
public class DefaultVerticalCS extends org.apache.sis.referencing.cs.DefaultVerticalCS {
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
        super(singletonMap(NAME_KEY, name), axis);
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
}
