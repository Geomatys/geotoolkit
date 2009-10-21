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
package org.geotoolkit.wmts.xml.v100;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}GetTile"/>
 *         &lt;element name="J" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="I" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="InfoFormat" type="{http://www.opengis.net/ows/1.1}MimeType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="service" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="WMTS" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetFeatureInfoType", propOrder = {
    "getTile",
    "j",
    "i",
    "infoFormat"
})
@XmlRootElement(name = "GetFeatureInfo")
public class GetFeatureInfo {

    @XmlElement(name = "GetTile", required = true)
    protected GetTile getTile;
    @XmlElement(name = "J", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger j;
    @XmlElement(name = "I", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger i;
    @XmlElement(name = "InfoFormat", required = true)
    protected String infoFormat;
    @XmlAttribute(required = true)
    protected String service;
    @XmlAttribute(required = true)
    protected String version;

    /**
     * The corresponding GetTile request parameters
     * 
     * @return
     *     possible object is
     *     {@link GetTile }
     *     
     */
    public GetTile getGetTile() {
        return getTile;
    }

    /**
     * The corresponding GetTile request parameters
     * 
     * @param value
     *     allowed object is
     *     {@link GetTile }
     *     
     */
    public void setGetTile(GetTile value) {
        this.getTile = value;
    }

    /**
     * Gets the value of the j property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getJ() {
        return j;
    }

    /**
     * Sets the value of the j property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setJ(BigInteger value) {
        this.j = value;
    }

    /**
     * Gets the value of the i property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getI() {
        return i;
    }

    /**
     * Sets the value of the i property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setI(BigInteger value) {
        this.i = value;
    }

    /**
     * Gets the value of the infoFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfoFormat() {
        return infoFormat;
    }

    /**
     * Sets the value of the infoFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfoFormat(String value) {
        this.infoFormat = value;
    }

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getService() {
        if (service == null) {
            return "WMTS";
        } else {
            return service;
        }
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setService(String value) {
        this.service = value;
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
        if (version == null) {
            return "1.0.0";
        } else {
            return version;
        }
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

    public String toKvp() {
        return "request=GetFeatureInfo&service="+ getService() +"&version="+ getVersion() +"&layer="+
               getGetTile().getLayer() +"&style="+ getGetTile().getStyle() +"&format="+
               getGetTile().getFormat() +"&tileMatrixSet="+ getGetTile().getTileMatrixSet() +
               "&tileMatrix="+ getGetTile().getTileMatrix() +"&tileRow="+ getGetTile().getTileRow() +
               "&tileCol="+ getGetTile().getTileCol() + "&I="+ getI() +"&J="+ getJ() +"&infoformat="+
               getInfoFormat();
    }
}
