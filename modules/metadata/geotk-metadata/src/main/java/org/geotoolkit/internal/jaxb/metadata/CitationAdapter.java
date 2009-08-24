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
import org.opengis.metadata.citation.Citation;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;


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
public final class CitationAdapter extends MetadataAdapter<CitationAdapter,Citation> {
    /**
     * Empty constructor for JAXB only.
     */
    public CitationAdapter() {
    }

    /**
     * Wraps an Citation value with a {@code CI_Citation} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CitationAdapter(final Citation metadata) {
        super(metadata);
    }

    /**
     * Returns the Citation value wrapped by a {@code CI_Citation} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CitationAdapter wrap(final Citation value) {
        return new CitationAdapter(value);
    }

    /**
     * Returns the {@link DefaultCitation} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "CI_Citation")
    public DefaultCitation getCitation() {
        final Citation metadata = this.metadata;
        return (metadata instanceof DefaultCitation) ?
            (DefaultCitation) metadata : new DefaultCitation(metadata);
    }

    /**
     * Sets the value for the {@link DefaultCitation}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setCitation(final DefaultCitation metadata) {
        this.metadata = metadata;
    }
}
