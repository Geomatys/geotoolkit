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
import org.geotoolkit.service.InterfaceImpl;
import org.opengis.service.Interface;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @module pending
 * @since 2.5
 * @source $URL: http://svn.geotools.org/trunk/modules/library/metadata/src/main/java/org/geotools/resources/jaxb/metadata/InterfaceAdapter.java $
 * @author Guilhem Legal
 */
public class InterfaceAdapter extends XmlAdapter<InterfaceAdapter, Interface> {
    
    private Interface interfac;
    
    /**
     * Empty constructor for JAXB only.
     */
    private InterfaceAdapter() {
    }

    /**
     * Wraps an Interface value with a {@code SV_Interface} tags at marshalling-time.
     *
     * @param interfac The interface value to marshall.
     */
    protected InterfaceAdapter(final Interface interfac) {
        this.interfac = interfac;
    }

    /**
     * Returns the Interface value covered by a {@code SV_Interface} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the interface value.
     */
    protected InterfaceAdapter wrap(final Interface value) {
        return new InterfaceAdapter(value);
    }

    /**
     * Returns the {@link InterfaceImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "SV_Interface")
    public InterfaceImpl getInterface() {
        return (interfac instanceof InterfaceImpl) ?
            (InterfaceImpl)interfac : new InterfaceImpl(interfac);
    }

    /**
     * Sets the value for the {@link InterfaceImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setInterface(final InterfaceImpl Interface) {
        this.interfac = Interface;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public Interface unmarshal(InterfaceAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.interfac;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the interface.
     * @return The adapter for this interface.
     */
    @Override
    public InterfaceAdapter marshal(Interface value) throws Exception {
        return new InterfaceAdapter(value);
    }

    
    

}
