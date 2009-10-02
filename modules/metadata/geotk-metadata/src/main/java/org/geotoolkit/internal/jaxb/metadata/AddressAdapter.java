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
import org.opengis.metadata.citation.Address;
import org.geotoolkit.metadata.iso.citation.DefaultAddress;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 2.5
 * @module
 */
public final class AddressAdapter extends MetadataAdapter<AddressAdapter,Address> {
    /**
     * Empty constructor for JAXB only.
     */
    public AddressAdapter() {
    }

    /**
     * Wraps an address value with a {@code CI_Address} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private AddressAdapter(final Address metadata) {
        super(metadata);
    }

    /**
     * Returns the address value wrapped by a {@code CI_Address} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected AddressAdapter wrap(final Address value) {
        return new AddressAdapter(value);
    }

    /**
     * Returns the {@link DefaultAddress} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "CI_Address")
    public DefaultAddress getElement() {
        final Address metadata = this.metadata;
        return (metadata instanceof DefaultAddress) ?
            (DefaultAddress) metadata : new DefaultAddress(metadata);
    }

    /**
     * Sets the value for the {@link DefaultAddress}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultAddress metadata) {
        this.metadata = metadata;
    }
}
