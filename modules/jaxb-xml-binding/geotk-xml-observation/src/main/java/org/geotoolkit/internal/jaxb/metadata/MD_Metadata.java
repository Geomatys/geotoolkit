/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
import org.geotoolkit.internal.jaxb.gco.PropertyType;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.opengis.metadata.Metadata;

/**
 * TODO move to geotk-metadata
 * @author Guilhem Legal (Geomatys)
 */
public class MD_Metadata extends PropertyType<MD_Metadata, Metadata> {

     /**
     * Empty constructor for JAXB only.
     */
    public MD_Metadata() {
    }

    /**
     * Wraps an address value with a {@code MD_Metadata} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_Metadata(final Metadata metadata) {
        super(metadata);
    }

    /**
     * Returns the address value wrapped by a {@code MD_Metadata} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MD_Metadata wrap(final Metadata value) {
        return new MD_Metadata(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<Metadata> getBoundType() {
        return Metadata.class;
    }

    /**
     * Returns the {@link DefaultMetadata} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultMetadata getElement() {
        return skip() ? null : DefaultMetadata.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultMetadata}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultMetadata metadata) {
        this.metadata = metadata;
    }

}
