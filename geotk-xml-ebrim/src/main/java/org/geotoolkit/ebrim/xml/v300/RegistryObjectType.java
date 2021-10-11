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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.geotoolkit.csw.xml.Settable;
import org.geotoolkit.ebrim.xml.RegistryObject;


/**
 * <p>Java class for RegistryObjectType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RegistryObjectType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}IdentifiableType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}Name" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}Description" minOccurs="0"/>
 *         &lt;element name="VersionInfo" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}VersionInfoType" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}Classification" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ExternalIdentifier" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="lid" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="objectType" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="status" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *     &lt;/extension>
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
    "versionInfo",
    "classification",
    "externalIdentifier"
})
@XmlSeeAlso({
    ExtrinsicObjectType.class,
    ClassificationSchemeType.class,
    ServiceType.class,
    ClassificationNodeType.class,
    AssociationType.class,
    OrganizationType.class,
    AdhocQueryType.class,
    RegistryType.class,
    ClassificationType.class,
    FederationType.class,
    ServiceBindingType.class,
    RegistryPackageType.class,
    NotificationType.class,
    SpecificationLinkType.class,
    ExternalLinkType.class,
    AuditableEventType.class,
    SubscriptionType.class,
    ExternalIdentifierType.class,
    PersonType.class
})
@XmlRootElement(name = "RegistryObject")
public class RegistryObjectType extends IdentifiableType implements RegistryObject, Settable {

    @XmlElement(name = "Name")
    private InternationalStringType name;
    @XmlElement(name = "Description")
    private InternationalStringType description;
    @XmlElement(name = "VersionInfo")
    private VersionInfoType versionInfo;
    @XmlElement(name = "Classification")
    private List<ClassificationType> classification;
    @XmlElement(name = "ExternalIdentifier")
    private List<ExternalIdentifierType> externalIdentifier;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String lid;
    @XmlAttribute
    private String objectType;
    @XmlAttribute
    private String status;

    /**
     * Gets the value of the name property.
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
     * Gets the value of the versionInfo property.
     */
    public VersionInfoType getVersionInfo() {
        return versionInfo;
    }

    /**
     * Sets the value of the versionInfo property.
     */
    public void setVersionInfo(final VersionInfoType value) {
        this.versionInfo = value;
    }

    /**
     * Gets the value of the classification property.
     */
    public List<ClassificationType> getClassification() {
        if (classification == null) {
            classification = new ArrayList<>();
        }
        return this.classification;
    }

    /**
     * Sets the value of the classification property.
     */
    public void setClassification(final ClassificationType classification) {
        if (this.classification == null) {
            this.classification = new ArrayList<>();
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
            externalIdentifier = new ArrayList<>();
        }
        return this.externalIdentifier;
    }

        /**
     * Sets the value of the externalIdentifier property.
     */
    public void setExternalIdentifier(final ExternalIdentifierType externalIdentifier) {
        if (this.externalIdentifier == null) {
            this.externalIdentifier = new ArrayList<>();
        }
        this.externalIdentifier.add(externalIdentifier);
    }

    /**
     * Sets the value of the externalIdentifier property.
     */
    public void setExternalIdentifier(List<ExternalIdentifierType> externalIdentifier) {
        if (externalIdentifier == null) {
            externalIdentifier = new ArrayList<>();
        }
        this.externalIdentifier = externalIdentifier;
    }

    /**
     * Gets the value of the lid property.
     */
    public String getLid() {
        return lid;
    }

    /**
     * Sets the value of the lid property.
     */
    public void setLid(final String value) {
        this.lid = value;
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

    /**
     * Return a BRIEF representation of the registryObject.
     * @return
     */
    @Override
    public RegistryObjectType toBrief() {
        RegistryObjectType brief = new RegistryObjectType();
        brief.setId(getId());
        brief.setLid(lid);
        brief.setObjectType(objectType);
        brief.setStatus(status);
        brief.setVersionInfo(versionInfo);
        return brief;
    }

    /**
     * Return a SUMMARY representation of the registryObject.
     * @return
     */
    @Override
    public RegistryObjectType toSummary() {
        RegistryObjectType summary = toBrief();
        summary.setSlot(getSlot());
        summary.setName(name);
        summary.setDescription(description);
        return summary;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (name != null) {
            sb.append("name:").append(name).append('\n');
        }
        if (description != null) {
            sb.append("description:").append(description).append('\n');
        }
        if (lid != null) {
            sb.append("lid:").append(lid).append('\n');
        }
        if (objectType != null) {
            sb.append("objectType:").append(objectType).append('\n');
        }
        if (status != null) {
            sb.append("status:").append(status).append('\n');
        }
        if (versionInfo != null) {
            sb.append("versionInfo:").append(versionInfo).append('\n');
        }
        if (classification != null) {
            sb.append("classification:\n");
            for (ClassificationType cl : classification) {
                sb.append(cl).append('\n');
            }
        }
        if (externalIdentifier != null) {
            sb.append("externalIdentifier:\n");
            for (ExternalIdentifierType cl : externalIdentifier) {
                sb.append(cl).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof RegistryObjectType && super.equals(obj)) {
            final RegistryObjectType that = (RegistryObjectType) obj;
            return Objects.equals(this.classification,     that.classification) &&
                   Objects.equals(this.description,        that.description) &&
                   Objects.equals(this.externalIdentifier, that.externalIdentifier) &&
                   Objects.equals(this.lid,                that.lid) &&
                   Objects.equals(this.name,               that.name) &&
                   Objects.equals(this.objectType,         that.objectType) &&
                   Objects.equals(this.status,             that.status) &&
                   Objects.equals(this.versionInfo,        that.versionInfo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + super.hashCode();
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 67 * hash + (this.versionInfo != null ? this.versionInfo.hashCode() : 0);
        hash = 67 * hash + (this.classification != null ? this.classification.hashCode() : 0);
        hash = 67 * hash + (this.externalIdentifier != null ? this.externalIdentifier.hashCode() : 0);
        hash = 67 * hash + (this.lid != null ? this.lid.hashCode() : 0);
        hash = 67 * hash + (this.objectType != null ? this.objectType.hashCode() : 0);
        hash = 67 * hash + (this.status != null ? this.status.hashCode() : 0);
        return hash;
    }

}
