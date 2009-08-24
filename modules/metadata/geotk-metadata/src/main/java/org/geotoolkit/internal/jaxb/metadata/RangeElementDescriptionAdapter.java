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
import org.geotoolkit.metadata.iso.content.DefaultRangeElementDescription;
import org.opengis.metadata.content.RangeElementDescription;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 * @module
 */
public final class RangeElementDescriptionAdapter extends MetadataAdapter<RangeElementDescriptionAdapter,RangeElementDescription> {
    /**
     * Empty constructor for JAXB only.
     */
    public RangeElementDescriptionAdapter() {
    }

    /**
     * Wraps an Citation value with a {@code MI_RangeElementDescription} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private RangeElementDescriptionAdapter(final RangeElementDescription metadata) {
        super(metadata);
    }

    /**
     * Returns the Citation value wrapped by a {@code MI_RangeElementDescription} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected RangeElementDescriptionAdapter wrap(final RangeElementDescription value) {
        return new RangeElementDescriptionAdapter(value);
    }

    /**
     * Returns the {@link DefaultRangeElementDescription} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "MI_RangeElementDescription")
    public DefaultRangeElementDescription getRangeElementDescription() {
        final RangeElementDescription metadata = this.metadata;
        return (metadata instanceof DefaultRangeElementDescription) ?
            (DefaultRangeElementDescription) metadata : new DefaultRangeElementDescription(metadata);
    }

    /**
     * Sets the value for the {@link DefaultRangeElementDescription}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setCitation(final DefaultRangeElementDescription metadata) {
        this.metadata = metadata;
    }
}
