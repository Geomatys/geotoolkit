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
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.datum;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import javax.xml.bind.annotation.XmlTransient;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.operation.Matrix;
import org.apache.sis.referencing.datum.BursaWolfParameters;

import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.util.ComparisonMode;

import static org.apache.sis.util.Utilities.deepEquals;


/**
 * Defines the location and precise orientation in 3-dimensional space of a defined ellipsoid
 * (or sphere) that approximates the shape of the earth. Used also for Cartesian coordinate
 * system centered in this ellipsoid (or sphere).
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @see Ellipsoid
 * @see PrimeMeridian
 *
 * @since 1.2
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@XmlTransient
public class DefaultGeodeticDatum extends org.apache.sis.referencing.datum.DefaultGeodeticDatum {
    /**
     * Default WGS 1984 datum (EPSG:6326).
     * Prime meridian is {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}.
     * This datum is used in GPS systems and is the default for most {@code org.geotoolkit} packages.
     *
     * @see DefaultEllipsoid#WGS84
     * @see org.geotoolkit.referencing.crs.DefaultGeographicCRS#WGS84
     */
    public static final DefaultGeodeticDatum WGS84;
    static {
        final ReferenceIdentifier[] identifiers = {
            new NamedIdentifier(Citations.OGC,    "WGS84"),
            new NamedIdentifier(Citations.ORACLE, "WGS 84"),
            new NamedIdentifier(null,             "WGS_84"),
            new NamedIdentifier(null,             "WGS 1984"),
            new NamedIdentifier(null,             "WGS_1984"),
            new NamedIdentifier(Citations.ESRI,   "D_WGS_1984"),
            new NamedIdentifier(Citations.EPSG,   "World Geodetic System 1984")
        };
        final Map<String,Object> properties = new HashMap<>(6);
        properties.put(NAME_KEY,  identifiers[0]);
        properties.put(ALIAS_KEY, identifiers);
        properties.put(IDENTIFIERS_KEY, new NamedIdentifier(Citations.EPSG, "6326"));
        WGS84 = new DefaultGeodeticDatum(properties, DefaultEllipsoid.WGS84);
    }

    /**
     * Default WGS 1972 datum (EPSG:6322).
     * Prime meridian is {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}.
     * This datum is used, together with {@linkplain #WGS84}, in
     * {@linkplain org.geotoolkit.referencing.operation.transform.EarthGravitationalModel
     * Earth Gravitational Model}.
     *
     * @see DefaultEllipsoid#WGS72
     *
     * @since 3.00
     */
    public static final DefaultGeodeticDatum WGS72;
    static {
        final ReferenceIdentifier[] identifiers = {
            new NamedIdentifier(Citations.OGC,  "WGS72"),
            new NamedIdentifier(Citations.EPSG, "World Geodetic System 1972")
        };
        final Map<String,Object> properties = new HashMap<>(6);
        properties.put(NAME_KEY,  identifiers[0]);
        properties.put(ALIAS_KEY, identifiers);
        properties.put(IDENTIFIERS_KEY, new NamedIdentifier(Citations.EPSG, "6322"));
        WGS72 = new DefaultGeodeticDatum(properties, DefaultEllipsoid.WGS72);
    }

    /**
     * Default spherical datum.
     * Prime meridian is {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}.
     *
     * {@note This datum is close, but not identical, to the datum based on <cite>GRS 1980
     *        Authalic Sphere</cite> (EPSG:6047). This datum uses a sphere radius of 6371000
     *        metres, while the GRS 1980 Authalic Sphere uses a sphere radius of 6371007 metres.}
     *
     * @see DefaultEllipsoid#SPHERE
     * @see org.geotoolkit.referencing.crs.DefaultGeographicCRS#SPHERE
     *
     * @since 3.15
     */
    public static final DefaultGeodeticDatum SPHERE = new DefaultGeodeticDatum(
            IdentifiedObjects.getProperties(DefaultEllipsoid.SPHERE), DefaultEllipsoid.SPHERE);

    /**
     * Constructs a new datum with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param datum The datum to copy.
     *
     * @since 2.2
     */
    public DefaultGeodeticDatum(final GeodeticDatum datum) {
        super(datum);
    }

    /**
     * Constructs a geodetic datum using the {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}
     * prime meridian. This is a convenience constructor for the very common case where the prime
     * meridian is the Greenwich one.
     *
     * @param name      The datum name.
     * @param ellipsoid The ellipsoid.
     *
     * @since 3.15
     */
    public DefaultGeodeticDatum(final String name, final Ellipsoid ellipsoid) {
        this(name, ellipsoid, DefaultPrimeMeridian.GREENWICH);
    }

    /**
     * Constructs a geodetic datum using the {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}
     * prime meridian. This is a convenience constructor for the very common case where the prime
     * meridian is the Greenwich one.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param ellipsoid  The ellipsoid.
     *
     * @since 3.15
     */
    public DefaultGeodeticDatum(final Map<String,?> properties, final Ellipsoid ellipsoid) {
        this(properties, ellipsoid, DefaultPrimeMeridian.GREENWICH);
    }

    /**
     * Constructs a geodetic datum from a name and the given prime meridian.
     *
     * @param name          The datum name.
     * @param ellipsoid     The ellipsoid.
     * @param primeMeridian The prime meridian. If omitted, the default is
     *                      {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}.
     */
    public DefaultGeodeticDatum(final String        name,
                                final Ellipsoid     ellipsoid,
                                final PrimeMeridian primeMeridian)
    {
        this(Collections.singletonMap(NAME_KEY, name), ellipsoid, primeMeridian);
    }

    /**
     * Constructs a geodetic datum from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractDatum#AbstractDatum(Map) super-class constructor}.
     * Additionally, the following properties are understood by this constructor:
     * <p>
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value #BURSA_WOLF_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link BursaWolfParameters} or an array of those&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getBursaWolfParameters}</td>
     *   </tr>
     * </table>
     *
     * @param properties    Set of properties. Should contains at least {@code "name"}.
     * @param ellipsoid     The ellipsoid.
     * @param primeMeridian The prime meridian. If omitted, the default is
     *                      {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}.
     */
    public DefaultGeodeticDatum(final Map<String,?> properties,
                                final Ellipsoid     ellipsoid,
                                final PrimeMeridian primeMeridian)
    {
        super(properties, ellipsoid, primeMeridian);
    }

    /**
     * Returns Bursa Wolf parameters for a datum shift toward the specified target, or {@code null}
     * if none. This method search only for Bursa-Wolf parameters explicitly specified in the
     * {@code properties} map at construction time. This method doesn't try to infer a set of
     * parameters from indirect informations. For example it doesn't try to inverse the parameters
     * specified in the {@code target} datum if none were found in this datum. If such an elaborated
     * search is wanted, use {@link #getAffineTransform} instead.
     *
     * @param  target The target geodetic datum.
     * @return Bursa Wolf parameters from this datum to the given target datum,
     *         or {@code null} if none.
     */
    public BursaWolfParameters getBursaWolfParameters(final GeodeticDatum target) {
        for (final BursaWolfParameters candidate : getBursaWolfParameters()) {
            if (deepEquals(target, candidate.getTargetDatum(), ComparisonMode.IGNORE_METADATA)) {
                return candidate.clone();
            }
        }
        return null;
    }

    /**
     * Returns a matrix that can be used to define a transformation to the specified datum.
     * If no transformation path is found, then this method returns {@code null}.
     *
     * @param  source The source datum.
     * @param  target The target datum.
     * @return An affine transform from {@code source} to {@code target}, or {@code null} if none.
     *
     * @see BursaWolfParameters#getAffineTransform
     */
    public static Matrix getAffineTransform(final GeodeticDatum source,
                                            final GeodeticDatum target)
    {
        if (source instanceof org.apache.sis.referencing.datum.DefaultGeodeticDatum) {
            return ((org.apache.sis.referencing.datum.DefaultGeodeticDatum) source)
                    .getPositionVectorTransformation(target, null);
        }
        return null;
    }

    /**
     * Returns {@code true} if the specified object is equals (at least on computation purpose)
     * to the {@link #WGS84} datum. This method may conservatively returns {@code false} if the
     * specified datum is uncertain (for example because it come from an other implementation).
     *
     * @param datum The datum to inspect.
     * @return {@code true} if the given datum is equal to WGS84 for computational purpose.
     */
    public static boolean isWGS84(final Datum datum) {
        if (datum instanceof AbstractIdentifiedObject) {
            return WGS84.equals((AbstractIdentifiedObject) datum, ComparisonMode.IGNORE_METADATA);
        }
        // Maybe the specified object has its own test...
        return datum!=null && datum.equals(WGS84);
    }
}
