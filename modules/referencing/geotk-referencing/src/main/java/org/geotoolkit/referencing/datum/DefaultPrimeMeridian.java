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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.measure.quantity.Angle;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.datum.PrimeMeridian;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.io.wkt.Formatter;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.util.Utilities;
import org.apache.sis.measure.Units;

import static org.geotoolkit.util.Utilities.hash;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.internal.InternalUtilities.epsilonEqual;


/**
 * A prime meridian defines the origin from which longitude values are determined.
 * The {@link #getName name} initial value is "Greenwich", and that value shall be
 * used when the {@linkplain #getGreenwichLongitude greenwich longitude} value is
 * zero.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 1.2
 * @module
 */
@Immutable
@XmlType(name = "PrimeMeridianType")
@XmlRootElement(name = "PrimeMeridian")
public class DefaultPrimeMeridian extends AbstractIdentifiedObject implements PrimeMeridian {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 541978454643213305L;;

    /**
     * The Greenwich meridian (EPSG:8901), with angular measurements in decimal degrees.
     */
    public static final DefaultPrimeMeridian GREENWICH;
    static {
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(NAME_KEY, "Greenwich");
        properties.put(IDENTIFIERS_KEY, new NamedIdentifier(Citations.EPSG, "8901"));
        GREENWICH = new DefaultPrimeMeridian(properties, 0, NonSI.DEGREE_ANGLE);
    }

    /**
     * Longitude of the prime meridian measured from the Greenwich meridian, positive eastward.
     */
    @XmlElement(required = true)
    private final double greenwichLongitude;

    /**
     * The angular unit of the {@linkplain #getGreenwichLongitude Greenwich longitude}.
     */
    private final Unit<Angle> angularUnit;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultPrimeMeridian() {
        this(GREENWICH);
    }

    /**
     * Constructs a new prime meridian with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param meridian The prime meridian to copy.
     *
     * @since 2.2
     */
    public DefaultPrimeMeridian(final PrimeMeridian meridian) {
        super(meridian);
        greenwichLongitude = meridian.getGreenwichLongitude();
        angularUnit        = meridian.getAngularUnit();
    }

    /**
     * Constructs a prime meridian from a name. The {@code greenwichLongitude} value
     * is assumed in {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * @param name                The datum name.
     * @param greenwichLongitude  The longitude value relative to the Greenwich Meridian,
     *                            in decimal degrees.
     */
    public DefaultPrimeMeridian(final String name, final double greenwichLongitude) {
        this(name, greenwichLongitude, NonSI.DEGREE_ANGLE);
    }

    /**
     * Constructs a prime meridian from a name.
     *
     * @param name                The datum name.
     * @param greenwichLongitude  The longitude value relative to the Greenwich Meridian.
     * @param angularUnit         The angular unit of the longitude.
     */
    public DefaultPrimeMeridian(final String name, final double greenwichLongitude,
                                final Unit<Angle> angularUnit)
    {
        this(Collections.singletonMap(NAME_KEY, name), greenwichLongitude, angularUnit);
    }

    /**
     * Constructs a prime meridian from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map)
     * super-class constructor}.
     *
     * @param properties          Set of properties. Should contains at least {@code "name"}.
     * @param greenwichLongitude  The longitude value relative to the Greenwich Meridian.
     * @param angularUnit         The angular unit of the longitude.
     */
    public DefaultPrimeMeridian(final Map<String,?> properties, final double greenwichLongitude,
                                final Unit<Angle> angularUnit)
    {
        super(properties);
        ensureNonNull("angularUnit", angularUnit);
        this.greenwichLongitude = greenwichLongitude;
        this.angularUnit = Units.ensureAngular(angularUnit);
    }

    /**
     * Returns a Geotk prime meridian implementation with the same values than the given arbitrary
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
    public static DefaultPrimeMeridian castOrCopy(final PrimeMeridian object) {
        return (object == null) || (object instanceof DefaultPrimeMeridian)
                ? (DefaultPrimeMeridian) object : new DefaultPrimeMeridian(object);
    }

    /**
     * Longitude of the prime meridian measured from the Greenwich meridian, positive eastward.
     * The {@code greenwichLongitude} initial value is zero, and that value shall be used
     * when the {@linkplain #getName meridian name} value is "Greenwich".
     *
     * @return The prime meridian Greenwich longitude, in {@linkplain #getAngularUnit angular unit}.
     */
    @Override
    public double getGreenwichLongitude() {
        return greenwichLongitude;
    }

    /**
     * Returns the longitude value relative to the Greenwich Meridian, expressed in the specified
     * units. This convenience method makes it easier to obtain longitude in decimal degrees
     * ({@code getGreenwichLongitude(NonSI.DEGREE_ANGLE)}), regardless of the underlying
     * angular units of this prime meridian.
     *
     * @param targetUnit The unit in which to express longitude.
     * @return The Greenwich longitude in the given units.
     */
    public double getGreenwichLongitude(final Unit<Angle> targetUnit) {
        return getAngularUnit().getConverterTo(targetUnit).convert(getGreenwichLongitude());
    }

    /**
     * Returns the angular unit of the {@linkplain #getGreenwichLongitude Greenwich longitude}.
     */
    @Override
    public Unit<Angle> getAngularUnit() {
        return angularUnit;
    }

    /**
     * Compare this prime meridian with the specified object for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true; // Slight optimization.
        }
        if (super.equals(object, mode)) {
            switch (mode) {
                case STRICT: {
                    final DefaultPrimeMeridian that = (DefaultPrimeMeridian) object;
                    return Utilities.equals(this.greenwichLongitude, that.greenwichLongitude) &&
                             Objects.equals(this.angularUnit,        that.angularUnit);
                }
                case BY_CONTRACT: {
                    final PrimeMeridian that = (PrimeMeridian) object;
                    return Utilities.equals(getGreenwichLongitude(), that.getGreenwichLongitude()) &&
                             Objects.equals(getAngularUnit(),        that.getAngularUnit());
                }
                default: {
                    final DefaultPrimeMeridian that = castOrCopy((PrimeMeridian) object);
                    return epsilonEqual(this.getGreenwichLongitude(NonSI.DEGREE_ANGLE),
                                        that.getGreenwichLongitude(NonSI.DEGREE_ANGLE), mode);
                    /*
                     * Note: if mode==IGNORE_METADATA, we relax the unit check because EPSG uses
                     *       sexagesimal degrees for the Greenwich meridian. Requirying the same
                     *       unit prevent Geodetic.isWGS84(...) method to recognize EPSG's WGS84.
                     */
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(greenwichLongitude, super.computeHashCode());
    }

    /**
     * Formats the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#PRIMEM"><cite>Well
     * Known Text</cite> (WKT)</A> element.
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name, which is {@code "PRIMEM"}.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public String formatWKT(final Formatter formatter) {
        /*
         * If the PrimeMeridian is written inside a "GEOGCS", then OGC say that it must be
         * written in the unit of the enclosing geographic coordinate system. Otherwise,
         * default to decimal degrees. Note that ESRI and GDAL don't follow this rule.
         */
        Unit<Angle> context = formatter.getConvention().forcedAngularUnit;
        if (context == null) {
            context = formatter.getAngularUnit();
            if (context == null) {
                context = NonSI.DEGREE_ANGLE;
            }
        }
        formatter.append(getGreenwichLongitude(context));
        return "PRIMEM";
    }
}
