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
import org.opengis.metadata.identification.Keywords;
import org.geotoolkit.metadata.iso.identification.DefaultKeywords;


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
public final class KeywordsAdapter extends MetadataAdapter<KeywordsAdapter,Keywords> {
    /**
     * Empty constructor for JAXB only.
     */
    public KeywordsAdapter() {
    }

    /**
     * Wraps an Keywords value with a {@code MD_Keywords} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private KeywordsAdapter(final Keywords metadata) {
        super(metadata);
    }

    /**
     * Returns the Keywords value wrapped by a {@code MD_Keywords} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected KeywordsAdapter wrap(final Keywords value) {
        return new KeywordsAdapter(value);
    }

    /**
     * Returns the {@link DefaultKeywords} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "MD_Keywords")
    public DefaultKeywords getKeywords() {
        final Keywords metadata = this.metadata;
        return (metadata instanceof DefaultKeywords) ?
            (DefaultKeywords) metadata : new DefaultKeywords(metadata);
    }

    /**
     * Sets the value for the {@link DefaultKeywords}. This method
     * is systematically called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setKeywords(final DefaultKeywords metadata) {
        this.metadata = metadata;
    }
}
