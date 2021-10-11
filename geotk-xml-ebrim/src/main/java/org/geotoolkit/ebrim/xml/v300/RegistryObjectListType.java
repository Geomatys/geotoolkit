/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.ebrim.xml.v300;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for RegistryObjectListType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RegistryObjectListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}Identifiable" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistryObjectListType", propOrder = {
    "identifiable"
})
public class RegistryObjectListType {

    @XmlTransient
    private static ObjectFactory FACTORY = new ObjectFactory();

    @XmlElementRef(name = "Identifiable", namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", type = JAXBElement.class)
    private List<JAXBElement<? extends IdentifiableType>> identifiable;

    /**
     * Gets the value of the identifiable property.
     */
    public List<JAXBElement<? extends IdentifiableType>> getIdentifiable() {
        if (identifiable == null) {
            identifiable = new ArrayList<JAXBElement<? extends IdentifiableType>>();
        }
        return this.identifiable;
    }

    public void setIdentifiable(final List<JAXBElement<? extends IdentifiableType>> identifiable) {
        this.identifiable = identifiable;
    }

    public void setIdentifiable(final IdentifiableType identifiable) {
        if (this.identifiable == null) {
            this.identifiable = new ArrayList<JAXBElement<? extends IdentifiableType>>();
        }
        this.identifiable.add(createIdentifiable(identifiable));
    }

    private JAXBElement<? extends IdentifiableType> createIdentifiable(final IdentifiableType id) {
        if (id instanceof ObjectRefType) {
            return FACTORY.createObjectRef((ObjectRefType)id);

        } else if (id instanceof ExtrinsicObjectType) {
            return FACTORY.createExtrinsicObject(( ExtrinsicObjectType)id);

        } else if (id instanceof ClassificationSchemeType) {
            return FACTORY.createClassificationScheme((ClassificationSchemeType)id);

        } else if (id instanceof ServiceType) {
            return FACTORY.createService((ServiceType)id);

        } else if (id instanceof ClassificationNodeType) {
            return FACTORY.createClassificationNode((ClassificationNodeType)id);

        } else if (id instanceof AssociationType) {
            return FACTORY.createAssociation((AssociationType)id);

        } else if (id instanceof OrganizationType) {
            return FACTORY.createOrganization((OrganizationType)id);

        } else if (id instanceof AdhocQueryType) {
            return FACTORY.createAdhocQuery((AdhocQueryType)id);

        } else if (id instanceof RegistryType) {
            return FACTORY.createRegistry((RegistryType)id);

        } else if (id instanceof ClassificationType) {
            return FACTORY.createClassification((ClassificationType)id);

        } else if (id instanceof FederationType) {
            return FACTORY.createFederation((FederationType)id);

        } else if (id instanceof ServiceBindingType) {
            return FACTORY.createServiceBinding((ServiceBindingType)id);

        } else if (id instanceof RegistryPackageType) {
            return FACTORY.createRegistryPackage((RegistryPackageType)id);

        } else if (id instanceof NotificationType) {
            return FACTORY.createNotification((NotificationType)id);

        } else if (id instanceof SpecificationLinkType) {
            return FACTORY.createSpecificationLink((SpecificationLinkType)id);

        } else if (id instanceof ExternalLinkType) {
            return FACTORY.createExternalLink((ExternalLinkType)id);

        } else if (id instanceof AuditableEventType) {
            return FACTORY.createAuditableEvent((AuditableEventType)id);

        } else if (id instanceof AuditableEventType) {
            return FACTORY.createAuditableEvent((AuditableEventType)id);

        } else if (id instanceof SubscriptionType) {
            return FACTORY.createSubscription((SubscriptionType)id);

        } else if (id instanceof ExternalIdentifierType) {
            return FACTORY.createExternalIdentifier((ExternalIdentifierType)id);

        } else if (id instanceof UserType) {
            return FACTORY.createUser((UserType)id);

        } else if (id instanceof PersonType) {
            return FACTORY.createPerson((PersonType)id);

        } else {
            throw new IllegalArgumentException("unexpected type in registryObjectListType");
        }
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append('[').append(this.getClass().getSimpleName()).append(']').append('\n');
        if (identifiable != null) {
            s.append("identifiable:\n");
            for (JAXBElement<? extends IdentifiableType> jb : identifiable) {
                s.append(jb.getValue()).append('\n');
            }
        }
        return s.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof RegistryObjectListType) {
            final RegistryObjectListType that = (RegistryObjectListType) obj;

            if (this.identifiable == null && that.identifiable == null) {
                return true;
            } else if (this.identifiable != null && that.identifiable != null) {
                if (this.identifiable.size() == that.identifiable.size()) {
                    for (int i = 0; i < this.identifiable.size(); i++) {
                        if (!this.identifiable.get(i).getValue().equals(that.identifiable.get(i).getValue())) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.identifiable != null ? this.identifiable.hashCode() : 0);
        return hash;
    }

}
