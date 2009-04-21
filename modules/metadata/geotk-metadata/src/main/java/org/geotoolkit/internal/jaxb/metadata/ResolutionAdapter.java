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
import org.opengis.metadata.identification.Resolution;
import org.geotoolkit.metadata.iso.identification.DefaultResolution;


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
public final class ResolutionAdapter extends MetadataAdapter<ResolutionAdapter,Resolution> {
    /**
     * Empty constructor for JAXB only.
     */
    public ResolutionAdapter() {
    }

    /**
     * Wraps an Resolution value with a {@code MD_Resolution} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private ResolutionAdapter(final Resolution metadata) {
        super(metadata);
    }

    /**
     * Returns the Resolution value wrapped by a {@code MD_Resolution} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected ResolutionAdapter wrap(final Resolution value) {
        return new ResolutionAdapter(value);
    }

    /**
     * Returns the {@link DefaultResolution} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "MD_Resolution")
    public DefaultResolution getResolution() {
        final Resolution metadata = this.metadata;
        return (metadata instanceof DefaultResolution) ?
            (DefaultResolution) metadata : new DefaultResolution(metadata);
    }

    /**
     * Sets the value for the {@link DefaultResolution}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setResolution(final DefaultResolution metadata) {
        this.metadata = metadata;
    }
}
