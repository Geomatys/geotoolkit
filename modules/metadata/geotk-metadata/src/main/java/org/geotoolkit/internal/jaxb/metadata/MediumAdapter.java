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
import org.opengis.metadata.distribution.Medium;
import org.geotoolkit.metadata.iso.distribution.DefaultMedium;


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
public final class MediumAdapter extends MetadataAdapter<MediumAdapter,Medium> {
    /**
     * Empty constructor for JAXB only.
     */
    public MediumAdapter() {
    }

    /**
     * Wraps an Medium value with a {@code MD_Medium} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MediumAdapter(final Medium metadata) {
        super(metadata);
    }

    /**
     * Returns the Medium value wrapped by a {@code MD_Medium} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MediumAdapter wrap(final Medium value) {
        return new MediumAdapter(value);
    }

    /**
     * Returns the {@link DefaultMedium} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "MD_Medium")
    public DefaultMedium getMedium() {
        final Medium metadata = this.metadata;
        return (metadata instanceof DefaultMedium) ?
            (DefaultMedium) metadata : new DefaultMedium(metadata);
    }

    /**
     * Sets the value for the {@link DefaultMedium}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setMedium(final DefaultMedium metadata) {
        this.metadata = metadata;
    }
}
