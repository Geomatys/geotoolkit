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
import org.geotoolkit.temporal.reference.DefaultTemporalCoordinateSystem;
import org.opengis.temporal.TemporalCoordinateSystem;

/**
 *JAXB adapter for {@link DefaultTemporalCoordinateSystem} values mapped to {@link TemporalCoordinateSystem}.
 *
 * @author Remi Marechal (Geomatys).
 */
public class TemporalCoordinateSystemAdapter extends XmlAdapter<DefaultTemporalCoordinateSystem, TemporalCoordinateSystem> {

    /**
     * Converts an object read from a XML stream to an {@link TemporalCoordinateSystem}
     * implementation. JAXB invokes automatically this method at unmarshalling time.
     *
     * @param v the value which will be convert.
     * @return A {@link TemporalCoordinateSystem} for the {@link DefaultTemporalCoordinateSystem} value.
     * @throws Exception
     */
    @Override
    public TemporalCoordinateSystem unmarshal(DefaultTemporalCoordinateSystem v) throws Exception {
        return v;
    }

    /**
     * Converts an {@link TemporalCoordinateSystem} to an object to formatted into a
     * XML stream. JAXB invokes automatically this method at marshalling time.
     *
     * @param v the value which will be convert.
     * @return The adapter for the {@link TemporalCoordinateSystem}.
     * @see DefaultTemporalCoordinateSystem#castOrCopy(org.opengis.temporal.TemporalCoordinateSystem)
     */
    @Override
    public DefaultTemporalCoordinateSystem marshal(TemporalCoordinateSystem v) throws Exception {
        return DefaultTemporalCoordinateSystem.castOrCopy(v);
    }
}
