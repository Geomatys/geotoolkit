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

import org.opengis.metadata.content.Band;
import org.opengis.metadata.content.RangeDimension;

import org.geotoolkit.internal.jaxb.gmi.MI_Band;
import org.geotoolkit.internal.jaxb.gco.PropertyType;
import org.apache.sis.metadata.iso.content.DefaultRangeDimension;


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
public final class MD_RangeDimension extends PropertyType<MD_RangeDimension, RangeDimension> {
    /**
     * Empty constructor for JAXB only.
     */
    public MD_RangeDimension() {
    }

    /**
     * Wraps an RangeDimension value with a {@code MD_RangeDimension} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_RangeDimension(final RangeDimension metadata) {
        super(metadata);
    }

    /**
     * Returns the RangeDimension value wrapped by a {@code MD_RangeDimension} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MD_RangeDimension wrap(final RangeDimension value) {
        return new MD_RangeDimension(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<RangeDimension> getBoundType() {
        return RangeDimension.class;
    }

    /**
     * Returns the {@link DefaultRangeDimension} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultRangeDimension getElement() {
        if (skip()) return null;
        final RangeDimension metadata = this.metadata;
        if (metadata instanceof Band) {
            return MI_Band.castOrCopy((Band) metadata);
        }
        return DefaultRangeDimension.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultRangeDimension}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultRangeDimension metadata) {
        this.metadata = metadata;
    }
}
