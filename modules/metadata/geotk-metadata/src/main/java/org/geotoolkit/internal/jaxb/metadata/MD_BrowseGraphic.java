/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
import org.opengis.metadata.identification.BrowseGraphic;
import org.apache.sis.metadata.iso.identification.DefaultBrowseGraphic;
import org.geotoolkit.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 2.5
 * @module
 */
public final class MD_BrowseGraphic extends PropertyType<MD_BrowseGraphic, BrowseGraphic> {
    /**
     * Empty constructor for JAXB only.
     */
    public MD_BrowseGraphic() {
    }

    /**
     * Wraps an BrowseGraphic value with a {@code MD_BrowseGraphic} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_BrowseGraphic(final BrowseGraphic metadata) {
        super(metadata);
    }

    /**
     * Returns the BrowseGraphic value wrapped by a {@code MD_BrowseGraphic} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MD_BrowseGraphic wrap(final BrowseGraphic value) {
        return new MD_BrowseGraphic(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<BrowseGraphic> getBoundType() {
        return BrowseGraphic.class;
    }

    /**
     * Returns the {@link DefaultBrowseGraphic} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultBrowseGraphic getElement() {
        return skip() ? null : DefaultBrowseGraphic.castOrCopy(metadata);
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
