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
package org.geotoolkit.internal.jaxb.primitive;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Surrounds boolean value by {@code <gco:Boolean>}.
 * The ISO-19139 standard specifies that primitive types have to be surrounded by an element
 * which represents the type of the value, using the namespace {@code gco} linked to the
 * {@link http://www.isotc211.org/2005/gco} URL. The JAXB default behavior is to marshall
 * primitive Java types directly "as is", without wrapping the value in the required element.
 * The role of this class is to add such wrapping.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
public final class BooleanAdapter extends XmlAdapter<BooleanAdapter, Boolean> {
    /**
     * Wraps the {@link Boolean#TRUE} value.
     */
    private static final BooleanAdapter TRUE = new BooleanAdapter(Boolean.TRUE);

    /**
     * Wraps the {@link Boolean#FALSE} value.
     */
    private static final BooleanAdapter FALSE = new BooleanAdapter(Boolean.FALSE);

    /**
     * The boolean value to handle.
     * This field should be considered final after construction.
     */
    @XmlElement(name = "Boolean")
    public Boolean value;

    /**
     * Empty constructor used only by JAXB.
     */
    public BooleanAdapter() {
    }

    /**
     * Constructs an adapter for the given value.
     *
     * @param value The value.
     */
    private BooleanAdapter(final Boolean value) {
        this.value = value;
    }

    /**
     * Allows JAXB to generate a Boolean object using the value found in the adapter.
     *
     * @param value The value wrapped in an adapter.
     * @return The boolean value extracted from the adapter.
     */
    @Override
    public Boolean unmarshal(final BooleanAdapter value) {
        if (value == null) {
            return null;
        }
        return value.value;
    }

    /**
     * Allows JAXB to change the result of the marshalling process, according to the
     * ISO-19139 standard and its requirements about primitive types.
     *
     * @param value The boolean value we want to surround by an element representing its type.
     * @return An adaptation of the boolean value, that is to say a boolean value surrounded
     *         by {@code <gco:Boolean>} element.
     */
    @Override
    public BooleanAdapter marshal(final Boolean value) {
        if (value == null) {
            return null;
        }
        final BooleanAdapter c = value ? TRUE : FALSE;
        assert value.equals(c.value) : value;
        return c;
    }
}
