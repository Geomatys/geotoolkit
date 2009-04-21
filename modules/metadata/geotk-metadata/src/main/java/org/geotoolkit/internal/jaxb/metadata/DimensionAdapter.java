/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.metadata.spatial.Dimension;
import org.geotoolkit.metadata.iso.spatial.DefaultDimension;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.0
 *
 * @since 2.5
 * @module
 */
public final class DimensionAdapter extends MetadataAdapter<DimensionAdapter,Dimension> {
    /**
     * Empty constructor for JAXB only.
     */
    public DimensionAdapter() {
    }

    /**
     * Wraps an Dimension value with a {@code MD_Dimension} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private DimensionAdapter(final Dimension metadata) {
        super(metadata);
    }

    /**
     * Returns the Dimension value wrapped by a {@code MD_Dimension} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected DimensionAdapter wrap(final Dimension value) {
        return new DimensionAdapter(value);
    }

    /**
     * Returns the {@link DefaultDimension} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "MD_Dimension")
    public DefaultDimension getDimension() {
        final Dimension metadata = this.metadata;
        return (metadata instanceof DefaultDimension) ?
            (DefaultDimension) metadata : new DefaultDimension(metadata);
    }

    /**
     * Sets the value for the {@link DefaultDimension}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setDimension(final DefaultDimension metadata) {
        this.metadata = metadata;
    }
}
