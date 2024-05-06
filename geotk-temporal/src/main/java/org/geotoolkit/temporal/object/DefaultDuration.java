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
package org.geotoolkit.temporal.object;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;


/**
 * A data type to be used for describing length or distance in the temporal dimension.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @author Remi Marechal (Geomatys)
 */
public abstract class DefaultDuration implements TemporalAmount {
    /**
     * Return the current length {@link Duration} exprimate in milli seconds.
     *
     * @return the current length {@link Duration} exprimate in milli seconds.
     */
    public abstract long getTimeInMillis();

    @Override
    public List<TemporalUnit> getUnits() {
        return List.of(ChronoUnit.MILLIS);
    }

    @Override
    public long get(TemporalUnit unit) {
        if (unit == ChronoUnit.MILLIS) {
            return getTimeInMillis();
        }
        throw new UnsupportedTemporalTypeException("The only supported unit is ChronoUnit.MILLIS.");
    }

    @Override
    public Temporal addTo(Temporal temporal) {
        return ((Instant) temporal).plusMillis(getTimeInMillis());
    }

    @Override
    public Temporal subtractFrom(Temporal temporal) {
        return ((Instant) temporal).minusMillis(getTimeInMillis());
    }
}
