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
package org.geotoolkit.sml.xml.v101;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractCapabilities;
import org.geotoolkit.sml.xml.AbstractCharacteristics;
import org.geotoolkit.sml.xml.AbstractClassification;
import org.geotoolkit.sml.xml.AbstractContact;
import org.geotoolkit.sml.xml.AbstractDataSource;
import org.geotoolkit.sml.xml.AbstractDocumentation;
import org.geotoolkit.sml.xml.AbstractHistory;
import org.geotoolkit.sml.xml.AbstractIdentification;
import org.geotoolkit.sml.xml.AbstractKeywords;
import org.geotoolkit.sml.xml.AbstractLegalConstraint;
import org.geotoolkit.sml.xml.AbstractProcess;
import org.geotoolkit.sml.xml.AbstractProcessChain;
import org.geotoolkit.sml.xml.AbstractProcessModel;
import org.geotoolkit.sml.xml.AbstractSensorML;
import org.geotoolkit.sml.xml.Component;
import org.geotoolkit.sml.xml.ComponentArray;
import org.geotoolkit.sml.xml.System;
import org.geotoolkit.sml.xml.SMLMember;
import javax.xml.namespace.QName;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.logging.Logging;


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
 *         &lt;group ref="{http://www.opengis.net/sensorML/1.0.1}metadataGroup"/>
 *         &lt;element name="member" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}_Process"/>
 *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}DocumentList"/>
 *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}ContactList"/>
 *                 &lt;/choice>
 *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}token" fixed="1.0.1" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
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
public class SensorML extends SensorObject implements AbstractSensorML {

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
    private List<SensorML.Member> member;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private String version;

    /**
     * An empty constructor used by JAXB
     */
    public SensorML() {

    }

    public SensorML(final String version, final List<SensorML.Member> members) {
        this.version = version;
        this.member  = members;
    }

    public SensorML(final AbstractSensorML sml) {
        if (sml != null) {
            this.version = sml.getVersion();
            if (sml.getMember() != null) {
                this.member  = new ArrayList<SensorML.Member>();
                for (SMLMember m : sml.getMember()) {
                    this.member.add(new Member(m));
                }
            }
            if (sml.getCapabilities() != null) {
                this.capabilities  = new ArrayList<Capabilities>();
                for (AbstractCapabilities m : sml.getCapabilities()) {
                    this.capabilities.add(new Capabilities(m));
                }
            }
            if (sml.getIdentification() != null) {
                this.identification  = new ArrayList<Identification>();
                for (AbstractIdentification m : sml.getIdentification()) {
                    this.identification.add(new Identification(m));
                }
            }
            if (sml.getCharacteristics() != null) {
                this.characteristics  = new ArrayList<Characteristics>();
                for (AbstractCharacteristics m : sml.getCharacteristics()) {
                    this.characteristics.add(new Characteristics(m));
                }
            }
            if (sml.getClassification() != null) {
                this.classification  = new ArrayList<Classification>();
                for (AbstractClassification m : sml.getClassification()) {
                    this.classification.add(new Classification(m));
                }
            }
            if (sml.getContact() != null) {
                this.contact  = new ArrayList<Contact>();
                for (AbstractContact m : sml.getContact()) {
                    this.contact.add(new Contact(m));
                }
            }
            if (sml.getDocumentation() != null) {
                this.documentation  = new ArrayList<Documentation>();
                for (AbstractDocumentation m : sml.getDocumentation()) {
                    this.documentation.add(new Documentation(m));
                }
            }
            if (sml.getHistory() != null) {
                this.history  = new ArrayList<History>();
                for (AbstractHistory m : sml.getHistory()) {
                    this.history.add(new History(m));
                }
            }
            if (sml.getKeywords() != null) {
                this.keywords  = new ArrayList<Keywords>();
                for (AbstractKeywords m : sml.getKeywords()) {
                    this.keywords.add(new Keywords(m));
                }
            }
            if (sml.getLegalConstraint() != null) {
                this.legalConstraint  = new ArrayList<LegalConstraint>();
                for (AbstractLegalConstraint m : sml.getLegalConstraint()) {
                    this.legalConstraint.add(new LegalConstraint(m));
                }
            }
            if (sml.getSecurityConstraint() != null) {
                this.securityConstraint = new SecurityConstraint(sml.getSecurityConstraint());
            }
            if (sml.getValidTime() != null) {
                this.validTime = new ValidTime(sml.getValidTime());
            }
        }
    }

