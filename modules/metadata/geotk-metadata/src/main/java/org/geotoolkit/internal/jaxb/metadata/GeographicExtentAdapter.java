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

import javax.xml.bind.annotation.XmlElementRef;
import org.opengis.metadata.extent.GeographicExtent;
import org.geotoolkit.metadata.iso.extent.AbstractGeographicExtent;


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
public final class GeographicExtentAdapter extends
        MetadataAdapter<GeographicExtentAdapter,GeographicExtent>
{
    /**
     * Empty constructor for JAXB only.
     */
    public GeographicExtentAdapter() {
    }

    /**
     * Wraps an GeographicExtent value with a {@code EX_GeographicExtent} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private GeographicExtentAdapter(final GeographicExtent metadata) {
        super(metadata);
    }

    /**
     * Returns the GeographicExtent value wrapped by a {@code EX_GeographicExtent} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected GeographicExtentAdapter wrap(final GeographicExtent value) {
        return new GeographicExtentAdapter(value);
    }

    /**
     * Returns the {@link AbstractGeographicExtent} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElementRef
    public AbstractGeographicExtent getGeographicExtent() {
        final GeographicExtent metadata = this.metadata;
        return (metadata instanceof AbstractGeographicExtent) ?
            (AbstractGeographicExtent) metadata : new AbstractGeographicExtent(metadata);
    }

    /**
     * Sets the value for the {@link AbstractGeographicExtent}. This method
     * is systematically called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setGeographicExtent(final AbstractGeographicExtent metadata) {
        this.metadata = metadata;
    }
}
