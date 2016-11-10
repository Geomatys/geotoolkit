/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2014, Geomatys
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
 */
package org.geotoolkit.temporal.reference;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.measure.UnitConverter;
import javax.measure.quantity.Time;
import javax.measure.Unit;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.measure.Units;

import org.geotoolkit.temporal.object.DefaultTemporalCoordinate;

import org.opengis.temporal.TemporalCoordinate;
import org.opengis.temporal.TemporalCoordinateSystem;

/**
 * A temporal coordinate system to simplify the computation of temporal distances
 * between points and the functional description of temporal operations.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 * @version 4.0
 * @since   4.0
 */
@XmlType(name = "TimeCoordinateSystem_Type", propOrder = {
    "origin",
    "interval"
})
@XmlRootElement(name = "TimeCoordinateSystem")
public class DefaultTemporalCoordinateSystem extends DefaultTemporalReferenceSystem implements TemporalCoordinateSystem {

    /**
     * Milli-second unity.
     *
     * @see #transformCoord(org.opengis.temporal.TemporalCoordinate)
     * @see #transformDateTime(java.util.Date)
     */
    private static Unit<Time> UNIT_MS = Units.SECOND.divide(1000);

    /**
     * The origin of the scale, it must be specified in the Gregorian calendar with time of day in UTC.
     */
    private Date origin;

    /**
     * The name of a single unit of measure used as the base interval for the scale.
     * it shall be one of those units of measure for time specified by ISO 31-1,
     * or a multiple of one of those units, as specified by ISO 1000.
     */
    private Unit<Time> interval;


    /**
     * Converter use to convert units from this {@link DefaultTemporalCoordinateSystem} to milli-second.
     *
     * @see #transformCoord(org.opengis.temporal.TemporalCoordinate)
     */
    private UnitConverter unitToMS = null;

    /**
     * Converter use to convert unit in milli-second into unit from this {@link DefaultTemporalCoordinateSystem}.
     *
     * @see #transformDateTime(java.util.Date)
     */
    private UnitConverter msToUnit = null;

    /**
     * Create a default {@link TemporalCoordinateSystem} implementation initialize with the given parameters.
     *  The properties given in argument follow the same rules than for the
     * {@linkplain DefaultTemporalReferenceSystem#DefaultTemporalReferenceSystem(java.util.Map, org.opengis.referencing.datum.TemporalDatum, org.opengis.referencing.cs.TimeCS)   super-class constructor}.
     * The following table is a reminder of current main (not all) properties:
     *
     * <table class="ISO 19108">
     *   <caption>Recognized properties (non exhaustive list)</caption>
     *   <tr>
     *     <th>Property name</th>
     *     <th>Value type</th>
     *     <th>Returned by</th>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#NAME_KEY}</td>
     *     <td>{@link org.opengis.referencing.Identifier} or {@link String}</td>
     *     <td>{@link #getName()}</td>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.datum.Datum#DOMAIN_OF_VALIDITY_KEY}</td>
     *     <td>{@link org.opengis.metadata.extent.Extent}</td>
     *     <td>{@link #getDomainOfValidity()}</td>
     *   </tr>
     * </table>
     *
     * @param properties The properties to be given to the coordinate reference system.
     * @param interval unit of measure used as the base interval for the scale.
     * @param origin position of the origin of the scale on which the temporal coordinate system is based
     * expressed as a date in the Gregorian calendar and time of day in UTC.
     */
    public DefaultTemporalCoordinateSystem(Map<String, ?> properties, Unit<Time> interval, Date origin) {
        super(properties);
        this.origin        = origin;
        ArgumentChecks.ensureNonNull("interval", interval);
        this.interval      = interval;
    }

//    /**
//     * Empty constructor only use for XML marshalling.
//     */
//    DefaultTemporalCoordinateSystem() {
//    }

    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The TemporalReferenceSystem to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(TemporalCoordinateSystem)
     */
    public DefaultTemporalCoordinateSystem(final TemporalCoordinateSystem object) {
        super(object);
        if (object != null) {
            this.origin = object.getOrigin();
            final Unit<Time> inter = object.getInterval();
            ArgumentChecks.ensureNonNull("interval", inter);
            this.interval = inter;
        }
    }

