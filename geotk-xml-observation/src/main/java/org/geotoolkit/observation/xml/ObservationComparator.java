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

import java.time.temporal.Temporal;
import java.util.Comparator;
import org.geotoolkit.temporal.object.InstantWrapper;
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
            final Temporal timeBegin1;
            if (o1.getSamplingTime() instanceof Period p) {
                timeBegin1 = p.getBeginning();
            } else if (o1.getSamplingTime() instanceof InstantWrapper d) {
                timeBegin1 = d.getTemporal();
            } else {
                throw new IllegalArgumentException("Unexpected time Object:" + o1.getSamplingTime());
            }
            final Temporal timeBegin2;
            if (o2.getSamplingTime() instanceof Period p) {
                timeBegin2 = p.getBeginning();
            } else if (o2.getSamplingTime() instanceof InstantWrapper d) {
                timeBegin2 = d.getTemporal();
            } else {
                throw new IllegalArgumentException("Unexpected time Object:" + o2.getSamplingTime());
            }
            return ((Comparable) timeBegin1).compareTo(timeBegin2);     // TODO: does not work if not the same class.
        } else if (o1.getSamplingTime() != null) {
            return -1;
        } else if (o2.getSamplingTime() != null) {
            return 1;
        } else {
            return 0;
        }
    }

}
