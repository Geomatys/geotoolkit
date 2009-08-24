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
package org.geotoolkit.internal.jaxb.text;

import javax.xml.bind.annotation.XmlElement;

import org.opengis.util.RecordType;
import org.geotoolkit.naming.DefaultRecordType;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;


/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface.
 * See package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
public final class RecordTypeAdapter extends MetadataAdapter<RecordTypeAdapter,RecordType> {
    /**
     * Empty constructor for JAXB only.
     */
    public RecordTypeAdapter() {
    }

    /**
     * Wraps an RecordType value with a {@code RecordType} tags at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private RecordTypeAdapter(final RecordType metadata) {
        super(metadata);
    }

    /**
     * Returns the apdapter wrapping a {@code RecordType} element.
     *
     * @param value The value to marshall.
     * @return The adapter that wrap the metadata value.
     */
    @Override
    protected RecordTypeAdapter wrap(final RecordType value) {
        return new RecordTypeAdapter(value);
    }

    /**
     * Returns the {@link DefaultRecordType} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "RecordType")
    public DefaultRecordType getRecordType() {
        final RecordType metadata = this.metadata;
        if (metadata instanceof DefaultRecordType) {
            return (DefaultRecordType) metadata;
        } else {
            return new DefaultRecordType(metadata);
        }
    }

    /**
     * Sets the value for the {@link DefaultRecordType}. This method is systematically
     * called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setRecordType(final DefaultRecordType metadata) {
        this.metadata = metadata;
    }
}
