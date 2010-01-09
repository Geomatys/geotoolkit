/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import org.opengis.metadata.distribution.DigitalTransferOptions;
import org.geotoolkit.metadata.iso.distribution.DefaultDigitalTransferOptions;


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
public final class DigitalTransferOptionsAdapter
        extends MetadataAdapter<DigitalTransferOptionsAdapter,DigitalTransferOptions>
{
    /**
     * Empty constructor for JAXB only.
     */
    public DigitalTransferOptionsAdapter() {
    }

    /**
     * Wraps an DigitalTransferOptions value with a {@code MD_DigitalTransferOptions}
     * element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private DigitalTransferOptionsAdapter(final DigitalTransferOptions metadata) {
        super(metadata);
    }

    /**
     * Returns the DigitalTransferOptions value wrapped by a
     * {@code MD_DigitalTransferOptions} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected DigitalTransferOptionsAdapter wrap(final DigitalTransferOptions value) {
        return new DigitalTransferOptionsAdapter(value);
    }

    /**
     * Returns the {@link DefaultDigitalTransferOptions} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MD_DigitalTransferOptions")
    public DefaultDigitalTransferOptions getElement() {
        final DigitalTransferOptions metadata = this.metadata;
        return (metadata instanceof DefaultDigitalTransferOptions) ?
            (DefaultDigitalTransferOptions) metadata : new DefaultDigitalTransferOptions(metadata);
    }

    /**
     * Sets the value for the {@link DefaultDigitalTransferOptions}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultDigitalTransferOptions metadata) {
        this.metadata = metadata;
    }
}
