/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */


package org.geotoolkit.sml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
//import us.gov.ic.ism.v2.ClassificationType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{urn:us:gov:ic:ism:v2}SecurityAttributesOptionGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Security")
public class Security {

    @XmlAttribute(name = "FGIsourceOpen", namespace = "urn:us:gov:ic:ism:v2")
    private List<String> fgIsourceOpen;
    @XmlAttribute(name = "FGIsourceprivate", namespace = "urn:us:gov:ic:ism:v2")
    private List<String> fgIsourceprivate;
    @XmlAttribute(name = "SARIdentifier", namespace = "urn:us:gov:ic:ism:v2")
    private List<String> sarIdentifier;
    @XmlAttribute(name = "SCIcontrols", namespace = "urn:us:gov:ic:ism:v2")
    private List<String> scIcontrols;
    /*@XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private ClassificationType classification;*/
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private String classificationReason;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private String classifiedBy;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private XMLGregorianCalendar dateOfExemptedSource;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private XMLGregorianCalendar declassDate;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private String declassEvent;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private List<String> declassException;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private Boolean declassManualReview;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private String derivedFrom;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private List<String> disseminationControls;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private List<String> nonICmarkings;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private List<String> ownerProducer;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private List<String> releasableTo;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    private List<String> typeOfExemptedSource;

    /**
     * Gets the value of the fgIsourceOpen property.
     */
    public List<String> getFGIsourceOpen() {
        if (fgIsourceOpen == null) {
            fgIsourceOpen = new ArrayList<String>();
        }
        return this.fgIsourceOpen;
    }

    /**
     * Gets the value of the fgIsourceprivate property.
     */
    public List<String> getFGIsourceprivate() {
        if (fgIsourceprivate == null) {
            fgIsourceprivate = new ArrayList<String>();
        }
        return this.fgIsourceprivate;
    }

    /**
     * Gets the value of the sarIdentifier property.
     */
    public List<String> getSARIdentifier() {
        if (sarIdentifier == null) {
            sarIdentifier = new ArrayList<String>();
        }
        return this.sarIdentifier;
    }

    /**
     * Gets the value of the scIcontrols property.
     */
    public List<String> getSCIcontrols() {
        if (scIcontrols == null) {
            scIcontrols = new ArrayList<String>();
        }
        return this.scIcontrols;
    }

    /**
     * Gets the value of the classification property.
     * 
     * @return
     *     possible object is
     *     {@link ClassificationType }
     *     
     
    public ClassificationType getClassification() {
        return classification;
    }

    /**
     * Sets the value of the classification property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassificationType }
     *     
     
    public void setClassification(ClassificationType value) {
        this.classification = value;
    }*/

    /**
     * Gets the value of the classificationReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassificationReason() {
        return classificationReason;
    }

    /**
     * Sets the value of the classificationReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassificationReason(String value) {
        this.classificationReason = value;
    }

    /**
     * Gets the value of the classifiedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassifiedBy() {
        return classifiedBy;
    }

    /**
     * Sets the value of the classifiedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassifiedBy(String value) {
        this.classifiedBy = value;
    }

    /**
     * Gets the value of the dateOfExemptedSource property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfExemptedSource() {
        return dateOfExemptedSource;
    }

    /**
     * Sets the value of the dateOfExemptedSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfExemptedSource(XMLGregorianCalendar value) {
        this.dateOfExemptedSource = value;
    }

    /**
     * Gets the value of the declassDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeclassDate() {
        return declassDate;
    }

    /**
     * Sets the value of the declassDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeclassDate(XMLGregorianCalendar value) {
        this.declassDate = value;
    }

    /**
     * Gets the value of the declassEvent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeclassEvent() {
        return declassEvent;
    }

    /**
     * Sets the value of the declassEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeclassEvent(String value) {
        this.declassEvent = value;
    }

    /**
     * Gets the value of the declassException property.
     */
    public List<String> getDeclassException() {
        if (declassException == null) {
            declassException = new ArrayList<String>();
        }
        return this.declassException;
    }

    /**
     * Gets the value of the declassManualReview property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeclassManualReview() {
        return declassManualReview;
    }

    /**
     * Sets the value of the declassManualReview property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeclassManualReview(Boolean value) {
        this.declassManualReview = value;
    }

    /**
     * Gets the value of the derivedFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDerivedFrom() {
        return derivedFrom;
    }

    /**
     * Sets the value of the derivedFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDerivedFrom(String value) {
        this.derivedFrom = value;
    }

    /**
     * Gets the value of the disseminationControls property.
     */
    public List<String> getDisseminationControls() {
        if (disseminationControls == null) {
            disseminationControls = new ArrayList<String>();
        }
        return this.disseminationControls;
    }

    /**
     * Gets the value of the nonICmarkings property.
     */
    public List<String> getNonICmarkings() {
        if (nonICmarkings == null) {
            nonICmarkings = new ArrayList<String>();
        }
        return this.nonICmarkings;
    }

    /**
     * Gets the value of the ownerProducer property.
     */
    public List<String> getOwnerProducer() {
        if (ownerProducer == null) {
            ownerProducer = new ArrayList<String>();
        }
        return this.ownerProducer;
    }

    /**
     * Gets the value of the releasableTo property.
     */
    public List<String> getReleasableTo() {
        if (releasableTo == null) {
            releasableTo = new ArrayList<String>();
        }
        return this.releasableTo;
    }

    /**
     * Gets the value of the typeOfExemptedSource property.
     */
    public List<String> getTypeOfExemptedSource() {
        if (typeOfExemptedSource == null) {
            typeOfExemptedSource = new ArrayList<String>();
        }
        return this.typeOfExemptedSource;
    }

}
