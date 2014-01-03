/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
 *    This class contains formulas from the public FTP area of NOAA.
 *    NOAAS's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.datum;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.quantity.Length;
import javax.xml.bind.annotation.XmlTransient;
import org.opengis.referencing.datum.Ellipsoid;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.geotoolkit.metadata.iso.citation.Citations;

import static java.lang.Double.*;


/**
 * Geometric figure that can be used to describe the approximate shape of the earth.
 * In mathematical terms, it is a surface formed by the rotation of an ellipse about
 * its minor axis. An ellipsoid requires two defining parameters:
 * <p>
 * <ul>
 *   <li>{@linkplain #getSemiMajorAxis() semi-major axis} and
 *       {@linkplain #getInverseFlattening() inverse flattening}, or</li>
 *   <li>{@linkplain #getSemiMajorAxis() semi-major axis} and
 *       {@linkplain #getSemiMinorAxis() semi-minor axis}.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 4.00
 *
 * @since 1.2
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@XmlTransient
public class DefaultEllipsoid extends org.apache.sis.referencing.datum.DefaultEllipsoid {
    /**
     * Returns a properties map with the given name and EPSG code.
     * This is used for the creation of default ellipsoid constants.
     */
    private static Map<String,?> properties(final String name, final int code, final Object alias) {
        final Map<String,Object> map = new HashMap<>(4);
        map.put(NAME_KEY, name);
        map.put(IDENTIFIERS_KEY, new NamedIdentifier(Citations.EPSG, String.valueOf(code)));
        if (alias != null) {
            map.put(ALIAS_KEY, alias);
        }
        return map;
    }

    /**
     * WGS 1984 ellipsoid (EPSG:7030) used in GPS systems.
     * The semi-major and semi-minor axis length are approximatively 6378137 and 6356752
     * {@linkplain SI#METRE metres} respectively.
     * This is the default ellipsoid for most {@code org.geotoolkit} packages.
     *
     * @see DefaultGeodeticDatum#WGS84
     */
    public static final DefaultEllipsoid WGS84 = createFlattenedSphere(
            properties("WGS84", 7030, "WGS 1984"), 6378137.0, 298.257223563, SI.METRE);

    /**
     * WGS 1972 ellipsoid (EPSG:7043).
     * The semi-major and semi-minor axis length are approximatively 6378135 and 6356751
     * {@linkplain SI#METRE metres} respectively.
     *
     * @see DefaultGeodeticDatum#WGS72
     *
     * @since 3.00
     */
    public static final DefaultEllipsoid WGS72 = createFlattenedSphere(
            properties("WGS72", 7043, "WGS 1972"), 6378135.0, 298.26, SI.METRE);

    /**
     * GRS 1980 ellipsoid (EPSG:7019), also called "<cite>International 1979</cite>".
     * The semi-major and semi-minor axis length are approximatively 6378137 and 6356752
     * {@linkplain SI#METRE metres} respectively. This ellipsoid is very close, but not
     * identical, to {@linkplain #WGS84}.
     *
     * {@note The <cite>NAD83</cite> ellipsoid uses the same semi-axis length and units.
     *        The <cite>Web Map Server</cite> <code>"CRS:83"</code> authority code uses that
     *        NAD83 ellipsoid. The <code>"IGNF:MILLER"</code> authority code uses the GRS80
     *        ellipsoid.}
     *
     * @since 2.2
     */
    public static final DefaultEllipsoid GRS80 = createFlattenedSphere(
            properties("GRS80", 7019, new String[] {"GRS 1980", "International 1979"}),
            6378137.0, 298.257222101, SI.METRE);

    /**
     * International 1924 ellipsoid (EPSG:7022).
     * The semi-major and semi-minor axis length are approximatively 6378388 and 6356912
     * {@linkplain SI#METRE metres} respectively.
     *
     * {@note The <cite>European Datum 1950</cite> ellipsoid uses the same
     *        semi-axis length and units.}
     */
    public static final DefaultEllipsoid INTERNATIONAL_1924 = createFlattenedSphere(
            properties("International 1924", 7022, null), 6378388.0, 297.0, SI.METRE);

    /**
     * Clarke 1866 ellipsoid (EPSG:7008).
     * The semi-major and semi-minor axis length are approximatively 6378206 and 6356584
     * {@linkplain SI#METRE metres} respectively.
     *
     * {@note The <cite>NAD27</cite> ellipsoid uses the same semi-axis length and units.
     *        The <cite>Web Map Server</cite> <code>"CRS:27"</code> authority code uses that
     *        NAD27 ellipsoid.}
     *
     * @since 2.2
     */
    public static final DefaultEllipsoid CLARKE_1866 = createEllipsoid(
            properties("Clarke 1866", 7008, null), 6378206.4, 6356583.8, SI.METRE);

    /**
     * A sphere with a radius of 6371000 {@linkplain SI#METRE metres}. Spheres use a simpler
     * algorithm for {@linkplain #orthodromicDistance orthodromic distance computation}, which
     * may be faster and more robust.
     *
     * {@note This ellipsoid is close to the <cite>GRS 1980 Authalic Sphere</cite> (EPSG:7048),
     *        which has a radius of 6371007 metres.}
     *
     * @see DefaultGeodeticDatum#SPHERE
     */
    public static final DefaultEllipsoid SPHERE =
            createEllipsoid("SPHERE", 6371000, 6371000, SI.METRE);

    /**
     * Constructs a new ellipsoid with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param ellipsoid The ellipsoid to copy.
     *
     * @since 2.2
     */
    protected DefaultEllipsoid(final Ellipsoid ellipsoid) {
        super(ellipsoid);
    }

    /**
     * Constructs a new ellipsoid using the specified axis length. The properties map is
     * given unchanged to the {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map)
     * super-class constructor}.
     *
     * @param properties        Set of properties. Should contains at least {@code "name"}.
     * @param semiMajorAxis     The equatorial radius.
     * @param semiMinorAxis     The polar radius.
     * @param inverseFlattening The inverse of the flattening value.
     * @param ivfDefinitive     {@code true} if the inverse flattening is definitive.
     * @param unit              The units of the semi-major and semi-minor axis values.
     *
     * @see #createEllipsoid
     * @see #createFlattenedSphere
     */
    protected DefaultEllipsoid(final Map<String,?> properties,
                               final double  semiMajorAxis,
                               final double  semiMinorAxis,
                               final double  inverseFlattening,
                               final boolean ivfDefinitive,
                               final Unit<Length> unit)
    {
        super(properties, semiMajorAxis, semiMinorAxis, inverseFlattening, ivfDefinitive, unit);
    }

    /**
     * Constructs a new ellipsoid using the specified axis length.
     *
     * @param name          The ellipsoid name.
     * @param semiMajorAxis The equatorial radius.
     * @param semiMinorAxis The polar radius.
     * @param unit          The units of the semi-major and semi-minor axis values.
     * @return An ellipsoid with the given axis length.
     */
    public static DefaultEllipsoid createEllipsoid(final String name,
                                                   final double semiMajorAxis,
                                                   final double semiMinorAxis,
                                                   final Unit<Length> unit)
    {
        return createEllipsoid(Collections.singletonMap(NAME_KEY, name),
                               semiMajorAxis, semiMinorAxis, unit);
    }

    /**
     * Constructs a new ellipsoid using the specified axis length. The properties map is
     * given unchanged to the {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map)
     * super-class constructor}.
     *
     * @param properties    Set of properties. Should contains at least {@code "name"}.
     * @param semiMajorAxis The equatorial radius.
     * @param semiMinorAxis The polar radius.
     * @param unit          The units of the semi-major and semi-minor axis values.
     * @return An ellipsoid with the given axis length.
     */
    public static DefaultEllipsoid createEllipsoid(final Map<String,?> properties,
                                                   final double semiMajorAxis,
                                                   final double semiMinorAxis,
                                                   final Unit<Length> unit)
    {
        if (semiMajorAxis == semiMinorAxis) {
            return new Spheroid(properties, semiMajorAxis, false, unit);
        } else {
            return new DefaultEllipsoid(properties, semiMajorAxis, semiMinorAxis,
                       semiMajorAxis/(semiMajorAxis-semiMinorAxis), false, unit);
        }
    }

    /**
     * Constructs a new ellipsoid using the specified axis length and inverse flattening value.
     *
     * @param name              The ellipsoid name.
     * @param semiMajorAxis     The equatorial radius.
     * @param inverseFlattening The inverse flattening value.
     * @param unit              The units of the semi-major and semi-minor axis
     *                          values.
     * @return An ellipsoid with the given axis length.
     */
    public static DefaultEllipsoid createFlattenedSphere(final String name,
                                                         final double semiMajorAxis,
                                                         final double inverseFlattening,
                                                         final Unit<Length> unit)
    {
        return createFlattenedSphere(Collections.singletonMap(NAME_KEY, name),
                                     semiMajorAxis, inverseFlattening, unit);
    }

    /**
     * Constructs a new ellipsoid using the specified axis length and
     * inverse flattening value. The properties map is given unchanged to the
     * {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map) super-class constructor}.
     *
     * @param properties        Set of properties. Should contains at least {@code "name"}.
     * @param semiMajorAxis     The equatorial radius.
     * @param inverseFlattening The inverse flattening value.
     * @param unit              The units of the semi-major and semi-minor axis
     *                          values.
     * @return An ellipsoid with the given axis length.
     */
    public static DefaultEllipsoid createFlattenedSphere(final Map<String,?> properties,
                                                         final double semiMajorAxis,
                                                         final double inverseFlattening,
                                                         final Unit<Length> unit)
    {
        if (isInfinite(inverseFlattening)) {
            return new Spheroid(properties, semiMajorAxis, true, unit);
        } else {
            return new DefaultEllipsoid(properties, semiMajorAxis,
                                        semiMajorAxis*(1-1/inverseFlattening),
                                        inverseFlattening, true, unit);
        }
    }

    /**
     * Returns the orthodromic distance between two geographic coordinates.
     * The orthodromic distance is the shortest distance between two points
     * on a sphere's surface. The default implementation delegates the work
     * to {@link #orthodromicDistance(double,double,double,double)}.
     *
     * @param  P1 Longitude and latitude of first point (in decimal degrees).
     * @param  P2 Longitude and latitude of second point (in decimal degrees).
     * @return The orthodromic distance (in the units of this ellipsoid).
     */
    public double orthodromicDistance(final Point2D P1, final Point2D P2) {
        return orthodromicDistance(P1.getX(), P1.getY(), P2.getX(), P2.getY());
    }
}
