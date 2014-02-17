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

package org.geotoolkit.citygml.xml.v100.appearance;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Base class for textures. "imageURI" can contain any valid URI from references to a local file to
 *                 preformatted web service requests. The linking to geometry and texture parameterization is provided by derived
 *                 classes.
 * 
 * <p>Java class for AbstractTextureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractTextureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/appearance/1.0}AbstractSurfaceDataType">
 *       &lt;sequence>
 *         &lt;element name="imageURI" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="mimeType" type="{http://www.opengis.net/citygml/1.0}MimeTypeType" minOccurs="0"/>
 *         &lt;element name="textureType" type="{http://www.opengis.net/citygml/appearance/1.0}TextureTypeType" minOccurs="0"/>
 *         &lt;element name="wrapMode" type="{http://www.opengis.net/citygml/appearance/1.0}WrapModeType" minOccurs="0"/>
 *         &lt;element name="borderColor" type="{http://www.opengis.net/citygml/appearance/1.0}ColorPlusOpacity" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/appearance/1.0}_GenericApplicationPropertyOfTexture" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractTextureType", propOrder = {
    "imageURI",
    "mimeType",
    "textureType",
    "wrapMode",
    "borderColor",
    "genericApplicationPropertyOfTexture"
})
@XmlSeeAlso({
    GeoreferencedTextureType.class,
    ParameterizedTextureType.class
})
public class AbstractTextureType extends AbstractSurfaceDataType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String imageURI;
    private String mimeType;
    private TextureTypeType textureType;
    private WrapModeType wrapMode;
    @XmlList
    @XmlElement(type = Double.class)
    private List<Double> borderColor;
    @XmlElement(name = "_GenericApplicationPropertyOfTexture")
    private List<Object> genericApplicationPropertyOfTexture;

    /**
     * Gets the value of the imageURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageURI() {
        return imageURI;
    }

    /**
     * Sets the value of the imageURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageURI(String value) {
        this.imageURI = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the textureType property.
     * 
     * @return
     *     possible object is
     *     {@link TextureTypeType }
     *     
     */
    public TextureTypeType getTextureType() {
        return textureType;
    }

    /**
     * Sets the value of the textureType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextureTypeType }
     *     
     */
    public void setTextureType(TextureTypeType value) {
        this.textureType = value;
    }

    /**
     * Gets the value of the wrapMode property.
     * 
     * @return
     *     possible object is
     *     {@link WrapModeType }
     *     
     */
    public WrapModeType getWrapMode() {
        return wrapMode;
    }

    /**
     * Sets the value of the wrapMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link WrapModeType }
     *     
     */
    public void setWrapMode(WrapModeType value) {
        this.wrapMode = value;
    }

    /**
     * Gets the value of the borderColor property.
     * 
     */
    public List<Double> getBorderColor() {
        if (borderColor == null) {
            borderColor = new ArrayList<Double>();
        }
        return this.borderColor;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfTexture property.
     * 
     */
    public List<Object> getGenericApplicationPropertyOfTexture() {
        if (genericApplicationPropertyOfTexture == null) {
            genericApplicationPropertyOfTexture = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfTexture;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (imageURI != null) {
            s.append("imageURI:").append(imageURI).append('\n');
        }
        if (mimeType != null) {
            s.append("mimeType:").append(mimeType).append('\n');
        }
        if (textureType != null) {
            s.append("textureType:").append(textureType).append('\n');
        }
        if (wrapMode != null) {
            s.append("wrapMode:").append(wrapMode).append('\n');
        }
        if (borderColor != null && borderColor.size() > 0) {
            s.append("borderColor:").append('\n');
            for (Double fp : borderColor) {
                s.append(fp).append('\n');
            }
        }
        if (genericApplicationPropertyOfTexture != null && genericApplicationPropertyOfTexture.size() > 0) {
            s.append("genericApplicationPropertyOfTexture:").append('\n');
            for (Object fp : genericApplicationPropertyOfTexture) {
                s.append(fp).append('\n');
            }
        }
        return s.toString();
    }
}
