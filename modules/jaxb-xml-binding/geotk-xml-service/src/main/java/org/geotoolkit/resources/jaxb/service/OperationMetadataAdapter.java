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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.service.OperationMetadataImpl;
import org.opengis.service.OperationMetadata;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class OperationMetadataAdapter extends XmlAdapter<OperationMetadataAdapter, OperationMetadata> {
    
    private OperationMetadata parameter;

    /**
     * Empty constructor for JAXB only.
     */
    private OperationMetadataAdapter() {
    }

    /**
     * Wraps an parameter value with a {@code SV_OperationMetadata} tags at marshalling-time.
     *
     * @param parameter The OperationMetadata value to marshall.
     */
    protected OperationMetadataAdapter(final OperationMetadata parameter) {
        this.parameter = parameter;
    }

    /**
     * Returns the OperationMetadata value covered by a {@code SV_OperationMetadata} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the OperationMetadata value.
     */
    protected OperationMetadataAdapter wrap(final OperationMetadata value) {
        return new OperationMetadataAdapter(value);
    }

    /**
     * Returns the {@link OperationMetadataImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "SV_OperationMetadata")
    public OperationMetadataImpl getOperationMetadata() {
        if (parameter instanceof OperationMetadataImpl) {
            OperationMetadataImpl param = (OperationMetadataImpl) parameter;
            if (param.isUuidref()) {
                return null;
            } else {
                return param;
            }
        } else {
            return new OperationMetadataImpl(parameter);
        }
    }

    /**
     * Sets the value for the {@link OperationMetadataImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setOperationMetadata(final OperationMetadataImpl OperationMetadata) {
        this.parameter = OperationMetadata;
    }

    /**
     * Returns the {@link OperationMetadataImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlAttribute(name = "uuidref")
    public String getUuidref() {
        if (parameter instanceof OperationMetadataImpl) {
            OperationMetadataImpl param = (OperationMetadataImpl) parameter;
            if (param.isUuidref()) {
                return param.getOperationName();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Sets the value for the {@link OperationMetadataImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setUuidref(final String uuidref) {
        this.parameter = new OperationMetadataImpl(uuidref);
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public OperationMetadata unmarshal(final OperationMetadataAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.parameter;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the OperationMetadata.
     * @return The adapter for this OperationMetadata.
     */
    @Override
    public OperationMetadataAdapter marshal(final OperationMetadata value) throws Exception {
        return new OperationMetadataAdapter(value);
    }

    
    

}
