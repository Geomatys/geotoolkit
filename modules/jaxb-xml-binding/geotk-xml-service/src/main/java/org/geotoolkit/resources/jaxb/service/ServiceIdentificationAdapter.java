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
import org.geotoolkit.service.ServiceIdentificationImpl;
import org.opengis.service.ServiceIdentification;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class ServiceIdentificationAdapter extends XmlAdapter<ServiceIdentificationAdapter, ServiceIdentification> {
    
    private ServiceIdentification service;
    
    /**
     * Empty constructor for JAXB only.
     */
    private ServiceIdentificationAdapter() {
    }

    /**
     * Wraps an ServiceIdentification value with a {@code CI_ServiceIdentification} tags at marshalling-time.
     *
     * @param service The service value to marshall.
     */
    protected ServiceIdentificationAdapter(final ServiceIdentification service) {
        this.service = service;
    }

    /**
     * Returns the ServiceIdentification value covered by a {@code CI_ServiceIdentification} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the service value.
     */
    protected ServiceIdentificationAdapter wrap(final ServiceIdentification value) {
        return new ServiceIdentificationAdapter(value);
    }

    /**
     * Returns the {@link ServiceIdentificationImpl} generated from the service value.
     * This method is systematically called at marshalling-time by JAXB.
     */  
    @XmlElement(name = "SV_ServiceIdentification")
    public ServiceIdentificationImpl getServiceIdentification() {
        return (service instanceof ServiceIdentificationImpl) ?
            (ServiceIdentificationImpl)service : new ServiceIdentificationImpl(service);
    }

    /**
     * Sets the value for the {@link ServiceIdentificationImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setServiceIdentification(final ServiceIdentificationImpl ServiceIdentification) {
        this.service = ServiceIdentification;
    }

    /**
     * Does the link between service red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this service value.
     * @return A java object which represents the service value.
     */
    @Override
    public ServiceIdentification unmarshal(final ServiceIdentificationAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.service;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the interface.
     * @return The adapter for this interface.
     */
    @Override
    public ServiceIdentificationAdapter marshal(final ServiceIdentification value) throws Exception {
        return wrap(value);
    }

    
    
}
