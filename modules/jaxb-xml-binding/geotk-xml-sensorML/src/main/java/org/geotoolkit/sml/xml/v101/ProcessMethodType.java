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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v311.AbstractGMLEntry;
import org.geotoolkit.gml.xml.v311.StringOrRefType;
import org.geotoolkit.gml.xml.v311.TimeInstantType;
import org.geotoolkit.gml.xml.v311.TimePeriodType;
import org.geotoolkit.sml.xml.AbstractAlgorithm;
import org.geotoolkit.sml.xml.AbstractCapabilities;
import org.geotoolkit.sml.xml.AbstractCharacteristics;
import org.geotoolkit.sml.xml.AbstractClassification;
import org.geotoolkit.sml.xml.AbstractContact;
import org.geotoolkit.sml.xml.AbstractDocumentation;
import org.geotoolkit.sml.xml.AbstractHistory;
import org.geotoolkit.sml.xml.AbstractIdentification;
import org.geotoolkit.sml.xml.AbstractImplementation;
import org.geotoolkit.sml.xml.AbstractKeywords;
import org.geotoolkit.sml.xml.AbstractLegalConstraint;
import org.geotoolkit.sml.xml.AbstractProcessMethod;
import org.geotoolkit.sml.xml.AbstractRules;
import org.geotoolkit.sml.xml.AbstractValidTime;
import org.geotoolkit.swe.xml.v101.DataRecordType;