    /**
     * Returns a Geotk implementation with the values of the given arbitrary implementation.
     * This method performs the first applicable action in the following choices:
     *
     * <ul>
     *   <li>If the given object is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given object is already an instance of
     *       {@code DefaultTemporalCoordinateSystem}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultTemporalCoordinateSystem} instance is created using the
     *       {@linkplain #DefaultTemporalCoordinateSystem(TemporalCoordinateSystem) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultTemporalCoordinateSystem castOrCopy(final TemporalCoordinateSystem object) {
        if (object == null || object instanceof DefaultTemporalCoordinateSystem) {
            return (DefaultTemporalCoordinateSystem) object;
        }
        return new DefaultTemporalCoordinateSystem(object);
    }

    /**
     * Returns position of the origin of the scale on which the temporal coordinate system is based.
     * <blockquote><font size="-1">The origin shall be specified in the Gregorian
     * calendar with time of day in UTC. The {@linkplain Date DateTime} may be truncated
     * to the appropriate level of resolution}.</font></blockquote>
     *
     * @return position of the origin of the scale on which the temporal coordinate system is based.
     */
    @Override
    @XmlElement(name = "originPosition", required = true)
    public Date getOrigin() {
        return origin;
    }

    /**
     * Returns the name of the single unit of measure used as the base interval for the scale.
     * <blockquote><font size="-1">The time interval selected as appropriate for the application,
     * but it shall be one of those units of measure for time specified by ISO 31-1,
     * or multiple of one of those units, as specified by ISO 1000.</font></blockquote>
     *
     * @return Standard unit of time used to measure duration on the axis of the coordinate system.
     */
    @Override
    public Unit<Time> getInterval() {
        return interval;
    }

    /**
     * Only use for XML binding.
     *
     * @return An {@link Interval} object adapted for XML binding.
     */
    @XmlElement(name = "interval", required = true)
    private Interval getinterval() {
        return new Interval(interval);
    }

