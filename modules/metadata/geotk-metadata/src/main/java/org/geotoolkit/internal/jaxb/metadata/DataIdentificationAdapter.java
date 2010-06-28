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
import org.opengis.metadata.identification.DataIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.13
 *
 * @since 2.5
 * @module
 */
public final class DataIdentificationAdapter extends MetadataAdapter<DataIdentificationAdapter,DataIdentification> {
    /**
     * Empty constructor for JAXB only.
     */
    public DataIdentificationAdapter() {
    }

    /**
     * Wraps an DataIdentification value with a {@code MD_DataIdentification} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private DataIdentificationAdapter(final DataIdentification metadata) {
        super(metadata);
    }

    /**
     * Returns the DataIdentification value wrapped by a {@code MD_DataIdentification} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected DataIdentificationAdapter wrap(final DataIdentification value) {
        return new DataIdentificationAdapter(value);
    }

    /**
     * Returns the {@link DefaultDataIdentification} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MD_DataIdentification")
    public DefaultDataIdentification getElement() {
        if (uuidref != null) {
            return null;
        }
        final DataIdentification metadata = this.metadata;
        return (metadata instanceof DefaultDataIdentification) ?
            (DefaultDataIdentification) metadata : new DefaultDataIdentification(metadata);
    }

    /**
     * Sets the value for the {@link DefaultDataIdentification}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultDataIdentification metadata) {
        this.metadata = metadata;
    }
}
