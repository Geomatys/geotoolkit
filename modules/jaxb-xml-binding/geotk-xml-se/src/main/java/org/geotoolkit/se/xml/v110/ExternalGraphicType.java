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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExternalGraphicType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExternalGraphicType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/se}OnlineResource"/>
 *           &lt;element ref="{http://www.opengis.net/se}InlineContent"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/se}Format"/>
 *         &lt;element ref="{http://www.opengis.net/se}ColorReplacement" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalGraphicType", propOrder = {
    "onlineResource",
    "inlineContent",
    "format",
    "colorReplacement"
})
public class ExternalGraphicType {

    @XmlElement(name = "OnlineResource")
    protected OnlineResourceType onlineResource;
    @XmlElement(name = "InlineContent")
    protected InlineContentType inlineContent;
    @XmlElement(name = "Format", required = true)
    protected String format;
    @XmlElement(name = "ColorReplacement")
    protected List<ColorReplacementType> colorReplacement;

    /**
     * Gets the value of the onlineResource property.
     * 
     * @return
     *     possible object is
     *     {@link OnlineResourceType }
     *     
     */
    public OnlineResourceType getOnlineResource() {
        return onlineResource;
    }

    /**
     * Sets the value of the onlineResource property.
     * 
     * @param value
     *     allowed object is
     *     {@link OnlineResourceType }
     *     
     */
    public void setOnlineResource(OnlineResourceType value) {
        this.onlineResource = value;
    }

    /**
     * Gets the value of the inlineContent property.
     * 
     * @return
     *     possible object is
     *     {@link InlineContentType }
     *     
     */
    public InlineContentType getInlineContent() {
        return inlineContent;
    }

    /**
     * Sets the value of the inlineContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link InlineContentType }
     *     
     */
    public void setInlineContent(InlineContentType value) {
        this.inlineContent = value;
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
     * Gets the value of the colorReplacement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colorReplacement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColorReplacement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ColorReplacementType }
     * 
     * 
     */
    public List<ColorReplacementType> getColorReplacement() {
        if (colorReplacement == null) {
            colorReplacement = new ArrayList<ColorReplacementType>();
        }
        return this.colorReplacement;
    }

}
