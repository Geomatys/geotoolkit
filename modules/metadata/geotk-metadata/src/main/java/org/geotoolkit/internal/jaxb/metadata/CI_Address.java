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
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlElementRef;
import org.opengis.metadata.citation.Address;
import org.apache.sis.metadata.iso.citation.DefaultAddress;
import org.geotoolkit.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 2.5
 * @module
 */
public final class CI_Address extends PropertyType<CI_Address, Address> {
    /**
     * Empty constructor for JAXB only.
     */
    public CI_Address() {
    }

    /**
     * Wraps an address value with a {@code CI_Address} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CI_Address(final Address metadata) {
        super(metadata);
    }

    /**
     * Returns the address value wrapped by a {@code CI_Address} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CI_Address wrap(final Address value) {
        return new CI_Address(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<Address> getBoundType() {
        return Address.class;
    }

    /**
     * Returns the {@link DefaultAddress} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultAddress getElement() {
        return skip() ? null : DefaultAddress.castOrCopy(metadata);
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
