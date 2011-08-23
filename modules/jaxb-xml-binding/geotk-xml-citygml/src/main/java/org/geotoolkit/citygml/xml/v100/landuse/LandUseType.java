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
package org.geotoolkit.citygml.xml.v100.landuse;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.citygml.xml.v100.AbstractCityObjectType;
import org.geotoolkit.gml.xml.v311.MultiSurfacePropertyType;


/**
 * Type describing the class for Land Use in all LOD. LandUse objects describe areas of the earth’s
 * surface dedicated to a specific land use. The geometry must consist of 3-D surfaces. As subclass of _CityObject, a
 * LandUse inherits all attributes and relations, in particular an id, names, external references, and generalization
 * relations. 
 * 
 * <p>Java class for LandUseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LandUseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/1.0}AbstractCityObjectType">
 *       &lt;sequence>
 *         &lt;element name="class" type="{http://www.opengis.net/citygml/landuse/1.0}LandUseClassType" minOccurs="0"/>
 *         &lt;element name="function" type="{http://www.opengis.net/citygml/landuse/1.0}LandUseFunctionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="usage" type="{http://www.opengis.net/citygml/landuse/1.0}LandUseUsageType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lod0MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod1MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod2MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod3MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod4MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/landuse/1.0}_GenericApplicationPropertyOfLandUse" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LandUseType", propOrder = {
    "clazz",
    "function",
    "usage",
    "lod0MultiSurface",
    "lod1MultiSurface",
    "lod2MultiSurface",
    "lod3MultiSurface",
    "lod4MultiSurface",
    "genericApplicationPropertyOfLandUse"
})
public class LandUseType extends AbstractCityObjectType {

    @XmlElement(name = "class")
    private String clazz;
    private List<String> function;
    private List<String> usage;
    private MultiSurfacePropertyType lod0MultiSurface;
    private MultiSurfacePropertyType lod1MultiSurface;
    private MultiSurfacePropertyType lod2MultiSurface;
    private MultiSurfacePropertyType lod3MultiSurface;
    private MultiSurfacePropertyType lod4MultiSurface;
    @XmlElement(name = "_GenericApplicationPropertyOfLandUse")
    private List<Object> genericApplicationPropertyOfLandUse;

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
     */
    public List<String> getUsage() {
        if (usage == null) {
            usage = new ArrayList<String>();
        }
        return this.usage;
    }

    /**
     * Gets the value of the lod0MultiSurface property.
     * 
     * @return
     *     possible object is
     *     {@link MultiSurfacePropertyType }
     *     
     */
    public MultiSurfacePropertyType getLod0MultiSurface() {
        return lod0MultiSurface;
    }

    /**
     * Sets the value of the lod0MultiSurface property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultiSurfacePropertyType }
     *     
     */
    public void setLod0MultiSurface(MultiSurfacePropertyType value) {
        this.lod0MultiSurface = value;
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
     * Gets the value of the genericApplicationPropertyOfLandUse property.
     * 
     */
    public List<Object> getGenericApplicationPropertyOfLandUse() {
        if (genericApplicationPropertyOfLandUse == null) {
            genericApplicationPropertyOfLandUse = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfLandUse;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (clazz != null) {
            s.append("class:").append(clazz).append('\n');
        }
        if (function != null && function.size() > 0) {
            s.append("function:").append('\n');
            for (String fp : function) {
                s.append(fp).append('\n');
            }
        }
        if (genericApplicationPropertyOfLandUse != null && genericApplicationPropertyOfLandUse.size() > 0) {
            s.append("genericApplicationPropertyOfCityModel:").append('\n');
            for (Object fp : genericApplicationPropertyOfLandUse) {
                s.append(fp).append('\n');
            }
        }
        if (usage != null && usage.size() > 0) {
            s.append("usage:").append('\n');
            for (String fp : usage) {
                s.append(fp).append('\n');
            }
        }
        if (lod0MultiSurface != null) {
            s.append("lod0MultiSurface:").append(lod0MultiSurface).append('\n');
        }
        if (lod1MultiSurface != null) {
            s.append("lod1MultiSurface:").append(lod1MultiSurface).append('\n');
        }
        if (lod2MultiSurface != null) {
            s.append("lod2MultiSurface:").append(lod2MultiSurface).append('\n');
        }
        if (lod3MultiSurface != null) {
            s.append("lod3MultiSurface:").append(lod3MultiSurface).append('\n');
        }
        if (lod4MultiSurface != null) {
            s.append("lod4MultiSurface:").append(lod4MultiSurface).append('\n');
        }
        return s.toString();
    }
}