    /**
     * Gets the value of the keywords property.
     */
    @Override
    public List<Keywords> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<Keywords>();
        }
        return this.keywords;
    }

    public void setKeywords(List<Keywords> keywords) {
        this.keywords = keywords;
    }

    /**
     * Gets the value of the identification property.
     */
    @Override
    public List<Identification> getIdentification() {
        if (identification == null) {
            identification = new ArrayList<Identification>();
        }
        return this.identification;
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setIdentification(final List<Identification> identification) {
        this.identification = identification;
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setIdentification(final Identification identification) {
        if (identification != null) {
            if (this.identification == null) {
                this.identification = new ArrayList<Identification>();
            }
            this.identification.add(identification);
        }
    }

    /**
     * Gets the value of the classification property.
     */
    @Override
    public List<Classification> getClassification() {
        if (classification == null) {
            classification = new ArrayList<Classification>();
        }
        return this.classification;
    }

    public void setClassification(List<Classification> classification) {
        this.classification = classification;
    }

    /**
     * Gets the value of the validTime property.
     *
     * @return
     *     possible object is
     *     {@link ValidTime }
     *
     */
    @Override
    public ValidTime getValidTime() {
        return validTime;
    }

    /**
     * Sets the value of the validTime property.
     */
    public void setValidTime(final ValidTime value) {
        this.validTime = value;
    }

    /**
     * Gets the value of the securityConstraint property.
     */
    @Override
    public SecurityConstraint getSecurityConstraint() {
        return securityConstraint;
    }

    /**
     * Sets the value of the securityConstraint property.
     */
    public void setSecurityConstraint(final SecurityConstraint value) {
        this.securityConstraint = value;
    }

    /**
     * Gets the value of the legalConstraint property.
     */
    @Override
    public List<LegalConstraint> getLegalConstraint() {
        if (legalConstraint == null) {
            legalConstraint = new ArrayList<LegalConstraint>();
        }
        return this.legalConstraint;
    }

    public void setLegalConstraint(List<LegalConstraint> legalConstraint) {
        this.legalConstraint = legalConstraint;
    }

    /**
     * Gets the value of the characteristics property.
     *
     */
    @Override
    public List<Characteristics> getCharacteristics() {
        if (characteristics == null) {
            characteristics = new ArrayList<Characteristics>();
        }
        return this.characteristics;
    }

    public void setCharacteristics(List<Characteristics> characteristics) {
        this.characteristics = characteristics;
    }

    /**
     * Gets the value of the capabilities property.
     */
    @Override
    public List<Capabilities> getCapabilities() {
        if (capabilities == null) {
            capabilities = new ArrayList<Capabilities>();
        }
        return this.capabilities;
    }

    public void setCapabilities(List<Capabilities> capabilities) {
        this.capabilities = capabilities;
    }

    /**
     * Gets the value of the contact property.
     */
    @Override
    public List<Contact> getContact() {
        if (contact == null) {
            contact = new ArrayList<Contact>();
        }
        return this.contact;
    }

    public void setContact(List<Contact> contact) {
        this.contact = contact;
    }

    /**
     * Gets the value of the documentation property.
     */
    @Override
    public List<Documentation> getDocumentation() {
        if (documentation == null) {
            documentation = new ArrayList<Documentation>();
        }
        return this.documentation;
    }

    public void setDocumentation(List<Documentation> documentation) {
        this.documentation = documentation;
    }

    /**
     * Gets the value of the history property.
     */
    @Override
    public List<History> getHistory() {
        if (history == null) {
            history = new ArrayList<History>();
        }
        return this.history;
    }

    public void setHistory(List<History> history) {
        this.history = history;
    }

    /**
     * Gets the value of the member property.
     */
    @Override
    public List<SensorML.Member> getMember() {
        if (member == null) {
            member = new ArrayList<SensorML.Member>();
        }
        return this.member;
    }

    /**
     * Sets the value of the member property.
     */
    public void setMember(final List<SensorML.Member> member) {
        this.member = member;
    }

    /**
     * Gets the value of the version property.
     */
    @Override
    public String getVersion() {
        if (version == null) {
            return "1.0.1";
        } else {
            return version;
        }
    }

    /**
     * Sets the value of the version property.
     */
    public void setVersion(final String value) {
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
            for (SensorML.Member k : member) {
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
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof SensorML) {
            final SensorML that = (SensorML) object;
            return Objects.equals(this.capabilities,       that.capabilities)       &&
                   Objects.equals(this.characteristics,    that.characteristics)    &&
                   Objects.equals(this.classification,     that.classification)     &&
                   Objects.equals(this.contact,            that.contact)            &&
                   Objects.equals(this.documentation,      that.contact)            &&
                   Objects.equals(this.history,            that.history)            &&
                   Objects.equals(this.identification,     that.identification)     &&
                   Objects.equals(this.keywords,           that.keywords)           &&
                   Objects.equals(this.legalConstraint,    that.legalConstraint)    &&
                   Objects.equals(this.member,             that.member)             &&
                   Objects.equals(this.securityConstraint, that.securityConstraint) &&
                   Objects.equals(this.validTime,          that.validTime)          &&
                   Objects.equals(this.version,            that.version);
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

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice>
     *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}_Process"/>
     *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}DocumentList"/>
     *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}ContactList"/>
     *       &lt;/choice>
     *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "process",
        "documentList",
        "contactList"
    })
    public static class Member implements SMLMember {
        private static final QName PROCESS_NAME = new QName("http://www.opengis.net/sensorML/1.0.1", "System");

        @XmlElementRef(name = "AbstractProcess", namespace = "http://www.opengis.net/sensorML/1.0.1", type = JAXBElement.class)
        private JAXBElement<? extends AbstractProcessType> process;
        @XmlElement(name = "DocumentList")
        private DocumentList documentList;
        @XmlElement(name = "ContactList")
        private ContactList contactList;
        @XmlAttribute(namespace = "http://www.opengis.net/gml")
        @XmlSchemaType(name = "anyURI")
        private String remoteSchema;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String type;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String href;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String role;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String arcrole;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String title;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String show;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String actuate;

        public Member() {
        }

        public Member(final AbstractProcessType process) {
            ObjectFactory factory = new ObjectFactory();
            if (process instanceof SystemType) {
                this.process = factory.createSystem((SystemType) process);
            } else if (process instanceof ComponentType) {
                this.process = factory.createComponent((ComponentType) process);
            } else if (process instanceof DataSourceType) {
                this.process = factory.createDataSource((DataSourceType) process);
            } else if (process instanceof ProcessChainType) {
                this.process = factory.createProcessChain((ProcessChainType) process);
            } else if (process instanceof ProcessModelType) {
                this.process = factory.createProcessModel((ProcessModelType) process);
            } else if (process instanceof ComponentArrayType) {
                this.process = factory.createComponentArray((ComponentArrayType) process);
            } else {
                Logging.getLogger("SensorML").warning("Unexpected AbstractProcessType:" + process);
            }
        }
        public Member(final SystemType system) {
            ObjectFactory factory = new ObjectFactory();
            this.process = factory.createSystem(system);
        }

        public Member(final ComponentType compo) {
            ObjectFactory factory = new ObjectFactory();
            this.process = factory.createComponent(compo);
        }

        public Member(final SMLMember sm) {
            if (sm != null) {
                this.actuate      = sm.getActuate();
                this.arcrole      = sm.getArcrole();
                this.href         = sm.getHref();
                this.remoteSchema = sm.getRemoteSchema();
                this.role         = sm.getRole();
                this.show         = sm.getShow();
                this.title        = sm.getTitle();
                this.type         = sm.getType();

                ObjectFactory factory = new ObjectFactory();
                if (sm.getRealProcess() instanceof System) {
                    this.process = factory.createSystem(new SystemType((SystemType)sm.getRealProcess()));
                } else if (sm.getRealProcess() instanceof Component) {
                    this.process = factory.createComponent(new ComponentType((Component) sm.getRealProcess()));
                } else if (sm.getRealProcess() instanceof AbstractDataSource) {
                    this.process = factory.createDataSource(new DataSourceType((AbstractDataSource) sm.getRealProcess()));
                } else if (sm.getRealProcess() instanceof AbstractProcessChain) {
                    this.process = factory.createProcessChain(new ProcessChainType((AbstractProcessChain) sm.getRealProcess()));
                } else if (sm.getRealProcess() instanceof AbstractProcessModel) {
                    this.process = factory.createProcessModel(new ProcessModelType((AbstractProcessModel) sm.getRealProcess()));
                } else if (sm.getRealProcess() instanceof ComponentArray) {
                    this.process = factory.createComponentArray(new ComponentArrayType((ComponentArray) sm.getRealProcess()));
                }

                if (sm.getContactList() != null) {
                    this.contactList = new ContactList(sm.getContactList());
                }
                if (sm.getDocumentList() != null) {
                    this.documentList = new DocumentList(sm.getDocumentList());
                }
            }
        }

        /**
         * Gets the value of the process property.
         *
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link DataSourceType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ProcessModelType }{@code >}
         *     {@link JAXBElement }{@code <}{@link SystemType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractProcessType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ProcessChainType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ComponentArrayType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ComponentType }{@code >}
         *
         */
        public JAXBElement<? extends AbstractProcessType> getProcess() {
            return process;
        }

        @Override
        public AbstractProcess getRealProcess() {
            if (process != null) {
                return process.getValue();
            }
            return null;
        }

        /**
         * Sets the value of the process property.
         *
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link DataSourceType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ProcessModelType }{@code >}
         *     {@link JAXBElement }{@code <}{@link SystemType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractProcessType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ProcessChainType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ComponentArrayType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ComponentType }{@code >}
         *
         */
        public void setProcess(final JAXBElement<? extends AbstractProcessType> value) {
            this.process = value;
        }

        public void setRealProcess(AbstractProcess process) {
            this.process = (process != null) ? new JAXBElement<AbstractProcessType>(PROCESS_NAME,
                    AbstractProcessType.class, (AbstractProcessType) process) : null;
        }

        /**
         * Gets the value of the documentList property.
         *
         * @return
         *     possible object is
         *     {@link DocumentList }
         *
         */
        public DocumentList getDocumentList() {
            return documentList;
        }

        /**
         * Sets the value of the documentList property.
         *
         * @param value
         *     allowed object is
         *     {@link DocumentList }
         *
         */
        public void setDocumentList(final DocumentList value) {
            this.documentList = value;
        }

        /**
         * Gets the value of the contactList property.
         *
         * @return
         *     possible object is
         *     {@link ContactList }
         *
         */
        public ContactList getContactList() {
            return contactList;
        }

        /**
         * Sets the value of the contactList property.
         *
         * @param value
         *     allowed object is
         *     {@link ContactList }
         *
         */
        public void setContactList(final ContactList value) {
            this.contactList = value;
        }

        /**
         * Gets the value of the remoteSchema property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getRemoteSchema() {
            return remoteSchema;
        }

        /**
         * Sets the value of the remoteSchema property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setRemoteSchema(final String value) {
            this.remoteSchema = value;
        }

        /**
         * Gets the value of the type property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setType(final String value) {
            this.type = value;
        }

        /**
         * Gets the value of the href property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getHref() {
            return href;
        }

        /**
         * Sets the value of the href property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setHref(final String value) {
            this.href = value;
        }

        /**
         * Gets the value of the role property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getRole() {
            return role;
        }

        /**
         * Sets the value of the role property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setRole(final String value) {
            this.role = value;
        }

        /**
         * Gets the value of the arcrole property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getArcrole() {
            return arcrole;
        }

        /**
         * Sets the value of the arcrole property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setArcrole(final String value) {
            this.arcrole = value;
        }

        /**
         * Gets the value of the title property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getTitle() {
            return title;
        }

        /**
         * Sets the value of the title property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setTitle(final String value) {
            this.title = value;
        }

        /**
         * Gets the value of the show property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getShow() {
            return show;
        }

        /**
         * Sets the value of the show property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setShow(final String value) {
            this.show = value;
        }

        /**
         * Gets the value of the actuate property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getActuate() {
            return actuate;
        }

        /**
         * Sets the value of the actuate property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setActuate(final String value) {
            this.actuate = value;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[SensorML Member]").append("\n");
            if (process != null)
                sb.append("process: ").append(process.getValue()).append('\n');
            if (documentList != null)
                sb.append("documentList: ").append(documentList).append('\n');
            if (contactList != null)
                sb.append("contactList: ").append(contactList).append('\n');
            if (remoteSchema != null)
                sb.append("remoteSchema: ").append(remoteSchema).append('\n');
            if (actuate != null)
                sb.append("actuate: ").append(actuate).append('\n');
            if (arcrole != null)
                sb.append("actuate: ").append(arcrole).append('\n');
            if (href != null)
                sb.append("href: ").append(href).append('\n');
            if (role != null)
                sb.append("role: ").append(role).append('\n');
            if (show != null)
                sb.append("show: ").append(show).append('\n');
            if (title != null)
                sb.append("title: ").append(title).append('\n');
            if (type != null)
                sb.append("type: ").append(type).append('\n');
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

            if (object instanceof Member) {
                final Member that = (Member) object;
                boolean proc = false;
                if (this.process != null && that.process != null) {
                    proc = Objects.equals(this.process.getValue(), that.process.getValue());
                } else if (this.process == null && that.process == null) {
                    proc = true;
                }

                return Objects.equals(this.actuate,      that.actuate)       &&
                       Objects.equals(this.arcrole,      that.arcrole)       &&
                       Objects.equals(this.contactList,  that.contactList)   &&
                       Objects.equals(this.documentList, that.documentList)  &&
                       Objects.equals(this.href,         that.href)          &&
                       proc                                                    &&
                       Objects.equals(this.remoteSchema, that.remoteSchema)  &&
                       Objects.equals(this.role,         that.role)          &&
                       Objects.equals(this.show,         that.show)          &&
                       Objects.equals(this.title,        that.title)         &&
                       Objects.equals(this.type,         that.type);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            Object proc = null;
            if (process != null)
                proc  = process.getValue();
            hash = 71 * hash + (proc != null ? proc.hashCode() : 0);
            hash = 71 * hash + (this.documentList != null ? this.documentList.hashCode() : 0);
            hash = 71 * hash + (this.contactList != null ? this.contactList.hashCode() : 0);
            hash = 71 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
            hash = 71 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
            hash = 71 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
            hash = 71 * hash + (this.href != null ? this.href.hashCode() : 0);
            hash = 71 * hash + (this.role != null ? this.role.hashCode() : 0);
            hash = 71 * hash + (this.show != null ? this.show.hashCode() : 0);
            hash = 71 * hash + (this.title != null ? this.title.hashCode() : 0);
            hash = 71 * hash + (this.type != null ? this.type.hashCode() : 0);
            return hash;
        }
    }

}
