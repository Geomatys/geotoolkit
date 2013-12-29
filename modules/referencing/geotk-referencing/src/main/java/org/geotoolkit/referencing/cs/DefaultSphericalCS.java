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
import org.opengis.referencing.cs.SphericalCS;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.geotoolkit.resources.Vocabulary;

import static java.util.Collections.singletonMap;
import static org.geotoolkit.referencing.cs.AbstractCS.name;


/**
 * A three-dimensional coordinate system with one distance measured from the origin and two angular
 * coordinates. Not to be confused with an {@linkplain DefaultEllipsoidalCS ellipsoidal coordinate
 * system} based on an ellipsoid "degenerated" into a sphere. A {@code SphericalCS} shall have
 * three {@linkplain #getAxis axis}.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CRS type(s)</TH></TR>
 * <TR><TD>
 *   {@link org.geotoolkit.referencing.crs.DefaultGeocentricCRS  Geocentric},
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
public class DefaultSphericalCS extends org.apache.sis.referencing.cs.DefaultSphericalCS {
    /**
     * A three-dimensional spherical CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#SPHERICAL_LONGITUDE longitude}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#SPHERICAL_LATITUDE latitude}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEOCENTRIC_RADIUS radius}</var>
     * axis.
     *
     * @see DefaultCartesianCS#GEOCENTRIC
     */
    public static final DefaultSphericalCS GEOCENTRIC = new DefaultSphericalCS(
                    name(Vocabulary.Keys.GEOCENTRIC),
                    DefaultCoordinateSystemAxis.SPHERICAL_LONGITUDE,
                    DefaultCoordinateSystemAxis.SPHERICAL_LATITUDE,
                    DefaultCoordinateSystemAxis.GEOCENTRIC_RADIUS);

    /**
     * Constructs a three-dimensional coordinate system from a name.
     *
     * @param name  The coordinate system name.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     * @param axis2 The third axis.
     */
    public DefaultSphericalCS(final String               name,
                              final CoordinateSystemAxis axis0,
                              final CoordinateSystemAxis axis1,
                              final CoordinateSystemAxis axis2)
    {
        super(singletonMap(NAME_KEY, name), axis0, axis1, axis2);
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
    public DefaultSphericalCS(final SphericalCS cs) {
        super(cs);
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
    public DefaultSphericalCS(final Map<String,?>   properties,
                              final CoordinateSystemAxis axis0,
                              final CoordinateSystemAxis axis1,
                              final CoordinateSystemAxis axis2)
    {
        super(properties, axis0, axis1, axis2);
    }
}
