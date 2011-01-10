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
import org.geotoolkit.service.ServiceTypeImpl;
import org.opengis.service.ServiceType;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class ServiceTypeAdapter extends XmlAdapter<ServiceTypeImpl, ServiceType> {
    
    private ServiceType serviceType;
    
    /**
     * Empty constructor for JAXB only.
     */
    private ServiceTypeAdapter() {
    }

    /**
     * Wraps an ServiceType value with a {@code CI_ServiceType} tags at marshalling-time.
     *
     * @param serviceType The serviceType value to marshall.
     */
    protected ServiceTypeAdapter(final ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Does the link between serviceType red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this serviceType value.
     * @return A java object which represents the serviceType value.
     */
    @Override
    public ServiceType unmarshal(final ServiceTypeImpl value) throws Exception {
        return value;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the interface.
     * @return The adapter for this interface.
     */
    @Override
    public ServiceTypeImpl marshal(final ServiceType value) throws Exception {
        return (ServiceTypeImpl) value;
    }

    
    
}
