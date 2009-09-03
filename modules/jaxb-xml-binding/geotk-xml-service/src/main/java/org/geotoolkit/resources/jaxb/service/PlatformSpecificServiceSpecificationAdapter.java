/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2008, Geotools Project Managment Committee (PMC)
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
import org.geotoolkit.service.PlatformSpecificServiceSpecificationImpl;
import org.opengis.service.PlatformSpecificServiceSpecification;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @since 2.5
 * @source $URL: http://svn.geotools.org/trunk/modules/library/metadata/src/main/java/org/geotools/resources/jaxb/metadata/PlatformSpecificServiceSpecificationAdapter.java $
 * @author Guilhem Legal
 */
public class PlatformSpecificServiceSpecificationAdapter extends XmlAdapter<PlatformSpecificServiceSpecificationAdapter, PlatformSpecificServiceSpecification> {
    
    private PlatformSpecificServiceSpecification platform;
    
    /**
     * Empty constructor for JAXB only.
     */
    private PlatformSpecificServiceSpecificationAdapter() {
    }

    /**
     * Wraps an platform value with a {@code SV_PlatformSpecificServiceSpecification} tags at marshalling-time.
     *
     * @param platform The PlatformSpecificServiceSpecification value to marshall.
     */
    protected PlatformSpecificServiceSpecificationAdapter(final PlatformSpecificServiceSpecification platform) {
        this.platform = platform;
    }

    /**
     * Returns the PlatformSpecificServiceSpecification value covered by a {@code SV_PlatformSpecificServiceSpecification} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the PlatformSpecificServiceSpecification value.
     */
    protected PlatformSpecificServiceSpecificationAdapter wrap(final PlatformSpecificServiceSpecification value) {
        return new PlatformSpecificServiceSpecificationAdapter(value);
    }

    /**
     * Returns the {@link PlatformSpecificServiceSpecificationImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "SV_PlatformSpecificServiceSpecification")
    public PlatformSpecificServiceSpecificationImpl getPlatformSpecificServiceSpecification() {
        return (platform instanceof PlatformSpecificServiceSpecificationImpl) ?
            (PlatformSpecificServiceSpecificationImpl)platform : new PlatformSpecificServiceSpecificationImpl(platform);
    }

    /**
     * Sets the value for the {@link PlatformSpecificServiceSpecificationImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setPlatformSpecificServiceSpecification(final PlatformSpecificServiceSpecificationImpl PlatformSpecificServiceSpecification) {
        this.platform = PlatformSpecificServiceSpecification;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public PlatformSpecificServiceSpecification unmarshal(PlatformSpecificServiceSpecificationAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.platform;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the PlatformSpecificServiceSpecification.
     * @return The adapter for this PlatformSpecificServiceSpecification.
     */
    @Override
    public PlatformSpecificServiceSpecificationAdapter marshal(PlatformSpecificServiceSpecification value) throws Exception {
        return new PlatformSpecificServiceSpecificationAdapter(value);
    }

    
    

}
