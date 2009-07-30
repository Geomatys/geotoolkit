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
import org.geotoolkit.metadata.iso.acquisition.DefaultRequestedDate;
import org.opengis.metadata.acquisition.RequestedDate;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 * @module
 */
public final class RequestedDateAdapter extends MetadataAdapter<RequestedDateAdapter,RequestedDate> {
    /**
     * Empty constructor for JAXB only.
     */
    public RequestedDateAdapter() {
    }

    /**
     * Wraps an RequestedDate value with a {@code MI_RequestedDate} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private RequestedDateAdapter(final RequestedDate metadata) {
        super(metadata);
    }

    /**
     * Returns the RequestedDate value wrapped by a {@code MI_RequestedDate} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected RequestedDateAdapter wrap(final RequestedDate value) {
        return new RequestedDateAdapter(value);
    }

    /**
     * Returns the {@link DefaultRequestedDate} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "MI_RequestedDate")
    public DefaultRequestedDate getRequestedDate() {
        final RequestedDate metadata = this.metadata;
        return (metadata instanceof DefaultRequestedDate) ?
            (DefaultRequestedDate) metadata : new DefaultRequestedDate(metadata);
    }

    /**
     * Sets the value for the {@link DefaultRequestedDate}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setRequestedDate(final DefaultRequestedDate metadata) {
        this.metadata = metadata;
    }
}
