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
import org.opengis.metadata.acquisition.RequestedDate;
import org.apache.sis.metadata.iso.acquisition.DefaultRequestedDate;
import org.geotoolkit.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.02
 * @module
 */
public final class MI_RequestedDate extends PropertyType<MI_RequestedDate, RequestedDate> {
    /**
     * Empty constructor for JAXB only.
     */
    public MI_RequestedDate() {
    }

    /**
     * Wraps an RequestedDate value with a {@code MI_RequestedDate} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MI_RequestedDate(final RequestedDate metadata) {
        super(metadata);
    }

    /**
     * Returns the RequestedDate value wrapped by a {@code MI_RequestedDate} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MI_RequestedDate wrap(final RequestedDate value) {
        return new MI_RequestedDate(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<RequestedDate> getBoundType() {
        return RequestedDate.class;
    }

    /**
     * Returns the {@link DefaultRequestedDate} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultRequestedDate getElement() {
        return skip() ? null : DefaultRequestedDate.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultRequestedDate}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultRequestedDate metadata) {
        this.metadata = metadata;
    }
}
