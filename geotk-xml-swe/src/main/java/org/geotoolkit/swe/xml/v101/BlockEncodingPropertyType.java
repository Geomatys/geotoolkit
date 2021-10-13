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
package org.geotoolkit.swe.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.BlockEncodingProperty;


/**
 * <p>Java class for BlockEncodingPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BlockEncodingPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}StandardFormat"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}BinaryBlock"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}TextBlock"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}XMLBlock"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BlockEncodingPropertyType", propOrder = {
    "standardFormat",
    "binaryBlock",
    "textBlock",
    "xmlBlock"
})
public class BlockEncodingPropertyType implements BlockEncodingProperty {

    @XmlElement(name = "StandardFormat")
    private StandardFormat standardFormat;
    @XmlElement(name = "BinaryBlock")
    private BinaryBlock binaryBlock;
    @XmlElement(name = "TextBlock")
    private TextBlockType textBlock;
    @XmlElement(name = "XMLBlock")
    private XMLBlockType xmlBlock;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlSchemaType(name = "anyURI")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;

    public BlockEncodingPropertyType() {

    }

    public BlockEncodingPropertyType(final BlockEncodingProperty be) {
        if (be != null) {
            this.actuate      = be.getActuate();
            this.arcrole      = be.getArcrole();
            this.href         = be.getHref();
            this.remoteSchema = be.getRemoteSchema();
            this.role         = be.getRole();
            this.show         = be.getShow();
            this.title        = be.getTitle();
            this.type         = be.getType();
            if (be.getBinaryBlock() != null) {
                this.binaryBlock = new BinaryBlock(be.getBinaryBlock());
            }
            if (be.getStandardFormat() != null) {
                this.standardFormat = new StandardFormat(be.getStandardFormat());
            }
            if (be.getTextBlock() != null) {
                this.textBlock = new TextBlockType(be.getTextBlock());
            }
            if (be.getXMLBlock() != null) {
                this.xmlBlock = new XMLBlockType(be.getXMLBlock());
            }
        }
    }

    /**
     * Gets the value of the standardFormat property.
     *
     * @return
     *     possible object is
     *     {@link StandardFormat }
     *
     */
    public StandardFormat getStandardFormat() {
        return standardFormat;
    }

    /**
     * Sets the value of the standardFormat property.
     *
     * @param value
     *     allowed object is
     *     {@link StandardFormat }
     *
     */
    public void setStandardFormat(final StandardFormat value) {
        this.standardFormat = value;
    }

    /**
     * Gets the value of the binaryBlock property.
     *
     * @return
     *     possible object is
     *     {@link BinaryBlock }
     *
     */
    public BinaryBlock getBinaryBlock() {
        return binaryBlock;
    }

    /**
     * Sets the value of the binaryBlock property.
     *
     * @param value
     *     allowed object is
     *     {@link BinaryBlock }
     *
     */
    public void setBinaryBlock(final BinaryBlock value) {
        this.binaryBlock = value;
    }

    /**
     * Gets the value of the textBlock property.
     *
     * @return
     *     possible object is
     *     {@link TextBlock }
     *
     */
    public TextBlockType getTextBlock() {
        return textBlock;
    }

    /**
     * Sets the value of the textBlock property.
     *
     * @param value
     *     allowed object is
     *     {@link TextBlock }
     *
     */
    public void setTextBlock(final TextBlockType value) {
        this.textBlock = value;
    }

    /**
     * Gets the value of the xmlBlock property.
     *
     * @return
     *     possible object is
     *     {@link XMLBlockType }
     *
     */
    public XMLBlockType getXMLBlock() {
        return xmlBlock;
    }

    /**
     * Sets the value of the xmlBlock property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLBlockType }
     *
     */
    public void setXMLBlock(final XMLBlockType value) {
        this.xmlBlock = value;
    }

    /**
     * Gets the value of the remoteSchema property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRemoteSchema(final String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(final String value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHref(final String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRole(final String value) {
        this.role = value;
    }

    /**
     * Gets the value of the arcrole property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setArcrole(final String value) {
        this.arcrole = value;
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
    public void setTitle(final String value) {
        this.title = value;
    }

    /**
     * Gets the value of the show property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setShow(final String value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setActuate(final String value) {
        this.actuate = value;
    }

}
