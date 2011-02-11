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
import org.opengis.metadata.citation.CitationDate;
import org.geotoolkit.metadata.iso.citation.DefaultCitationDate;


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
public final class CI_Date extends MetadataAdapter<CI_Date, CitationDate> {
    /**
     * Empty constructor for JAXB only.
     */
    public CI_Date() {
    }

    /**
     * Wraps an CitationDate value with a {@code CI_Date} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CI_Date(final CitationDate metadata) {
        super(metadata);
    }

    /**
     * Returns the CitationDate value wrapped by a {@code CI_Date} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CI_Date wrap(final CitationDate value) {
        return new CI_Date(value);
    }

    /**
     * Returns the {@link DefaultCitationDate} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultCitationDate getElement() {
        final CitationDate metadata = this.metadata;
        return (metadata instanceof DefaultCitationDate) ?
            (DefaultCitationDate) metadata : new DefaultCitationDate(metadata);
    }

    /**
     * Sets the value for the {@link DefaultCitationDate}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultCitationDate metadata) {
        this.metadata = metadata;
    }
}
