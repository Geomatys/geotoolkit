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
import org.geotoolkit.service.OperationImpl;
import org.opengis.service.Operation;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class OperationAdapter extends XmlAdapter<OperationAdapter, Operation> {
    
    private Operation operation;
    
    /**
     * Empty constructor for JAXB only.
     */
    private OperationAdapter() {
    }

    /**
     * Wraps an operation value with a {@code SV_Operation} tags at marshalling-time.
     *
     * @param operation The Operation value to marshall.
     */
    protected OperationAdapter(final Operation operation) {
        this.operation = operation;
    }

    /**
     * Returns the Operation value covered by a {@code SV_Operation} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the Operation value.
     */
    protected OperationAdapter wrap(final Operation value) {
        return new OperationAdapter(value);
    }

    /**
     * Returns the {@link OperationImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "SV_Operation")
    public OperationImpl getOperation() {
        return (operation instanceof OperationImpl) ?
            (OperationImpl)operation : new OperationImpl(operation);
    }

    /**
     * Sets the value for the {@link OperationImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setOperation(final OperationImpl Operation) {
        this.operation = Operation;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public Operation unmarshal(final OperationAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.operation;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the Operation.
     * @return The adapter for this Operation.
     */
    @Override
    public OperationAdapter marshal(final Operation value) throws Exception {
        return new OperationAdapter(value);
    }

    
    

}
