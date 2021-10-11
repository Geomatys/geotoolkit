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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolygonType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PolygonType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractGeometryType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}extrude" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}tessellate" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}altitudeModeGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}outerBoundaryIs" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}innerBoundaryIs" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PolygonSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PolygonObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "PolygonType", propOrder = {
    "extrude",
    "tessellate",
    "altitudeModeGroup",
    "outerBoundaryIs",
    "innerBoundaryIs",
    "polygonSimpleExtensionGroup",
    "polygonObjectExtensionGroup"
})
public class PolygonType extends AbstractGeometryType {

    @XmlElement(defaultValue = "0")
    private Boolean extrude;
    @XmlElement(defaultValue = "0")
    private Boolean tessellate;
    @XmlElementRef(name = "altitudeModeGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class)
    private JAXBElement<?> altitudeModeGroup;
    private BoundaryType outerBoundaryIs;
    private List<BoundaryType> innerBoundaryIs;
    @XmlElement(name = "PolygonSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    private List<Object> polygonSimpleExtensionGroup;
    @XmlElement(name = "PolygonObjectExtensionGroup")
    private List<AbstractObjectType> polygonObjectExtensionGroup;

    /**
     * Gets the value of the extrude property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isExtrude() {
        return extrude;
    }

    /**
     * Sets the value of the extrude property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setExtrude(final Boolean value) {
        this.extrude = value;
    }

    /**
     * Gets the value of the tessellate property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isTessellate() {
        return tessellate;
    }

    /**
     * Sets the value of the tessellate property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setTessellate(final Boolean value) {
        this.tessellate = value;
    }

    /**
     * Gets the value of the altitudeModeGroup property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link AltitudeModeEnumType }{@code >}
     *
     */
    public JAXBElement<?> getAltitudeModeGroup() {
        return altitudeModeGroup;
    }

    /**
     * Sets the value of the altitudeModeGroup property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link AltitudeModeEnumType }{@code >}
     *
     */
    public void setAltitudeModeGroup(final JAXBElement<?> value) {
        this.altitudeModeGroup = ((JAXBElement<?> ) value);
    }

    /**
     * Gets the value of the outerBoundaryIs property.
     *
     * @return
     *     possible object is
     *     {@link BoundaryType }
     *
     */
    public BoundaryType getOuterBoundaryIs() {
        return outerBoundaryIs;
    }

    /**
     * Sets the value of the outerBoundaryIs property.
     *
     * @param value
     *     allowed object is
     *     {@link BoundaryType }
     *
     */
    public void setOuterBoundaryIs(final BoundaryType value) {
        this.outerBoundaryIs = value;
    }

    /**
     * Gets the value of the innerBoundaryIs property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the innerBoundaryIs property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInnerBoundaryIs().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BoundaryType }
     *
     *
     */
    public List<BoundaryType> getInnerBoundaryIs() {
        if (innerBoundaryIs == null) {
            innerBoundaryIs = new ArrayList<BoundaryType>();
        }
        return this.innerBoundaryIs;
    }

    /**
     * Gets the value of the polygonSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the polygonSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolygonSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getPolygonSimpleExtensionGroup() {
        if (polygonSimpleExtensionGroup == null) {
            polygonSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.polygonSimpleExtensionGroup;
    }

    /**
     * Gets the value of the polygonObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the polygonObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolygonObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getPolygonObjectExtensionGroup() {
        if (polygonObjectExtensionGroup == null) {
            polygonObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.polygonObjectExtensionGroup;
    }

}
