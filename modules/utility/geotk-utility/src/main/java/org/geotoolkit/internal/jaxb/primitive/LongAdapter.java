/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.internal.jaxb.primitive;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Surrounds long values by {@code <gco:Integer>}.
 * The ISO-19139 standard specifies that primitive types have to be surrounded by an element
 * which represents the type of the value, using the namespace {@code gco} linked to the
 * {@code http://www.isotc211.org/2005/gco} URL. The JAXB default behavior is to marshall
 * primitive Java types directly "as is", without wrapping the value in the required element.
 * The role of this class is to add such wrapping.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
public final class LongAdapter extends XmlAdapter<LongAdapter, Long> {
    /**
     * The long value to handle.
     */
    @XmlElement(name = "Integer")
    public Long value;

    /**
     * Empty constructor used only by JAXB.
     */
    public LongAdapter() {
    }

    /**
     * Constructs an adapter for the given value.
     *
     * @param value The value.
     */
    private LongAdapter(final Long value) {
        this.value = value;
    }

    /**
     * Allows JAXB to generate a Long object using the value found in the adapter.
     *
     * @param value The value wrapped in an adapter.
     * @return The long value extracted from the adapter.
     */
    @Override
    public Long unmarshal(final LongAdapter value) {
        if (value == null) {
            return null;
        }
        return value.value;
    }

    /**
     * Allows JAXB to change the result of the marshalling process, according to the
     * ISO-19139 standard and its requirements about primitive types.
     *
     * @param value The integer value we want to surround by an element representing its type.
     * @return An adaptation of the integer value, that is to say a integer value surrounded
     *         by {@code <gco:Integer>} element.
     */
    @Override
    public LongAdapter marshal(final Long value) {
        if (value == null) {
            return null;
        }
        return new LongAdapter(value);
    }
}
