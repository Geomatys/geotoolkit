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
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlElement;

import org.opengis.metadata.Identifier;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.referencing.DefaultReferenceIdentifier;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
public final class IdentifierAdapter extends MetadataAdapter<IdentifierAdapter,Identifier> {
    /**
     * Empty constructor for JAXB only.
     */
    public IdentifierAdapter() {
    }

    /**
     * Wraps an Identifier value with a {@code MD_Identifier} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private IdentifierAdapter(final Identifier metadata) {
        super(metadata);
    }

    /**
     * Returns the Identifier value wrapped by a {@code MD_Identifier} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected IdentifierAdapter wrap(final Identifier value) {
        return new IdentifierAdapter(value);
    }

    /**
     * Returns the {@link DefaultIdentifier} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "MD_Identifier")
    public DefaultIdentifier getIdentifier() {
        final Identifier metadata = this.metadata;
        if (metadata instanceof ReferenceIdentifier) {
            return null;
        }
        return (metadata instanceof DefaultIdentifier) ?
            (DefaultIdentifier) metadata : new DefaultIdentifier(metadata);
    }

    /**
     * Sets the value for the {@link DefaultIdentifier}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setIdentifier(final DefaultIdentifier metadata) {
        this.metadata = metadata;
    }

    /**
     * Returns the {@link DefaultIdentifier} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "RS_Identifier")
    public DefaultReferenceIdentifier getReferenceIdentifier() {
        final Identifier metadata = this.metadata;
        if (!(metadata instanceof ReferenceIdentifier)) {
            return null;
        }
        return (metadata instanceof DefaultReferenceIdentifier) ?
            (DefaultReferenceIdentifier) metadata :
            new DefaultReferenceIdentifier((ReferenceIdentifier) metadata);
    }

    /**
     * Sets the value for the {@link DefaultIdentifier}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setReferenceIdentifier(final DefaultReferenceIdentifier metadata) {
        this.metadata = metadata;
    }
}
