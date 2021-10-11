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
package org.geotoolkit.se.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for FeatureTypeStyleType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FeatureTypeStyleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}Name" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Description" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}FeatureTypeName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}SemanticTypeIdentifier" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{http://www.opengis.net/se}Rule"/>
 *           &lt;element ref="{http://www.opengis.net/se}OnlineResource"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.opengis.net/se}VersionType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureTypeStyleType", propOrder = {
    "name",
    "description",
    "featureTypeName",
    "semanticTypeIdentifier",
    "ruleOrOnlineResource"
})
public class FeatureTypeStyleType {

    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Description")
    protected DescriptionType description;
    @XmlElement(name = "FeatureTypeName")
    protected QName featureTypeName;
    @XmlElement(name = "SemanticTypeIdentifier")
    protected List<String> semanticTypeIdentifier;
    @XmlElements({
        @XmlElement(name = "Rule", type = RuleType.class),
        @XmlElement(name = "OnlineResource", type = OnlineResourceType.class)
    })
    protected List<Object> ruleOrOnlineResource;
    @XmlAttribute
    protected String version;

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return
     *     possible object is
     *     {@link DescriptionType }
     *
     */
    public DescriptionType getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value
     *     allowed object is
     *     {@link DescriptionType }
     *
     */
    public void setDescription(final DescriptionType value) {
        this.description = value;
    }

    /**
     * Gets the value of the featureTypeName property.
     *
     * @return
     *     possible object is
     *     {@link QName }
     *
     */
    public QName getFeatureTypeName() {
        return featureTypeName;
    }

    /**
     * Sets the value of the featureTypeName property.
     *
     * @param value
     *     allowed object is
     *     {@link QName }
     *
     */
    public void setFeatureTypeName(final QName value) {
        this.featureTypeName = value;
    }

    /**
     * Gets the value of the semanticTypeIdentifier property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the semanticTypeIdentifier property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSemanticTypeIdentifier().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getSemanticTypeIdentifier() {
        if (semanticTypeIdentifier == null) {
            semanticTypeIdentifier = new ArrayList<String>();
        }
        return this.semanticTypeIdentifier;
    }

    /**
     * Gets the value of the ruleOrOnlineResource property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleOrOnlineResource property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleOrOnlineResource().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RuleType }
     * {@link OnlineResourceType }
     *
     *
     */
    public List<Object> getRuleOrOnlineResource() {
        if (ruleOrOnlineResource == null) {
            ruleOrOnlineResource = new ArrayList<Object>();
        }
        return this.ruleOrOnlineResource;
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
    public void setVersion(final String value) {
        this.version = value;
    }

}
