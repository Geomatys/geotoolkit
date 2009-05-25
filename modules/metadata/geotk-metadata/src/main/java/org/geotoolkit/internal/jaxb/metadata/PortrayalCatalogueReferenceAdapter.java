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
import org.opengis.metadata.PortrayalCatalogueReference;
import org.geotoolkit.metadata.iso.DefaultPortrayalCatalogueReference;


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
public final class PortrayalCatalogueReferenceAdapter
        extends MetadataAdapter<PortrayalCatalogueReferenceAdapter,PortrayalCatalogueReference>
{
    /**
     * Empty constructor for JAXB only.
     */
    public PortrayalCatalogueReferenceAdapter() {
    }

    /**
     * Wraps an PortrayalCatalogueReference value with a {@code MD_PortrayalCatalogueReference}
     * element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private PortrayalCatalogueReferenceAdapter(final PortrayalCatalogueReference metadata) {
        super(metadata);
    }

    /**
     * Returns the PortrayalCatalogueReference value wrapped by a
     * {@code MD_PortrayalCatalogueReference} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected PortrayalCatalogueReferenceAdapter wrap(final PortrayalCatalogueReference value) {
        return new PortrayalCatalogueReferenceAdapter(value);
    }

    /**
     * Returns the {@link DefaultPortrayalCatalogueReference} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "MD_PortrayalCatalogueReference")
    public DefaultPortrayalCatalogueReference getPortrayalCatalogueReference() {
        final PortrayalCatalogueReference metadata = this.metadata;
        return (metadata instanceof DefaultPortrayalCatalogueReference) ?
            (DefaultPortrayalCatalogueReference) metadata :
            new DefaultPortrayalCatalogueReference(metadata);
    }

    /**
     * Sets the value for the {@link DefaultPortrayalCatalogueReference}. This method is
     * systematically called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setPortrayalCatalogueReference(final DefaultPortrayalCatalogueReference metadata) {
        this.metadata = metadata;
    }
}
