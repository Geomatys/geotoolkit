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
 * Texture parameterization using a transformation matrix. The transformation matrix "worldToTexture"
 *                 can be used to derive texture coordinates from an object's location. This 3x4 matrix T computes the coordinates
 *                 (s,t) from a homogeneous world position p as (s,t) = (s'/q', t'/q') with (s', t', q') = T*p. Thus, perspective
 *                 projections can be specified. The SRS can be specified using standard attributes. If an object is given in a
 *                 different reference system, it is transformed to the SRS before applying the transformation. A transformation
 *                 matrix can be used for whole surfaces. It is not required to specify it per LinearRing. 
 * 
 * <p>Java class for TexCoordGenType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TexCoordGenType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/appearance/1.0}AbstractTextureParameterizationType">
 *       &lt;sequence>
 *         &lt;element name="worldToTexture">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.opengis.net/citygml/1.0>TransformationMatrix3x4Type">
 *                 &lt;attGroup ref="{http://www.opengis.net/gml}SRSReferenceGroup"/>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.opengis.net/citygml/appearance/1.0}_GenericApplicationPropertyOfTexCoordGen" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TexCoordGenType", propOrder = {
    "worldToTexture",
    "genericApplicationPropertyOfTexCoordGen"
})
public class TexCoordGenType extends AbstractTextureParameterizationType {

    @XmlElement(required = true)
    private TexCoordGenType.WorldToTexture worldToTexture;
    @XmlElement(name = "_GenericApplicationPropertyOfTexCoordGen")
    private List<Object> genericApplicationPropertyOfTexCoordGen;

    /**
     * Gets the value of the worldToTexture property.
     * 
     * @return
     *     possible object is
     *     {@link TexCoordGenType.WorldToTexture }
     *     
     */
    public TexCoordGenType.WorldToTexture getWorldToTexture() {
        return worldToTexture;
    }

    /**
     * Sets the value of the worldToTexture property.
     * 
     * @param value
     *     allowed object is
     *     {@link TexCoordGenType.WorldToTexture }
     *     
     */
    public void setWorldToTexture(TexCoordGenType.WorldToTexture value) {
        this.worldToTexture = value;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfTexCoordGen property.
     * 
     */
    public List<Object> getGenericApplicationPropertyOfTexCoordGen() {
        if (genericApplicationPropertyOfTexCoordGen == null) {
            genericApplicationPropertyOfTexCoordGen = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfTexCoordGen;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (worldToTexture != null) {
            s.append("worldToTexture:").append(worldToTexture).append('\n');
        }
        if (genericApplicationPropertyOfTexCoordGen != null && genericApplicationPropertyOfTexCoordGen.size() > 0) {
            s.append("genericApplicationPropertyOfTexCoordGen:").append('\n');
            for (Object fp : genericApplicationPropertyOfTexCoordGen) {
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
     *     &lt;extension base="&lt;http://www.opengis.net/citygml/1.0>TransformationMatrix3x4Type">
     *       &lt;attGroup ref="{http://www.opengis.net/gml}SRSReferenceGroup"/>
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
    public static class WorldToTexture {

        @XmlValue
        private List<Double> value;
        @XmlAttribute
        @XmlSchemaType(name = "anyURI")
        private String srsName;
        @XmlAttribute
        @XmlSchemaType(name = "positiveInteger")
        private Integer srsDimension;
        @XmlAttribute
        private List<String> axisLabels;
        @XmlAttribute
        private List<String> uomLabels;

        /**
         * Used for texture parameterization. The Transformation matrix is a 3 by 4 matrix, thus it must be a
         * list with 12 items. The order the matrix element are represented is row-major, i. e. the first 4 elements
         * represent the first row, the fifth to the eight element the second row,... Gets the value of the value property.
         * 
         */
        public List<Double> getValue() {
            if (value == null) {
                value = new ArrayList<Double>();
            }
            return this.value;
        }

        /**
         * Gets the value of the srsName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSrsName() {
            return srsName;
        }

        /**
         * Sets the value of the srsName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSrsName(String value) {
            this.srsName = value;
        }

        /**
         * Gets the value of the srsDimension property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public Integer getSrsDimension() {
            return srsDimension;
        }

        /**
         * Sets the value of the srsDimension property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setSrsDimension(Integer value) {
            this.srsDimension = value;
        }

        /**
         * Gets the value of the axisLabels property.
         * 
         */
        public List<String> getAxisLabels() {
            if (axisLabels == null) {
                axisLabels = new ArrayList<String>();
            }
            return this.axisLabels;
        }

        /**
         * Gets the value of the uomLabels property.
         * 
         */
        public List<String> getUomLabels() {
            if (uomLabels == null) {
                uomLabels = new ArrayList<String>();
            }
            return this.uomLabels;
        }

        @Override
        public String toString() {
            final StringBuilder s = new StringBuilder("[WorldToTexture]");
            if (srsDimension != null) {
                s.append("srsDimension:").append(srsDimension).append('\n');
            }
            if (srsName != null) {
                s.append("srsName:").append(srsName).append('\n');
            }
            if (axisLabels != null && axisLabels.size() > 0) {
                s.append("axisLabels:").append('\n');
                for (String fp : axisLabels) {
                    s.append(fp).append('\n');
                }
            }
            if (uomLabels != null && uomLabels.size() > 0) {
                s.append("uomLabels:").append('\n');
                for (String fp : uomLabels) {
                    s.append(fp).append('\n');
                }
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
