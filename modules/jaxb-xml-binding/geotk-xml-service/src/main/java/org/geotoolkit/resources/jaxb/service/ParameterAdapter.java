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
import org.geotoolkit.service.ParameterImpl;
import org.opengis.service.Parameter;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @module pending
 * @since 2.5
 * @source $URL: http://svn.geotools.org/trunk/modules/library/metadata/src/main/java/org/geotools/resources/jaxb/metadata/ParameterAdapter.java $
 * @author Guilhem Legal
 */
public class ParameterAdapter extends XmlAdapter<ParameterAdapter, Parameter> {
    
    private Parameter parameter;
    
    /**
     * Empty constructor for JAXB only.
     */
    private ParameterAdapter() {
    }

    /**
     * Wraps an parameter value with a {@code SV_Parameter} tags at marshalling-time.
     *
     * @param parameter The Parameter value to marshall.
     */
    protected ParameterAdapter(final Parameter parameter) {
        this.parameter = parameter;
    }

    /**
     * Returns the Parameter value covered by a {@code SV_Parameter} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the Parameter value.
     */
    protected ParameterAdapter wrap(final Parameter value) {
        return new ParameterAdapter(value);
    }

    /**
     * Returns the {@link ParameterImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "SV_Parameter")
    public ParameterImpl getParameter() {
        return (parameter instanceof ParameterImpl) ?
            (ParameterImpl)parameter : new ParameterImpl(parameter);
    }

    /**
     * Sets the value for the {@link ParameterImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setParameter(final ParameterImpl Parameter) {
        this.parameter = Parameter;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public Parameter unmarshal(ParameterAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.parameter;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the Parameter.
     * @return The adapter for this Parameter.
     */
    @Override
    public ParameterAdapter marshal(Parameter value) throws Exception {
        return new ParameterAdapter(value);
    }

    
    

}
