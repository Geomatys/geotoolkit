/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.referencing.datum;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.referencing.datum.TemporalDatum;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;
import org.geotoolkit.referencing.datum.DefaultTemporalDatum;


/**
 * JAXB adapter for {@link TemporalDatum}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 3.04
 * @module
 */
public final class TemporalDatumAdapter extends MetadataAdapter<TemporalDatumAdapter,TemporalDatum> {
    /**
     * Empty constructor for JAXB only.
     */
    public TemporalDatumAdapter() {
    }

    /**
     * Wraps a Vertical Datum value with a {@code gml:temporalDatum} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private TemporalDatumAdapter(final TemporalDatum metadata) {
        super(metadata);
    }

    /**
     * Returns the TemporalDatum value wrapped by a {@code gml:temporalDatum} element.
     *
     * @param  value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected TemporalDatumAdapter wrap(final TemporalDatum value) {
        return new TemporalDatumAdapter(value);
    }

    /**
     * Returns the {@link DefaultTemporalDatum} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "TemporalDatum")
    public DefaultTemporalDatum getElement() {
        final TemporalDatum metadata = this.metadata;
        return (metadata instanceof DefaultTemporalDatum) ?
            (DefaultTemporalDatum) metadata : new DefaultTemporalDatum(metadata);
    }

    /**
     * Sets the value for the {@link DefaultTemporalDatum}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultTemporalDatum metadata) {
        this.metadata = metadata;
    }
}
