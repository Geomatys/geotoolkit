/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
 * Surrounds float values by {@code <gco:Decimal>}.
 * The ISO-19139 standard specifies that primitive types have to be surrounded by an element
 * which represents the type of the value, using the namespace {@code gco} linked to the
 * {@link http://www.isotc211.org/2005/gco} URL. The JAXB default behavior is to marshall
 * primitive Java types directly "as is", without wrapping the value in the required element.
 * The role of this class is to add such wrapping.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
public final class FloatAdapter extends XmlAdapter<FloatAdapter, Float> {
    /**
     * The float value to handle.
     */
    @XmlElement(name = "Decimal")
    public Float value;

    /**
     * Empty constructor used only by JAXB.
     */
    public FloatAdapter() {
    }

    /**
     * Constructs an adapter for the given value.
     *
     * @param value The value.
     */
    private FloatAdapter(final Float value) {
        this.value = value;
    }

    /**
     * Allows JAXB to generate a Float object using the value found in the adapter.
     *
     * @param value The value wrapped in an adapter.
     * @return The float value extracted from the adapter.
     */
    @Override
    public Float unmarshal(final FloatAdapter value) {
        if (value == null) {
            return null;
        }
        return value.value;
    }

    /**
     * Allows JAXB to change the result of the marshalling process, according to the
     * ISO-19139 standard and its requirements about primitive types.
     *
     * @param value The float value we want to surround by an element representing its type.
     * @return An adaptation of the float value, that is to say a float value surrounded
     *         by {@code <gco:Decimal>} element.
     */
    @Override
    public FloatAdapter marshal(final Float value) {
        if (value == null) {
            return null;
        }
        return new FloatAdapter(value);
    }
}
