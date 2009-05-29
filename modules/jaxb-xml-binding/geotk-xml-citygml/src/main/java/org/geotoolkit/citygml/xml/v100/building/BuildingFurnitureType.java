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
import org.geotoolkit.citygml.xml.v100.ImplicitRepresentationPropertyType;
import org.geotoolkit.gml.xml.v311modified.GeometryPropertyType;


/**
 * Type for building furnitures. As subclass of _CityObject, a BuildingFurniture inherits all
 *                 attributes and relations, in particular an id, names, external references, and generalization relations.
 *             
 * 
 * <p>Java class for BuildingFurnitureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BuildingFurnitureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/1.0}AbstractCityObjectType">
 *       &lt;sequence>
 *         &lt;element name="class" type="{http://www.opengis.net/citygml/building/1.0}BuildingFurnitureClassType" minOccurs="0"/>
 *         &lt;element name="function" type="{http://www.opengis.net/citygml/building/1.0}BuildingFurnitureFunctionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="usage" type="{http://www.opengis.net/citygml/building/1.0}BuildingFurnitureUsageType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lod4Geometry" type="{http://www.opengis.net/gml}GeometryPropertyType" minOccurs="0"/>
 *         &lt;element name="lod4ImplicitRepresentation" type="{http://www.opengis.net/citygml/1.0}ImplicitRepresentationPropertyType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/building/1.0}_GenericApplicationPropertyOfBuildingFurniture" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BuildingFurnitureType", propOrder = {
    "clazz",
    "function",
    "usage",
    "lod4Geometry",
    "lod4ImplicitRepresentation",
    "genericApplicationPropertyOfBuildingFurniture"
})
public class BuildingFurnitureType
    extends AbstractCityObjectType
{

    @XmlElement(name = "class")
    protected String clazz;
    protected List<String> function;
    protected List<String> usage;
    protected GeometryPropertyType lod4Geometry;
    protected ImplicitRepresentationPropertyType lod4ImplicitRepresentation;
    @XmlElement(name = "_GenericApplicationPropertyOfBuildingFurniture")
    protected List<Object> genericApplicationPropertyOfBuildingFurniture;

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
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the function property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFunction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFunction() {
        if (function == null) {
            function = new ArrayList<String>();
        }
        return this.function;
    }

    /**
     * Gets the value of the usage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the usage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
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
     * Gets the value of the lod4ImplicitRepresentation property.
     * 
     * @return
     *     possible object is
     *     {@link ImplicitRepresentationPropertyType }
     *     
     */
    public ImplicitRepresentationPropertyType getLod4ImplicitRepresentation() {
        return lod4ImplicitRepresentation;
    }

    /**
     * Sets the value of the lod4ImplicitRepresentation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImplicitRepresentationPropertyType }
     *     
     */
    public void setLod4ImplicitRepresentation(ImplicitRepresentationPropertyType value) {
        this.lod4ImplicitRepresentation = value;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfBuildingFurniture property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the genericApplicationPropertyOfBuildingFurniture property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGenericApplicationPropertyOfBuildingFurniture().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getGenericApplicationPropertyOfBuildingFurniture() {
        if (genericApplicationPropertyOfBuildingFurniture == null) {
            genericApplicationPropertyOfBuildingFurniture = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfBuildingFurniture;
    }

}
