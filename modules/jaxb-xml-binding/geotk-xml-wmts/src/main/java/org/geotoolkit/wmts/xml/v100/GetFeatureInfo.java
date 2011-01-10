/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.RequestBase;
import org.geotoolkit.util.Version;


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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getTile",
    "j",
    "i",
    "infoFormat"
})
@XmlRootElement(name = "GetFeatureInfo")
public class GetFeatureInfo implements RequestBase {

    @XmlElement(name = "GetTile", required = true)
    private GetTile getTile;
    @XmlElement(name = "J", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer j;
    @XmlElement(name = "I", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer i;
    @XmlElement(name = "InfoFormat", required = true)
    private String infoFormat;
    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute(required = true)
    private String version;

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
    public void setGetTile(final GetTile value) {
        this.getTile = value;
    }

    /**
     * Gets the value of the j property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getJ() {
        return j;
    }

    /**
     * Sets the value of the j property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setJ(final Integer value) {
        this.j = value;
    }

    /**
     * Gets the value of the i property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getI() {
        return i;
    }

    /**
     * Sets the value of the i property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setI(final Integer value) {
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
    public void setInfoFormat(final String value) {
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
    @Override
    public String getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setService(final String value) {
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
    @Override
    public Version getVersion() {
        if (version != null) {
            return new Version(version);
        }
        return null;
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

    public String toKvp() {
        return "request=GetFeatureInfo&service="+ getService() +"&version="+ getVersion() +"&layer="+
               getGetTile().getLayer() +"&style="+ getGetTile().getStyle() +"&format="+
               getGetTile().getFormat() +"&tileMatrixSet="+ getGetTile().getTileMatrixSet() +
               "&tileMatrix="+ getGetTile().getTileMatrix() +"&tileRow="+ getGetTile().getTileRow() +
               "&tileCol="+ getGetTile().getTileCol() + "&I="+ getI() +"&J="+ getJ() +"&infoformat="+
               getInfoFormat();
    }
}
