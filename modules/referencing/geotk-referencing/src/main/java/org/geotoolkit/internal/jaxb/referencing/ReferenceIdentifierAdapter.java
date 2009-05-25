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

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.ReferenceIdentifier;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 * <p>
 * Note that a class of the same name is defined in the {@link org.geotoolkit.internal.jaxb.metadata}
 * package, which serve the same purpose (wrapping exactly the same object) but using the ISO 19139
 * syntax instead.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class ReferenceIdentifierAdapter extends
        XmlAdapter<SimpleReferenceIdentifier, ReferenceIdentifier>
{
    /**
     * Empty constructor for JAXB only.
     */
    public ReferenceIdentifierAdapter() {
    }

    /**
     * Substitutes the adapter value read from an XML stream by the object which will
     * contains the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param  adapter The adapter for this metadata value.
     * @return A code list which represents the metadata value.
     */
    @Override
    public ReferenceIdentifier unmarshal(final SimpleReferenceIdentifier adapter) {
        if (adapter != null) {
            final Citation authority;
            final String codeSpace = adapter.codeSpace;
            if (codeSpace != null) {
                authority = Citations.fromName(codeSpace);
            } else {
                authority = null;
            }
            return new NamedIdentifier(authority, adapter.value);
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
    public SimpleReferenceIdentifier marshal(final ReferenceIdentifier value) {
        if (value == null) {
            return null;
        }
        return new SimpleReferenceIdentifier(value);
    }
}
