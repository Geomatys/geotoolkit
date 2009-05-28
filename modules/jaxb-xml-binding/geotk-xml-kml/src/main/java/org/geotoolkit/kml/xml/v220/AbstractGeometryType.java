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
package org.geotoolkit.kml.xml.v220;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractGeometryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractGeometryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractGeometrySimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractGeometryObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGeometryType", propOrder = {
    "abstractGeometrySimpleExtensionGroup",
    "abstractGeometryObjectExtensionGroup"
})
@XmlSeeAlso({
    ModelType.class,
    PointType.class,
    PolygonType.class,
    LinearRingType.class,
    MultiGeometryType.class,
    LineStringType.class
})
public abstract class AbstractGeometryType
    extends AbstractObjectType
{

    @XmlElement(name = "AbstractGeometrySimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    private List<Object> abstractGeometrySimpleExtensionGroup;
    @XmlElement(name = "AbstractGeometryObjectExtensionGroup")
    private List<AbstractObjectType> abstractGeometryObjectExtensionGroup;

    /**
     * Gets the value of the abstractGeometrySimpleExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractGeometrySimpleExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractGeometrySimpleExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAbstractGeometrySimpleExtensionGroup() {
        if (abstractGeometrySimpleExtensionGroup == null) {
            abstractGeometrySimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.abstractGeometrySimpleExtensionGroup;
    }

    /**
     * Gets the value of the abstractGeometryObjectExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractGeometryObjectExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractGeometryObjectExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     * 
     * 
     */
    public List<AbstractObjectType> getAbstractGeometryObjectExtensionGroup() {
        if (abstractGeometryObjectExtensionGroup == null) {
            abstractGeometryObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.abstractGeometryObjectExtensionGroup;
    }

}
