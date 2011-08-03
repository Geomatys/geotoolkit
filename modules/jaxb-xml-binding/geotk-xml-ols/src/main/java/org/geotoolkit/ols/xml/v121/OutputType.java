/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;


/**
 * <p>Java class for OutputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OutputType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="BBoxContext" type="{http://www.opengis.net/gml}EnvelopeType"/>
 *         &lt;element name="CenterContext" type="{http://www.opengis.net/xls}CenterContextType"/>
 *       &lt;/choice>
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="BGcolor" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="transparent" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="content" type="{http://www.opengis.net/xls}presentationContentType" default="URL" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OutputType", propOrder = {
    "bBoxContext",
    "centerContext"
})
public class OutputType {

    @XmlElement(name = "BBoxContext")
    private EnvelopeType bBoxContext;
    @XmlElement(name = "CenterContext")
    private CenterContextType centerContext;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer width;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer height;
    @XmlAttribute
    private String format;
    @XmlAttribute(name = "BGcolor")
    private String bGcolor;
    @XmlAttribute
    private Boolean transparent;
    @XmlAttribute
    private PresentationContentType content;

    /**
     * Gets the value of the bBoxContext property.
     * 
     * @return
     *     possible object is
     *     {@link EnvelopeType }
     *     
     */
    public EnvelopeType getBBoxContext() {
        return bBoxContext;
    }

    /**
     * Sets the value of the bBoxContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnvelopeType }
     *     
     */
    public void setBBoxContext(EnvelopeType value) {
        this.bBoxContext = value;
    }

    /**
     * Gets the value of the centerContext property.
     * 
     * @return
     *     possible object is
     *     {@link CenterContextType }
     *     
     */
    public CenterContextType getCenterContext() {
        return centerContext;
    }

    /**
     * Sets the value of the centerContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link CenterContextType }
     *     
     */
    public void setCenterContext(CenterContextType value) {
        this.centerContext = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWidth(Integer value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHeight(Integer value) {
        this.height = value;
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
     * Gets the value of the bGcolor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBGcolor() {
        return bGcolor;
    }

    /**
     * Sets the value of the bGcolor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBGcolor(String value) {
        this.bGcolor = value;
    }

    /**
     * Gets the value of the transparent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTransparent() {
        return transparent;
    }

    /**
     * Sets the value of the transparent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTransparent(Boolean value) {
        this.transparent = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link PresentationContentType }
     *     
     */
    public PresentationContentType getContent() {
        if (content == null) {
            return PresentationContentType.URL;
        } else {
            return content;
        }
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link PresentationContentType }
     *     
     */
    public void setContent(PresentationContentType value) {
        this.content = value;
    }

}
