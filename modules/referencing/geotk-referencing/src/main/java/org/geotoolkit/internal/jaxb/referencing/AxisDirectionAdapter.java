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
package org.geotoolkit.internal.jaxb.referencing;

import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.internal.jaxb.referencing.AxisDirectionAdapter.AxisDirectionType;
import org.opengis.referencing.cs.AxisDirection;


/**
 * JAXB adapter for {@link AxisDirection}, in order to integrate the value in an element
 * complying with ISO-19139 standard.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public final class AxisDirectionAdapter extends XmlAdapter<AxisDirectionType, AxisDirection> {

    private AxisDirectionType axisDirection;

    /**
     * Empty constructor for JAXB only.
     */
    private AxisDirectionAdapter() {
    }

    /**
     * Wraps an axis direction value at marshalling-time.
     *
     * @param axis The value to marshall.
     */
    public AxisDirectionAdapter(final AxisDirection axis) {
        axisDirection = new AxisDirectionType();
        axisDirection.value = axis.identifier();
        axisDirection.codeSpace = "EPSG";
    }

    /**
     * Substitutes the adapter value read from an XML stream by the object which will
     * contains the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param  adapter The adapter for this metadata value.
     * @return A code list which represents the metadata value.
     */
    @Override
    public AxisDirection unmarshal(final AxisDirectionType axisDirection) {
        if (axisDirection != null) {
            return AxisDirection.valueOf(axisDirection.value);
        }
        return null;
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

    public static class AxisDirectionType {

        /**
         * The XML value.
         */
        @XmlValue
        public String value;
        /**
         * The code space as a XML attribute. This is often {@code "EPSG"}.
         */
        @XmlAttribute
        public String codeSpace;

        private AxisDirectionType() {

        }

        public AxisDirectionType(AxisDirection value) {
           this.codeSpace = "EPSG";
           this.value     = value.identifier();
        }

    }
}