/**
 * Complex Type for process methods definition
 * 
 * <p>Java class for ProcessMethodType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessMethodType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGMLType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/sensorML/1.0.1}metadataGroup" minOccurs="0"/>
 *         &lt;element name="rules">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="RulesDefinition">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
 *                             &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}ruleLanguage" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="algorithm" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AlgorithmDefinition">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
 *                             &lt;element name="mathML" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence minOccurs="0">
 *                                       &lt;any/>
 *                                     &lt;/sequence>
 *                                     &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="implementation" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice minOccurs="0">
 *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}ProcessChain"/>
 *                   &lt;element name="ImplementationCode">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
 *                             &lt;group ref="{http://www.opengis.net/sensorML/1.0.1}metadataGroup" minOccurs="0"/>
 *                             &lt;element name="sourceRef" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="binaryRef" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="language" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *                           &lt;attribute name="framework" type="{http://www.w3.org/2001/XMLSchema}token" />
 *                           &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}token" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/choice>
 *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessMethodType", propOrder = {
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
    "rules",
    "algorithm",
    "implementation"
})
public class ProcessMethodType extends AbstractGMLEntry implements AbstractProcessMethod {

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
    private ProcessMethodType.Rules rules;
    private ProcessMethodType.Algorithm algorithm;
    private List<ProcessMethodType.Implementation> implementation;

    public ProcessMethodType() {

    }

    public ProcessMethodType(AbstractProcessMethod method) {
        super(method);
        if (method != null) {

            if (method.getAlgorithm() != null) {
                throw new IllegalArgumentException("Algorithm are not yet convertible");
            }
            if (method.getImplementation() != null) {
                throw new IllegalArgumentException("Implementation are not yet convertible");
            }
            if (method.getRules() != null) {
                throw new IllegalArgumentException("Rules are not yet convertible");
            }
            if (method.getCapabilities() != null) {
                this.capabilities = new ArrayList<org.geotoolkit.sml.xml.v101.Capabilities>();
                for (AbstractCapabilities oldCapa : method.getCapabilities()) {
                    this.capabilities.add(new org.geotoolkit.sml.xml.v101.Capabilities(oldCapa));
                }
            }
            if (method.getCharacteristics() != null) {
                this.characteristics = new ArrayList<org.geotoolkit.sml.xml.v101.Characteristics>();
                for (AbstractCharacteristics oldChar : method.getCharacteristics()) {
                    this.characteristics.add(new org.geotoolkit.sml.xml.v101.Characteristics(oldChar));
                }
            }

            if (method.getClassification() != null) {
            this.classification = new ArrayList<org.geotoolkit.sml.xml.v101.Classification>();
            for (AbstractClassification oldClass : method.getClassification()) {
                this.classification.add(new org.geotoolkit.sml.xml.v101.Classification(oldClass));
            }}

            if (method.getContact() != null) {
            this.contact = new ArrayList<org.geotoolkit.sml.xml.v101.Contact>();
            for (AbstractContact oldContact : method.getContact()) {
                this.contact.add(new org.geotoolkit.sml.xml.v101.Contact(oldContact));
            }}
            if (method.getDocumentation() != null) {
            this.documentation = new ArrayList<org.geotoolkit.sml.xml.v101.Documentation>();
            for (AbstractDocumentation oldDoc : method.getDocumentation()) {
                this.documentation.add(new org.geotoolkit.sml.xml.v101.Documentation(oldDoc));
            }}
            if (method.getHistory() != null) {
            this.history = new ArrayList<org.geotoolkit.sml.xml.v101.History>();
            for (AbstractHistory oldhist : method.getHistory()) {
                this.history.add(new org.geotoolkit.sml.xml.v101.History(oldhist));
            }}
            if (method.getIdentification() != null) {
            this.identification = new ArrayList<org.geotoolkit.sml.xml.v101.Identification>();
            for (AbstractIdentification oldIdent : method.getIdentification()) {
                this.identification.add(new org.geotoolkit.sml.xml.v101.Identification(oldIdent));
            }}
            if (method.getKeywords() != null) {
            this.keywords = new ArrayList<org.geotoolkit.sml.xml.v101.Keywords>();
            for (AbstractKeywords oldKeyw : method.getKeywords()) {
                this.keywords.add(new org.geotoolkit.sml.xml.v101.Keywords(oldKeyw));
            }}
            if (method.getLegalConstraint() != null) {
            this.legalConstraint = new ArrayList<org.geotoolkit.sml.xml.v101.LegalConstraint>();
            for (AbstractLegalConstraint oldcons : method.getLegalConstraint()) {
                this.legalConstraint.add(new org.geotoolkit.sml.xml.v101.LegalConstraint(oldcons));
            }}

            if (method.getLegalConstraint() != null) {
                this.securityConstraint = new org.geotoolkit.sml.xml.v101.SecurityConstraint(method.getSecurityConstraint());
            }
            if (method.getValidTime() != null) {
                this.validTime = new ValidTime(method.getValidTime());
            }
        }
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
     * Sets the value of the keywords property.
     *
     */
    public void setKeywords(List<Keywords> keywords) {
        this.keywords = keywords;
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setKeywords(Keywords keywords) {
        if (keywords != null) {
            if (this.keywords == null) {
                this.keywords = new ArrayList<Keywords>();
            }
            this.keywords.add(keywords);
        }
    }
    
    /**
     * Gets the value of the identification property.
     * 
     */
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
    public void setIdentification(List<Identification> identification) {
        this.identification = identification;
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setIdentification(Identification identification) {
        if (identification != null) {
            if (this.identification == null) {
                this.identification = new ArrayList<Identification>();
            }
            this.identification.add(identification);
        }
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setIdentification(IdentifierList identification) {
        if (identification != null) {
            if (this.identification == null) {
                this.identification = new ArrayList<Identification>();
            }
            this.identification.add(new Identification(identification));
        }
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
     * Sets the value of the keywords property.
     *
     */
    public void setClassification(List<Classification> classification) {
       this.classification = classification;
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setClassification(Classification classification) {
        if (classification != null) {
            if (this.classification == null) {
                this.classification = new ArrayList<Classification>();
            }
            this.classification.add(classification);
        }
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setClassification(ClassifierList classification) {
        if (classification != null) {
            if (this.classification == null) {
                this.classification = new ArrayList<Classification>();
            }
            this.classification.add(new Classification(classification));
        }
    }
    
    /**
     * Gets the value of the validTime property.
     * 
     * @return
     *     possible object is
     *     {@link ValidTime }
     *     
     */
    public ValidTime getValidTime() {
        return validTime;
    }

     /**
     * Sets the value of the validTime property.
     */
    public void setValidTime(AbstractValidTime value) {
        this.validTime = new ValidTime(value);
    }

     /**
     * Sets the value of the validTime property.
     */
    public void setValidTime(TimePeriodType value) {
        this.validTime = new ValidTime(value);
    }

     /**
     * Sets the value of the validTime property.
     */
    public void setValidTime(TimeInstantType value) {
        this.validTime = new ValidTime(value);
    }

    /**
     * Gets the value of the securityConstraint property.
     * 
     * @return
     *     possible object is
     *     {@link SecurityConstraint }
     *     
     */
    public SecurityConstraint getSecurityConstraint() {
        return securityConstraint;
    }

    /**
     * Sets the value of the securityConstraint property.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityConstraint }
     *     
     */
    public void setSecurityConstraint(SecurityConstraint value) {
        this.securityConstraint = value;
    }

    /**
     * Gets the value of the legalConstraint property.
     */
    public List<LegalConstraint> getLegalConstraint() {
        if (legalConstraint == null) {
            legalConstraint = new ArrayList<LegalConstraint>();
        }
        return this.legalConstraint;
    }

    /**
     * Gets the value of the legalConstraint property.
     *
     */
    public void setLegalConstraint(LegalConstraint legalConstraint) {
        if (legalConstraint != null) {
            if (this.legalConstraint == null) {
                this.legalConstraint = new ArrayList<LegalConstraint>();
            }
            this.legalConstraint.add(legalConstraint);
        }
    }

    /**
     * Gets the value of the legalConstraint property.
     *
     */
    public void setLegalConstraint(Rights legalConstraint) {
        if (legalConstraint != null) {
            if (this.legalConstraint == null) {
                this.legalConstraint = new ArrayList<LegalConstraint>();
            }
            this.legalConstraint.add(new LegalConstraint(legalConstraint));
        }
    }

    /**
     * Gets the value of the legalConstraint property.
     *
     */
    public void setLegalConstraint(List<LegalConstraint> legalConstraint) {
        this.legalConstraint = legalConstraint;
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
     * Sets the value of the characteristics property.
     *
     */
    public void setCharacteristics(List<Characteristics> characteristics) {
        this.characteristics = characteristics;
    }

    /**
     * Sets the value of the characteristics property.
     *
     */
    public void setCharacteristics(Characteristics characteristics) {
        if (characteristics != null) {
            if (this.characteristics == null) {
                this.characteristics = new ArrayList<Characteristics>();
            }
            this.characteristics.add(characteristics);
        }
    }

    /**
     * Sets the value of the characteristics property.
     *
     */
    public void setCharacteristics(DataRecordType characteristics) {
        if (characteristics != null) {
            if (this.characteristics == null) {
                this.characteristics = new ArrayList<Characteristics>();
            }
            this.characteristics.add(new Characteristics(characteristics));
        }
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
     * Sets the value of the capabilities property.
     *
     */
    public void setCapabilities(Capabilities capabilties) {
        if (capabilties != null) {
            if (this.capabilities == null) {
                this.capabilities = new ArrayList<Capabilities>();
            }
            this.capabilities.add(capabilties);
        }
    }

    /**
     * Sets the value of the capabilities property.
     *
     */
    public void setCapabilities(DataRecordType capabilties) {
        if (capabilties != null) {
            if (this.capabilities == null) {
                this.capabilities = new ArrayList<Capabilities>();
            }
            this.capabilities.add(new Capabilities(capabilties));
        }
    }

    /**
     * Sets the value of the capabilities property.
     *
     */
    public void setCapabilities(List<Capabilities> capabilities) {
        this.capabilities = capabilities;
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
     * Sets the value of the contact property.
     *
     */
    public void setContact(Contact contact) {
        if (contact != null) {
            if (this.contact == null) {
                this.contact = new ArrayList<Contact>();
            }
            this.contact.add(contact);
        }
    }

    /**
     * Sets the value of the contact property.
     *
     */
    public void setContact(ResponsibleParty contact) {
        if (contact != null) {
            if (this.contact == null) {
                this.contact = new ArrayList<Contact>();
            }
            this.contact.add(new Contact(contact));
        }
    }

    /**
     * sets the value of the contact property.
     *
     */
    public void setContact(List<Contact> contact) {
        this.contact = contact;
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
     * Sets the value of the contact property.
     *
     */
    public void setDocumentation(Documentation documentation) {
        if (documentation != null) {
            if (this.documentation == null) {
                this.documentation = new ArrayList<Documentation>();
            }
            this.documentation.add(documentation);
        }
    }

    /**
     * Sets the value of the contact property.
     *
     */
    public void setDocumentation(Document documentation) {
        if (documentation != null) {
            if (this.documentation == null) {
                this.documentation = new ArrayList<Documentation>();
            }
            this.documentation.add(new Documentation(documentation));
        }
    }

    /**
     * sets the value of the contact property.
     *
     */
    public void setDocumentation(List<Documentation> documentation) {
        this.documentation = documentation;
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
     * Sets the value of the history property.
     */
    public void setHistory(List<History> history) {
        this.history = history;
    }

    /**
     * Sets the value of the history property.
     */
    public void setHistory(History history) {
        if (history != null) {
            if (this.history == null) {
                this.history = new ArrayList<History>();
            }
            this.history.add(history);
        }
    }
    
    /**
     * Gets the value of the rules property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessMethodType.Rules }
     *     
     */
    public ProcessMethodType.Rules getRules() {
        return rules;
    }

    /**
     * Sets the value of the rules property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessMethodType.Rules }
     *     
     */
    public void setRules(ProcessMethodType.Rules value) {
        this.rules = value;
    }

    /**
     * Gets the value of the algorithm property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessMethodType.Algorithm }
     *     
     */
    public ProcessMethodType.Algorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the value of the algorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessMethodType.Algorithm }
     *     
     */
    public void setAlgorithm(ProcessMethodType.Algorithm value) {
        this.algorithm = value;
    }

    /**
     * Gets the value of the implementation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the implementation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImplementation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcessMethodType.Implementation }
     * 
     * 
     */
    public List<ProcessMethodType.Implementation> getImplementation() {
        if (implementation == null) {
            implementation = new ArrayList<ProcessMethodType.Implementation>();
        }
        return this.implementation;
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
     *       &lt;sequence>
     *         &lt;element name="AlgorithmDefinition">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
     *                   &lt;element name="mathML" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence minOccurs="0">
     *                             &lt;any/>
     *                           &lt;/sequence>
     *                           &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "algorithmDefinition"
    })
    public static class Algorithm implements AbstractAlgorithm {

        @XmlElement(name = "AlgorithmDefinition", required = true)
        private ProcessMethodType.Algorithm.AlgorithmDefinition algorithmDefinition;

        /**
         * Gets the value of the algorithmDefinition property.
         * 
         * @return
         *     possible object is
         *     {@link ProcessMethodType.Algorithm.AlgorithmDefinition }
         *     
         */
        public ProcessMethodType.Algorithm.AlgorithmDefinition getAlgorithmDefinition() {
            return algorithmDefinition;
        }

        /**
         * Sets the value of the algorithmDefinition property.
         * 
         * @param value
         *     allowed object is
         *     {@link ProcessMethodType.Algorithm.AlgorithmDefinition }
         *     
         */
        public void setAlgorithmDefinition(ProcessMethodType.Algorithm.AlgorithmDefinition value) {
            this.algorithmDefinition = value;
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
         *       &lt;sequence>
         *         &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
         *         &lt;element name="mathML" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence minOccurs="0">
         *                   &lt;any/>
         *                 &lt;/sequence>
         *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "description",
            "mathML"
        })
        public static class AlgorithmDefinition {

            @XmlElement(namespace = "http://www.opengis.net/gml")
            private StringOrRefType description;
            private ProcessMethodType.Algorithm.AlgorithmDefinition.MathML mathML;

            /**
             * Textual description of the algorithm
             * 
             * @return
             *     possible object is
             *     {@link StringOrRefType }
             *     
             */
            public StringOrRefType getDescription() {
                return description;
            }

            /**
             * Sets the value of the description property.
             * 
             * @param value
             *     allowed object is
             *     {@link StringOrRefType }
             *     
             */
            public void setDescription(StringOrRefType value) {
                this.description = value;
            }

            /**
             * Gets the value of the mathML property.
             * 
             * @return
             *     possible object is
             *     {@link ProcessMethodType.Algorithm.AlgorithmDefinition.MathML }
             *     
             */
            public ProcessMethodType.Algorithm.AlgorithmDefinition.MathML getMathML() {
                return mathML;
            }

            /**
             * Sets the value of the mathML property.
             * 
             * @param value
             *     allowed object is
             *     {@link ProcessMethodType.Algorithm.AlgorithmDefinition.MathML }
             *     
             */
            public void setMathML(ProcessMethodType.Algorithm.AlgorithmDefinition.MathML value) {
                this.mathML = value;
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
             *       &lt;sequence minOccurs="0">
             *         &lt;any/>
             *       &lt;/sequence>
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
                "any"
            })
            public static class MathML {

                @XmlAnyElement(lax = true)
                private Object any;
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

                /**
                 * Gets the value of the any property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Element }
                 *     {@link Object }
                 *     
                 */
                public Object getAny() {
                    return any;
                }

                /**
                 * Sets the value of the any property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Element }
                 *     {@link Object }
                 *     
                 */
                public void setAny(Object value) {
                    this.any = value;
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
                public void setRemoteSchema(String value) {
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
                public void setType(String value) {
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
                public void setHref(String value) {
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
                public void setRole(String value) {
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
                public void setArcrole(String value) {
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
                public void setTitle(String value) {
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
                public void setShow(String value) {
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
                public void setActuate(String value) {
                    this.actuate = value;
                }

            }

        }

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
     *       &lt;choice minOccurs="0">
     *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}ProcessChain"/>
     *         &lt;element name="ImplementationCode">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
     *                   &lt;group ref="{http://www.opengis.net/sensorML/1.0.1}metadataGroup" minOccurs="0"/>
     *                   &lt;element name="sourceRef" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="binaryRef" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="language" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
     *                 &lt;attribute name="framework" type="{http://www.w3.org/2001/XMLSchema}token" />
     *                 &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}token" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "processChain",
        "implementationCode"
    })
    public static class Implementation implements AbstractImplementation {

        @XmlElement(name = "ProcessChain")
        private ProcessChainType processChain;
        @XmlElement(name = "ImplementationCode")
        private ProcessMethodType.Implementation.ImplementationCode implementationCode;
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

        /**
         * Gets the value of the processChain property.
         * 
         * @return
         *     possible object is
         *     {@link ProcessChainType }
         *     
         */
        public ProcessChainType getProcessChain() {
            return processChain;
        }

        /**
         * Sets the value of the processChain property.
         * 
         * @param value
         *     allowed object is
         *     {@link ProcessChainType }
         *     
         */
        public void setProcessChain(ProcessChainType value) {
            this.processChain = value;
        }

        /**
         * Gets the value of the implementationCode property.
         * 
         * @return
         *     possible object is
         *     {@link ProcessMethodType.Implementation.ImplementationCode }
         *     
         */
        public ProcessMethodType.Implementation.ImplementationCode getImplementationCode() {
            return implementationCode;
        }

        /**
         * Sets the value of the implementationCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link ProcessMethodType.Implementation.ImplementationCode }
         *     
         */
        public void setImplementationCode(ProcessMethodType.Implementation.ImplementationCode value) {
            this.implementationCode = value;
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
        public void setRemoteSchema(String value) {
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
        public void setType(String value) {
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
        public void setHref(String value) {
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
        public void setRole(String value) {
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
        public void setArcrole(String value) {
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
        public void setTitle(String value) {
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
        public void setShow(String value) {
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
        public void setActuate(String value) {
            this.actuate = value;
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
         *       &lt;sequence>
         *         &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
         *         &lt;group ref="{http://www.opengis.net/sensorML/1.0.1}metadataGroup" minOccurs="0"/>
         *         &lt;element name="sourceRef" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="binaryRef" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="language" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
         *       &lt;attribute name="framework" type="{http://www.w3.org/2001/XMLSchema}token" />
         *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}token" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "description",
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
            "sourceRef",
            "binaryRef"
        })
        public static class ImplementationCode {

            @XmlElement(namespace = "http://www.opengis.net/gml")
            private StringOrRefType description;
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
            private ProcessMethodType.Implementation.ImplementationCode.SourceRef sourceRef;
            private ProcessMethodType.Implementation.ImplementationCode.BinaryRef binaryRef;
            @XmlAttribute(required = true)
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlSchemaType(name = "token")
            private String language;
            @XmlAttribute
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlSchemaType(name = "token")
            private String framework;
            @XmlAttribute
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlSchemaType(name = "token")
            private String version;

            /**
             * Textual description of the algorithm
             * 
             * @return
             *     possible object is
             *     {@link StringOrRefType }
             *     
             */
            public StringOrRefType getDescription() {
                return description;
            }

            /**
             * Sets the value of the description property.
             * 
             * @param value
             *     allowed object is
             *     {@link StringOrRefType }
             *     
             */
            public void setDescription(StringOrRefType value) {
                this.description = value;
            }

            /**
             * Gets the value of the keywords property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the keywords property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getKeywords().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Keywords }
             * 
             * 
             */
            public List<Keywords> getKeywords() {
                if (keywords == null) {
                    keywords = new ArrayList<Keywords>();
                }
                return this.keywords;
            }

            /**
             * Gets the value of the identification property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the identification property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getIdentification().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Identification }
             * 
             * 
             */
            public List<Identification> getIdentification() {
                if (identification == null) {
                    identification = new ArrayList<Identification>();
                }
                return this.identification;
            }

            /**
             * Gets the value of the classification property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the classification property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getClassification().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Classification }
             * 
             * 
             */
            public List<Classification> getClassification() {
                if (classification == null) {
                    classification = new ArrayList<Classification>();
                }
                return this.classification;
            }

            /**
             * Gets the value of the validTime property.
             * 
             * @return
             *     possible object is
             *     {@link ValidTime }
             *     
             */
            public ValidTime getValidTime() {
                return validTime;
            }

            /**
             * Sets the value of the validTime property.
             * 
             * @param value
             *     allowed object is
             *     {@link ValidTime }
             *     
             */
            public void setValidTime(ValidTime value) {
                this.validTime = value;
            }

            /**
             * Gets the value of the securityConstraint property.
             * 
             * @return
             *     possible object is
             *     {@link SecurityConstraint }
             *     
             */
            public SecurityConstraint getSecurityConstraint() {
                return securityConstraint;
            }

            /**
             * Sets the value of the securityConstraint property.
             * 
             * @param value
             *     allowed object is
             *     {@link SecurityConstraint }
             *     
             */
            public void setSecurityConstraint(SecurityConstraint value) {
                this.securityConstraint = value;
            }

            /**
             * Gets the value of the legalConstraint property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the legalConstraint property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getLegalConstraint().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link LegalConstraint }
             * 
             * 
             */
            public List<LegalConstraint> getLegalConstraint() {
                if (legalConstraint == null) {
                    legalConstraint = new ArrayList<LegalConstraint>();
                }
                return this.legalConstraint;
            }

            /**
             * Gets the value of the characteristics property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the characteristics property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getCharacteristics().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Characteristics }
             * 
             * 
             */
            public List<Characteristics> getCharacteristics() {
                if (characteristics == null) {
                    characteristics = new ArrayList<Characteristics>();
                }
                return this.characteristics;
            }

            /**
             * Gets the value of the capabilities property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the capabilities property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getCapabilities().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Capabilities }
             * 
             * 
             */
            public List<Capabilities> getCapabilities() {
                if (capabilities == null) {
                    capabilities = new ArrayList<Capabilities>();
                }
                return this.capabilities;
            }

            /**
             * Gets the value of the contact property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the contact property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getContact().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Contact }
             * 
             * 
             */
            public List<Contact> getContact() {
                if (contact == null) {
                    contact = new ArrayList<Contact>();
                }
                return this.contact;
            }

            /**
             * Gets the value of the documentation property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the documentation property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getDocumentation().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Documentation }
             * 
             * 
             */
            public List<Documentation> getDocumentation() {
                if (documentation == null) {
                    documentation = new ArrayList<Documentation>();
                }
                return this.documentation;
            }

            /**
             * Gets the value of the history property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the history property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getHistory().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link History }
             * 
             * 
             */
            public List<History> getHistory() {
                if (history == null) {
                    history = new ArrayList<History>();
                }
                return this.history;
            }

            /**
             * Gets the value of the sourceRef property.
             * 
             * @return
             *     possible object is
             *     {@link ProcessMethodType.Implementation.ImplementationCode.SourceRef }
             *     
             */
            public ProcessMethodType.Implementation.ImplementationCode.SourceRef getSourceRef() {
                return sourceRef;
            }

            /**
             * Sets the value of the sourceRef property.
             * 
             * @param value
             *     allowed object is
             *     {@link ProcessMethodType.Implementation.ImplementationCode.SourceRef }
             *     
             */
            public void setSourceRef(ProcessMethodType.Implementation.ImplementationCode.SourceRef value) {
                this.sourceRef = value;
            }

            /**
             * Gets the value of the binaryRef property.
             * 
             * @return
             *     possible object is
             *     {@link ProcessMethodType.Implementation.ImplementationCode.BinaryRef }
             *     
             */
            public ProcessMethodType.Implementation.ImplementationCode.BinaryRef getBinaryRef() {
                return binaryRef;
            }

            /**
             * Sets the value of the binaryRef property.
             * 
             * @param value
             *     allowed object is
             *     {@link ProcessMethodType.Implementation.ImplementationCode.BinaryRef }
             *     
             */
            public void setBinaryRef(ProcessMethodType.Implementation.ImplementationCode.BinaryRef value) {
                this.binaryRef = value;
            }

            /**
             * Gets the value of the language property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getLanguage() {
                return language;
            }

            /**
             * Sets the value of the language property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setLanguage(String value) {
                this.language = value;
            }

            /**
             * Gets the value of the framework property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFramework() {
                return framework;
            }

            /**
             * Sets the value of the framework property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFramework(String value) {
                this.framework = value;
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
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setVersion(String value) {
                this.version = value;
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
             *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class BinaryRef {

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
                public void setRemoteSchema(String value) {
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
                public void setType(String value) {
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
                public void setHref(String value) {
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
                public void setRole(String value) {
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
                public void setArcrole(String value) {
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
                public void setTitle(String value) {
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
                public void setShow(String value) {
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
                public void setActuate(String value) {
                    this.actuate = value;
                }

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
             *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class SourceRef {

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
                public void setRemoteSchema(String value) {
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
                public void setType(String value) {
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
                public void setHref(String value) {
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
                public void setRole(String value) {
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
                public void setArcrole(String value) {
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
                public void setTitle(String value) {
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
                public void setShow(String value) {
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
                public void setActuate(String value) {
                    this.actuate = value;
                }

            }

        }

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
     *       &lt;sequence>
     *         &lt;element name="RulesDefinition">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
     *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}ruleLanguage" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "rulesDefinition"
    })
    public static class Rules implements AbstractRules {

        @XmlElement(name = "RulesDefinition", required = true)
        private ProcessMethodType.Rules.RulesDefinition rulesDefinition;

        /**
         * Gets the value of the rulesDefinition property.
         * 
         * @return
         *     possible object is
         *     {@link ProcessMethodType.Rules.RulesDefinition }
         *     
         */
        public ProcessMethodType.Rules.RulesDefinition getRulesDefinition() {
            return rulesDefinition;
        }

        /**
         * Sets the value of the rulesDefinition property.
         * 
         * @param value
         *     allowed object is
         *     {@link ProcessMethodType.Rules.RulesDefinition }
         *     
         */
        public void setRulesDefinition(ProcessMethodType.Rules.RulesDefinition value) {
            this.rulesDefinition = value;
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
         *       &lt;sequence>
         *         &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
         *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}ruleLanguage" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "description",
            "ruleLanguage"
        })
        public static class RulesDefinition {

            @XmlElement(namespace = "http://www.opengis.net/gml")
            private StringOrRefType description;
            @XmlElementRef(name = "ruleLanguage", namespace = "http://www.opengis.net/sensorML/1.0.1", type = JAXBElement.class)
            private JAXBElement<? extends RuleLanguageType> ruleLanguage;

            /**
             * Textual description of the i/o structure
             * 
             * @return
             *     possible object is
             *     {@link StringOrRefType }
             *     
             */
            public StringOrRefType getDescription() {
                return description;
            }

            /**
             * Sets the value of the description property.
             * 
             * @param value
             *     allowed object is
             *     {@link StringOrRefType }
             *     
             */
            public void setDescription(StringOrRefType value) {
                this.description = value;
            }

            /**
             * Gets the value of the ruleLanguage property.
             * 
             * @return
             *     possible object is
             *     {@link JAXBElement }{@code <}{@link Schematron }{@code >}
             *     {@link JAXBElement }{@code <}{@link RelaxNG }{@code >}
             *     {@link JAXBElement }{@code <}{@link RuleLanguageType }{@code >}
             *     
             */
            public JAXBElement<? extends RuleLanguageType> getRuleLanguage() {
                return ruleLanguage;
            }

            /**
             * Sets the value of the ruleLanguage property.
             * 
             * @param value
             *     allowed object is
             *     {@link JAXBElement }{@code <}{@link Schematron }{@code >}
             *     {@link JAXBElement }{@code <}{@link RelaxNG }{@code >}
             *     {@link JAXBElement }{@code <}{@link RuleLanguageType }{@code >}
             *     
             */
            public void setRuleLanguage(JAXBElement<? extends RuleLanguageType> value) {
                this.ruleLanguage = ((JAXBElement<? extends RuleLanguageType> ) value);
            }

        }

    }

}
