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
import org.opengis.metadata.citation.OnlineResource;
import org.geotoolkit.metadata.iso.citation.DefaultOnlineResource;


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
public final class CI_OnlineResource extends MetadataAdapter<CI_OnlineResource, OnlineResource> {
    /**
     * Empty constructor for JAXB only.
     */
    public CI_OnlineResource() {
    }

    /**
     * Wraps an OnlineResource value with a {@code CI_OnlineResource} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CI_OnlineResource(final OnlineResource metadata) {
        super(metadata);
    }

    /**
     * Returns the OnlineResource value wrapped by a {@code CI_OnlineResource} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CI_OnlineResource wrap(final OnlineResource value) {
        return new CI_OnlineResource(value);
    }

    /**
     * Returns the {@link DefaultOnlineResource} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultOnlineResource getElement() {
        if (skip()) return null;
        final OnlineResource metadata = this.metadata;
        return (metadata instanceof DefaultOnlineResource) ?
            (DefaultOnlineResource) metadata : new DefaultOnlineResource(metadata);
    }

    /**
     * Sets the value for the {@link DefaultOnlineResource}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultOnlineResource metadata) {
        this.metadata = metadata;
    }
}
