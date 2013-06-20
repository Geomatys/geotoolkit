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
import org.opengis.metadata.content.RangeElementDescription;
import org.apache.sis.metadata.iso.content.DefaultRangeElementDescription;
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
public final class MI_RangeElementDescription
        extends PropertyType<MI_RangeElementDescription, RangeElementDescription>
{
    /**
     * Empty constructor for JAXB only.
     */
    public MI_RangeElementDescription() {
    }

    /**
     * Wraps an Citation value with a {@code MI_RangeElementDescription} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MI_RangeElementDescription(final RangeElementDescription metadata) {
        super(metadata);
    }

    /**
     * Returns the Citation value wrapped by a {@code MI_RangeElementDescription} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MI_RangeElementDescription wrap(final RangeElementDescription value) {
        return new MI_RangeElementDescription(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<RangeElementDescription> getBoundType() {
        return RangeElementDescription.class;
    }

    /**
     * Returns the {@link DefaultRangeElementDescription} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultRangeElementDescription getElement() {
        return skip() ? null : DefaultRangeElementDescription.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultRangeElementDescription}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultRangeElementDescription metadata) {
        this.metadata = metadata;
    }
}
