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
package org.geotoolkit.ebrim.xml.v250;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.ebrim.xml.RegistryObject;


/**
 * id may be empty.
 * If specified it may be in urn:uuid format or be in some arbitrary format.
 * If id is empty registry must generate globally unique id.
 *
 * If id is provided and in proper UUID syntax (starts with urn:uuid:) registry will honour it.
 *
 * If id is provided and is not in proper UUID syntax then it is used for
 * linkage within document and is ignored by the registry. In this case the
 * registry generates a UUID for id attribute.
 *
 * id must not be null when object is being retrieved from the registry.
 *
 *
 *
 * <p>Java class for RegistryObjectType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RegistryObjectType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}Name" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}Description" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}Slot" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}Classification" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ExternalIdentifier" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="home" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="objectType" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="status">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NCName">
 *             &lt;enumeration value="Submitted"/>
 *             &lt;enumeration value="Approved"/>
 *             &lt;enumeration value="Deprecated"/>
 *             &lt;enumeration value="Withdrawn"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistryObjectType", propOrder = {
    "name",
    "description",
    "slot",
    "classification",
    "externalIdentifier"
})
@XmlSeeAlso({
    UserType.class,
    ClassificationNodeType.class,
    AssociationType.class,
    OrganizationType.class,
    ClassificationType.class,
    RegistryEntryType.class,
    ServiceBindingType.class,
    SpecificationLinkType.class,
    ExternalLinkType.class,
    AuditableEventType.class,
    ExternalIdentifierType.class,
    AdhocQueryType.class,
    Subscription.class
})
@XmlRootElement(name = "RegistryObject")
public class RegistryObjectType implements RegistryObject {

    @XmlElement(name = "Name")
    private InternationalStringType name;
    @XmlElement(name = "Description")
    private InternationalStringType description;
    @XmlElement(name = "Slot")
    private List<SlotType> slot;
    @XmlElement(name = "Classification")
    private List<ClassificationType> classification;
    @XmlElement(name = "ExternalIdentifier")
    private List<ExternalIdentifierType> externalIdentifier;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String id;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String home;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String objectType;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String status;

    /**
     * Gets the value of the name property.
     *
     */
    @Override
    public InternationalStringType getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     */
    public void setName(final InternationalStringType value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     *
     */
    public InternationalStringType getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     */
    public void setDescription(final InternationalStringType value) {
        this.description = value;
    }

    /**
     * Gets the value of the slot property.
     *
     */
    public List<SlotType> getSlot() {
        if (slot == null) {
            slot = new ArrayList<SlotType>();
        }
        return this.slot;
    }

    /**
     * Sets the value of the slot property.
     */
    public void setSlot(final SlotType slot) {
        if (this.slot == null) {
            this.slot = new ArrayList<SlotType>();
        }
        this.slot.add(slot);
    }

     /**
     * Sets the value of the slot property.
     */
    public void setSlot(final List<SlotType> slot) {
        this.slot = slot;
    }


    /**
     * Gets the value of the classification property.
     */
    public List<ClassificationType> getClassification() {
        if (classification == null) {
            classification = new ArrayList<ClassificationType>();
        }
        return this.classification;
    }

    /**
     * Sets the value of the classification property.
     */
    public void setClassification(final ClassificationType classification) {
        if (this.classification == null) {
            this.classification = new ArrayList<ClassificationType>();
        }
        this.classification.add(classification);
    }

    /**
     * Sets the value of the classification property.
     */
    public void setClassification(final List<ClassificationType> classification) {
       this.classification = classification;
    }

    /**
     * Gets the value of the externalIdentifier property.
     */
    public List<ExternalIdentifierType> getExternalIdentifier() {
        if (externalIdentifier == null) {
            externalIdentifier = new ArrayList<ExternalIdentifierType>();
        }
        return this.externalIdentifier;
    }

    /**
     * Sets the value of the externalIdentifier property.
     */
    public void setExternalIdentifier(final ExternalIdentifierType externalIdentifier) {
        if (this.externalIdentifier == null) {
            this.externalIdentifier = new ArrayList<ExternalIdentifierType>();
        }
        this.externalIdentifier.add(externalIdentifier);
    }

    /**
     * Sets the value of the externalIdentifier property.
     */
    public void setExternalIdentifier(List<ExternalIdentifierType> externalIdentifier) {
        if (externalIdentifier == null) {
            externalIdentifier = new ArrayList<ExternalIdentifierType>();
        }
        this.externalIdentifier = externalIdentifier;
    }

    /**
     * Gets the value of the id property.
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     */
    public void setId(final String value) {
        this.id = value;
    }

    /**
     * Gets the value of the home property.
     */
    public String getHome() {
        return home;
    }

    /**
     * Sets the value of the home property.
     */
    public void setHome(final String value) {
        this.home = value;
    }

    /**
     * Gets the value of the objectType property.
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Sets the value of the objectType property.
     */
    public void setObjectType(final String value) {
        this.objectType = value;
    }

    /**
     * Gets the value of the status property.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     */
    public void setStatus(final String value) {
        this.status = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[").append(getClass().getSimpleName()).append("]\n");
        if (id != null) {
            sb.append("id:").append(id).append('\n');
        }
        if (name != null) {
            sb.append("name:").append(name).append('\n');
        }
        if (home != null) {
            sb.append("home:").append(home).append('\n');
        }
        if (objectType != null) {
            sb.append("objectType:").append(objectType).append('\n');
        }
        if (status != null) {
            sb.append("status:").append(status).append('\n');
        }
        if (description != null) {
            sb.append("description:").append(description).append('\n');
        }
        if (classification != null) {
            sb.append("classification:\n");
            for (ClassificationType cl : classification) {
                sb.append(cl).append('\n');
            }
        }
        if (externalIdentifier != null) {
            sb.append("externalIdentifier:\n");
            for (ExternalIdentifierType ei : externalIdentifier) {
                sb.append(ei).append('\n');
            }
        }
        if (slot != null) {
            sb.append("slot:\n");
            for (SlotType sl : slot) {
                sb.append(sl).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof RegistryObjectType) {
            final RegistryObjectType that = (RegistryObjectType) obj;
            return Objects.equals(this.classification,     that.classification) &&
                   Objects.equals(this.description,        that.description) &&
                   Objects.equals(this.externalIdentifier, that.externalIdentifier) &&
                   Objects.equals(this.home,               that.home) &&
                   Objects.equals(this.id,                 that.id) &&
                   Objects.equals(this.name,               that.name) &&
                   Objects.equals(this.objectType,         that.objectType) &&
                   Objects.equals(this.slot,               that.slot) &&
                   Objects.equals(this.status,             that.status);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 23 * hash + (this.slot != null ? this.slot.hashCode() : 0);
        hash = 23 * hash + (this.classification != null ? this.classification.hashCode() : 0);
        hash = 23 * hash + (this.externalIdentifier != null ? this.externalIdentifier.hashCode() : 0);
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 23 * hash + (this.home != null ? this.home.hashCode() : 0);
        hash = 23 * hash + (this.objectType != null ? this.objectType.hashCode() : 0);
        hash = 23 * hash + (this.status != null ? this.status.hashCode() : 0);
        return hash;
    }

}
