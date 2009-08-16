/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.referencing.ReferenceIdentifier;
import org.geotoolkit.referencing.DefaultReferenceIdentifier;


/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 * <p>
 * Note that a class of the same name is defined in the {@link org.geotoolkit.internal.jaxb.referencing}
 * package, which serves the same purpose (wrapping exactly the same object) but using the GML syntax
 * instead.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class ReferenceIdentifierAdapter extends MetadataAdapter<ReferenceIdentifierAdapter,ReferenceIdentifier> {
    /**
     * Empty constructor for JAXB only.
     */
    public ReferenceIdentifierAdapter() {
    }

    /**
     * Wraps an Identifier value with a {@code RS_Identifier} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private ReferenceIdentifierAdapter(final ReferenceIdentifier metadata) {
        super(metadata);
    }

    /**
     * Returns the Identifier value covered by a {@code RS_Identifier} element.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the metadata value.
     */
    @Override
    protected ReferenceIdentifierAdapter wrap(ReferenceIdentifier value) {
        return new ReferenceIdentifierAdapter(value);
    }

    /**
     * Returns the {@link NamedIdentifier} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "RS_Identifier")
    public DefaultReferenceIdentifier getIdentifier() {
        final ReferenceIdentifier metadata = this.metadata;
        if (metadata instanceof DefaultReferenceIdentifier) {
            return (DefaultReferenceIdentifier) metadata;
        } else {
            return new DefaultReferenceIdentifier(metadata);
        }
    }

    /**
     * Sets the value for the {@link NamedIdentifier}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setIdentifier(final DefaultReferenceIdentifier metadata) {
        this.metadata = metadata;
    }
}
