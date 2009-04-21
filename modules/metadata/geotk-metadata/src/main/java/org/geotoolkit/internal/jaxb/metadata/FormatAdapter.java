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
import org.opengis.metadata.distribution.Format;
import org.geotoolkit.metadata.iso.distribution.DefaultFormat;


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
public final class FormatAdapter extends MetadataAdapter<FormatAdapter,Format> {
    /**
     * Empty constructor for JAXB only.
     */
    public FormatAdapter() {
    }

    /**
     * Wraps an Format value with a {@code MD_Format} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private FormatAdapter(final Format metadata) {
        super(metadata);
    }

    /**
     * Returns the Format value wrapped by a {@code MD_Format} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected FormatAdapter wrap(final Format value) {
        return new FormatAdapter(value);
    }

    /**
     * Returns the {@link DefaultFormat} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "MD_Format")
    public DefaultFormat getFormat() {
        final Format metadata = this.metadata;
        return (metadata instanceof DefaultFormat) ?
            (DefaultFormat) metadata : new DefaultFormat(metadata);
    }

    /**
     * Sets the value for the {@link DefaultFormat}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setFormat(final DefaultFormat metadata) {
        this.metadata = metadata;
    }
}
