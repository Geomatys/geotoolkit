/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal.jaxb.referencing.cs;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.referencing.cs.CartesianCS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;


/**
 * JAXB adapter for {@link CartesianCS}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 3.04
 * @module
 */
public final class CartesianCSAdapter extends MetadataAdapter<CartesianCSAdapter, CartesianCS> {
    /**
     * Empty constructor for JAXB only.
     */
    public CartesianCSAdapter() {
    }

    /**
     * Wraps a {@link CartesianCS} value with a {@code gml:cartesianCS} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CartesianCSAdapter(final CartesianCS metadata) {
        super(metadata);
    }

    /**
     * Returns the {@link CartesianCS} value wrapped by a {@code gml:cartesianCS} element.
     *
     * @param  value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CartesianCSAdapter wrap(final CartesianCS value) {
        return new CartesianCSAdapter(value);
    }

    /**
     * Returns the {@link DefaultCartesianCS} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "CartesianCS")
    public DefaultCartesianCS getElement() {
        final CartesianCS metadata = this.metadata;
        return (metadata instanceof DefaultCartesianCS) ?
            (DefaultCartesianCS) metadata : new DefaultCartesianCS(metadata);
    }

    /**
     * Sets the value for the {@link DefaultCartesianCS}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultCartesianCS metadata) {
        this.metadata = metadata;
    }
}
