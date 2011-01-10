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
package org.geotoolkit.resources.jaxb.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.service.PlatformNeutralServiceSpecificationImpl;
import org.opengis.service.PlatformNeutralServiceSpecification;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class PlatformNeutralServiceSpecificationAdapter extends XmlAdapter<PlatformNeutralServiceSpecificationAdapter, PlatformNeutralServiceSpecification> {
    
    private PlatformNeutralServiceSpecification platform;
    
    /**
     * Empty constructor for JAXB only.
     */
    private PlatformNeutralServiceSpecificationAdapter() {
    }
    

    /**
     * Wraps an platform value with a {@code SV_PlatformNeutralServiceSpecification} tags at marshalling-time.
     *
     * @param platform The PlatformNeutralServiceSpecification value to marshall.
     */
    protected PlatformNeutralServiceSpecificationAdapter(final PlatformNeutralServiceSpecification platform) {
        this.platform = platform;
    }

    /**
     * Returns the PlatformNeutralServiceSpecification value covered by a {@code SV_PlatformNeutralServiceSpecification} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the PlatformNeutralServiceSpecification value.
     */
    protected PlatformNeutralServiceSpecificationAdapter wrap(final PlatformNeutralServiceSpecification value) {
        return new PlatformNeutralServiceSpecificationAdapter(value);
    }

    /**
     * Returns the {@link PlatformNeutralServiceSpecificationImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "SV_PlatformNeutralServiceSpecification")
    public PlatformNeutralServiceSpecificationImpl getPlatformNeutralServiceSpecification() {
        return (platform instanceof PlatformNeutralServiceSpecificationImpl) ?
            (PlatformNeutralServiceSpecificationImpl)platform : new PlatformNeutralServiceSpecificationImpl(platform);
    }

    /**
     * Sets the value for the {@link PlatformNeutralServiceSpecificationImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setPlatformNeutralServiceSpecification(final PlatformNeutralServiceSpecificationImpl PlatformNeutralServiceSpecification) {
        this.platform = PlatformNeutralServiceSpecification;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public PlatformNeutralServiceSpecification unmarshal(final PlatformNeutralServiceSpecificationAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.platform;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the PlatformNeutralServiceSpecification.
     * @return The adapter for this PlatformNeutralServiceSpecification.
     */
    @Override
    public PlatformNeutralServiceSpecificationAdapter marshal(final PlatformNeutralServiceSpecification value) throws Exception {
        return new PlatformNeutralServiceSpecificationAdapter(value);
    }

    
    

}

