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
import org.opengis.metadata.extent.VerticalExtent;
import org.geotoolkit.metadata.iso.extent.DefaultVerticalExtent;


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
public final class VerticalExtentAdapter extends MetadataAdapter<VerticalExtentAdapter,VerticalExtent> {
    /**
     * Empty constructor for JAXB only.
     */
    public VerticalExtentAdapter() {
    }

    /**
     * Wraps an VerticalExtent value with a {@code EX_VerticalExtent} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private VerticalExtentAdapter(final VerticalExtent metadata) {
        super(metadata);
    }

    /**
     * Returns the VerticalExtent value wrapped by a {@code EX_VerticalExtent} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected VerticalExtentAdapter wrap(final VerticalExtent value) {
        return new VerticalExtentAdapter(value);
    }

    /**
     * Returns the {@link DefaultVerticalExtent} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "EX_VerticalExtent")
    public DefaultVerticalExtent getVerticalExtent() {
        final VerticalExtent metadata = this.metadata;
        return (metadata instanceof DefaultVerticalExtent) ?
            (DefaultVerticalExtent) metadata : new DefaultVerticalExtent(metadata);
    }

    /**
     * Sets the value for the {@link DefaultVerticalExtent}. This
     * method is systematically called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setVerticalExtent(final DefaultVerticalExtent metadata) {
        this.metadata = metadata;
    }
}
