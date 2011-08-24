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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * Texture parameterization using texture coordinates: Each gml:LinearRing that is part of the surface
 *                 requires a separate "textureCoordinates"-entry with 2 doubles per ring vertex. The "ring"- attribute provides the
 *                 gml:id of the target LinearRing. It is prohibited to link texture coordinates to any other object type than
 *                 LinearRing. Thus, surfaces not consisting of LinearRings cannot be textured this way. Use transformation matrices
 *                 (see below) or georeferenced textures instead. 
 * 
 * <p>Java class for TexCoordListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TexCoordListType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/appearance/1.0}AbstractTextureParameterizationType">
 *       &lt;sequence>
 *         &lt;element name="textureCoordinates" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.opengis.net/gml>doubleList">
 *                 &lt;attribute name="ring" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.opengis.net/citygml/appearance/1.0}_GenericApplicationPropertyOfTexCoordList" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TexCoordListType", propOrder = {
    "textureCoordinates",
    "genericApplicationPropertyOfTexCoordList"
})
public class TexCoordListType extends AbstractTextureParameterizationType {

    @XmlElement(required = true)
    private List<TexCoordListType.TextureCoordinates> textureCoordinates;
    @XmlElement(name = "_GenericApplicationPropertyOfTexCoordList")
    private List<Object> genericApplicationPropertyOfTexCoordList;

    /**
     * Gets the value of the textureCoordinates property.
     * 
     */
    public List<TexCoordListType.TextureCoordinates> getTextureCoordinates() {
        if (textureCoordinates == null) {
            textureCoordinates = new ArrayList<TexCoordListType.TextureCoordinates>();
        }
        return this.textureCoordinates;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfTexCoordList property.
     * 
     */
    public List<Object> getGenericApplicationPropertyOfTexCoordList() {
        if (genericApplicationPropertyOfTexCoordList == null) {
            genericApplicationPropertyOfTexCoordList = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfTexCoordList;
    }


    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (textureCoordinates != null && textureCoordinates.size() > 0) {
            s.append("textureCoordinates:").append('\n');
            for (TextureCoordinates fp : textureCoordinates) {
                s.append(fp).append('\n');
            }
        }
        if (genericApplicationPropertyOfTexCoordList != null && genericApplicationPropertyOfTexCoordList.size() > 0) {
            s.append("genericApplicationPropertyOfTexCoordList:").append('\n');
            for (Object fp : genericApplicationPropertyOfTexCoordList) {
                s.append(fp).append('\n');
            }
        }
        return s.toString();
    }
    
    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.opengis.net/gml>doubleList">
     *       &lt;attribute name="ring" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class TextureCoordinates {

        @XmlValue
        private List<Double> value;
        @XmlAttribute(required = true)
        @XmlSchemaType(name = "anyURI")
        private String ring;

        /**
         * XML List based on XML Schema double type.  An element of this type contains a space-separated list of double values Gets the value of the value property.
         * 
         */
        public List<Double> getValue() {
            if (value == null) {
                value = new ArrayList<Double>();
            }
            return this.value;
        }

        /**
         * Gets the value of the ring property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRing() {
            return ring;
        }

        /**
         * Sets the value of the ring property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRing(String value) {
            this.ring = value;
        }

        @Override
        public String toString() {
            final StringBuilder s = new StringBuilder("[TextureCoordinates]");
            if (ring != null) {
                s.append("ring:").append(ring).append('\n');
            }
            if (value != null && value.size() > 0) {
                s.append("value:").append('\n');
                for (Double fp : value) {
                    s.append(fp).append('\n');
                }
            }
            return s.toString();
        }
    }

}
