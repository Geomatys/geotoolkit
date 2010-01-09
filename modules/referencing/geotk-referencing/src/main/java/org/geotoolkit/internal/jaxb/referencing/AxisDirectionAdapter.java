/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal.jaxb.referencing;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.opengis.referencing.cs.AxisDirection;
import org.geotoolkit.internal.CodeLists;


/**
 * JAXB adapter for {@link AxisDirection}, in order to integrate the value in an element
 * complying with ISO-19139 standard.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.02
 *
 * @since 3.00
 * @module
 */
public final class AxisDirectionAdapter extends XmlAdapter<AxisDirectionType, AxisDirection> {
    /**
     * Substitutes the adapter value read from an XML stream by the object which will
     * contains the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param  adapter The adapter for this metadata value.
     * @return A code list which represents the metadata value.
     */
    @Override
    public AxisDirection unmarshal(final AxisDirectionType adapter) {
        return (adapter != null) ? CodeLists.valueOf(AxisDirection.class, adapter.value) : null;
    }

    /**
     * Substitutes the code list by the adapter to be marshalled into an XML file
     * or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param  value The code list value.
     * @return The adapter for the given code list.
     */
    @Override
    public AxisDirectionType marshal(final AxisDirection value) {
        return new AxisDirectionType(value);
    }
}
