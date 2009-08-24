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
import org.opengis.metadata.lineage.Lineage;
import org.geotoolkit.metadata.iso.lineage.DefaultLineage;


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
public final class LineageAdapter extends MetadataAdapter<LineageAdapter,Lineage> {
    /**
     * Empty constructor for JAXB only.
     */
    public LineageAdapter() {
    }

    /**
     * Wraps an Lineage value with a {@code LI_Lineage} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private LineageAdapter(final Lineage metadata) {
        super(metadata);
    }

    /**
     * Returns the Lineage value wrapped by a {@code LI_Lineage} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected LineageAdapter wrap(final Lineage value) {
        return new LineageAdapter(value);
    }

    /**
     * Returns the {@link DefaultLineage} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "LI_Lineage")
    public DefaultLineage getLineage() {
        final Lineage metadata = this.metadata;
        return (metadata instanceof DefaultLineage) ?
            (DefaultLineage) metadata : new DefaultLineage(metadata);
    }

    /**
     * Sets the value for the {@link DefaultLineage}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setLineage(final DefaultLineage metadata) {
        this.metadata = metadata;
    }
}
