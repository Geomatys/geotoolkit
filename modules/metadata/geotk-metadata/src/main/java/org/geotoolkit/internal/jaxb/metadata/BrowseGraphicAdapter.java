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
import org.opengis.metadata.identification.BrowseGraphic;
import org.geotoolkit.metadata.iso.identification.DefaultBrowseGraphic;


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
public final class BrowseGraphicAdapter
        extends MetadataAdapter<BrowseGraphicAdapter,BrowseGraphic>
{
    /**
     * Empty constructor for JAXB only.
     */
    public BrowseGraphicAdapter() {
    }

    /**
     * Wraps an BrowseGraphic value with a {@code MD_BrowseGraphic} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private BrowseGraphicAdapter(final BrowseGraphic metadata) {
        super(metadata);
    }

    /**
     * Returns the BrowseGraphic value wrapped by a {@code MD_BrowseGraphic} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected BrowseGraphicAdapter wrap(final BrowseGraphic value) {
        return new BrowseGraphicAdapter(value);
    }

    /**
     * Returns the {@link DefaultBrowseGraphic} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MD_BrowseGraphic")
    public DefaultBrowseGraphic getElement() {
        final BrowseGraphic metadata = this.metadata;
        return (metadata instanceof DefaultBrowseGraphic) ?
            (DefaultBrowseGraphic) metadata : new DefaultBrowseGraphic(metadata);
    }

    /**
     * Sets the value for the {@link DefaultBrowseGraphic}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultBrowseGraphic metadata) {
        this.metadata = metadata;
    }
}
