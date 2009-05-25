/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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
import java.util.Date;
import java.util.Map;

import org.opengis.util.InternationalString;
import org.opengis.referencing.datum.TemporalDatum;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.resources.Vocabulary;


/**
 * A temporal datum defines the origin of a temporal coordinate reference system.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
public class DefaultTemporalDatum extends AbstractDatum implements TemporalDatum {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 3357241732140076884L;

    /**
     * Datum for time measured since January 1st, 4713 BC at 12:00 UTC.
     *
     * @see org.geotoolkit.referencing.crs.DefaultTemporalCRS#JULIAN
     *
     * @since 2.5
     */
    public static final DefaultTemporalDatum JULIAN = new DefaultTemporalDatum(
            name(Vocabulary.Keys.JULIAN), new Date(-2440588 * (24*60*60*1000L) + (12*60*60*1000L)));

    /**
     * Datum for time measured since November 17, 1858 at 00:00 UTC.
     * A <cite>Modified Julian day</cite> (MJD) is defined relative to <cite>Julian day</cite>
     * (JD) as {@code MJD = JD − 2400000.5}.
     *
     * @see org.geotoolkit.referencing.crs.DefaultTemporalCRS#MODIFIED_JULIAN
     *
     * @since 2.5
     */
    public static final DefaultTemporalDatum MODIFIED_JULIAN = new DefaultTemporalDatum(
            name(Vocabulary.Keys.MODIFIED_JULIAN), new Date(-40587 * (24*60*60*1000L)));

    /**
     * Datum for time measured since May 24, 1968 at 00:00 UTC. This epoch was introduced by NASA
     * for the space program. A <cite>Truncated Julian day</cite> (TJD) is defined relative to
     * <cite>Julian day</cite> (JD) as {@code TJD = JD − 2440000.5}.
     *
     * @see org.geotoolkit.referencing.crs.DefaultTemporalCRS#TRUNCATED_JULIAN
     *
     * @since 2.5
     */
    public static final DefaultTemporalDatum TRUNCATED_JULIAN = new DefaultTemporalDatum(
            name(Vocabulary.Keys.TRUNCATED_JULIAN), new Date(-587 * (24*60*60*1000L)));

    /**
     * Datum for time measured since December 31, 1899 at 12:00 UTC.
     * A <cite>Dublin Julian day</cite> (DJD) is defined relative to <cite>Julian day</cite> (JD)
     * as {@code DJD = JD − 2415020}.
     *
     * @see org.geotoolkit.referencing.crs.DefaultTemporalCRS#DUBLIN_JULIAN
     *
     * @since 2.5
     */
    public static final DefaultTemporalDatum DUBLIN_JULIAN = new DefaultTemporalDatum(
            name(Vocabulary.Keys.DUBLIN_JULIAN), new Date(-25568 * (24*60*60*1000L) + (12*60*60*1000L)));

    /**
     * Default datum for time measured since January 1st, 1970 at 00:00 UTC.
     *
     * @see org.geotoolkit.referencing.crs.DefaultTemporalCRS#UNIX
     * @see org.geotoolkit.referencing.crs.DefaultTemporalCRS#JAVA
     */
    public static final DefaultTemporalDatum UNIX = new DefaultTemporalDatum(
            "UNIX", new Date(0));

    /**
     * The date and time origin of this temporal datum.
     */
    private final long origin;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultTemporalDatum() {
        this(org.geotoolkit.internal.referencing.NullReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new datum with the same values than the specified one.
     * This copy constructor provides a way to wrap an arbitrary implementation into a
     * Geotoolkit one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param datum The datum to copy.
     *
     * @since 2.2
     */
    public DefaultTemporalDatum(final TemporalDatum datum) {
        super(datum);
        origin = datum.getOrigin().getTime();
    }

    /**
     * Constructs a temporal datum from a name.
     *
     * @param name   The datum name.
     * @param origin The date and time origin of this temporal datum.
     */
    public DefaultTemporalDatum(final String name, final Date origin) {
        this(Collections.singletonMap(NAME_KEY, name), origin);
    }

    /**
     * Constructs a temporal datum from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractDatum#AbstractDatum(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param origin The date and time origin of this temporal datum.
     */
    public DefaultTemporalDatum(final Map<String,?> properties, final Date origin) {
        super(properties);
        ensureNonNull("origin", origin);
        this.origin = origin.getTime();
    }

    /**
     * The date and time origin of this temporal datum.
     *
     * @return The date and time origin of this temporal datum.
     */
    @Override
    public Date getOrigin() {
        return new Date(origin);
    }

    /**
     * Description of the point or points used to anchor the datum to the Earth.
     */
    @Override
    public InternationalString getAnchorPoint() {
        return super.getAnchorPoint();
    }

    /**
     * The time after which this datum definition is valid.
     */
    @Override
    public Date getRealizationEpoch() {
        return super.getRealizationEpoch();
    }

    /**
     * Compares this temporal datum with the specified object for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @param  compareMetadata {@code true} for performing a strict comparison, or
     *         {@code false} for comparing only properties relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final AbstractIdentifiedObject object, final boolean compareMetadata) {
        if (object == this) {
            return true; // Slight optimization.
        }
        if (super.equals(object, compareMetadata)) {
            final DefaultTemporalDatum that = (DefaultTemporalDatum) object;
            return this.origin == that.origin;
        }
        return false;
    }

    /**
     * Returns a hash value for this temporal datum. {@linkplain #getName Name},
     * {@linkplain #getRemarks remarks} and the like are not taken in account. In
     * other words, two temporal datums will return the same hash value if they
     * are equal in the sense of
     * <code>{@link #equals equals}(AbstractIdentifiedObject, <strong>false</strong>)</code>.
     *
     * @return The hash code value. This value doesn't need to be the same
     *         in past or future versions of this class.
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ (int)origin ^ (int)(origin >>> 32);
    }
}
