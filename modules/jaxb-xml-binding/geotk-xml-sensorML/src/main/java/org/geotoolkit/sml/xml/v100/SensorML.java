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
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractSensorML;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/sensorML/1.0}metadataGroup"/>
 *         &lt;element name="member" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0}_Process"/>
 *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0}DocumentList"/>
 *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0}ContactList"/>
 *                 &lt;/choice>
 *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}token" fixed="1.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SensorMLType", propOrder = {
    "keywords",
    "identification",
    "classification",
    "validTime",
    "securityConstraint",
    "legalConstraint",
    "characteristics",
    "capabilities",
    "contact",
    "documentation",
    "history",
    "member"
})
@XmlRootElement(name = "SensorML")
public class SensorML extends AbstractSensorML {

    private List<Keywords> keywords;
    private List<Identification> identification;
    private List<Classification> classification;
    private ValidTime validTime;
    private SecurityConstraint securityConstraint;
    private List<LegalConstraint> legalConstraint;
    private List<Characteristics> characteristics;
    private List<Capabilities> capabilities;
    private List<Contact> contact;
    private List<Documentation> documentation;
    private List<History> history;
    @XmlElement(required = true)
    private List<Member> member;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String version;

    /**
     * An empty constructor used by JAXB
     */
    public SensorML() {

    }

    public SensorML(String version, List<Member> members) {
        this.version = version;
        this.member  = members;
    }

