/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008, Geomatys
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
package org.geotoolkit.resources.jaxb.feature.catalog;

import org.geotoolkit.util.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI Multiplicity. See
 * package documentation for more information about JAXB and Multiplicity.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class MultiplicityAdapter extends XmlAdapter<MultiplicityAdapter, Multiplicity> {
    
    private Multiplicity multiplicity;
    
    /**
     * Empty constructor for JAXB only.
     */
    private MultiplicityAdapter() {
    }

    /**
     * Wraps an Multiplicity value with a {@code SV_Multiplicity} tags at marshalling-time.
     *
     * @param multiplicity The Multiplicity value to marshall.
     */
    protected MultiplicityAdapter(final Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    /**
     * Returns the Multiplicity value covered by a {@code SV_Multiplicity} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the Multiplicity value.
     */
    protected MultiplicityAdapter wrap(final Multiplicity value) {
        return new MultiplicityAdapter(value);
    }

    /**
     * Returns the {@link MultiplicityImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "Multiplicity", namespace = "http://www.isotc211.org/2005/gco")
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    /**
     * Sets the value for the {@link MultiplicityImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setMultiplicity(final Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public Multiplicity unmarshal(final MultiplicityAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.multiplicity;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the Multiplicity.
     * @return The adapter for this Multiplicity.
     */
    @Override
    public MultiplicityAdapter marshal(final Multiplicity value) throws Exception {
        return new MultiplicityAdapter(value);
    }

    
    

}
