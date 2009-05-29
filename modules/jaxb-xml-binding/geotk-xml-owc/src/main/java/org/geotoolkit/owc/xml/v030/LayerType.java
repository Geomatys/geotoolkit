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
package org.geotoolkit.owc.xml.v030;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List; 
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v212.AbstractFeatureCollectionType;

import org.geotoolkit.kml.xml.v220.DocumentType;
import org.geotoolkit.ogc.xml.v100.FilterType;
 

/**
 * <p>Java class for LayerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LayerType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows-context}AbstractResourceType">
 *       &lt;sequence>
 *         &lt;element name="DimensionList" type="{http://www.opengis.net/ows-context}DimensionListType" minOccurs="0"/>
 *         &lt;element name="ResponseCRS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParameterList" type="{http://www.opengis.net/ows-context}ParameterListType" minOccurs="0"/>
 *         &lt;element name="Depth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Resx" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Resy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Resz" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MaxFeatures" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Filter" minOccurs="0"/>
 *         &lt;element name="InlineGeometry" type="{http://www.opengis.net/gml}AbstractFeatureCollectionType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Document" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Extension" type="{http://www.opengis.net/ows-context}ExtensionType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="queryable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LayerType", propOrder = {
    "dimensionList",
    "responseCRS",
    "parameterList",
    "depth",
    "resx",
    "resy",
    "resz",
    "maxFeatures",
    "filter",
    "inlineGeometry",
    "document",
    "extension"
})
public class LayerType
    extends AbstractResourceType
{

    @XmlElement(name = "DimensionList")
    protected DimensionListType dimensionList;
    @XmlElement(name = "ResponseCRS")
    protected String responseCRS;
    @XmlElement(name = "ParameterList")
    protected ParameterListType parameterList;
    @XmlElement(name = "Depth")
    protected String depth;
    @XmlElement(name = "Resx")
    protected String resx;
    @XmlElement(name = "Resy")
    protected String resy;
    @XmlElement(name = "Resz")
    protected String resz;
    @XmlElement(name = "MaxFeatures")
    protected BigInteger maxFeatures;
    @XmlElement(name = "Filter", namespace = "http://www.opengis.net/ogc")
    protected FilterType filter;
    @XmlElement(name = "InlineGeometry")
    protected AbstractFeatureCollectionType inlineGeometry;
    @XmlElement(name = "Document", namespace = "http://www.opengis.net/kml/2.2")
    protected List<DocumentType> document;
    @XmlElement(name = "Extension")
    protected ExtensionType extension;
    @XmlAttribute
    protected Boolean queryable;

    /**
     * Gets the value of the dimensionList property.
     * 
     * @return
     *     possible object is
     *     {@link DimensionListType }
     *     
     */
    public DimensionListType getDimensionList() {
        return dimensionList;
    }

    /**
     * Sets the value of the dimensionList property.
     * 
     * @param value
     *     allowed object is
     *     {@link DimensionListType }
     *     
     */
    public void setDimensionList(DimensionListType value) {
        this.dimensionList = value;
    }

    /**
     * Gets the value of the responseCRS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseCRS() {
        return responseCRS;
    }

    /**
     * Sets the value of the responseCRS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseCRS(String value) {
        this.responseCRS = value;
    }

    /**
     * Gets the value of the parameterList property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterListType }
     *     
     */
    public ParameterListType getParameterList() {
        return parameterList;
    }

    /**
     * Sets the value of the parameterList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterListType }
     *     
     */
    public void setParameterList(ParameterListType value) {
        this.parameterList = value;
    }

    /**
     * Gets the value of the depth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepth() {
        return depth;
    }

    /**
     * Sets the value of the depth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepth(String value) {
        this.depth = value;
    }

    /**
     * Gets the value of the resx property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResx() {
        return resx;
    }

    /**
     * Sets the value of the resx property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResx(String value) {
        this.resx = value;
    }

    /**
     * Gets the value of the resy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResy() {
        return resy;
    }

    /**
     * Sets the value of the resy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResy(String value) {
        this.resy = value;
    }

    /**
     * Gets the value of the resz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResz() {
        return resz;
    }

    /**
     * Sets the value of the resz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResz(String value) {
        this.resz = value;
    }

    /**
     * Gets the value of the maxFeatures property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxFeatures() {
        return maxFeatures;
    }

    /**
     * Sets the value of the maxFeatures property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxFeatures(BigInteger value) {
        this.maxFeatures = value;
    }

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link FilterType }
     *     
     */
    public FilterType getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterType }
     *     
     */
    public void setFilter(FilterType value) {
        this.filter = value;
    }

    /**
     * Gets the value of the inlineGeometry property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractFeatureCollectionType }
     *     
     */
    public AbstractFeatureCollectionType getInlineGeometry() {
        return inlineGeometry;
    }

    /**
     * Sets the value of the inlineGeometry property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractFeatureCollectionType }
     *     
     */
    public void setInlineGeometry(AbstractFeatureCollectionType value) {
        this.inlineGeometry = value;
    }

    /**
     * Gets the value of the document property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the document property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocument().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocumentType }
     * 
     * 
     */
    public List<DocumentType> getDocument() {
        if (document == null) {
            document = new ArrayList<DocumentType>();
        }
        return this.document;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link ExtensionType }
     *     
     */
    public ExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensionType }
     *     
     */
    public void setExtension(ExtensionType value) {
        this.extension = value;
    }

    /**
     * Gets the value of the queryable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isQueryable() {
        if (queryable == null) {
            return false;
        } else {
            return queryable;
        }
    }

    /**
     * Sets the value of the queryable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setQueryable(Boolean value) {
        this.queryable = value;
    }

}
