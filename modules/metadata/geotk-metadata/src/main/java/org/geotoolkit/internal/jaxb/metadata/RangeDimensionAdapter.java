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

import javax.xml.bind.annotation.XmlElementRef;
import org.opengis.metadata.content.RangeDimension;
import org.geotoolkit.metadata.iso.content.DefaultRangeDimension;


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
public final class RangeDimensionAdapter
        extends MetadataAdapter<RangeDimensionAdapter,RangeDimension>
{
    /**
     * Empty constructor for JAXB only.
     */
    public RangeDimensionAdapter() {
    }

    /**
     * Wraps an RangeDimension value with a {@code MD_RangeDimension} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private RangeDimensionAdapter(final RangeDimension metadata) {
        super(metadata);
    }

    /**
     * Returns the RangeDimension value wrapped by a {@code MD_RangeDimension} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected RangeDimensionAdapter wrap(final RangeDimension value) {
        return new RangeDimensionAdapter(value);
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
        final RangeDimension metadata = this.metadata;
        return (metadata instanceof DefaultRangeDimension) ?
            (DefaultRangeDimension) metadata : new DefaultRangeDimension(metadata);
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
