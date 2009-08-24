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
import org.opengis.metadata.lineage.Source;
import org.geotoolkit.metadata.iso.lineage.DefaultSource;


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
public final class SourceAdapter extends MetadataAdapter<SourceAdapter,Source> {
    /**
     * Empty constructor for JAXB only.
     */
    public SourceAdapter() {
    }

    /**
     * Wraps an Source value with a {@code LI_Source} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private SourceAdapter(final Source metadata) {
        super(metadata);
    }

    /**
     * Returns the Source value wrapped by a {@code LI_Source} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected SourceAdapter wrap(final Source value) {
        return new SourceAdapter(value);
    }

    /**
     * Returns the {@link DefaultSource} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "LI_Source")
    public DefaultSource getSource() {
        final Source metadata = this.metadata;
        return (metadata instanceof DefaultSource) ?
            (DefaultSource) metadata : new DefaultSource(metadata);
    }

    /**
     * Sets the value for the {@link DefaultSource}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setSource(final DefaultSource metadata) {
        this.metadata = metadata;
    }
}
