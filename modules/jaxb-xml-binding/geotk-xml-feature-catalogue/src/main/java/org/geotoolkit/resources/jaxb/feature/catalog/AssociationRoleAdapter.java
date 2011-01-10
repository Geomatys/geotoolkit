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
import org.geotoolkit.feature.catalog.AssociationRoleImpl;
import org.opengis.feature.catalog.AssociationRole;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI AssociationRole. See
 * package documentation for more information about JAXB and AssociationRole.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public class AssociationRoleAdapter extends XmlAdapter<AssociationRoleAdapter, AssociationRole> {
    
    private AssociationRole association;
    
    @XmlIDREF
    @XmlAttribute(namespace="http://www.w3.org/1999/xlink")
    private AssociationRoleImpl href;
    
    /**
     * Empty constructor for JAXB only.
     */
    private AssociationRoleAdapter() {
    }

    /**
     * Wraps an AssociationRole value with a {@code SV_AssociationRole} tags at marshalling-time.
     *
     * @param association The AssociationRole value to marshall.
     */
    public AssociationRoleAdapter(final AssociationRole association) {
        if (association instanceof AssociationRoleImpl && ((AssociationRoleImpl)association).isReference()) {
            this.href        = (AssociationRoleImpl) association;
            this.association = null;
        } else {
            this.href        = null;
            this.association = association;
        }
    }

    /**
     * Returns the AssociationRole value covered by a {@code SV_AssociationRole} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the AssociationRole value.
     */
    protected AssociationRoleAdapter wrap(final AssociationRole value) {
        return new AssociationRoleAdapter(value);
    }

    /**
     * Returns the {@link AssociationRoleImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "FC_AssociationRole")
    public AssociationRoleImpl getAssociationRole() {
        if (association == null) 
            return null;
        return (association instanceof AssociationRoleImpl) ?
            (AssociationRoleImpl)association : new AssociationRoleImpl(association);
    }

    /**
     * Sets the value for the {@link AssociationRoleImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setAssociationRole(final AssociationRoleImpl AssociationRole) {
        this.association = AssociationRole;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public AssociationRole unmarshal(final AssociationRoleAdapter value) throws Exception {
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
     * @param value The bound type value, here the AssociationRole.
     * @return The adapter for this AssociationRole.
     */
    @Override
    public AssociationRoleAdapter marshal(final AssociationRole value) throws Exception {
        return new AssociationRoleAdapter(value);
    }

    
    

}
