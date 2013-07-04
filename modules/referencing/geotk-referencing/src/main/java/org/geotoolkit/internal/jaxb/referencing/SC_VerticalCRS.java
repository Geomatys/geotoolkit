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

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.opengis.referencing.crs.VerticalCRS;
import org.apache.sis.internal.jaxb.AdapterReplacement;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;


/**
 * JAXB adapter for {@link VerticalCRS}, in order to integrate the value in an element
 * complying with OGC/ISO standard. Note that the CRS is formatted using the GML schema,
 * not the ISO 19139 one.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 4.01
 *
 * @since 3.00
 * @module
 */
public final class SC_VerticalCRS extends org.apache.sis.internal.jaxb.gml.SC_VerticalCRS implements AdapterReplacement {
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
     * Replaces the {@code sis-metadata} adapter by this adapter.
     */
    @Override
    public void register(final Marshaller marshaller) {
        marshaller.setAdapter(org.apache.sis.internal.jaxb.gml.SC_VerticalCRS.class, this);
    }

    /**
     * Replaces the {@code sis-metadata} adapter by this adapter.
     */
    @Override
    public void register(final Unmarshaller unmarshaller) {
        unmarshaller.setAdapter(org.apache.sis.internal.jaxb.gml.SC_VerticalCRS.class, this);
    }

    /**
     * Returns the Vertical CRS value wrapped by a {@code gml:VerticalCRS} tags.
     *
     * @param value The value to marshal.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected org.apache.sis.internal.jaxb.gml.SC_VerticalCRS wrap(final VerticalCRS value) {
        return new SC_VerticalCRS(value);
    }

    /**
     * Returns the {@link DefaultVerticalCRS} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    public Object getElement() {
        return skip() ? null : DefaultVerticalCRS.castOrCopy(metadata);
    }
}
