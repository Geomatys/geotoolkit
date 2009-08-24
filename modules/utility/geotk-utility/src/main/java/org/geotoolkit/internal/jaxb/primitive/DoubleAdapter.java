/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
 * Surrounds double values by {@code <gco:Decimal>}.
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
public final class DoubleAdapter extends XmlAdapter<DoubleAdapter, Double> {
    /**
     * Frequently used constants.
     */
    private static final DoubleAdapter
            P0   = new DoubleAdapter(   0.0),
            P1   = new DoubleAdapter(   1.0),
            N1   = new DoubleAdapter(  -1.0),
            P45  = new DoubleAdapter(  45.0),
            N45  = new DoubleAdapter( -45.0),
            P90  = new DoubleAdapter(  90.0),
            N90  = new DoubleAdapter( -90.0),
            P180 = new DoubleAdapter( 180.0),
            N180 = new DoubleAdapter(-180.0),
            P360 = new DoubleAdapter( 360.0),
            N360 = new DoubleAdapter(-360.0);

    /**
     * The double value to handle.
     */
    @XmlElement(name = "Decimal")
    public Double value;

    /**
     * Empty constructor used only by JAXB.
     */
    public DoubleAdapter() {
    }

    /**
     * Constructs an adapter for the given value.
     *
     * @param value The value.
     */
    private DoubleAdapter(final Double value) {
        this.value = value;
    }

    /**
     * Allows JAXB to generate a Double object using the value found in the adapter.
     *
     * @param value The value wrapped in an adapter.
     * @return The double value extracted from the adapter.
     */
    @Override
    public Double unmarshal(final DoubleAdapter value) {
        if (value == null) {
            return null;
        }
        return value.value;
    }

    /**
     * Allows JAXB to change the result of the marshalling process, according to the
     * ISO-19139 standard and its requirements about primitive types.
     *
     * @param value The double value we want to surround by an element representing its type.
     * @return An adaptation of the double value, that is to say a double value surrounded
     *         by {@code <gco:Decimal>} element.
     */
    @Override
    public DoubleAdapter marshal(final Double value) {
        if (value == null) {
            return null;
        }
        final DoubleAdapter c;
        final int index = value.intValue();
        if (index == value.doubleValue()) {
            switch (index) {
                case    0: c = P0;   break;
                case    1: c = P1;   break;
                case   -1: c = N1;   break;
                case   45: c = P45;  break;
                case  -45: c = N45;  break;
                case   90: c = P90;  break;
                case  -90: c = N90;  break;
                case  180: c = P180; break;
                case -180: c = N180; break;
                case  360: c = P360; break;
                case -360: c = N360; break;
                default: c = new DoubleAdapter(value);
            }
        } else {
            c = new DoubleAdapter(value);
        }
        assert value.equals(c.value) : value;
        return c;
    }
}
