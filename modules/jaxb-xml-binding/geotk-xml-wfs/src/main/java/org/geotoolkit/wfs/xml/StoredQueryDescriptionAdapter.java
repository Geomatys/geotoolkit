/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wfs.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.wfs.xml.v200.StoredQueryDescriptionType;

/**
 * JAXB adapter in order to map implementing class with the Geotoolkit interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @module pending
 * @author Guilhem Legal
 */
public class StoredQueryDescriptionAdapter extends XmlAdapter<StoredQueryDescriptionAdapter, StoredQueryDescription> {
    
    private StoredQueryDescription queries;
    
    /**
     * Empty constructor for JAXB only.
     */
    private StoredQueryDescriptionAdapter() {
    }

    /**
     * Wraps an Interface value with a {@code SV_Interface} tags at marshalling-time.
     *
     * @param interfac The interface value to marshall.
     */
    protected StoredQueryDescriptionAdapter(final StoredQueryDescription interfac) {
        this.queries = interfac;
    }

    /**
     * Returns the Interface value covered by a {@code SV_Interface} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the interface value.
     */
    protected StoredQueryDescriptionAdapter wrap(final StoredQueryDescription value) {
        return new StoredQueryDescriptionAdapter(value);
    }

    /**
     * Returns the {@link StoredQueryDescriptionType} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "StoredQueryDescription")
    public StoredQueryDescriptionType getStoredQueryDescription() {
        return (queries instanceof StoredQueryDescriptionType) ? 
                (StoredQueryDescriptionType)queries : new StoredQueryDescriptionType(queries);
    }

    /**
     * Sets the value for the {@link StoredQueryDescriptionType}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setStoredQueryDescription(final StoredQueryDescriptionType Interface) {
        this.queries = Interface;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public StoredQueryDescription unmarshal(final StoredQueryDescriptionAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.queries;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the interface.
     * @return The adapter for this interface.
     */
    @Override
    public StoredQueryDescriptionAdapter marshal(final StoredQueryDescription value) throws Exception {
        return new StoredQueryDescriptionAdapter(value);
    }

    
    

}
