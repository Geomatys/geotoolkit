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
 * <p>Java class for PairType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PairType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}key" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}styleUrl" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractStyleSelectorGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PairSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PairObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PairType", propOrder = {
    "key",
    "styleUrl",
    "abstractStyleSelectorGroup",
    "pairSimpleExtensionGroup",
    "pairObjectExtensionGroup"
})
public class PairType extends AbstractObjectType {

    @XmlElement(defaultValue = "normal")
    private StyleStateEnumType key;
    @XmlSchemaType(name = "anyURI")
    private String styleUrl;
    @XmlElementRef(name = "AbstractStyleSelectorGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class)
    private JAXBElement<? extends AbstractStyleSelectorType> abstractStyleSelectorGroup;
    @XmlElement(name = "PairSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    private List<Object> pairSimpleExtensionGroup;
    @XmlElement(name = "PairObjectExtensionGroup")
    private List<AbstractObjectType> pairObjectExtensionGroup;

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link StyleStateEnumType }
     *     
     */
    public StyleStateEnumType getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link StyleStateEnumType }
     *     
     */
    public void setKey(StyleStateEnumType value) {
        this.key = value;
    }

    /**
     * Gets the value of the styleUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStyleUrl() {
        return styleUrl;
    }

    /**
     * Sets the value of the styleUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStyleUrl(String value) {
        this.styleUrl = value;
    }

    /**
     * Gets the value of the abstractStyleSelectorGroup property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractStyleSelectorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link StyleMapType }{@code >}
     *     {@link JAXBElement }{@code <}{@link StyleType }{@code >}
     *     
     */
    public JAXBElement<? extends AbstractStyleSelectorType> getAbstractStyleSelectorGroup() {
        return abstractStyleSelectorGroup;
    }

    /**
     * Sets the value of the abstractStyleSelectorGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractStyleSelectorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link StyleMapType }{@code >}
     *     {@link JAXBElement }{@code <}{@link StyleType }{@code >}
     *     
     */
    public void setAbstractStyleSelectorGroup(JAXBElement<? extends AbstractStyleSelectorType> value) {
        this.abstractStyleSelectorGroup = ((JAXBElement<? extends AbstractStyleSelectorType> ) value);
    }

    /**
     * Gets the value of the pairSimpleExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pairSimpleExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPairSimpleExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getPairSimpleExtensionGroup() {
        if (pairSimpleExtensionGroup == null) {
            pairSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.pairSimpleExtensionGroup;
    }

    /**
     * Gets the value of the pairObjectExtensionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pairObjectExtensionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPairObjectExtensionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     * 
     * 
     */
    public List<AbstractObjectType> getPairObjectExtensionGroup() {
        if (pairObjectExtensionGroup == null) {
            pairObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.pairObjectExtensionGroup;
    }

}
