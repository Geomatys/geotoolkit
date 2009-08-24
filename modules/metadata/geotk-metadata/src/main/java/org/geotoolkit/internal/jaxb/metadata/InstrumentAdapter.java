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

import javax.xml.bind.annotation.XmlElement;
import org.geotoolkit.metadata.iso.acquisition.DefaultInstrument;
import org.opengis.metadata.acquisition.Instrument;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 * @module
 */
public final class InstrumentAdapter extends MetadataAdapter<InstrumentAdapter,Instrument> {
    /**
     * Empty constructor for JAXB only.
     */
    public InstrumentAdapter() {
    }

    /**
     * Wraps an Instrument value with a {@code MI_Instrument} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private InstrumentAdapter(final Instrument metadata) {
        super(metadata);
    }

    /**
     * Returns the Instrument value wrapped by a {@code MI_Instrument} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected InstrumentAdapter wrap(final Instrument value) {
        return new InstrumentAdapter(value);
    }

    /**
     * Returns the {@link DefaultInstrument} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "MI_Instrument")
    public DefaultInstrument getInstrument() {
        final Instrument metadata = this.metadata;
        return (metadata instanceof DefaultInstrument) ?
            (DefaultInstrument) metadata : new DefaultInstrument(metadata);
    }

    /**
     * Sets the value for the {@link DefaultInstrument}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setInstrument(final DefaultInstrument metadata) {
        this.metadata = metadata;
    }
}
