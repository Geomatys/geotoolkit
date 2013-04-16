/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.jaxb.gco;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.opengis.util.InternationalString;
import org.apache.sis.util.iso.SimpleInternationalString;


/**
 * JAXB adapter for string values mapped to {@link InternationalString}. At the difference of
 * {@link InternationalStringAdapter}, this converter doesn't wrap the string in a new object.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class InternationalStringConverter extends XmlAdapter<String,InternationalString> {
    /**
     * Empty constructor for JAXB.
     */
    public InternationalStringConverter() {
    }

    /**
     * Converts an object read from a XML stream to an {@link InternationalString}
     * implementation. JAXB invokes automatically this method at unmarshalling time.
     *
     * @param  adapter The adapter for the string value.
     * @return An {@link InternationalString} for the string value.
     */
    @Override
    public InternationalString unmarshal(final String adapter) {
        return (adapter != null) ? new SimpleInternationalString(adapter) : null;
    }

    /**
     * Converts an {@link InternationalString} to an object to formatted into a
     * XML stream. JAXB invokes automatically this method at marshalling time.
     *
     * @param  value The string value.
     * @return The adapter for the string.
     */
    @Override
    public String marshal(final InternationalString value) {
        return (value != null) ? value.toString() : null;
    }
}
