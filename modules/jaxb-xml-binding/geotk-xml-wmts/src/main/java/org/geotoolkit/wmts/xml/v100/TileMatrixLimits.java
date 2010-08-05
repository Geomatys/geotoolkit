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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="TileMatrix" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MinTileRow" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="MaxTileRow" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="MinTileCol" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="MaxTileCol" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
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
    "tileMatrix",
    "minTileRow",
    "maxTileRow",
    "minTileCol",
    "maxTileCol"
})
@XmlRootElement(name = "TileMatrixLimits")
public class TileMatrixLimits {

    @XmlElement(name = "TileMatrix", required = true)
    private String tileMatrix;
    @XmlElement(name = "MinTileRow", required = true)
    @XmlSchemaType(name = "positiveInteger")
    private Integer minTileRow;
    @XmlElement(name = "MaxTileRow", required = true)
    @XmlSchemaType(name = "positiveInteger")
    private Integer maxTileRow;
    @XmlElement(name = "MinTileCol", required = true)
    @XmlSchemaType(name = "positiveInteger")
    private Integer minTileCol;
    @XmlElement(name = "MaxTileCol", required = true)
    @XmlSchemaType(name = "positiveInteger")
    private Integer maxTileCol;

    /**
     * Gets the value of the tileMatrix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTileMatrix() {
        return tileMatrix;
    }

    /**
     * Sets the value of the tileMatrix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTileMatrix(String value) {
        this.tileMatrix = value;
    }

    /**
     * Gets the value of the minTileRow property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinTileRow() {
        return minTileRow;
    }

    /**
     * Sets the value of the minTileRow property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinTileRow(Integer value) {
        this.minTileRow = value;
    }

    /**
     * Gets the value of the maxTileRow property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxTileRow() {
        return maxTileRow;
    }

    /**
     * Sets the value of the maxTileRow property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxTileRow(Integer value) {
        this.maxTileRow = value;
    }

    /**
     * Gets the value of the minTileCol property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinTileCol() {
        return minTileCol;
    }

    /**
     * Sets the value of the minTileCol property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinTileCol(Integer value) {
        this.minTileCol = value;
    }

    /**
     * Gets the value of the maxTileCol property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxTileCol() {
        return maxTileCol;
    }

    /**
     * Sets the value of the maxTileCol property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxTileCol(Integer value) {
        this.maxTileCol = value;
    }

}
