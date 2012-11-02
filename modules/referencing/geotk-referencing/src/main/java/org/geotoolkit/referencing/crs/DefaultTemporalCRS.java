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
import java.util.Date;
import java.util.Collections;
import javax.measure.quantity.Duration;
import javax.measure.converter.UnitConverter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.datum.TemporalDatum;

import org.geotoolkit.measure.Units;
import org.geotoolkit.referencing.cs.DefaultTimeCS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.AbstractReferenceSystem;
import org.geotoolkit.referencing.datum.DefaultTemporalDatum;


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
 */
@Immutable
@XmlRootElement(name = "TemporalCRS")
public class DefaultTemporalCRS extends AbstractSingleCRS implements TemporalCRS {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 3000119849197222007L;

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
     * A converter from values in this CRS to values in milliseconds.
     * Will be constructed only when first needed.
     */
    private transient UnitConverter toMillis;

    /**
     * The {@linkplain TemporalDatum#getOrigin origin} in milliseconds since January 1st, 1970.
     * This field could be implicit in the {@link #toMillis} converter, but we still handle it
     * explicitly in order to use integer arithmetic.
     */
    private transient long origin;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultTemporalCRS() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

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

    /**
     * Wraps an arbitrary temporal CRS into a Geotk implementation. This method is useful
     * if the user wants to take advantage of {@link #toDate} and {@link #toValue} methods.
     * If the supplied CRS is already an instance of {@code DefaultTemporalCRS} or is {@code null},
     * then it is returned unchanged.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultTemporalCRS castOrCopy(final TemporalCRS object) {
        return (object == null || object instanceof DefaultTemporalCRS)
                ? (DefaultTemporalCRS) object : new DefaultTemporalCRS(object);
    }

    /**
     * Initialize the fields required for {@link #toDate} and {@link #toValue} operations.
     */
    private void initializeConverter() {
        origin   = getDatum().getOrigin().getTime();
        toMillis = getCoordinateSystem().getAxis(0).getUnit().asType(Duration.class).getConverterTo(Units.MILLISECOND);
    }

    /**
     * Returns the coordinate system.
     */
    @Override
    @XmlElement(name="temporalCS")
    public TimeCS getCoordinateSystem() {
        return (TimeCS) super.getCoordinateSystem();
    }

    /**
     * Used by JAXB only (invoked by reflection).
     */
    final void setCoordinateSystem(final TimeCS cs) {
        super.setCoordinateSystem(cs);
    }

    /**
     * Returns the datum.
     */
    @Override
    @XmlElement(name="temporalDatum")
    public TemporalDatum getDatum() {
        return (TemporalDatum) super.getDatum();
    }

    /**
     * Used by JAXB only (invoked by reflection).
     */
    final void setDatum(final TemporalDatum datum) {
        super.setDatum(datum);
    }

    /**
     * Convert the given value into a {@link Date} object. If the given value is
     * {@link Double#NaN NaN} or infinite, then this method returns {@code null}.
     * This is consistent with usage in {@link org.geotoolkit.util.DateRange},
     * where the {@code null} value is used for unbounded ranges.
     * <p>
     * This method is the converse of {@link #toValue(Date)}.
     *
     * @param  value A value in this axis unit.
     * @return The value as a {@linkplain Date date}, or {@code null} if the given
     *         value is NaN or infinite.
     */
    public Date toDate(final double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return null;
        }
        if (toMillis == null) {
            initializeConverter();
        }
        return new Date(Math.round(toMillis.convert(value)) + origin);
    }

    /**
     * Convert the given {@linkplain Date date} into a value in this axis unit.
     * If the given time is {@code null}, then this method returns {@link Double#NaN NaN}.
     * <p>
     * This method is the converse of {@link #toDate(double)}.
     *
     * @param  time The value as a {@linkplain Date date}, or {@code null}.
     * @return value A value in this axis unit, or {@link Double#NaN NaN}
     *         if the given time is {@code null}.
     */
    public double toValue(final Date time) {
        if (time == null) {
            return Double.NaN;
        }
        if (toMillis == null) {
            initializeConverter();
        }
        return toMillis.inverse().convert(time.getTime() - origin);
    }
}
