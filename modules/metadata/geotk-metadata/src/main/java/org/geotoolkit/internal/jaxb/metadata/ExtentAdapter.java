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
import org.opengis.metadata.extent.Extent;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;


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
public final class ExtentAdapter extends MetadataAdapter<ExtentAdapter,Extent> {
    /**
     * Empty constructor for JAXB only.
     */
    public ExtentAdapter() {
    }

    /**
     * Wraps an Extent value with a {@code EX_Extent} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private ExtentAdapter(final Extent metadata) {
        super(metadata);
    }

    /**
     * Returns the Extent value wrapped by a {@code EX_Extent} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected ExtentAdapter wrap(final Extent value) {
        return new ExtentAdapter(value);
    }

    /**
     * Returns the {@link DefaultExtent} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "EX_Extent")
    public DefaultExtent getElement() {
        final Extent metadata = this.metadata;
        return (metadata instanceof DefaultExtent) ?
            (DefaultExtent) metadata : new DefaultExtent(metadata);
    }

    /**
     * Sets the value for the {@link DefaultExtent}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultExtent metadata) {
        this.metadata = metadata;
    }
}
