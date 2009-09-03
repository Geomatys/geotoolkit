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
import org.geotoolkit.service.PortImpl;
import org.opengis.service.Port;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @since 2.5
 * @source $URL: http://svn.geotools.org/trunk/modules/library/metadata/src/main/java/org/geotools/resources/jaxb/metadata/PortAdapter.java $
 * @author Guilhem Legal
 */
public class PortAdapter extends XmlAdapter<PortAdapter, Port> {
    
    private Port port;
    
    /**
     * Empty constructor for JAXB only.
     */
    private PortAdapter() {
    }

    /**
     * Wraps an Port value with a {@code CI_Port} tags at marshalling-time.
     *
     * @param port The port value to marshall.
     */
    protected PortAdapter(final Port port) {
        this.port = port;
    }

    /**
     * Returns the Port value covered by a {@code CI_Port} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the port value.
     */
    protected PortAdapter wrap(final Port value) {
        return new PortAdapter(value);
    }

    /**
     * Returns the {@link PortImpl} generated from the port value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "SV_Port")
    public PortImpl getPort() {
        return (port instanceof PortImpl) ?
            (PortImpl)port : new PortImpl(port);
    }

    /**
     * Sets the value for the {@link PortImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setPort(final PortImpl Port) {
        this.port = Port;
    }

    /**
     * Does the link between port red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this port value.
     * @return A java object which represents the port value.
     */
    @Override
    public Port unmarshal(PortAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.port;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the interface.
     * @return The adapter for this interface.
     */
    @Override
    public PortAdapter marshal(Port value) throws Exception {
        return new PortAdapter(value);
    }

    
    
}
