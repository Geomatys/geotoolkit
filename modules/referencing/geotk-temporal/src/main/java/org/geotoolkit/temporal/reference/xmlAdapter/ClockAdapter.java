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
package org.geotoolkit.temporal.reference.xmlAdapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.temporal.reference.DefaultClock;
import org.opengis.temporal.Clock;

/**
 * JAXB adapter for {@link DefaultClock} values mapped to {@link Clock}.
 *
 * @author Remi Marechal (Geomatys).
 * @author Guilhem Legal (Geomatys).
 */
public class ClockAdapter extends XmlAdapter<DefaultClock, Clock> {

    /**
     * Converts an object read from a XML stream to an {@link Clock}
     * implementation. JAXB invokes automatically this method at unmarshalling time.
     *
     * @param  v The adapter for the {@link Clock} value.
     * @return An {@link Clock} for the {@link DefaultClock} value.
     */
    @Override
    public Clock unmarshal(DefaultClock v) throws Exception {
        return v;
    }

    /**
     * Converts an {@link Clock} to an object to formatted into a
     * XML stream. JAXB invokes automatically this method at marshalling time.
     *
     * @param  value The {@link Clock} value.
     * @return The adapter for the {@link Clock}.
     */
    @Override
    public DefaultClock marshal(Clock v) throws Exception {
        return DefaultClock.castOrCopy(v);
    }
}
