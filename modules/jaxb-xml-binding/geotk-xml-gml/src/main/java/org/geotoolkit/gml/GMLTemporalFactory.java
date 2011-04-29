/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml;

import java.util.Date;
import org.geotoolkit.gml.xml.v311.TimeInstantType;
import org.geotoolkit.gml.xml.v311.TimePeriodType;
import org.geotoolkit.gml.xml.v311.TimePositionType;
import org.geotoolkit.temporal.factory.DefaultTemporalFactory;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.Position;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class GMLTemporalFactory extends DefaultTemporalFactory {

    @Override
    protected void setOrdering(Organizer orgnzr) {
        orgnzr.before(DefaultTemporalFactory.class, false);
    }
    
    @Override
    public Instant createInstant(Position pstn) {
        return new TimeInstantType(pstn);
    }

    @Override
    public Period createPeriod(Instant begin, Instant end) {
       Position beginPosition = null;
       if (begin != null) {
            beginPosition = begin.getPosition();
       }
       Position endPosition = null;
       if (end != null) {
            endPosition = end.getPosition();
       }
       return new TimePeriodType(beginPosition, endPosition);
    }

    @Override
    public Position createPosition(Date date) {
        return new TimePositionType(date);
    }
}
