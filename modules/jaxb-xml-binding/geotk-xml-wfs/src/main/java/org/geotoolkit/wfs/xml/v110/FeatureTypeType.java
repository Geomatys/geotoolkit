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
package org.geotoolkit.wfs.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.ows.xml.v100.KeywordsType;
import org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType;


/**
 * 
 *             An element of this type that describes a feature in an application
 *             namespace shall have an xml xmlns specifier, e.g.
 *             xmlns:bo="http://www.BlueOx.org/BlueOx"
 *          
 * 
 * <p>Java class for FeatureTypeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FeatureTypeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Abstract" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Keywords" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="DefaultSRS" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *             &lt;element name="OtherSRS" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element name="NoSRS">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
 *         &lt;element name="Operations" type="{http://www.opengis.net/wfs}OperationsType" minOccurs="0"/>
 *         &lt;element name="OutputFormats" type="{http://www.opengis.net/wfs}OutputFormatListType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}WGS84BoundingBox" maxOccurs="unbounded"/>
 *         &lt;element name="MetadataURL" type="{http://www.opengis.net/wfs}MetadataURLType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureTypeType", propOrder = {
    "name",
    "title",
    "_abstract",
    "keywords",
    "defaultSRS",
    "otherSRS",
    "noSRS",
    "operations",
    "outputFormats",
    "wgs84BoundingBox",
    "metadataURL"
})
public class FeatureTypeType {

    @XmlElement(name = "Name", required = true)
    private QName name;
    @XmlElement(name = "Title", required = true)
    private String title;
    @XmlElement(name = "Abstract")
    private String _abstract;
    @XmlElement(name = "Keywords", namespace = "http://www.opengis.net/ows")
    private List<KeywordsType> keywords;
    @XmlElement(name = "DefaultSRS")
    @XmlSchemaType(name = "anyURI")
    private String defaultSRS;
    @XmlElement(name = "OtherSRS")
    @XmlSchemaType(name = "anyURI")
    private List<String> otherSRS;
    @XmlElement(name = "NoSRS")
    private FeatureTypeType.NoSRS noSRS;
    @XmlElement(name = "Operations")
    private OperationsType operations;
    @XmlElement(name = "OutputFormats")
    private OutputFormatListType outputFormats;
    @XmlElement(name = "WGS84BoundingBox", namespace = "http://www.opengis.net/ows", required = true)
    private List<WGS84BoundingBoxType> wgs84BoundingBox;
    @XmlElement(name = "MetadataURL")
    private List<MetadataURLType> metadataURL;

    public FeatureTypeType() {

    }

    public FeatureTypeType(QName name, String title, String defaultSRS, List<String> otherCRS, List<WGS84BoundingBoxType> wgs84BoundingBox) {
        this.name       = name;
        this.title      = title;
        this.defaultSRS = defaultSRS;
        this.otherSRS   = otherCRS;
        this.wgs84BoundingBox = wgs84BoundingBox;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setName(QName value) {
        this.name = value;
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
     * Gets the value of the abstract property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Sets the value of the abstract property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbstract(String value) {
        this._abstract = value;
    }

    /**
     * Gets the value of the keywords property.
     */
    public List<KeywordsType> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<KeywordsType>();
        }
        return this.keywords;
    }

    /**
     * Gets the value of the defaultSRS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultSRS() {
        return defaultSRS;
    }

    /**
     * Sets the value of the defaultSRS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultSRS(String value) {
        this.defaultSRS = value;
    }

    /**
     * Gets the value of the otherSRS property.
     */
    public List<String> getOtherSRS() {
        if (otherSRS == null) {
            otherSRS = new ArrayList<String>();
        }
        return this.otherSRS;
    }

    /**
     * Gets the value of the noSRS property.
     * 
     * @return
     *     possible object is
     *     {@link FeatureTypeType.NoSRS }
     *     
     */
    public FeatureTypeType.NoSRS getNoSRS() {
        return noSRS;
    }

    /**
     * Sets the value of the noSRS property.
     * 
     * @param value
     *     allowed object is
     *     {@link FeatureTypeType.NoSRS }
     *     
     */
    public void setNoSRS(FeatureTypeType.NoSRS value) {
        this.noSRS = value;
    }

    /**
     * Gets the value of the operations property.
     * 
     * @return
     *     possible object is
     *     {@link OperationsType }
     *     
     */
    public OperationsType getOperations() {
        return operations;
    }

    /**
     * Sets the value of the operations property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperationsType }
     *     
     */
    public void setOperations(OperationsType value) {
        this.operations = value;
    }

    /**
     * Gets the value of the outputFormats property.
     * 
     * @return
     *     possible object is
     *     {@link OutputFormatListType }
     *     
     */
    public OutputFormatListType getOutputFormats() {
        return outputFormats;
    }

    /**
     * Sets the value of the outputFormats property.
     * 
     * @param value
     *     allowed object is
     *     {@link OutputFormatListType }
     *     
     */
    public void setOutputFormats(OutputFormatListType value) {
        this.outputFormats = value;
    }

    /**
     * Gets the value of the wgs84BoundingBox property.
     */
    public List<WGS84BoundingBoxType> getWGS84BoundingBox() {
        if (wgs84BoundingBox == null) {
            wgs84BoundingBox = new ArrayList<WGS84BoundingBoxType>();
        }
        return this.wgs84BoundingBox;
    }

    /**
     * Gets the value of the metadataURL property.
     */
    public List<MetadataURLType> getMetadataURL() {
        if (metadataURL == null) {
            metadataURL = new ArrayList<MetadataURLType>();
        }
        return this.metadataURL;
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
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class NoSRS {


    }

}
