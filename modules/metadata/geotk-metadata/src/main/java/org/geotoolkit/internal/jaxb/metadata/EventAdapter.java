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
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlElement;
import org.geotoolkit.metadata.iso.acquisition.DefaultEvent;
import org.opengis.metadata.acquisition.Event;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 3.02
 * @module
 */
public final class EventAdapter extends MetadataAdapter<EventAdapter,Event> {
    /**
     * Empty constructor for JAXB only.
     */
    public EventAdapter() {
    }

    /**
     * Wraps an Event value with a {@code MI_Event} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private EventAdapter(final Event metadata) {
        super(metadata);
    }

    /**
     * Returns the Event value wrapped by a {@code MI_Event} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected EventAdapter wrap(final Event value) {
        return new EventAdapter(value);
    }

    /**
     * Returns the {@link DefaultEvent} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MI_Event")
    public DefaultEvent getElement() {
        final Event metadata = this.metadata;
        return (metadata instanceof DefaultEvent) ?
            (DefaultEvent) metadata : new DefaultEvent(metadata);
    }

    /**
     * Sets the value for the {@link DefaultEvent}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultEvent metadata) {
        this.metadata = metadata;
    }
}
