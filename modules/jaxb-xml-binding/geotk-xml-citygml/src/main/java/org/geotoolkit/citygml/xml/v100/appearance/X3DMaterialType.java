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
import javax.xml.bind.annotation.XmlType;


/**
 * Class for defining constant surface properties. It is based on X3D's material definition. In
 *                 addition, "isSmooth" provides a hint for value interpolation. The link to surface geometry is established via the
 *                 "target"-property. Only gml:MultiSurface or decendants of gml:AbstractSurfaceType are valid targets.
 *             
 * 
 * <p>Java class for X3DMaterialType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="X3DMaterialType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/appearance/1.0}AbstractSurfaceDataType">
 *       &lt;sequence>
 *         &lt;element name="ambientIntensity" type="{http://www.opengis.net/citygml/1.0}doubleBetween0and1" minOccurs="0"/>
 *         &lt;element name="diffuseColor" type="{http://www.opengis.net/citygml/appearance/1.0}Color" minOccurs="0"/>
 *         &lt;element name="emissiveColor" type="{http://www.opengis.net/citygml/appearance/1.0}Color" minOccurs="0"/>
 *         &lt;element name="specularColor" type="{http://www.opengis.net/citygml/appearance/1.0}Color" minOccurs="0"/>
 *         &lt;element name="shininess" type="{http://www.opengis.net/citygml/1.0}doubleBetween0and1" minOccurs="0"/>
 *         &lt;element name="transparency" type="{http://www.opengis.net/citygml/1.0}doubleBetween0and1" minOccurs="0"/>
 *         &lt;element name="isSmooth" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="target" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/appearance/1.0}_GenericApplicationPropertyOfX3DMaterial" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "X3DMaterialType", propOrder = {
    "ambientIntensity",
    "diffuseColor",
    "emissiveColor",
    "specularColor",
    "shininess",
    "transparency",
    "isSmooth",
    "target",
    "genericApplicationPropertyOfX3DMaterial"
})
public class X3DMaterialType extends AbstractSurfaceDataType {

    @XmlElement(defaultValue = "0.2")
    private Double ambientIntensity;
    @XmlList
    @XmlElement(type = Double.class, defaultValue = "0.8 0.8 0.8")
    private List<Double> diffuseColor;
    @XmlList
    @XmlElement(type = Double.class, defaultValue = "0.0 0.0 0.0")
    private List<Double> emissiveColor;
    @XmlList
    @XmlElement(type = Double.class, defaultValue = "1.0 1.0 1.0")
    private List<Double> specularColor;
    @XmlElement(defaultValue = "0.2")
    private Double shininess;
    @XmlElement(defaultValue = "0.0")
    private Double transparency;
    @XmlElement(defaultValue = "false")
    private Boolean isSmooth;
    @XmlSchemaType(name = "anyURI")
    private List<String> target;
    @XmlElement(name = "_GenericApplicationPropertyOfX3DMaterial")
    private List<Object> genericApplicationPropertyOfX3DMaterial;

    /**
     * Gets the value of the ambientIntensity property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getAmbientIntensity() {
        return ambientIntensity;
    }

    /**
     * Sets the value of the ambientIntensity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAmbientIntensity(Double value) {
        this.ambientIntensity = value;
    }

    /**
     * Gets the value of the diffuseColor property.
     * 
     */
    public List<Double> getDiffuseColor() {
        if (diffuseColor == null) {
            diffuseColor = new ArrayList<Double>();
        }
        return this.diffuseColor;
    }

    /**
     * Gets the value of the emissiveColor property.
     * 
     */
    public List<Double> getEmissiveColor() {
        if (emissiveColor == null) {
            emissiveColor = new ArrayList<Double>();
        }
        return this.emissiveColor;
    }

    /**
     * Gets the value of the specularColor property.
     * 
     */
    public List<Double> getSpecularColor() {
        if (specularColor == null) {
            specularColor = new ArrayList<Double>();
        }
        return this.specularColor;
    }

    /**
     * Gets the value of the shininess property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getShininess() {
        return shininess;
    }

    /**
     * Sets the value of the shininess property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setShininess(Double value) {
        this.shininess = value;
    }

    /**
     * Gets the value of the transparency property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getTransparency() {
        return transparency;
    }

    /**
     * Sets the value of the transparency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setTransparency(Double value) {
        this.transparency = value;
    }

    /**
     * Gets the value of the isSmooth property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsSmooth() {
        return isSmooth;
    }

    /**
     * Sets the value of the isSmooth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsSmooth(Boolean value) {
        this.isSmooth = value;
    }

    /**
     * Gets the value of the target property.
     * 
     */
    public List<String> getTarget() {
        if (target == null) {
            target = new ArrayList<String>();
        }
        return this.target;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfX3DMaterial property.
     * 
     */
    public List<Object> getGenericApplicationPropertyOfX3DMaterial() {
        if (genericApplicationPropertyOfX3DMaterial == null) {
            genericApplicationPropertyOfX3DMaterial = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfX3DMaterial;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (ambientIntensity != null) {
            s.append("ambientIntensity:").append(ambientIntensity).append('\n');
        }
        if (shininess != null) {
            s.append("shininess:").append(shininess).append('\n');
        }
        if (transparency != null) {
            s.append("transparency:").append(transparency).append('\n');
        }
        if (diffuseColor != null && diffuseColor.size() > 0) {
            s.append("diffuseColor:").append('\n');
            for (Double fp : diffuseColor) {
                s.append(fp).append('\n');
            }
        }
        if (target != null && target.size() > 0) {
            s.append("target:").append('\n');
            for (String fp : target) {
                s.append(fp).append('\n');
            }
        }
        if (emissiveColor != null && emissiveColor.size() > 0) {
            s.append("emissiveColor:").append('\n');
            for (Double fp : emissiveColor) {
                s.append(fp).append('\n');
            }
        }
        if (specularColor != null && specularColor.size() > 0) {
            s.append("specularColor:").append('\n');
            for (Double fp : specularColor) {
                s.append(fp).append('\n');
            }
        }
        if (genericApplicationPropertyOfX3DMaterial != null && genericApplicationPropertyOfX3DMaterial.size() > 0) {
            s.append("genericApplicationPropertyOfX3DMaterial:").append('\n');
            for (Object fp : genericApplicationPropertyOfX3DMaterial) {
                s.append(fp).append('\n');
            }
        }
        return s.toString();
    }
}
