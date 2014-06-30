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

import java.util.Map;
import java.util.Collections;
import net.jcip.annotations.Immutable;
import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.datum.TemporalDatum;
import org.geotoolkit.referencing.cs.DefaultTimeCS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.AbstractReferenceSystem;
import org.geotoolkit.referencing.datum.DefaultTemporalDatum;
import javax.xml.bind.annotation.XmlTransient;


/**
 * A 1D coordinate reference system used for the recording of time.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CS type(s)</TH></TR>
 * <TR><TD>
 *   {@link TimeCS Time}
 * </TD></TR></TABLE>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 1.2
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Immutable
@Deprecated
@XmlTransient
public class DefaultTemporalCRS extends org.apache.sis.referencing.crs.DefaultTemporalCRS {
    /**
     * Time measured in days since January 1st, 4713 BC at 12:00 UTC.
     *
     * @see DefaultTemporalDatum#JULIAN
     * @see DefaultTimeCS#DAYS
     *
     * @since 2.5
     */
    public static final DefaultTemporalCRS JULIAN = new DefaultTemporalCRS(
            DefaultTemporalDatum.JULIAN, DefaultTimeCS.DAYS);

    /**
     * Time measured in days since November 17, 1858 at 00:00 UTC.
     * A <cite>Modified Julian day</cite> (MJD) is defined relative to <cite>Julian day</cite>
     * (JD) as {@code MJD = JD − 2400000.5}.
     *
     * @see DefaultTemporalDatum#MODIFIED_JULIAN
     * @see DefaultTimeCS#DAYS
     *
     * @since 2.5
     */
    public static final DefaultTemporalCRS MODIFIED_JULIAN = new DefaultTemporalCRS(
            DefaultTemporalDatum.MODIFIED_JULIAN, DefaultTimeCS.DAYS);

    /**
     * Time measured in days since May 24, 1968 at 00:00 UTC. This epoch was introduced by NASA
     * for the space program. A <cite>Truncated Julian day</cite> (TJD) is defined relative to
     * <cite>Julian day</cite> (JD) as {@code TJD = JD − 2440000.5}.
     *
     * @see DefaultTemporalDatum#TRUNCATED_JULIAN
     * @see DefaultTimeCS#DAYS
     *
     * @since 2.5
     */
    public static final DefaultTemporalCRS TRUNCATED_JULIAN = new DefaultTemporalCRS(
            DefaultTemporalDatum.TRUNCATED_JULIAN, DefaultTimeCS.DAYS);

    /**
     * Time measured in days since December 31, 1899 at 12:00 UTC.
     * A <cite>Dublin Julian day</cite> (DJD) is defined relative to <cite>Julian day</cite> (JD)
     * as {@code DJD = JD − 2415020}.
     *
     * @see DefaultTemporalDatum#DUBLIN_JULIAN
     * @see DefaultTimeCS#DAYS
     *
     * @since 2.5
     */
    public static final DefaultTemporalCRS DUBLIN_JULIAN = new DefaultTemporalCRS(
            DefaultTemporalDatum.DUBLIN_JULIAN, DefaultTimeCS.DAYS);

    /**
     * Time measured in seconds since January 1st, 1970 at 00:00 UTC.
     *
     * @see DefaultTemporalDatum#UNIX
     * @see DefaultTimeCS#SECONDS
     *
     * @since 2.5
     */
    public static final DefaultTemporalCRS UNIX = new DefaultTemporalCRS(
            DefaultTemporalDatum.UNIX, DefaultTimeCS.SECONDS);

    /**
     * Time measured in milliseconds since January 1st, 1970 at 00:00 UTC.
     *
     * @see DefaultTemporalDatum#UNIX
     * @see DefaultTimeCS#MILLISECONDS
     *
     * @since 2.5
     */
    public static final DefaultTemporalCRS JAVA = new DefaultTemporalCRS(
            DefaultTemporalDatum.UNIX, DefaultTimeCS.MILLISECONDS);

    /**
     * Constructs a new temporal CRS with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param crs The coordinate reference system to copy.
     *
     * @since 2.2
     *
     * @see #castOrCopy(TemporalCRS)
     */
    public DefaultTemporalCRS(final TemporalCRS crs) {
        super(crs);
    }

    /**
     * Constructs a temporal CRS with the same properties than the given datum.
     * The inherited properties include the {@linkplain #getName name} and aliases.
     *
     * @param datum The datum.
     * @param cs The coordinate system.
     *
     * @since 2.5
     */
    public DefaultTemporalCRS(final TemporalDatum datum, final TimeCS cs) {
        this(IdentifiedObjects.getProperties(datum), datum, cs);
    }

    /**
     * Constructs a temporal CRS from a name.
     *
     * @param name The name.
     * @param datum The datum.
     * @param cs The coordinate system.
     */
    public DefaultTemporalCRS(final String         name,
                              final TemporalDatum datum,
                              final TimeCS           cs)
    {
        this(Collections.singletonMap(NAME_KEY, name), datum, cs);
    }

    /**
     * Constructs a temporal CRS from a set of properties. The properties are given unchanged to
     * the {@linkplain AbstractReferenceSystem#AbstractReferenceSystem(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param cs The coordinate system.
     * @param datum The datum.
     */
    public DefaultTemporalCRS(final Map<String,?> properties,
                              final TemporalDatum datum,
                              final TimeCS        cs)
    {
        super(properties, datum, cs);
    }
}
