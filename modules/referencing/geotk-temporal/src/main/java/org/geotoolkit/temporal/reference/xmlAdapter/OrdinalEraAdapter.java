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
import org.geotoolkit.temporal.reference.DefaultOrdinalEra;
import org.opengis.temporal.OrdinalEra;

/**
 * JAXB adapter for {@link DefaultOrdinalEra} values mapped to {@link OrdinalEra}.
 *
 * @author Remi Marechal (Geomatys).
 * @author Guilhem Legal (Geomatys).
 * @version 4.0
 * @since   4.0
 */
public class OrdinalEraAdapter extends XmlAdapter<DefaultOrdinalEra, OrdinalEra> {

    /**
     * Converts an object read from a XML stream to an {@link OrdinalEra}
     * implementation. JAXB invokes automatically this method at unmarshalling time.
     *
     * @param  adapter The adapter for the string value.
     * @return An {@link OrdinalEra} for the {@link DefaultOrdinalEra} value.
     */
    @Override
    public OrdinalEra unmarshal(DefaultOrdinalEra v) throws Exception {
        return v;
    }

    /**
     * Converts an {@link OrdinalEra} to an object to formatted into a
     * XML stream. JAXB invokes automatically this method at marshalling time.
     *
     * @param  value The string value.
     * @return The adapter for the string.
     */
    @Override
    public DefaultOrdinalEra marshal(OrdinalEra v) throws Exception {
        return (DefaultOrdinalEra) v;
    }
    
}
