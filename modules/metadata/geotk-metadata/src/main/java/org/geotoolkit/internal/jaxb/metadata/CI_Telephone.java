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
import org.opengis.metadata.citation.Telephone;
import org.geotoolkit.metadata.iso.citation.DefaultTelephone;
import org.geotoolkit.internal.jaxb.gco.PropertyType;


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
public final class CI_Telephone extends PropertyType<CI_Telephone, Telephone> {
    /**
     * Empty constructor for JAXB only.
     */
    public CI_Telephone() {
    }

    /**
     * Wraps an Telephone value with a {@code CI_Telephone} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CI_Telephone(final Telephone metadata) {
        super(metadata);
    }

    /**
     * Returns the Telephone value wrapped by a {@code CI_Telephone} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CI_Telephone wrap(final Telephone value) {
        return new CI_Telephone(value);
    }

    /**
     * Returns the {@link DefaultTelephone} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultTelephone getElement() {
        if (skip()) return null;
        final Telephone metadata = this.metadata;
        return (metadata instanceof DefaultTelephone) ?
            (DefaultTelephone) metadata : new DefaultTelephone(metadata);
    }

    /**
     * Sets the value for the {@link DefaultTelephone}. This method
     * is systematically called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultTelephone metadata) {
        this.metadata = metadata;
    }
}
