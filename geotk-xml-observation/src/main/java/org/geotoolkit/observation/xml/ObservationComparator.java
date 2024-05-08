/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.observation.xml;

import java.time.Instant;
import java.util.Comparator;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.opengis.observation.Observation;
import org.opengis.temporal.Period;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ObservationComparator implements Comparator<Observation> {

    @Override
    public int compare(Observation o1, Observation o2) {
        if (o1.getSamplingTime() != null && o2.getSamplingTime() != null) {
            final Instant timeBegin1;
            if (o1.getSamplingTime() instanceof Period) {
                timeBegin1 = ((Period)o1.getSamplingTime()).getBeginning();
            } else if (o1.getSamplingTime() instanceof DefaultInstant d) {
                timeBegin1 = d.getInstant();
            } else {
                throw new IllegalArgumentException("Unexpected time Object:" + o1.getSamplingTime());
            }
            final Instant timeBegin2;
            if (o2.getSamplingTime() instanceof Period) {
                timeBegin2 = ((Period)o2.getSamplingTime()).getBeginning();
            } else if (o2.getSamplingTime() instanceof DefaultInstant d) {
                timeBegin2 = d.getInstant();
            } else {
                throw new IllegalArgumentException("Unexpected time Object:" + o2.getSamplingTime());
            }
            return timeBegin1.compareTo(timeBegin2);
        } else if (o1.getSamplingTime() != null) {
            return -1;
        } else if (o2.getSamplingTime() != null) {
            return 1;
        } else {
            return 0;
        }
    }

}
