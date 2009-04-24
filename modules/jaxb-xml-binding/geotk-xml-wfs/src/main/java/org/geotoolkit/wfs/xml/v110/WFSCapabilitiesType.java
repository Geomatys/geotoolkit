/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2009, Geomatys
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


package org.geotoolkit.wfs.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v110modified.FilterCapabilities;
import org.geotoolkit.ows.xml.v100.CapabilitiesBaseType;


/**
 * XML encoded WFS GetCapabilities operation response. This document provides clients with service metadata about a
 * specific service instance, including metadata about the tightly-coupled data served.
 * If the server does not implement the updateSequence parameter,
 * the server shall always return the complete Capabilities document,
 * without the updateSequence parameter.
 * When the server implements the updateSequence parameter and the GetCapabilities operation request included
 * the updateSequence parameter with the current value, the server shall return this element with only the "version" and
 * "updateSequence" attributes.
 * Otherwise, all optional elements shall be included or not depending on the actual value of the
 * Contents parameter in the GetCapabilities operation request.
 *          
 * 
 * <p>Java class for WFS_CapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WFS_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows}CapabilitiesBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wfs}FeatureTypeList" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}ServesGMLObjectTypeList" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}SupportsGMLObjectTypeList" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Filter_Capabilities"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WFS_CapabilitiesType", propOrder = {
    "featureTypeList",
    "servesGMLObjectTypeList",
    "supportsGMLObjectTypeList",
    "filterCapabilities"
})
@XmlRootElement(name = "WFSCapabilities")
public class WFSCapabilitiesType extends CapabilitiesBaseType {

    @XmlElement(name = "FeatureTypeList")
    private FeatureTypeListType featureTypeList;
    @XmlElement(name = "ServesGMLObjectTypeList")
    private GMLObjectTypeListType servesGMLObjectTypeList;
    @XmlElement(name = "SupportsGMLObjectTypeList")
    private GMLObjectTypeListType supportsGMLObjectTypeList;
    @XmlElement(name = "Filter_Capabilities", namespace = "http://www.opengis.net/ogc", required = true)
    private FilterCapabilities filterCapabilities;

    /**
     * Gets the value of the featureTypeList property.
     * 
     * @return
     *     possible object is
     *     {@link FeatureTypeListType }
     *     
     */
    public FeatureTypeListType getFeatureTypeList() {
        return featureTypeList;
    }

    /**
     * Sets the value of the featureTypeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link FeatureTypeListType }
     *     
     */
    public void setFeatureTypeList(FeatureTypeListType value) {
        this.featureTypeList = value;
    }

    /**
     * Gets the value of the servesGMLObjectTypeList property.
     * 
     * @return
     *     possible object is
     *     {@link GMLObjectTypeListType }
     *     
     */
    public GMLObjectTypeListType getServesGMLObjectTypeList() {
        return servesGMLObjectTypeList;
    }

    /**
     * Sets the value of the servesGMLObjectTypeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link GMLObjectTypeListType }
     *     
     */
    public void setServesGMLObjectTypeList(GMLObjectTypeListType value) {
        this.servesGMLObjectTypeList = value;
    }

    /**
     * Gets the value of the supportsGMLObjectTypeList property.
     * 
     * @return
     *     possible object is
     *     {@link GMLObjectTypeListType }
     *     
     */
    public GMLObjectTypeListType getSupportsGMLObjectTypeList() {
        return supportsGMLObjectTypeList;
    }

    /**
     * Sets the value of the supportsGMLObjectTypeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link GMLObjectTypeListType }
     *     
     */
    public void setSupportsGMLObjectTypeList(GMLObjectTypeListType value) {
        this.supportsGMLObjectTypeList = value;
    }

    /**
     * Gets the value of the filterCapabilities property.
     * 
     * @return
     *     possible object is
     *     {@link FilterCapabilities }
     *     
     */
    public FilterCapabilities getFilterCapabilities() {
        return filterCapabilities;
    }

    /**
     * Sets the value of the filterCapabilities property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterCapabilities }
     *     
     */
    public void setFilterCapabilities(FilterCapabilities value) {
        this.filterCapabilities = value;
    }

}
