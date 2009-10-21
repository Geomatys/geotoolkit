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
import org.geotoolkit.service.ServiceProviderImpl;
import org.opengis.service.ServiceProvider;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @module pending
 * @since 2.5
 * @source $URL: http://svn.geotools.org/trunk/modules/library/metadata/src/main/java/org/geotools/resources/jaxb/metadata/ServiceProviderAdapter.java $
 * @author Guilhem Legal
 */
public class ServiceProviderAdapter extends XmlAdapter<ServiceProviderAdapter, ServiceProvider> {
    
    private ServiceProvider serviceProvider;
    
    /**
     * Empty constructor for JAXB only.
     */
    private ServiceProviderAdapter() {
    }

    /**
     * Wraps an serviceProvider value with a {@code SV_ServiceProvider} tags at marshalling-time.
     *
     * @param serviceProvider The ServiceProvider value to marshall.
     */
    protected ServiceProviderAdapter(final ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    /**
     * Returns the ServiceProvider value covered by a {@code SV_ServiceProvider} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the ServiceProvider value.
     */
    protected ServiceProviderAdapter wrap(final ServiceProvider value) {
        return new ServiceProviderAdapter(value);
    }

    /**
     * Returns the {@link ServiceProviderImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "SV_ServiceProvider")
    public ServiceProviderImpl getServiceProvider() {
        return (serviceProvider instanceof ServiceProviderImpl) ?
            (ServiceProviderImpl)serviceProvider : new ServiceProviderImpl(serviceProvider);
    }

    /**
     * Sets the value for the {@link ServiceProviderImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setServiceProvider(final ServiceProviderImpl ServiceProvider) {
        this.serviceProvider = ServiceProvider;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public ServiceProvider unmarshal(ServiceProviderAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.serviceProvider;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the ServiceProvider.
     * @return The adapter for this ServiceProvider.
     */
    @Override
    public ServiceProviderAdapter marshal(ServiceProvider value) throws Exception {
        return new ServiceProviderAdapter(value);
    }

    
    

}