    /**
     * Returns a transformation from a value of a {@linkplain TemporalCoordinate coordinate} within this
     * temporal coordinate system and returns the equivalent {@linkplain DateAndTime date
     * and time} in the Gregorian Calendar and UTC.
     *
     * @param c_value The {@linkplain TemporalCoordinate coordinate} which will be transformed.
     * @return Convertion of a {@linkplain TemporalCoordinate coordinate} in this coordinate system
     *  to a date in the Gregorian calendar and a time in UTC.
     */
    @Override
    public Date transformCoord(final TemporalCoordinate c_value) {

        if (unitToMS == null) unitToMS = interval.getConverterTo(UNIT_MS);
        Date response;
        DefaultTemporalCoordinate value = (DefaultTemporalCoordinate) c_value;
//        Number f = 0;
        if (value.getFrame() != null && value.getFrame() instanceof TemporalCoordinateSystem) {
            if (value.getCoordinateValue() != null) {
                final String interStr = interval.toString();
                final float n = value.getCoordinateValue().floatValue();
                double f = unitToMS.convert(n);
//                if (YEAR_STR.equals(interStr)) {
//                    f = n * (float) YEAR_MS;
//                } else if (MONTH_STR.equals(interStr)) {
//                    f = n * (float) MONTH_MS;
//                } else if (WEEK_STR.equals(interStr)) {
//                    f = n * (float) WEEK_MS;
//                } else if (DAY_STR.equals(interStr)) {
//                    f = n * (float) DAY_MS;
//                } else if (HOUR_STR.equals(interStr)) {
//                    f = n * (float) HOUR_MS;
//                } else if (MINUTE_STR.equals(interStr)) {
//                    f = n * (float) MINUTE_MS;
//                } else if (SECOND_STR.equals(interStr)) {
//                    f = n * (float) SECOND_MS;
//                } else if (MILLISECOND_STR.equals(interStr)) {
//                    f = n;
//                } else {
//                    throw new IllegalArgumentException("The name of a single unit of measure used as the base interval for the scale in this current TemporalCoordinateSystem is not supported !");
//                }
//                response = new Date(origin.getTime() + f.longValue());
                response = new Date(origin.getTime() + Double.doubleToLongBits(f));
                return response;
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("The TemporalCoordinate argument must be a TemporalCoordinate ! ");
        }
    }

    /**
     * Returns transformation of a {@linkplain DateAndTime date and time} in the Gregorian Calendar and UTC
     * to an equivalent {@linkplain TemporalCoordinate coordinate} within this temporal
     * coordinate system.
     *
     * @param dateTime  The {@linkplain DateAndTime date and time} which will be converted.
     * @return Convertion of a date in the Gregorian calendar and a time in UTC to a coordinate
     * in this temporal coordinate system.
     */
    @Override
    public TemporalCoordinate transformDateTime(final Date dateTime) {
        if (msToUnit == null) msToUnit = (unitToMS != null) ? unitToMS.inverse() : UNIT_MS.getConverterTo(interval);
//        final String intervalStr = interval.toString();
        Number coordinateValue = Math.abs(dateTime.getTime() - origin.getTime());
        final float val = coordinateValue.floatValue();

        coordinateValue = msToUnit.convert(val);
//        if (YEAR_STR.equals(intervalStr)) {
//            coordinateValue = Float.valueOf( val / YEAR_MS );
//        } else if (MONTH_STR.equals(intervalStr)) {
//            coordinateValue = Float.valueOf( val / MONTH_MS );
//        } else if (WEEK_STR.equals(intervalStr)) {
//            coordinateValue = Float.valueOf( val / WEEK_MS );
//        } else if (DAY_STR.equals(intervalStr)) {
//            coordinateValue = Float.valueOf( val / DAY_MS );
//        } else if (HOUR_STR.equals(intervalStr)) {
//            coordinateValue = Float.valueOf( val / HOUR_MS );
//        } else if (MINUTE_STR.equals(intervalStr)) {
//            coordinateValue = Float.valueOf( val / MINUTE_MS );
//        } else if (SECOND_STR.equals(intervalStr)) {
//            coordinateValue = Float.valueOf( val / SECOND_MS );
//        }

        return new DefaultTemporalCoordinate(this, null, coordinateValue);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object object, ComparisonMode mode) {
        if (object == this) return true;
        final boolean sup = super.equals(object, mode);
        if (!sup) return false;
//        if (object instanceof DefaultTemporalCoordinateSystem && super.equals(object)) {
            if (object instanceof DefaultTemporalCoordinateSystem) {
                final DefaultTemporalCoordinateSystem that = (DefaultTemporalCoordinateSystem) object;

                return Objects.equals(this.interval, that.interval) &&
                        Objects.equals(this.origin, that.origin);
            }
//        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected long computeHashCode() {
        int hash = 5;
        hash = 37 * hash + (this.interval != null ? this.interval.hashCode() : 0);
        hash = 37 * hash + (this.origin != null ? this.origin.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n').append("TemporalCoordinateSystem:").append('\n');
        if (interval != null) {
            s.append("interval:").append(interval).append('\n');
        }
        if (origin != null) {
            s.append("origin:").append(origin).append('\n');
        }
        return s.toString();
    }

    /**
     * Internal class only use to adapt ISO to XML in relation with "interval" XML element.
     */
    private static final class Interval {

        @XmlValue
        private final static double VALUE = 1.0;

        @XmlAttribute
        private final Unit<Time> unit;

        private Interval(Unit<Time> unit) {
            this.unit = unit;
        }
    }
}
