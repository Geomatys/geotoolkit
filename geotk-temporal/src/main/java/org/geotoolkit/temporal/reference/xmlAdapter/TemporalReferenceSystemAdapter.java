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
import org.geotoolkit.temporal.reference.DefaultTemporalReferenceSystem;
import org.opengis.temporal.TemporalReferenceSystem;

/**
 * JAXB adapter for {@link DefaultClock} values mapped to {@link ClockTime}.
 *
 * @author Remi Marechal (Geomatys).
 */
public class TemporalReferenceSystemAdapter extends XmlAdapter<DefaultTemporalReferenceSystem, TemporalReferenceSystem>{

    /**
     * Converts an object read from a XML stream to an {@link TemporalReferenceSystem}
     * implementation. JAXB invokes automatically this method at unmarshalling time.
     *
     * @param v the value which will be convert.
     * @return A {@link TemporalReferenceSystem} for the {@link DefaultTemporalReferenceSystem} value.
     * @throws Exception
     */
    @Override
    public TemporalReferenceSystem unmarshal(DefaultTemporalReferenceSystem v) {
        return v;
    }

    /**
     * Converts an {@link TemporalReferenceSystem} to an object to formatted into a
     * XML stream. JAXB invokes automatically this method at marshalling time.
     *
     * @param v the value which will be convert.
     * @return The adapter for the {@link TemporalReferenceSystem}.
     * @see DefaultTemporalReferenceSystem#castOrCopy(org.opengis.temporal.TemporalReferenceSystem)
     */
    @Override
    public DefaultTemporalReferenceSystem marshal(TemporalReferenceSystem v) throws Exception {
        return DefaultTemporalReferenceSystem.castOrCopy(v);
    }
}
