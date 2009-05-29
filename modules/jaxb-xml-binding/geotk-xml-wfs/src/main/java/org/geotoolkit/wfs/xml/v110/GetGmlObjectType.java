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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v110modified.GmlObjectIdType;


/**
 * A GetGmlObjectType element contains exactly one GmlObjectId.  
 * The value of the gml:id attribute on that GmlObjectId is used as a unique key to retrieve the complex element with a
 * gml:id attribute with the same value.  
 *          
 * 
 * <p>Java class for GetGmlObjectType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetGmlObjectType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wfs}BaseRequestType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}GmlObjectId"/>
 *       &lt;/sequence>
 *       &lt;attribute name="outputFormat" type="{http://www.w3.org/2001/XMLSchema}string" default="GML3" />
 *       &lt;attribute name="traverseXlinkDepth" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="traverseXlinkExpiry" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetGmlObjectType", propOrder = {
    "gmlObjectId"
})
@XmlRootElement(name = "GetGmlObject")
public class GetGmlObjectType extends BaseRequestType {

    @XmlElement(name = "GmlObjectId", namespace = "http://www.opengis.net/ogc", required = true)
    private GmlObjectIdType gmlObjectId;
    @XmlAttribute
    private String outputFormat;
    @XmlAttribute(required = true)
    private String traverseXlinkDepth;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer traverseXlinkExpiry;

    public GetGmlObjectType() {

    }

    public GetGmlObjectType(String service, String version, String handle, GmlObjectIdType gmlObjectId, String outputFormat) {
        super(service, version, handle);
        this.gmlObjectId  = gmlObjectId;
        this.outputFormat = outputFormat;
    }

    public GetGmlObjectType(String service, String version, String handle, GmlObjectIdType gmlObjectId, String outputFormat,
            String traverseXlinkDepth, Integer traverseXlinkExpiry) {
        super(service, version, handle);
        this.gmlObjectId  = gmlObjectId;
        this.outputFormat = outputFormat;
        this.traverseXlinkDepth  = traverseXlinkDepth;
        this.traverseXlinkExpiry = traverseXlinkExpiry;
    }
    
    /**
     * Gets the value of the gmlObjectId property.
     * 
     * @return
     *     possible object is
     *     {@link GmlObjectIdType }
     *     
     */
    public GmlObjectIdType getGmlObjectId() {
        return gmlObjectId;
    }

    /**
     * Sets the value of the gmlObjectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link GmlObjectIdType }
     *     
     */
    public void setGmlObjectId(GmlObjectIdType value) {
        this.gmlObjectId = value;
    }

    /**
     * Gets the value of the outputFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutputFormat() {
        if (outputFormat == null) {
            return "GML3";
        } else {
            return outputFormat;
        }
    }

    /**
     * Sets the value of the outputFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutputFormat(String value) {
        this.outputFormat = value;
    }

    /**
     * Gets the value of the traverseXlinkDepth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTraverseXlinkDepth() {
        return traverseXlinkDepth;
    }

    /**
     * Sets the value of the traverseXlinkDepth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTraverseXlinkDepth(String value) {
        this.traverseXlinkDepth = value;
    }

    /**
     * Gets the value of the traverseXlinkExpiry property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTraverseXlinkExpiry() {
        return traverseXlinkExpiry;
    }

    /**
     * Sets the value of the traverseXlinkExpiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTraverseXlinkExpiry(Integer value) {
        this.traverseXlinkExpiry = value;
    }

}
