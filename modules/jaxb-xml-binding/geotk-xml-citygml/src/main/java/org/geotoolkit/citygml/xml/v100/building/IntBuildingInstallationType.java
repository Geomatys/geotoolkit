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
package org.geotoolkit.citygml.xml.v100.building;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.citygml.xml.v100.AbstractCityObjectType;
import org.geotoolkit.gml.xml.v311.GeometryPropertyType;


/**
 * An IntBuildingInstallation is an interior part of a Building which has a specific function or
 *  semantical meaning.
 * Examples are interior stairs, railings, radiators or pipes. As subclass of _CityObject,
 * a nIntBuildingInstallation inherits all attributes and relations, in particular an id, names, external references,
 * and generalization relations.
 * 
 * <p>Java class for IntBuildingInstallationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntBuildingInstallationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/1.0}AbstractCityObjectType">
 *       &lt;sequence>
 *         &lt;element name="class" type="{http://www.opengis.net/citygml/building/1.0}IntBuildingInstallationClassType" minOccurs="0"/>
 *         &lt;element name="function" type="{http://www.opengis.net/citygml/building/1.0}IntBuildingInstallationFunctionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="usage" type="{http://www.opengis.net/citygml/building/1.0}IntBuildingInstallationUsageType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lod4Geometry" type="{http://www.opengis.net/gml}GeometryPropertyType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/building/1.0}_GenericApplicationPropertyOfIntBuildingInstallation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntBuildingInstallationType", propOrder = {
    "clazz",
    "function",
    "usage",
    "lod4Geometry",
    "genericApplicationPropertyOfIntBuildingInstallation"
})
public class IntBuildingInstallationType extends AbstractCityObjectType {

    @XmlElement(name = "class")
    private String clazz;
    private List<String> function;
    private List<String> usage;
    private GeometryPropertyType lod4Geometry;
    @XmlElement(name = "_GenericApplicationPropertyOfIntBuildingInstallation")
    private List<Object> genericApplicationPropertyOfIntBuildingInstallation;

    /**
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

    /**
     * Gets the value of the function property.
     */
    public List<String> getFunction() {
        if (function == null) {
            function = new ArrayList<String>();
        }
        return this.function;
    }

    /**
     * Gets the value of the usage property.
     */
    public List<String> getUsage() {
        if (usage == null) {
            usage = new ArrayList<String>();
        }
        return this.usage;
    }

    /**
     * Gets the value of the lod4Geometry property.
     * 
     * @return
     *     possible object is
     *     {@link GeometryPropertyType }
     *     
     */
    public GeometryPropertyType getLod4Geometry() {
        return lod4Geometry;
    }

    /**
     * Sets the value of the lod4Geometry property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeometryPropertyType }
     *     
     */
    public void setLod4Geometry(GeometryPropertyType value) {
        this.lod4Geometry = value;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfIntBuildingInstallation property.
     */
    public List<Object> getGenericApplicationPropertyOfIntBuildingInstallation() {
        if (genericApplicationPropertyOfIntBuildingInstallation == null) {
            genericApplicationPropertyOfIntBuildingInstallation = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfIntBuildingInstallation;
    }

}
