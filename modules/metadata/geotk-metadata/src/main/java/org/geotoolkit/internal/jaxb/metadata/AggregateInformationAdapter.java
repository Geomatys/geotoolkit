/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
import org.opengis.metadata.identification.AggregateInformation;
import org.geotoolkit.metadata.iso.identification.DefaultAggregateInformation;


/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class AggregateInformationAdapter extends
        MetadataAdapter<AggregateInformationAdapter,AggregateInformation>
{
    /**
     * Empty constructor for JAXB only.
     */
    public AggregateInformationAdapter() {
    }

    /**
     * Wraps an Aggregate Information value with a {@code MD_AggregateInformation}
     * element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private AggregateInformationAdapter(final AggregateInformation metadata) {
        super(metadata);
    }

    /**
     * Returns the Aggregate Information value covered by a {@code MD_AggregateInformation} element.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the metadata value.
     */
    @Override
    protected AggregateInformationAdapter wrap(final AggregateInformation value) {
        return new AggregateInformationAdapter(value);
    }

    /**
     * Returns the {@link DefaultAggregateInformation} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "MD_AggregateInformation")
    public DefaultAggregateInformation getAggregateInfo() {
        final AggregateInformation metadata = this.metadata;
        return (metadata instanceof DefaultAggregateInformation) ?
            (DefaultAggregateInformation) metadata : new DefaultAggregateInformation(metadata);
    }

    /**
     * Sets the value for the {@link DefaultAggregateInformation}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setAggregateInfo(final DefaultAggregateInformation metadata) {
        this.metadata = metadata;
    }
}
