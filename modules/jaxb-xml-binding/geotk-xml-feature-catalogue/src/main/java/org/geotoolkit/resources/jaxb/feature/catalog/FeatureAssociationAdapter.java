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
package org.geotoolkit.resources.jaxb.feature.catalog;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.feature.catalog.FeatureAssociationImpl;
import org.opengis.feature.catalog.FeatureAssociation;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI FeatureAssociation. See
 * package documentation for more information about JAXB and FeatureAssociation.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class FeatureAssociationAdapter extends XmlAdapter<FeatureAssociationAdapter, FeatureAssociation> {
    
    private FeatureAssociation association;
    
    @XmlIDREF
    @XmlAttribute(namespace="http://www.w3.org/1999/xlink")
    private FeatureAssociationImpl href;
     
    /**
     * Empty constructor for JAXB only.
     */
    private FeatureAssociationAdapter() {
    }

    /**
     * Wraps an FeatureAssociation value with a {@code SV_FeatureAssociation} tags at marshalling-time.
     *
     * @param association The FeatureAssociation value to marshall.
     */
    protected FeatureAssociationAdapter(final FeatureAssociation association) {
        if (association instanceof FeatureAssociationImpl && ((FeatureAssociationImpl)association).isReference()) {
            this.href    = (FeatureAssociationImpl) association;
        } else {
            this.association = association;
        }
    }

    /**
     * Returns the FeatureAssociation value covered by a {@code SV_FeatureAssociation} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the FeatureAssociation value.
     */
    protected FeatureAssociationAdapter wrap(final FeatureAssociation value) {
        return new FeatureAssociationAdapter(value);
    }

    /**
     * Returns the {@link FeatureAssociationImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "FC_FeatureAssociation")
    public FeatureAssociationImpl getFeatureAssociation() {
        if (association == null) 
            return null;
        return (association instanceof FeatureAssociationImpl) ?
            (FeatureAssociationImpl)association : new FeatureAssociationImpl(association);
    }

    /**
     * Sets the value for the {@link FeatureAssociationImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setFeatureAssociation(final FeatureAssociationImpl FeatureAssociation) {
        this.association = FeatureAssociation;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public FeatureAssociation unmarshal(FeatureAssociationAdapter value) throws Exception {
        if (value == null) {
            return null;
        } else if (value.href != null) {
            return value.href;
        } else {
            return value.association;
        }
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the FeatureAssociation.
     * @return The adapter for this FeatureAssociation.
     */
    @Override
    public FeatureAssociationAdapter marshal(FeatureAssociation value) throws Exception {
        return new FeatureAssociationAdapter(value);
    }

    
    

}
