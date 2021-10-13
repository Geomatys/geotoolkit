/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.feature.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.ComparisonMode;
import org.opengis.feature.catalog.AssociationRole;
import org.opengis.feature.catalog.FeatureAssociation;
import org.opengis.feature.catalog.FeatureCatalogue;
import org.opengis.feature.catalog.PropertyType;
import org.opengis.util.LocalName;


/**
 * Relationship that links instances of this feature type with instances of the same or of a different feature type.  - The memberOf-linkBetween association in the General Feature Model is not directly implemented here since it can be easily derived from combining the Role and MemberOf associations.
 *
 * <p>Java class for FC_FeatureAssociation_Type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FC_FeatureAssociation_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gfc}FC_FeatureType_Type">
 *       &lt;sequence>
 *         &lt;element name="roleName" type="{http://www.isotc211.org/2005/gfc}FC_AssociationRole_PropertyType" maxOccurs="unbounded" minOccurs="2"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "role"
})
@XmlRootElement( name = "FC_FeatureAssociation")
public class FeatureAssociationImpl extends FeatureTypeImpl implements FeatureAssociation {

    @XmlElement(required = true)
    private List<AssociationRole> role;

    @XmlTransient
    protected boolean rootElement = true;


    /**
     * An empty constructor used by JAXB
     */
    public FeatureAssociationImpl() {

    }

    /**
     * Clone a FeatureAssociation
     */
    public FeatureAssociationImpl(final FeatureAssociation feature) {
        super(feature);
        if (feature != null) {
            this.role = feature.getRole();
            this.setId("fassoc-" + feature.getCode());
        }
    }

    /**
     * Build a new FeatureAssociation
     */
    public FeatureAssociationImpl(final String id, final LocalName typeName, final String definition, final String code, final Boolean isAbstract, final List<LocalName> aliases,
            final FeatureCatalogue catalogue, final List<PropertyType> carrierOfCharacteristics, final List<AssociationRole> roleName) {
       super(id, typeName, definition, code, isAbstract, aliases, catalogue, carrierOfCharacteristics);
       this.role = roleName;
       this.setId("fassoc-" + code);
    }

    /**
     * Gets the value of the roleName property.
     */
    @Override
    public List<AssociationRole> getRole() {
        if (role == null) {
            role = new ArrayList<>();
        }
        return this.role;
    }

    public void setRole(final List<AssociationRole> role) {
         this.role = role;
    }

    public void setRole(final AssociationRole role) {
        if (this.role == null) {
            this.role = new ArrayList<>();
        }
        this.role.add(role);
    }

    @Override
    public FeatureAssociationImpl getReferenceableObject() {
        FeatureAssociationImpl reference = new FeatureAssociationImpl(this);
        reference.setReference(true);
        return reference;
    }

    /**
     * This java object contains cycle. this cycle cannot be handle by JAXB.
     * We must create reference mark in the xml.
     * @param marshaller
     */
    private void beforeMarshal(final Marshaller marshaller) {
        if (rootElement) {
            beforeMarshal(new HashMap<>());
        }
    }

    @Override
    public Map<String, Referenceable> beforeMarshal(Map<String, Referenceable> alreadySee) {
        alreadySee = super.beforeMarshal(alreadySee);
        rootElement = false;
        List<AssociationRole> replacement = new ArrayList<>();
        for (AssociationRole r: getRole()) {
            AssociationRoleImpl ri = (AssociationRoleImpl) r;

            if (alreadySee.get(ri.getId()) != null) {
                replacement.add(ri.getReferenceableObject());
            } else {
                alreadySee = ri.beforeMarshal(alreadySee);
                replacement.add(ri);
            }
        }
        role = replacement;
        return alreadySee;
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (!isReference()) {
            if (role != null) {
                s.append("role").append('\n');
                setReference(true);
                for (AssociationRole r: role) {
                    s.append(r).append('\n');
                }
                setReference(false);
            }
        }
        return s.toString();
    }


    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (super.equals(object) && object instanceof FeatureAssociationImpl) {
            final FeatureAssociationImpl that = (FeatureAssociationImpl) object;

            return Objects.equals(this.role, that.role);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
