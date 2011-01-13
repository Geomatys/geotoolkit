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
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;


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
public final class GeographicBoundingBoxAdapter extends
        MetadataAdapter<GeographicBoundingBoxAdapter,GeographicBoundingBox>
{
    /**
     * Empty constructor for JAXB only.
     */
    public GeographicBoundingBoxAdapter() {
    }

    /**
     * Wraps an GeographicBoundingBox value with a {@code EX_GeographicBoundingBox} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private GeographicBoundingBoxAdapter(final GeographicBoundingBox metadata) {
        super(metadata);
    }

    /**
     * Returns the GeographicBoundingBox value wrapped by a {@code EX_GeographicBoundingBox} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected GeographicBoundingBoxAdapter wrap(final GeographicBoundingBox value) {
        return new GeographicBoundingBoxAdapter(value);
    }

    /**
     * Returns the {@link DefaultGeographicBoundingBox} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "EX_GeographicBoundingBox")
    public DefaultGeographicBoundingBox getElement() {
        final GeographicBoundingBox metadata = this.metadata;
        return (metadata instanceof DefaultGeographicBoundingBox) ?
            (DefaultGeographicBoundingBox) metadata : new DefaultGeographicBoundingBox(metadata);
    }

    /**
     * Sets the value for the {@link DefaultGeographicBoundingBox}. This
     * method is systematically called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultGeographicBoundingBox metadata) {
        this.metadata = metadata;
    }
}
