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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.citygml.xml.v100.AbstractCityObjectType;
import org.geotoolkit.gml.xml.v311.MultiSurfacePropertyType;


/**
 * A BoundarySurface is a thematic object which classifies surfaces bounding a building or a room.
 * The geometry of a BoundarySurface is given by MultiSurfaces.
 * As it is a subclass of _CityObject, it inherits all atributes and relations,
 * in particular the external references, and the generalization relations.
 *             
 * 
 * <p>Java class for AbstractBoundarySurfaceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractBoundarySurfaceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/1.0}AbstractCityObjectType">
 *       &lt;sequence>
 *         &lt;element name="lod2MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod3MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod4MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="opening" type="{http://www.opengis.net/citygml/building/1.0}OpeningPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/building/1.0}_GenericApplicationPropertyOfBoundarySurface" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractBoundarySurfaceType", propOrder = {
    "lod2MultiSurface",
    "lod3MultiSurface",
    "lod4MultiSurface",
    "opening",
    "genericApplicationPropertyOfBoundarySurface"
})
@XmlSeeAlso({
    InteriorWallSurfaceType.class,
    RoofSurfaceType.class,
    ClosureSurfaceType.class,
    WallSurfaceType.class,
    FloorSurfaceType.class,
    CeilingSurfaceType.class,
    GroundSurfaceType.class
})
public abstract class AbstractBoundarySurfaceType extends AbstractCityObjectType {

    private MultiSurfacePropertyType lod2MultiSurface;
    private MultiSurfacePropertyType lod3MultiSurface;
    private MultiSurfacePropertyType lod4MultiSurface;
    private List<OpeningPropertyType> opening;
    @XmlElement(name = "_GenericApplicationPropertyOfBoundarySurface")
    private List<Object> genericApplicationPropertyOfBoundarySurface;

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
     * Gets the value of the opening property.
     */
    public List<OpeningPropertyType> getOpening() {
        if (opening == null) {
            opening = new ArrayList<OpeningPropertyType>();
        }
        return this.opening;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfBoundarySurface property.
     */
    public List<Object> getGenericApplicationPropertyOfBoundarySurface() {
        if (genericApplicationPropertyOfBoundarySurface == null) {
            genericApplicationPropertyOfBoundarySurface = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfBoundarySurface;
    }

}
