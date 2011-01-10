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
import org.geotoolkit.feature.catalog.InheritanceRelationImpl;
import org.opengis.feature.catalog.InheritanceRelation;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI inheritanceRelation. See
 * package documentation for more information about JAXB and inheritanceRelation.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class InheritanceRelationAdapter extends XmlAdapter<InheritanceRelationAdapter, InheritanceRelation> {
    
    private InheritanceRelation relation;
    
    @XmlIDREF
    @XmlAttribute(namespace="http://www.w3.org/1999/xlink")
    private InheritanceRelationImpl href;
     
    /**
     * Empty constructor for JAXB only.
     */
    private InheritanceRelationAdapter() {
    }

    /**
     * Wraps an inheritanceRelation value with a {@code SV_inheritanceRelation} tags at marshalling-time.
     *
     * @param relation The inheritanceRelation value to marshall.
     */
    protected InheritanceRelationAdapter(final InheritanceRelation relation) {
        if (relation instanceof InheritanceRelationImpl && ((InheritanceRelationImpl)relation).isReference()) {
            this.href    = (InheritanceRelationImpl)relation;
            this.relation = null;
        } else {
            this.href    = null;
            this.relation = relation;
        }
    }

    /**
     * Returns the inheritanceRelation value covered by a {@code SV_inheritanceRelation} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the inheritanceRelation value.
     */
    protected InheritanceRelationAdapter wrap(final InheritanceRelation value) {
        return new InheritanceRelationAdapter(value);
    }

    /**
     * Returns the {@link inheritanceRelationImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "FC_InheritanceRelation")
    public InheritanceRelationImpl getinheritanceRelation() {
        if (relation == null) {
            return null;
        }
        return (relation instanceof InheritanceRelationImpl) ?
            (InheritanceRelationImpl)relation : new InheritanceRelationImpl(relation);
    }

    /**
     * Sets the value for the {@link inheritanceRelationImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setinheritanceRelation(final InheritanceRelationImpl inheritanceRelation) {
        this.relation = inheritanceRelation;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public InheritanceRelation unmarshal(final InheritanceRelationAdapter value) throws Exception {
        if (value == null) {
            return null;
        } else if (value.href != null) {
            return value.href;
        } else {
            return value.relation;
        }
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the inheritanceRelation.
     * @return The adapter for this inheritanceRelation.
     */
    @Override
    public InheritanceRelationAdapter marshal(final InheritanceRelation value) throws Exception {
        return new InheritanceRelationAdapter(value);
    }

    
    

}
