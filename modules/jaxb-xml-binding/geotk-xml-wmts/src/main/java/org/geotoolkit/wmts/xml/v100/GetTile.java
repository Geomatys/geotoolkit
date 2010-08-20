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

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="Layer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Style" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Format" type="{http://www.opengis.net/ows/1.1}MimeType"/>
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}DimensionNameValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TileMatrixSet" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TileMatrix" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TileRow" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="TileCol" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
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
    "layer",
    "style",
    "format",
    "dimensionNameValue",
    "tileMatrixSet",
    "tileMatrix",
    "tileRow",
    "tileCol"
})
@XmlRootElement(name = "GetTile")
public class GetTile implements RequestBase {

    @XmlElement(name = "Layer", required = true)
    private String layer;
    @XmlElement(name = "Style", required = true)
    private String style;
    @XmlElement(name = "Format", required = true)
    private String format;
    @XmlElement(name = "DimensionNameValue")
    private List<DimensionNameValue> dimensionNameValue;
    @XmlElement(name = "TileMatrixSet", required = true)
    private String tileMatrixSet;
    @XmlElement(name = "TileMatrix", required = true)
    private String tileMatrix;
    @XmlElement(name = "TileRow", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer tileRow;
    @XmlElement(name = "TileCol", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer tileCol;
    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute(required = true)
    private String version;

    /**
     * Gets the value of the layer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer() {
        return layer;
    }

    /**
     * Sets the value of the layer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer(String value) {
        this.layer = value;
    }

    /**
     * Gets the value of the style property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the value of the style property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStyle(String value) {
        this.style = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Dimension name and value Gets the value of the dimensionNameValue property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link DimensionNameValue }
     * 
     * 
     */
    public List<DimensionNameValue> getDimensionNameValue() {
        if (dimensionNameValue == null) {
            dimensionNameValue = new ArrayList<DimensionNameValue>();
        }
        return this.dimensionNameValue;
    }

    /**
     * Gets the value of the tileMatrixSet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTileMatrixSet() {
        return tileMatrixSet;
    }

    /**
     * Sets the value of the tileMatrixSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTileMatrixSet(String value) {
        this.tileMatrixSet = value;
    }

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
     * Gets the value of the tileRow property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTileRow() {
        return tileRow;
    }

    /**
     * Sets the value of the tileRow property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTileRow(Integer value) {
        this.tileRow = value;
    }

    /**
     * Gets the value of the tileCol property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTileCol() {
        return tileCol;
    }

    /**
     * Sets the value of the tileCol property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTileCol(Integer value) {
        this.tileCol = value;
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
    public void setVersion(String value) {
        this.version = value;
    }

    public String toKvp() {
        return "request=GetTile&service="+ getService() +"&version="+ getVersion() +"&layer="+
               getLayer() +"&style="+ getStyle() +"&format="+ getFormat() +"&tileMatrixSet="+
               getTileMatrixSet() +"&tileMatrix="+ getTileMatrix() +"&tileRow="+ getTileRow() +
               "&tileCol="+ getTileCol();
    }
}
