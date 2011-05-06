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
import org.opengis.metadata.identification.Keywords;
import org.geotoolkit.metadata.iso.identification.DefaultKeywords;
import org.geotoolkit.internal.jaxb.gco.PropertyType;


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
public final class MD_Keywords extends PropertyType<MD_Keywords, Keywords> {
    /**
     * Empty constructor for JAXB only.
     */
    public MD_Keywords() {
    }

    /**
     * Wraps an Keywords value with a {@code MD_Keywords} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_Keywords(final Keywords metadata) {
        super(metadata);
    }

    /**
     * Returns the Keywords value wrapped by a {@code MD_Keywords} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MD_Keywords wrap(final Keywords value) {
        return new MD_Keywords(value);
    }

    /**
     * Returns the {@link DefaultKeywords} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultKeywords getElement() {
        if (skip()) return null;
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
    public void setElement(final DefaultKeywords metadata) {
        this.metadata = metadata;
    }
}
