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
package org.geotoolkit.internal.jaxb.referencing;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.ReferenceIdentifier;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.ImmutableIdentifier;


/**
 * JAXB adapter mapping the GeoAPI {@link ReferenceIdentifier} to an implementation class that can
 * be marshalled. See the package documentation for more information about JAXB and interfaces.
 * <p>
 * The XML produced by this adapter use the GML syntax. The {@code ToString} inner class performs
 * a mapping in which only the code (without codespace) is marshalled.
 * <p>
 * Note that a class of the same name is defined in the {@link org.geotoolkit.internal.jaxb.metadata}
 * package, which serve the same purpose (wrapping exactly the same interface) but using the ISO 19139
 * syntax instead.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class RS_Identifier extends XmlAdapter<XMLReferenceIdentifier, ReferenceIdentifier> {
    /**
     * Substitutes the adapter value read from an XML stream by the object which will
     * contains the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param  adapter The adapter for this metadata value.
     * @return A metadata which represents the value.
     */
    @Override
    public ReferenceIdentifier unmarshal(final XMLReferenceIdentifier adapter) {
        if (adapter != null) {
            final Citation authority = Citations.fromName(adapter.codeSpace); // May be null.
            return new ImmutableIdentifier(authority, Citations.getIdentifier(authority), adapter.code);
        }
        return null;
    }

    /**
     * Substitutes the identifier by the adapter to be marshalled into an XML file
     * or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param  value The metadata value.
     * @return The adapter for the given metadata.
     */
    @Override
    public XMLReferenceIdentifier marshal(final ReferenceIdentifier value) {
        return (value != null) ? new XMLReferenceIdentifier(value) : null;
    }

    /**
     * JAXB adapter in order to map a {@link ReferenceIdentifier} to a {@link String}.
     * Its goal is to keep only the {@code name} part of a {@link ReferenceIdentifier}.
     *
     * @author Cédric Briançon (Geomatys)
     * @version 3.06
     *
     * @since 3.06
     * @module
     */
    public static final class ToString extends XmlAdapter<String, ReferenceIdentifier> {
        /**
         * Substitutes the value read from an XML stream by the object which will
         * contains the value. JAXB calls automatically this method at unmarshalling time.
         *
         * @param  adapter The metadata value.
         * @return A metadata which represents the metadata value.
         */
        @Override
        public ReferenceIdentifier unmarshal(final String adapter) {
            if (adapter != null) {
                return new ImmutableIdentifier(null, null, adapter);
            }
            return null;
        }

        /**
         * Substitutes the identifier by the adapter to be marshalled into an XML file
         * or stream. JAXB calls automatically this method at marshalling time.
         *
         * @param  value The code list value.
         * @return The adapter for the given code list.
         */
        @Override
        public String marshal(final ReferenceIdentifier value) {
            return (value != null) ? value.getCode() : null;
        }
    }
}
