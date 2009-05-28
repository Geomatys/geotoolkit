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
package org.geotoolkit.citygml.xml.v100.transportation;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.MultiSurfacePropertyType;


/**
 * Type describing the class for traffic Areas. Traffic areas are the surfaces where traffic actually
 *                 takes place. As subclass of _CityObject, a TrafficArea inherits all attributes and relations, in particular an id,
 *                 names, external references, and generalization relations. 
 * 
 * <p>Java class for TrafficAreaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrafficAreaType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/transportation/1.0}AbstractTransportationObjectType">
 *       &lt;sequence>
 *         &lt;element name="usage" type="{http://www.opengis.net/citygml/transportation/1.0}TrafficAreaUsageType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="function" type="{http://www.opengis.net/citygml/transportation/1.0}TrafficAreaFunctionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="surfaceMaterial" type="{http://www.opengis.net/citygml/transportation/1.0}TrafficSurfaceMaterialType" minOccurs="0"/>
 *         &lt;element name="lod2MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod3MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod4MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/transportation/1.0}_GenericApplicationPropertyOfTrafficArea" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrafficAreaType", propOrder = {
    "usage",
    "function",
    "surfaceMaterial",
    "lod2MultiSurface",
    "lod3MultiSurface",
    "lod4MultiSurface",
    "genericApplicationPropertyOfTrafficArea"
})
public class TrafficAreaType
    extends AbstractTransportationObjectType
{

    protected List<String> usage;
    protected List<String> function;
    protected String surfaceMaterial;
    protected MultiSurfacePropertyType lod2MultiSurface;
    protected MultiSurfacePropertyType lod3MultiSurface;
    protected MultiSurfacePropertyType lod4MultiSurface;
    @XmlElement(name = "_GenericApplicationPropertyOfTrafficArea")
    protected List<Object> genericApplicationPropertyOfTrafficArea;

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
     * Gets the value of the surfaceMaterial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurfaceMaterial() {
        return surfaceMaterial;
    }

    /**
     * Sets the value of the surfaceMaterial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurfaceMaterial(String value) {
        this.surfaceMaterial = value;
    }

    /**
     * Gets the value of the lod2MultiSurface property.
     * 
     * @return
     *     possible object is
     *     {@link MultiSurfacePropertyType }
     *     
     */
    public MultiSurfacePropertyType getLod2MultiSurface() {
        return lod2MultiSurface;
    }

    /**
     * Sets the value of the lod2MultiSurface property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultiSurfacePropertyType }
     *     
     */
    public void setLod2MultiSurface(MultiSurfacePropertyType value) {
        this.lod2MultiSurface = value;
    }

    /**
     * Gets the value of the lod3MultiSurface property.
     * 
     * @return
     *     possible object is
     *     {@link MultiSurfacePropertyType }
     *     
     */
    public MultiSurfacePropertyType getLod3MultiSurface() {
        return lod3MultiSurface;
    }

    /**
     * Sets the value of the lod3MultiSurface property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultiSurfacePropertyType }
     *     
     */
    public void setLod3MultiSurface(MultiSurfacePropertyType value) {
        this.lod3MultiSurface = value;
    }

    /**
     * Gets the value of the lod4MultiSurface property.
     * 
     * @return
     *     possible object is
     *     {@link MultiSurfacePropertyType }
     *     
     */
    public MultiSurfacePropertyType getLod4MultiSurface() {
        return lod4MultiSurface;
    }

    /**
     * Sets the value of the lod4MultiSurface property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultiSurfacePropertyType }
     *     
     */
    public void setLod4MultiSurface(MultiSurfacePropertyType value) {
        this.lod4MultiSurface = value;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfTrafficArea property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the genericApplicationPropertyOfTrafficArea property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGenericApplicationPropertyOfTrafficArea().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getGenericApplicationPropertyOfTrafficArea() {
        if (genericApplicationPropertyOfTrafficArea == null) {
            genericApplicationPropertyOfTrafficArea = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfTrafficArea;
    }

}
