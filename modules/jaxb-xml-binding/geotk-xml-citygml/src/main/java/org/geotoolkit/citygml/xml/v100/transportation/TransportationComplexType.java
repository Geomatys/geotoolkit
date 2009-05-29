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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.GeometricComplexPropertyType;
import org.geotoolkit.gml.xml.v311modified.MultiSurfacePropertyType;


/**
 * Type describing transportation complexes, which are aggregated features, e.g. roads, which consist
 *                 of parts (traffic areas, e.g. pedestrian path, and auxiliary traffic areas). As subclass of _CityObject, a
 *                 TransportationComplex inherits all attributes and relations, in particular an id, names, external references, and
 *                 generalization relations. 
 * 
 * <p>Java class for TransportationComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransportationComplexType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/transportation/1.0}AbstractTransportationObjectType">
 *       &lt;sequence>
 *         &lt;element name="function" type="{http://www.opengis.net/citygml/transportation/1.0}TransportationComplexFunctionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="usage" type="{http://www.opengis.net/citygml/transportation/1.0}TransportationComplexUsageType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="trafficArea" type="{http://www.opengis.net/citygml/transportation/1.0}TrafficAreaPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="auxiliaryTrafficArea" type="{http://www.opengis.net/citygml/transportation/1.0}AuxiliaryTrafficAreaPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lod0Network" type="{http://www.opengis.net/gml}GeometricComplexPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lod1MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod2MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod3MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod4MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/transportation/1.0}_GenericApplicationPropertyOfTransportationComplex" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransportationComplexType", propOrder = {
    "function",
    "usage",
    "trafficArea",
    "auxiliaryTrafficArea",
    "lod0Network",
    "lod1MultiSurface",
    "lod2MultiSurface",
    "lod3MultiSurface",
    "lod4MultiSurface",
    "genericApplicationPropertyOfTransportationComplex"
})
@XmlSeeAlso({
    RoadType.class,
    SquareType.class,
    TrackType.class,
    RailwayType.class
})
public class TransportationComplexType
    extends AbstractTransportationObjectType
{

    protected List<String> function;
    protected List<String> usage;
    protected List<TrafficAreaPropertyType> trafficArea;
    protected List<AuxiliaryTrafficAreaPropertyType> auxiliaryTrafficArea;
    protected List<GeometricComplexPropertyType> lod0Network;
    protected MultiSurfacePropertyType lod1MultiSurface;
    protected MultiSurfacePropertyType lod2MultiSurface;
    protected MultiSurfacePropertyType lod3MultiSurface;
    protected MultiSurfacePropertyType lod4MultiSurface;
    @XmlElement(name = "_GenericApplicationPropertyOfTransportationComplex")
    protected List<Object> genericApplicationPropertyOfTransportationComplex;

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
     * Gets the value of the trafficArea property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trafficArea property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrafficArea().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TrafficAreaPropertyType }
     * 
     * 
     */
    public List<TrafficAreaPropertyType> getTrafficArea() {
        if (trafficArea == null) {
            trafficArea = new ArrayList<TrafficAreaPropertyType>();
        }
        return this.trafficArea;
    }

    /**
     * Gets the value of the auxiliaryTrafficArea property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the auxiliaryTrafficArea property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuxiliaryTrafficArea().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AuxiliaryTrafficAreaPropertyType }
     * 
     * 
     */
    public List<AuxiliaryTrafficAreaPropertyType> getAuxiliaryTrafficArea() {
        if (auxiliaryTrafficArea == null) {
            auxiliaryTrafficArea = new ArrayList<AuxiliaryTrafficAreaPropertyType>();
        }
        return this.auxiliaryTrafficArea;
    }

    /**
     * Gets the value of the lod0Network property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lod0Network property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLod0Network().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GeometricComplexPropertyType }
     * 
     * 
     */
    public List<GeometricComplexPropertyType> getLod0Network() {
        if (lod0Network == null) {
            lod0Network = new ArrayList<GeometricComplexPropertyType>();
        }
        return this.lod0Network;
    }

    /**
     * Gets the value of the lod1MultiSurface property.
     * 
     * @return
     *     possible object is
     *     {@link MultiSurfacePropertyType }
     *     
     */
    public MultiSurfacePropertyType getLod1MultiSurface() {
        return lod1MultiSurface;
    }

    /**
     * Sets the value of the lod1MultiSurface property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultiSurfacePropertyType }
     *     
     */
    public void setLod1MultiSurface(MultiSurfacePropertyType value) {
        this.lod1MultiSurface = value;
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
     * Gets the value of the genericApplicationPropertyOfTransportationComplex property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the genericApplicationPropertyOfTransportationComplex property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGenericApplicationPropertyOfTransportationComplex().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getGenericApplicationPropertyOfTransportationComplex() {
        if (genericApplicationPropertyOfTransportationComplex == null) {
            genericApplicationPropertyOfTransportationComplex = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfTransportationComplex;
    }

}
