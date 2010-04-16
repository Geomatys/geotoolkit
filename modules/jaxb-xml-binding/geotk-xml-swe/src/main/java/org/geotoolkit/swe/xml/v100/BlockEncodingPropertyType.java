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
package org.geotoolkit.swe.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractEncodingProperty;
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
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}StandardFormat"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}BinaryBlock"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}TextBlock"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}XMLBlock"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BlockEncodingPropertyType", propOrder = {
    "standardFormat",
    "binaryBlock",
    "textBlock",
    "xmlBlock"
})
public class BlockEncodingPropertyType implements BlockEncodingProperty, AbstractEncodingProperty {

    @XmlElement(name = "StandardFormat")
    private StandardFormat standardFormat;
    @XmlElement(name = "BinaryBlock")
    private BinaryBlock binaryBlock;
    @XmlElement(name = "TextBlock")
    private TextBlock textBlock;
    @XmlElement(name = "XMLBlock")
    private XMLBlockType xmlBlock;

    @XmlTransient
    private AbstractEncodingType hiddenEncoding;

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

    public BlockEncodingPropertyType(BlockEncodingProperty be) {
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
                this.textBlock = new TextBlock(be.getTextBlock());
            }
            if (be.getXMLBlock() != null) {
                this.xmlBlock = new XMLBlockType(be.getXMLBlock());
            }
        }
    }

    public AbstractEncodingType getEncoding() {
        if (standardFormat != null)
            return standardFormat;
        else if (binaryBlock != null)
            return binaryBlock;
        else if (textBlock != null)
            return textBlock;
        else if (xmlBlock != null)
            return xmlBlock;
        return null;
    }

    public void setToHref(){
        if (getEncoding() != null) {
            this.href = getEncoding().getId();
            hiddenEncoding = getEncoding();
            clearEncoding();
        }
    }

    private void clearEncoding() {
        standardFormat = null;
        binaryBlock    = null;
        textBlock      = null;
        xmlBlock       = null;
    }

    /**
     * Gets the value of the standardFormat property.
     */
    public StandardFormat getStandardFormat() {
        return standardFormat;
    }

    /**
     * Sets the value of the standardFormat property.
     */
    public void setStandardFormat(StandardFormat value) {
        this.standardFormat = value;
    }

    /**
     * Gets the value of the binaryBlock property.
     */
    public BinaryBlock getBinaryBlock() {
        return binaryBlock;
    }

    /**
     * Sets the value of the binaryBlock property.
     */
    public void setBinaryBlock(BinaryBlock value) {
        this.binaryBlock = value;
    }

    /**
     * Gets the value of the textBlock property.
     */
    public TextBlock getTextBlock() {
        return textBlock;
    }

    /**
     * Sets the value of the textBlock property.
     */
    public void setTextBlock(TextBlock value) {
        this.textBlock = value;
    }

    /**
     * Gets the value of the xmlBlock property.
     */
    public XMLBlockType getXMLBlock() {
        return xmlBlock;
    }

    /**
     * Sets the value of the xmlBlock property.
     */
    public void setXMLBlock(XMLBlockType value) {
        this.xmlBlock = value;
    }

    /**
     * Gets the value of the remoteSchema property.
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     * 
     * @param value
     */
    public void setRemoteSchema(String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the type property.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     * 
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Gets the value of the arcrole property.
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     */
    public void setArcrole(String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the show property.
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     */
    public void setShow(String value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     */
    public void setActuate(String value) {
        this.actuate = value;
    }

}
