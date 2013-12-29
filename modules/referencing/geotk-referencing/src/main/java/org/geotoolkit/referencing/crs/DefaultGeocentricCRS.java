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
package org.geotoolkit.referencing.crs;

import java.util.Collections;
import java.util.Map;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.SphericalCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.referencing.datum.GeodeticDatum;

import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.referencing.AbstractReferenceSystem;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.cs.DefaultSphericalCS;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.resources.Vocabulary;


/**
 * A 3D coordinate reference system with the origin at the approximate centre of mass of the earth.
 * A geocentric CRS deals with the earth's curvature by taking a 3D spatial view, which obviates
 * the need to model the earth's curvature.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CS type(s)</TH></TR>
 * <TR><TD>
 *   {@link CartesianCS Cartesian},
 *   {@link SphericalCS Spherical}
 * </TD></TR></TABLE>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 1.2
 * @module
 */
@Immutable
@XmlRootElement(name = "GeocentricCRS")
public class DefaultGeocentricCRS extends AbstractSingleCRS implements GeocentricCRS {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 6784642848287659827L;

    /**
     * The default geocentric CRS with a
     * {@linkplain DefaultCartesianCS#GEOCENTRIC Cartesian coordinate system}.
     * Prime meridian is Greenwich, geodetic datum is WGS84 and linear units are metres.
     * The <var>X</var> axis points towards the prime meridian.
     * The <var>Y</var> axis points East.
     * The <var>Z</var> axis points North.
     */
    public static final DefaultGeocentricCRS CARTESIAN = new DefaultGeocentricCRS(
                        name(Vocabulary.Keys.CARTESIAN),
                        DefaultGeodeticDatum.WGS84, DefaultCartesianCS.GEOCENTRIC);

    /**
     * The default geocentric CRS with a
     * {@linkplain DefaultSphericalCS#GEOCENTRIC spherical coordinate system}.
     * Prime meridian is Greenwich, geodetic datum is WGS84 and linear units are metres.
     */
    public static final DefaultGeocentricCRS SPHERICAL = new DefaultGeocentricCRS(
                        name(Vocabulary.Keys.SPHERICAL),
                        DefaultGeodeticDatum.WGS84, DefaultSphericalCS.GEOCENTRIC);

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultGeocentricCRS() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new geocentric CRS with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param crs The coordinate reference system to copy.
     *
     * @since 2.2
     */
    public DefaultGeocentricCRS(final GeocentricCRS crs) {
        super(crs);
    }

    /**
     * Constructs a geocentric CRS from a name.
     *
     * @param name The name.
     * @param datum The datum.
     * @param cs The coordinate system.
     */
    public DefaultGeocentricCRS(final String         name,
                                final GeodeticDatum datum,
                                final CartesianCS      cs)
    {
        this(Collections.singletonMap(NAME_KEY, name), datum, cs);
    }

    /**
     * Constructs a geocentric CRS from a name.
     *
     * @param name The name.
     * @param datum The datum.
     * @param cs The coordinate system.
     */
    public DefaultGeocentricCRS(final String         name,
                                final GeodeticDatum datum,
                                final SphericalCS      cs)
    {
        this(Collections.singletonMap(NAME_KEY, name), datum, cs);
    }

    /**
     * Constructs a geographic CRS from a set of properties. The properties are given unchanged to
     * the {@linkplain AbstractReferenceSystem#AbstractReferenceSystem(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param datum The datum.
     * @param cs The coordinate system.
     */
    public DefaultGeocentricCRS(final Map<String,?> properties,
                                final GeodeticDatum datum,
                                final CartesianCS   cs)
    {
        super(properties, datum, cs);
    }

    /**
     * Constructs a geographic CRS from a set of properties.
     * The properties are given unchanged to the
     * {@linkplain AbstractReferenceSystem#AbstractReferenceSystem(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param datum The datum.
     * @param cs The coordinate system.
     */
    public DefaultGeocentricCRS(final Map<String,?> properties,
                                final GeodeticDatum datum,
                                final SphericalCS   cs)
    {
        super(properties, datum, cs);
    }

    /**
     * Returns a Geotk CRS implementation with the same values than the given arbitrary
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
    public static DefaultGeocentricCRS castOrCopy(final GeocentricCRS object) {
        return (object == null) || (object instanceof DefaultGeocentricCRS)
                ? (DefaultGeocentricCRS) object : new DefaultGeocentricCRS(object);
    }

    /**
     * Returns the datum.
     */
    @Override
    @XmlElement(name="geodeticDatum")
    public GeodeticDatum getDatum() {
        return (GeodeticDatum) super.getDatum();
    }

    /**
     * Used by JAXB only (invoked by reflection).
     */
    final void setDatum(final GeodeticDatum datum) {
        super.setDatum(datum);
    }

    /**
     * Formats the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#GEOCCS"><cite>Well
     * Known Text</cite> (WKT)</A> element.
     *
     * @param  formatter The formatter to use.
     * @return The name of the WKT element type, which is {@code "GEOCCS"}.
     */
    @Override
    public String formatTo(final Formatter formatter) { // TODO: should be protected.
        final Unit<?> unit = getUnit();
        final GeodeticDatum datum = getDatum();
        formatter.append(datum);
        formatter.append(datum.getPrimeMeridian());
        formatter.append(unit);
        final CoordinateSystem cs = formatter.getConvention().toConformCS(getCoordinateSystem());
        if (!(cs instanceof CartesianCS)) {
            formatter.setInvalidWKT(cs);
        }
        final int dimension = cs.getDimension();
        for (int i=0; i<dimension; i++) {
            formatter.append(cs.getAxis(i));
        }
        if (unit == null) {
            formatter.setInvalidWKT(this);
        }
        return "GEOCCS";
    }
}
