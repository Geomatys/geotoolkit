/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.internal.jaxb.referencing.datum;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.referencing.datum.VerticalDatum;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;
import org.geotoolkit.referencing.datum.DefaultVerticalDatum;


/**
 * JAXB adapter for {@link VerticalDatum}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.04
 *
 * @since 3.00
 * @module
 */
public final class VerticalDatumAdapter extends MetadataAdapter<VerticalDatumAdapter,VerticalDatum> {
    /**
     * Empty constructor for JAXB only.
     */
    public VerticalDatumAdapter() {
    }

    /**
     * Wraps a Vertical Datum value with a {@code gml:verticalDatum} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private VerticalDatumAdapter(final VerticalDatum metadata) {
        super(metadata);
    }

    /**
     * Returns the VerticalDatum value wrapped by a {@code gml:verticalDatum} element.
     *
     * @param  value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected VerticalDatumAdapter wrap(final VerticalDatum value) {
        return new VerticalDatumAdapter(value);
    }

    /**
     * Returns the {@link DefaultVerticalDatum} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "VerticalDatum")
    public DefaultVerticalDatum getVerticalDatum() {
        final VerticalDatum metadata = this.metadata;
        return (metadata instanceof DefaultVerticalDatum) ?
            (DefaultVerticalDatum) metadata : new DefaultVerticalDatum(metadata);
    }

    /**
     * Sets the value for the {@link DefaultVerticalDatum}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setVerticalDatum(final DefaultVerticalDatum metadata) {
        this.metadata = metadata;
    }
}
