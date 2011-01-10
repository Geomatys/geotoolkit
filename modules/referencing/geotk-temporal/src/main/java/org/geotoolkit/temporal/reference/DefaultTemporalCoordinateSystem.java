/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import org.geotoolkit.util.Utilities;
import org.geotoolkit.temporal.object.DefaultTemporalCoordinate;

import org.opengis.metadata.extent.Extent;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.temporal.TemporalCoordinate;
import org.opengis.temporal.TemporalCoordinateSystem;
import org.opengis.util.InternationalString;

import static org.geotoolkit.temporal.object.TemporalConstants.*;

/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public class DefaultTemporalCoordinateSystem extends DefaultTemporalReferenceSystem implements TemporalCoordinateSystem {

    /**
     * The origin of the scale, it must be specified in the Gregorian calendar with time of day in UTC.
     */
    private Date origin;
    /**
     * The name of a single unit of measure used as the base interval for the scale.
     * it shall be one of those units of measure for time specified by ISO 31-1, or a multiple of one of those units, as specified by ISO 1000.
     */
    private InternationalString interval;

    public DefaultTemporalCoordinateSystem(final ReferenceIdentifier name, final Extent domainOfValidity, final Date origin, final InternationalString interval) {
        super(name, domainOfValidity);
        this.origin = origin;
        this.interval = interval;
    }

    public void setOrigin(final Date origin) {
        this.origin = origin;
    }

    public void setInterval(final InternationalString interval) {
        this.interval = interval;
    }

    @Override
    public Date getOrigin() {
        return origin;
    }

    @Override
    public InternationalString getInterval() {
        return interval;
    }

    /**
     * Returns the equivalent Date in the Gregorian calendar and UTC of a coordinate value defined in this temporal coordinate system.
     * @param c_value
     * @return Date
     */
    @Override
    public Date transformCoord(final TemporalCoordinate c_value) {
        Date response;

        DefaultTemporalCoordinate value = (DefaultTemporalCoordinate) c_value;
        Number f = 0;
        if (value.getFrame() != null && value.getFrame() instanceof TemporalCoordinateSystem) {
            if (value.getCoordinateValue() != null) {
                final String interStr = interval.toString();
                final float n = value.getCoordinateValue().floatValue();


                if (YEAR_STR.equals(interStr)) {
                    f = n * (float) YEAR_MS;
                } else if (MONTH_STR.equals(interStr)) {
                    f = n * (float) MONTH_MS;
                } else if (WEEK_STR.equals(interStr)) {
                    f = n * (float) WEEK_MS;
                } else if (DAY_STR.equals(interStr)) {
                    f = n * (float) DAY_MS;
                } else if (HOUR_STR.equals(interStr)) {
                    f = n * (float) HOUR_MS;
                } else if (MINUTE_STR.equals(interStr)) {
                    f = n * (float) MINUTE_MS;
                } else if (SECOND_STR.equals(interStr)) {
                    f = n * (float) SECOND_MS;
                } else if (MILLISECOND_STR.equals(interStr)) {
                    f = n;
                } else {
                    throw new IllegalArgumentException("The name of a single unit of measure used as the base interval for the scale in this current TemporalCoordinateSystem is not supported !");
                }
                response = new Date(origin.getTime() + f.longValue());
                return response;
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("The TemporalCoordinate argument must be a TemporalCoordinate ! ");
        }
    }

    /**
     * Returns the equivalent TemporalCoordinate of a Date in Gregorian Calendar.
     * Default of unit is millisecond.
     * @param dateTime
     * @return TemporalCoordinate
     */
    @Override
    public TemporalCoordinate transformDateTime(final Date dateTime) {
        final String intervalStr = interval.toString();
        Number coordinateValue = Math.abs(dateTime.getTime() - origin.getTime());
        final float val = coordinateValue.floatValue();

        if (YEAR_STR.equals(intervalStr)) {
            coordinateValue = Float.valueOf( val / YEAR_MS );
        } else if (MONTH_STR.equals(intervalStr)) {
            coordinateValue = Float.valueOf( val / MONTH_MS );
        } else if (WEEK_STR.equals(intervalStr)) {
            coordinateValue = Float.valueOf( val / WEEK_MS );
        } else if (DAY_STR.equals(intervalStr)) {
            coordinateValue = Float.valueOf( val / DAY_MS );
        } else if (HOUR_STR.equals(intervalStr)) {
            coordinateValue = Float.valueOf( val / HOUR_MS );
        } else if (MINUTE_STR.equals(intervalStr)) {
            coordinateValue = Float.valueOf( val / MINUTE_MS );
        } else if (SECOND_STR.equals(intervalStr)) {
            coordinateValue = Float.valueOf( val / SECOND_MS );
        }
        
        return new DefaultTemporalCoordinate(this, null, coordinateValue);
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultTemporalCoordinateSystem && super.equals(object)) {
            if (object instanceof DefaultTemporalCoordinateSystem) {
                final DefaultTemporalCoordinateSystem that = (DefaultTemporalCoordinateSystem) object;

                return Utilities.equals(this.interval, that.interval) &&
                        Utilities.equals(this.origin, that.origin);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.interval != null ? this.interval.hashCode() : 0);
        hash = 37 * hash + (this.origin != null ? this.origin.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("TemporalCoordinateSystem:").append('\n');
        if (interval != null) {
            s.append("interval:").append(interval).append('\n');
        }
        if (origin != null) {
            s.append("origin:").append(origin).append('\n');
        }
        return s.toString();
    }
}
