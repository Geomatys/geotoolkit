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

import java.util.Date;
import java.util.Map;
import java.util.Collections;
import javax.xml.bind.annotation.XmlTransient;
import org.opengis.referencing.datum.TemporalDatum;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.referencing.datum.AbstractDatum.name;


/**
 * A temporal datum defines the origin of a temporal coordinate reference system.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 4.00
 *
 * @since 1.2
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@XmlTransient
public class DefaultTemporalDatum extends org.apache.sis.referencing.datum.DefaultTemporalDatum {
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
    public DefaultTemporalDatum(final TemporalDatum datum) {
        super(datum);
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
        super(properties, origin);
    }
}
