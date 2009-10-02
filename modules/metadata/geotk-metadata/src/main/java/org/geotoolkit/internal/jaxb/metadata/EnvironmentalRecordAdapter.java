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
import org.geotoolkit.metadata.iso.acquisition.DefaultEnvironmentalRecord;
import org.opengis.metadata.acquisition.EnvironmentalRecord;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 3.02
 * @module
 */
public final class EnvironmentalRecordAdapter extends MetadataAdapter<EnvironmentalRecordAdapter,EnvironmentalRecord> {
    /**
     * Empty constructor for JAXB only.
     */
    public EnvironmentalRecordAdapter() {
    }

    /**
     * Wraps an EnvironmentalRecord value with a {@code MI_EnvironmentalRecord} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private EnvironmentalRecordAdapter(final EnvironmentalRecord metadata) {
        super(metadata);
    }

    /**
     * Returns the EnvironmentalRecord value wrapped by a {@code MI_EnvironmentalRecord} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected EnvironmentalRecordAdapter wrap(final EnvironmentalRecord value) {
        return new EnvironmentalRecordAdapter(value);
    }

    /**
     * Returns the {@link DefaultEnvironmentalRecord} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MI_EnvironmentalRecord")
    public DefaultEnvironmentalRecord getElement() {
        final EnvironmentalRecord metadata = this.metadata;
        return (metadata instanceof DefaultEnvironmentalRecord) ?
            (DefaultEnvironmentalRecord) metadata : new DefaultEnvironmentalRecord(metadata);
    }

    /**
     * Sets the value for the {@link DefaultEnvironmentalRecord}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultEnvironmentalRecord metadata) {
        this.metadata = metadata;
    }
}
