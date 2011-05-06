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
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlElementRef;

import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.ServiceIdentification;

import org.geotoolkit.metadata.iso.identification.AbstractIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 2.5
 * @module
 */
public final class MD_Identification extends MetadataAdapter<MD_Identification, Identification> {
    /**
     * Empty constructor for JAXB only.
     */
    public MD_Identification() {
    }

    /**
     * Wraps an Identification value with a {@code MD_Identification} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_Identification(final Identification metadata) {
        super(metadata);
    }

    /**
     * Returns the Identification value wrapped by a {@code MD_Identification} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MD_Identification wrap(final Identification value) {
        return new MD_Identification(value);
    }

    /**
     * Returns the {@link AbstractIdentification} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public AbstractIdentification getElement() {
        if (skip()) return null;
        final Identification metadata = this.metadata;
        if (metadata instanceof AbstractIdentification) {
            return (AbstractIdentification) metadata;
        }
        if (metadata instanceof DataIdentification) {
            return new DefaultDataIdentification((DataIdentification) metadata);
        }
        if (metadata instanceof ServiceIdentification) {
            return new DefaultServiceIdentification((ServiceIdentification) metadata);
        }
        return new AbstractIdentification(metadata);
    }

    /**
     * Sets the value for the {@link AbstractIdentification}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final AbstractIdentification metadata) {
        this.metadata = metadata;
    }
}
