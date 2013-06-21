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
package org.geotoolkit.internal.jaxb.referencing;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.referencing.crs.VerticalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.apache.sis.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter for {@link VerticalCRS}, in order to integrate the value in an element
 * complying with OGC/ISO standard. Note that the CRS is formatted using the GML schema,
 * not the ISO 19139 one.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 * @module
 */
public final class SC_VerticalCRS extends PropertyType<SC_VerticalCRS, VerticalCRS> {
    /**
     * Empty constructor for JAXB only.
     */
    public SC_VerticalCRS() {
    }

    /**
     * Wraps a Vertical CRS value with a {@code gml:VerticalCRS} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private SC_VerticalCRS(final VerticalCRS metadata) {
        super(metadata);
    }

    /**
     * Returns the Vertical CRS value wrapped by a {@code gml:VerticalCRS} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected SC_VerticalCRS wrap(final VerticalCRS value) {
        return new SC_VerticalCRS(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<VerticalCRS> getBoundType() {
        return VerticalCRS.class;
    }

    /**
     * Returns the {@link DefaultVerticalCRS} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "VerticalCRS")
    public DefaultVerticalCRS getElement() {
        return skip() ? null : DefaultVerticalCRS.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultVerticalCRS}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultVerticalCRS metadata) {
        this.metadata = metadata;
    }
}
