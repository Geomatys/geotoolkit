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
package org.geotoolkit.internal.jaxb.referencing;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.referencing.crs.VerticalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;


/**
 * JAXB adapter for {@link VerticalCRS}, in order to integrate the value in an element
 * complying with OGC/ISO standard. Note that the CRS is formatted using the GML schema,
 * not the ISO 19139 one.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public final class VerticalCRSAdapter extends MetadataAdapter<VerticalCRSAdapter,VerticalCRS> {
    /**
     * Empty constructor for JAXB only.
     */
    public VerticalCRSAdapter() {
    }

    /**
     * Wraps a Vertical CRS value with a {@code gml:VerticalCRS} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private VerticalCRSAdapter(final VerticalCRS metadata) {
        super(metadata);
    }

    /**
     * Returns the Vertical CRS value wrapped by a {@code gml:VerticalCRS} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected VerticalCRSAdapter wrap(final VerticalCRS value) {
        return new VerticalCRSAdapter(value);
    }

    /**
     * Returns the {@link DefaultVerticalCRS} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "VerticalCRS")
    public DefaultVerticalCRS getVerticalCRS() {
        final VerticalCRS metadata = this.metadata;
        return (metadata instanceof DefaultVerticalCRS) ?
            (DefaultVerticalCRS) metadata : new DefaultVerticalCRS(metadata);
    }

    /**
     * Sets the value for the {@link DefaultVerticalCRS}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setVerticalCRS(final DefaultVerticalCRS metadata) {
        this.metadata = metadata;
    }
}
