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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.internal.jaxb.gco.Multiplicity;
import org.apache.sis.util.ComparisonMode;
import org.opengis.feature.catalog.AssociationRole;
import org.opengis.feature.catalog.Constraint;
import org.opengis.feature.catalog.DefinitionReference;
import org.opengis.feature.catalog.FeatureAssociation;
import org.opengis.feature.catalog.FeatureType;
import org.opengis.feature.catalog.RoleType;
import org.opengis.util.LocalName;


/**
 * A role of the association FC_AssociationRole::relation.
 * - [ocl] - roleName = FC_Member::memberName; - FC_PropertyType::cardinality realizes GF_AssociationRole::cardinality - [/ocl]
 *
 * <p>Java class for FC_AssociationRole_Type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FC_AssociationRole_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gfc}AbstractFC_PropertyType_Type">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://www.isotc211.org/2005/gfc}FC_RoleType_PropertyType"/>
 *         &lt;element name="isOrdered" type="{http://www.isotc211.org/2005/gco}Boolean_PropertyType"/>
 *         &lt;element name="isNavigable" type="{http://www.isotc211.org/2005/gco}Boolean_PropertyType"/>
 *         &lt;element name="relation" type="{http://www.isotc211.org/2005/gfc}FC_FeatureAssociation_PropertyType"/>
 *         &lt;element name="rolePlayer" type="{http://www.isotc211.org/2005/gfc}FC_FeatureType_PropertyType"/>
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
@XmlType(name = "FC_AssociationRole_Type", propOrder = {
    "type",
    "isOrdered",
    "isNavigable",
    "relation",
    "valueType"
})
@XmlRootElement(name="FC_AssociationRole")
public class AssociationRoleImpl extends PropertyTypeImpl implements AssociationRole {

    @XmlElement(required = true)
    private RoleType type;
    @XmlElement(required = true)
    private Boolean isOrdered;
    @XmlElement(required = true)
    private Boolean isNavigable;
    @XmlElement(required = true)
    private FeatureAssociation relation;
    @XmlElement(required = true)
    private FeatureType valueType;

    /**
     * An empty constructor used by JAXB
     */
    public AssociationRoleImpl() {

    }

    /**
     * Clone a AssociationRole
     */
    public AssociationRoleImpl(final String id, final LocalName memberName, final String definition, final Multiplicity cardinality,
            final FeatureType featureType, final List<Constraint> constrainedBy, final DefinitionReference definitionReference,
            final RoleType type, final Boolean isOrdered, final Boolean isNavigable, final FeatureAssociation relation, final FeatureType rolePlayer) {
        super(id, memberName, definition, cardinality, featureType, constrainedBy, definitionReference);
        this.isNavigable = isNavigable;
        this.isOrdered   = isOrdered;
        this.relation    = relation;
        this.valueType  = rolePlayer;
        this.type        = type;
    }

     /**
     * Build a new AssociationRole
     */
    public AssociationRoleImpl(final AssociationRole feature) {
        super(feature);
        if (feature != null) {
            this.isNavigable = feature.getIsNavigable();
            this.isOrdered   = feature.getIsOrdered();
            this.relation    = feature.getRelation();
            this.valueType   = feature.getValueType();
            this.type        = feature.getType();
        }
    }

    /**
     * Gets the value of the type property.
     *
     */
    @Override
    public RoleType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     */
    public void setType(final RoleType value) {
        this.type = value;
    }

    /**
     * Gets the value of the isOrdered property.
     *
     */
    @Override
    public Boolean getIsOrdered() {
        return isOrdered;
    }

    /**
     * Sets the value of the isOrdered property.
     *
     */
    public void setIsOrdered(final Boolean value) {
        this.isOrdered = value;
    }

    /**
     * Gets the value of the isNavigable property.
     *
     */
    @Override
    public Boolean getIsNavigable() {
        return isNavigable;
    }

    /**
     * Sets the value of the isNavigable property.
     */
    public void setIsNavigable(final Boolean value) {
        this.isNavigable = value;
    }

    /**
     * Gets the value of the relation property.
     *
     */
    @Override
    public FeatureAssociation getRelation() {
        return relation;
    }

    /**
     * Sets the value of the relation property.
     */
    public void setRelation(final FeatureAssociation value) {
        this.relation = value;
    }

    /**
     * Gets the value of the rolePlayer property.
     *
     */
    @Override
    public FeatureType getValueType() {
        return valueType;
    }

    /**
     * Sets the value of the rolePlayer property.
     *
     */
    public void setValueType(final FeatureType value) {
        this.valueType = value;
    }

    @Override
    public AssociationRoleImpl getReferenceableObject() {
        AssociationRoleImpl result = new AssociationRoleImpl(this);
        result.setReference(true);
        //ensure that relation is reference
        if (result.relation != null) {
            ((FeatureAssociationImpl)result.relation).setReference(true);
        }
        return result;
    }

    @Override
    public Map<String, Referenceable> beforeMarshal(Map<String, Referenceable> alreadySee) {
        super.beforeMarshal(alreadySee);

        if (valueType != null) {
            if (alreadySee.get(valueType.getId()) != null) {
                valueType = ((FeatureTypeImpl)valueType).getReferenceableObject();
            } else {
                alreadySee = ((FeatureTypeImpl)valueType).beforeMarshal(alreadySee);
            }
        }

        if (relation != null) {
            if (alreadySee.get(relation.getId()) != null) {
                relation = ((FeatureAssociationImpl)relation).getReferenceableObject();
            } else {
                alreadySee = ((FeatureAssociationImpl)relation).beforeMarshal(alreadySee);
            }
        }
        return alreadySee;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (type != null) {
            s.append("type: ").append(type).append('\n');
        }
        if (isOrdered != null) {
            s.append("is ordered: ").append(isOrdered).append('\n');
        }
        if (isNavigable != null) {
            s.append("is navigable: ").append(isNavigable).append('\n');
        }
        if (relation != null) {
            s.append("relation: ").append(relation.toString()).append('\n');
        }
        if (valueType != null) {
            s.append("Value type: ").append(valueType.toString()).append('\n');
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
        if (super.equals(object) && object instanceof AssociationRoleImpl) {
            final AssociationRoleImpl that = (AssociationRoleImpl) object;

            // to avoid infinite cycle
            boolean association = false;
            if (this.relation != null && that.relation != null) {
                association = Objects.equals(this.relation.getCode(), that.relation.getCode());
            } else if (this.relation == null && that.relation == null) {
                association = true;

            } else {
                return false;
            }

            return Objects.equals(this.isNavigable, that.isNavigable) &&
                   Objects.equals(this.isOrdered,   that.isOrdered)   &&
                   Objects.equals(this.type,        that.type)        &&
                   Objects.equals(this.valueType,   that.valueType)   &&
                   association;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + super.hashCode();
        return hash;
    }

}