    /**
     * Gets the value of the keywords property.
     */
    public List<Keywords> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<Keywords>();
        }
        return this.keywords;
    }

    /**
     * Gets the value of the identification property.
     */
    public List<Identification> getIdentification() {
        if (identification == null) {
            identification = new ArrayList<Identification>();
        }
        return this.identification;
    }

    /**
     * Gets the value of the classification property.
     */
    public List<Classification> getClassification() {
        if (classification == null) {
            classification = new ArrayList<Classification>();
        }
        return this.classification;
    }

    /**
     * Gets the value of the validTime property.
     */
    public ValidTime getValidTime() {
        return validTime;
    }

    /**
     * Sets the value of the validTime property.
     */
    public void setValidTime(ValidTime value) {
        this.validTime = value;
    }

    /**
     * Gets the value of the securityConstraint property.
     */
    public SecurityConstraint getSecurityConstraint() {
        return securityConstraint;
    }

    /**
     * Sets the value of the securityConstraint property.
     */
    public void setSecurityConstraint(SecurityConstraint value) {
        this.securityConstraint = value;
    }

    /**
     */
    public List<LegalConstraint> getLegalConstraint() {
        if (legalConstraint == null) {
            legalConstraint = new ArrayList<LegalConstraint>();
        }
        return this.legalConstraint;
    }

    /**
     * Gets the value of the characteristics property.
     */
    public List<Characteristics> getCharacteristics() {
        if (characteristics == null) {
            characteristics = new ArrayList<Characteristics>();
        }
        return this.characteristics;
    }

    /**
     * Gets the value of the capabilities property.
     */
    public List<Capabilities> getCapabilities() {
        if (capabilities == null) {
            capabilities = new ArrayList<Capabilities>();
        }
        return this.capabilities;
    }

    /**
     * Gets the value of the contact property.
     */
    public List<Contact> getContact() {
        if (contact == null) {
            contact = new ArrayList<Contact>();
        }
        return this.contact;
    }

    /**
     * Gets the value of the documentation property.
     */
    public List<Documentation> getDocumentation() {
        if (documentation == null) {
            documentation = new ArrayList<Documentation>();
        }
        return this.documentation;
    }

    /**
     * Gets the value of the history property.
     */
    public List<History> getHistory() {
        if (history == null) {
            history = new ArrayList<History>();
        }
        return this.history;
    }

    /**
     * Gets the value of the member property.
     */
    public List<Member> getMember() {
        if (member == null) {
            member = new ArrayList<Member>();
        }
        return this.member;
    }

    /**
     * Gets the value of the member property.
     */
    public void setMember(List<Member> member) {
        this.member = member;
    }

    /**
     * Gets the value of the member property.
     */
    public void setMember(Member member) {
        if (this.member == null) {
            this.member = new ArrayList<Member>();
        }
        this.member.add(member);
    }

    /**
     * Gets the value of the member property.
     */
    public void setMember(SystemType member) {
        if (this.member == null) {
            this.member = new ArrayList<Member>();
        }
        this.member.add(new Member(member));
    }

    /**
     * Gets the value of the member property.
     */
    public void setMember(ComponentType member) {
        if (this.member == null) {
            this.member = new ArrayList<Member>();
        }
        this.member.add(new Member(member));
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     */
    public void setVersion(String value) {
        this.version = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[SensorML]").append("\n");
        if (keywords != null) {
            sb.append("Keywords:").append('\n');
            for (Keywords k : keywords) {
                sb.append(k).append('\n');
            }
        }
        if (identification != null) {
            sb.append("Identification:").append('\n');
            for (Identification k : identification) {
                sb.append(k).append('\n');
            }
        }
        if (classification != null) {
            sb.append("Identification:").append('\n');
            for (Classification k : classification) {
                sb.append(k).append('\n');
            }
        }
        if (validTime != null) {
            sb.append("validTime:").append(validTime).append('\n');
        }
        if (securityConstraint != null) {
            sb.append("securityConstraint:").append(securityConstraint).append('\n');
        }
        if (legalConstraint != null) {
            sb.append("legalConstraint:").append('\n');
            for (LegalConstraint k : legalConstraint) {
                sb.append(k).append('\n');
            }
        }
        if (characteristics != null) {
            sb.append("characteristics:").append('\n');
            for (Characteristics k : characteristics) {
                sb.append(k).append('\n');
            }
        }
        if (capabilities != null) {
            sb.append("capabilities:").append('\n');
            for (Capabilities k : capabilities) {
                sb.append(k).append('\n');
            }
        }
        if (contact != null) {
            sb.append("contact:").append('\n');
            for (Contact k : contact) {
                sb.append(k).append('\n');
            }
        }
        if (documentation != null) {
            sb.append("documentation:").append('\n');
            for (Documentation k : documentation) {
                sb.append(k).append('\n');
            }
        }
        if (history != null) {
            sb.append("history:").append('\n');
            for (History k : history) {
                sb.append(k).append('\n');
            }
        }
        if (member != null) {
            sb.append("member:").append('\n');
            for (Member k : member) {
                sb.append(k).append('\n');
            }
        }
        if (version != null) {
            sb.append("version:").append(version).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof SensorML) {
            final SensorML that = (SensorML) object;
            return Utilities.equals(this.capabilities,       that.capabilities)       &&
                   Utilities.equals(this.characteristics,    that.characteristics)    &&
                   Utilities.equals(this.classification,     that.classification)     &&
                   Utilities.equals(this.contact,            that.contact)            &&
                   Utilities.equals(this.documentation,      that.contact)            &&
                   Utilities.equals(this.history,            that.history)            &&
                   Utilities.equals(this.identification,     that.identification)     &&
                   Utilities.equals(this.keywords,           that.keywords)           &&
                   Utilities.equals(this.legalConstraint,    that.legalConstraint)    &&
                   Utilities.equals(this.member,             that.member)             &&
                   Utilities.equals(this.securityConstraint, that.securityConstraint) &&
                   Utilities.equals(this.validTime,          that.validTime)          &&
                   Utilities.equals(this.version,            that.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (this.keywords != null ? this.keywords.hashCode() : 0);
        hash = 83 * hash + (this.identification != null ? this.identification.hashCode() : 0);
        hash = 83 * hash + (this.classification != null ? this.classification.hashCode() : 0);
        hash = 83 * hash + (this.validTime != null ? this.validTime.hashCode() : 0);
        hash = 83 * hash + (this.securityConstraint != null ? this.securityConstraint.hashCode() : 0);
        hash = 83 * hash + (this.legalConstraint != null ? this.legalConstraint.hashCode() : 0);
        hash = 83 * hash + (this.characteristics != null ? this.characteristics.hashCode() : 0);
        hash = 83 * hash + (this.capabilities != null ? this.capabilities.hashCode() : 0);
        hash = 83 * hash + (this.contact != null ? this.contact.hashCode() : 0);
        hash = 83 * hash + (this.documentation != null ? this.documentation.hashCode() : 0);
        hash = 83 * hash + (this.history != null ? this.history.hashCode() : 0);
        hash = 83 * hash + (this.member != null ? this.member.hashCode() : 0);
        hash = 83 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }

}
