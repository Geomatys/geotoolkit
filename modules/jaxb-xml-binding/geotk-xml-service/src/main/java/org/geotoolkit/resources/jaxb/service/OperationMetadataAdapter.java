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
import org.geotoolkit.service.OperationMetadataImpl;
import org.opengis.service.OperationMetadata;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @since 2.5
 * @source $URL: http://svn.geotools.org/trunk/modules/library/metadata/src/main/java/org/geotools/resources/jaxb/metadata/OperationMetadataAdapter.java $
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
        return (parameter instanceof OperationMetadataImpl) ?
            (OperationMetadataImpl)parameter : new OperationMetadataImpl(parameter);
    }

    /**
     * Sets the value for the {@link OperationMetadataImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setOperationMetadata(final OperationMetadataImpl OperationMetadata) {
        this.parameter = OperationMetadata;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public OperationMetadata unmarshal(OperationMetadataAdapter value) throws Exception {
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
    public OperationMetadataAdapter marshal(OperationMetadata value) throws Exception {
        return new OperationMetadataAdapter(value);
    }

    
    

}
