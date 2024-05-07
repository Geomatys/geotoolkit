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
package org.geotoolkit.temporal.factory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.temporal.object.DefaultTemporalPosition;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalFactory;
import org.opengis.temporal.TemporalPosition;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DefaultTemporalFactory implements TemporalFactory {
    /**
     * Count to ensure period unicity.
     */
    private long periodCount;

    /**
     * Count to ensure instant unicity.
     */
    private long instantCount;

    public DefaultTemporalFactory() {
    }

    @Override
    public Period createPeriod(final Instant begin, final Instant end) {
        final Map<String, Object> prop = new HashMap<>();
        prop.put(IdentifiedObject.NAME_KEY, "period" + periodCount++);
        return new DefaultPeriod(prop, begin, end);
    }

    @Override
    public Instant createInstant(final Date instant) {
        final Map<String, Object> prop = new HashMap<>();
        prop.put(IdentifiedObject.NAME_KEY, "instant" + instantCount++);
        if (instant != null) {
            return new DefaultInstant(prop, instant);
        } else {
            final TemporalPosition position = new DefaultTemporalPosition(CommonCRS.Temporal.JULIAN.crs(), IndeterminateValue.UNKNOWN);
            return new DefaultInstant(prop, position);
        }
    }

    @Override
    public TemporalPosition createTemporalPosition(final TemporalCRS frame,
            final IndeterminateValue indeterminatePosition) {
        return new DefaultTemporalPosition(frame, indeterminatePosition);
    }
}
