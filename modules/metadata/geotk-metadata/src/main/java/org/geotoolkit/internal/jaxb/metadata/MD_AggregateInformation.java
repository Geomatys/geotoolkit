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
import org.opengis.metadata.identification.AggregateInformation;
import org.geotoolkit.metadata.iso.identification.DefaultAggregateInformation;


/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.05
 *
 * @since 3.00
 * @module
 */
public final class MD_AggregateInformation extends
        MetadataAdapter<MD_AggregateInformation, AggregateInformation>
{
    /**
     * Empty constructor for JAXB only.
     */
    public MD_AggregateInformation() {
    }

    /**
     * Wraps an Aggregate Information value with a {@code MD_AggregateInformation}
     * element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_AggregateInformation(final AggregateInformation metadata) {
        super(metadata);
    }

    /**
     * Returns the Aggregate Information value covered by a {@code MD_AggregateInformation} element.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the metadata value.
     */
    @Override
    protected MD_AggregateInformation wrap(final AggregateInformation value) {
        return new MD_AggregateInformation(value);
    }

    /**
     * Returns the {@link DefaultAggregateInformation} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MD_AggregateInformation")
    public DefaultAggregateInformation getElement() {
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
    public void setElement(final DefaultAggregateInformation metadata) {
        this.metadata = metadata;
    }
}
