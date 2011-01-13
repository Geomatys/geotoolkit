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

import javax.xml.bind.annotation.XmlElement;
import org.opengis.metadata.quality.DataQuality;
import org.geotoolkit.metadata.iso.quality.DefaultDataQuality;


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
public final class DataQualityAdapter extends MetadataAdapter<DataQualityAdapter,DataQuality> {
    /**
     * Empty constructor for JAXB only.
     */
    public DataQualityAdapter() {
    }

    /**
     * Wraps an DataQuality value with a {@code DQ_DataQuality} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private DataQualityAdapter(final DataQuality metadata) {
        super(metadata);
    }

    /**
     * Returns the DataQuality value wrapped by a {@code DQ_DataQuality} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected DataQualityAdapter wrap(final DataQuality value) {
        return new DataQualityAdapter(value);
    }

    /**
     * Returns the {@link DefaultDataQuality} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "DQ_DataQuality")
    public DefaultDataQuality getElement() {
        final DataQuality metadata = this.metadata;
        return (metadata instanceof DefaultDataQuality) ?
            (DefaultDataQuality) metadata : new DefaultDataQuality(metadata);
    }

    /**
     * Sets the value for the {@link DefaultDataQuality}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultDataQuality metadata) {
        this.metadata = metadata;
    }
}
