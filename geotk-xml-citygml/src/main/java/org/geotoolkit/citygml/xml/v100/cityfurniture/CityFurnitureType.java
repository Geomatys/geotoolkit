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
package org.geotoolkit.citygml.xml.v100.cityfurniture;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.citygml.xml.v100.AbstractCityObjectType;
import org.geotoolkit.citygml.xml.v100.ImplicitRepresentationPropertyType;
import org.geotoolkit.gml.xml.v311.GeometryPropertyType;
import org.geotoolkit.gml.xml.v311.MultiCurvePropertyType;

/**
 * Type describing city furnitures, like traffic lights, benches, ... As subclass of _CityObject, a
 *                 CityFurniture inherits all attributes and relations, in particular an id, names, external references, and
 *                 generalization relations.
 *
 * <p>Java class for CityFurnitureType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CityFurnitureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/1.0}AbstractCityObjectType">
 *       &lt;sequence>
 *         &lt;element name="class" type="{http://www.opengis.net/citygml/cityfurniture/1.0}CityFurnitureClassType" minOccurs="0"/>
 *         &lt;element name="function" type="{http://www.opengis.net/citygml/cityfurniture/1.0}CityFurnitureFunctionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lod1Geometry" type="{http://www.opengis.net/gml}GeometryPropertyType" minOccurs="0"/>
 *         &lt;element name="lod2Geometry" type="{http://www.opengis.net/gml}GeometryPropertyType" minOccurs="0"/>
 *         &lt;element name="lod3Geometry" type="{http://www.opengis.net/gml}GeometryPropertyType" minOccurs="0"/>
 *         &lt;element name="lod4Geometry" type="{http://www.opengis.net/gml}GeometryPropertyType" minOccurs="0"/>
 *         &lt;element name="lod1TerrainIntersection" type="{http://www.opengis.net/gml}MultiCurvePropertyType" minOccurs="0"/>
 *         &lt;element name="lod2TerrainIntersection" type="{http://www.opengis.net/gml}MultiCurvePropertyType" minOccurs="0"/>
 *         &lt;element name="lod3TerrainIntersection" type="{http://www.opengis.net/gml}MultiCurvePropertyType" minOccurs="0"/>
 *         &lt;element name="lod4TerrainIntersection" type="{http://www.opengis.net/gml}MultiCurvePropertyType" minOccurs="0"/>
 *         &lt;element name="lod1ImplicitRepresentation" type="{http://www.opengis.net/citygml/1.0}ImplicitRepresentationPropertyType" minOccurs="0"/>
 *         &lt;element name="lod2ImplicitRepresentation" type="{http://www.opengis.net/citygml/1.0}ImplicitRepresentationPropertyType" minOccurs="0"/>
 *         &lt;element name="lod3ImplicitRepresentation" type="{http://www.opengis.net/citygml/1.0}ImplicitRepresentationPropertyType" minOccurs="0"/>
 *         &lt;element name="lod4ImplicitRepresentation" type="{http://www.opengis.net/citygml/1.0}ImplicitRepresentationPropertyType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/cityfurniture/1.0}_GenericApplicationPropertyOfCityFurniture" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CityFurnitureType", propOrder = {
    "clazz",
    "function",
    "lod1Geometry",
    "lod2Geometry",
    "lod3Geometry",
    "lod4Geometry",
    "lod1TerrainIntersection",
    "lod2TerrainIntersection",
    "lod3TerrainIntersection",
    "lod4TerrainIntersection",
    "lod1ImplicitRepresentation",
    "lod2ImplicitRepresentation",
    "lod3ImplicitRepresentation",
    "lod4ImplicitRepresentation",
    "genericApplicationPropertyOfCityFurniture"
})
public class CityFurnitureType extends AbstractCityObjectType {

    @XmlElement(name = "class")
    private String clazz;
    private List<String> function;
    private GeometryPropertyType lod1Geometry;
    private GeometryPropertyType lod2Geometry;
    private GeometryPropertyType lod3Geometry;
    private GeometryPropertyType lod4Geometry;
    private MultiCurvePropertyType lod1TerrainIntersection;
    private MultiCurvePropertyType lod2TerrainIntersection;
    private MultiCurvePropertyType lod3TerrainIntersection;
    private MultiCurvePropertyType lod4TerrainIntersection;
    private ImplicitRepresentationPropertyType lod1ImplicitRepresentation;
    private ImplicitRepresentationPropertyType lod2ImplicitRepresentation;
    private ImplicitRepresentationPropertyType lod3ImplicitRepresentation;
    private ImplicitRepresentationPropertyType lod4ImplicitRepresentation;
    @XmlElement(name = "_GenericApplicationPropertyOfCityFurniture")
    private List<Object> genericApplicationPropertyOfCityFurniture;

    /**
     * Gets the value of the clazz property.
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     */
    public void setClazz(final String value) {
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
     * Gets the value of the lod1Geometry property.
     */
    public GeometryPropertyType getLod1Geometry() {
        return lod1Geometry;
    }

    /**
     * Sets the value of the lod1Geometry property.
     */
    public void setLod1Geometry(final GeometryPropertyType value) {
        this.lod1Geometry = value;
    }

    /**
     * Gets the value of the lod2Geometry property.
     */
    public GeometryPropertyType getLod2Geometry() {
        return lod2Geometry;
    }

    /**
     * Sets the value of the lod2Geometry property.
     */
    public void setLod2Geometry(final GeometryPropertyType value) {
        this.lod2Geometry = value;
    }

    /**
     * Gets the value of the lod3Geometry property.
     */
    public GeometryPropertyType getLod3Geometry() {
        return lod3Geometry;
    }

    /**
     * Sets the value of the lod3Geometry property.
     */
    public void setLod3Geometry(final GeometryPropertyType value) {
        this.lod3Geometry = value;
    }

    /**
     * Gets the value of the lod4Geometry property.
     */
    public GeometryPropertyType getLod4Geometry() {
        return lod4Geometry;
    }

    /**
     * Sets the value of the lod4Geometry property.
     */
    public void setLod4Geometry(final GeometryPropertyType value) {
        this.lod4Geometry = value;
    }

    /**
     * Gets the value of the lod1TerrainIntersection property.
     */
    public MultiCurvePropertyType getLod1TerrainIntersection() {
        return lod1TerrainIntersection;
    }

    /**
     * Sets the value of the lod1TerrainIntersection property.
     */
    public void setLod1TerrainIntersection(final MultiCurvePropertyType value) {
        this.lod1TerrainIntersection = value;
    }

    /**
     * Gets the value of the lod2TerrainIntersection property.
     */
    public MultiCurvePropertyType getLod2TerrainIntersection() {
        return lod2TerrainIntersection;
    }

    /**
     * Sets the value of the lod2TerrainIntersection property.
     */
    public void setLod2TerrainIntersection(final MultiCurvePropertyType value) {
        this.lod2TerrainIntersection = value;
    }

    /**
     * Gets the value of the lod3TerrainIntersection property.
     */
    public MultiCurvePropertyType getLod3TerrainIntersection() {
        return lod3TerrainIntersection;
    }

    /**
     * Sets the value of the lod3TerrainIntersection property.
     */
    public void setLod3TerrainIntersection(final MultiCurvePropertyType value) {
        this.lod3TerrainIntersection = value;
    }

    /**
     * Gets the value of the lod4TerrainIntersection property.
     */
    public MultiCurvePropertyType getLod4TerrainIntersection() {
        return lod4TerrainIntersection;
    }

    /**
     * Sets the value of the lod4TerrainIntersection property.
     */
    public void setLod4TerrainIntersection(final MultiCurvePropertyType value) {
        this.lod4TerrainIntersection = value;
    }

    /**
     * Gets the value of the lod1ImplicitRepresentation property.
     */
    public ImplicitRepresentationPropertyType getLod1ImplicitRepresentation() {
        return lod1ImplicitRepresentation;
    }

    /**
     * Sets the value of the lod1ImplicitRepresentation property.
     */
    public void setLod1ImplicitRepresentation(final ImplicitRepresentationPropertyType value) {
        this.lod1ImplicitRepresentation = value;
    }

    /**
     * Gets the value of the lod2ImplicitRepresentation property.
     */
    public ImplicitRepresentationPropertyType getLod2ImplicitRepresentation() {
        return lod2ImplicitRepresentation;
    }

    /**
     * Sets the value of the lod2ImplicitRepresentation property.
     */
    public void setLod2ImplicitRepresentation(final ImplicitRepresentationPropertyType value) {
        this.lod2ImplicitRepresentation = value;
    }

    /**
     * Gets the value of the lod3ImplicitRepresentation property.
     */
    public ImplicitRepresentationPropertyType getLod3ImplicitRepresentation() {
        return lod3ImplicitRepresentation;
    }

    /**
     * Sets the value of the lod3ImplicitRepresentation property.
     */
    public void setLod3ImplicitRepresentation(final ImplicitRepresentationPropertyType value) {
        this.lod3ImplicitRepresentation = value;
    }

    /**
     * Gets the value of the lod4ImplicitRepresentation property.
     */
    public ImplicitRepresentationPropertyType getLod4ImplicitRepresentation() {
        return lod4ImplicitRepresentation;
    }

    /**
     * Sets the value of the lod4ImplicitRepresentation property.
     */
    public void setLod4ImplicitRepresentation(final ImplicitRepresentationPropertyType value) {
        this.lod4ImplicitRepresentation = value;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfCityFurniture property.
     */
    public List<Object> getGenericApplicationPropertyOfCityFurniture() {
        if (genericApplicationPropertyOfCityFurniture == null) {
            genericApplicationPropertyOfCityFurniture = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfCityFurniture;
    }
}
