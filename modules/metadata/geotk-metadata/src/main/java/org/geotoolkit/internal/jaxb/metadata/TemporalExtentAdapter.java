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

import javax.xml.bind.annotation.XmlElementRef;
import org.opengis.metadata.extent.TemporalExtent;
import org.geotoolkit.metadata.iso.extent.DefaultTemporalExtent;


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
public final class TemporalExtentAdapter
        extends MetadataAdapter<TemporalExtentAdapter,TemporalExtent>
{
    /**
     * Empty constructor for JAXB only.
     */
    public TemporalExtentAdapter() {
    }

    /**
     * Wraps an TemporalExtent value with a {@code EX_TemporalExtent} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private TemporalExtentAdapter(final TemporalExtent metadata) {
        super(metadata);
    }

    /**
     * Returns the TemporalExtent value wrapped by a {@code EX_TemporalExtent} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected TemporalExtentAdapter wrap(final TemporalExtent value) {
        return new TemporalExtentAdapter(value);
    }

    /**
     * Returns the {@link DefaultTemporalExtent} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElementRef
    public DefaultTemporalExtent getTemporalExtent() {
        final TemporalExtent metadata = this.metadata;
        return (metadata instanceof DefaultTemporalExtent) ?
            (DefaultTemporalExtent) metadata : new DefaultTemporalExtent(metadata);
    }

    /**
     * Sets the value for the {@link DefaultTemporalExtent}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setTemporalExtent(final DefaultTemporalExtent metadata) {
        this.metadata = metadata;
    }
}
