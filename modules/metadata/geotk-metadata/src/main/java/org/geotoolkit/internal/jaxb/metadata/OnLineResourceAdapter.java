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
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.metadata.citation.OnLineResource;
import org.geotoolkit.metadata.iso.citation.DefaultOnLineResource;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.0
 *
 * @since 2.5
 * @module
 */
public final class OnLineResourceAdapter
        extends MetadataAdapter<OnLineResourceAdapter,OnLineResource>
{
    /**
     * Empty constructor for JAXB only.
     */
    public OnLineResourceAdapter() {
    }

    /**
     * Wraps an OnLineResource value with a {@code CI_OnLineResource} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private OnLineResourceAdapter(final OnLineResource metadata) {
        super(metadata);
    }

    /**
     * Returns the OnLineResource value wrapped by a {@code CI_OnLineResource} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected OnLineResourceAdapter wrap(final OnLineResource value) {
        return new OnLineResourceAdapter(value);
    }

    /**
     * Returns the {@link DefaultOnLineResource} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "CI_OnlineResource")
    public DefaultOnLineResource getOnLineResource() {
        final OnLineResource metadata = this.metadata;
        return (metadata instanceof DefaultOnLineResource) ?
            (DefaultOnLineResource) metadata : new DefaultOnLineResource(metadata);
    }

    /**
     * Sets the value for the {@link DefaultOnLineResource}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setOnLineResource(final DefaultOnLineResource metadata) {
        this.metadata = metadata;
    }
}
